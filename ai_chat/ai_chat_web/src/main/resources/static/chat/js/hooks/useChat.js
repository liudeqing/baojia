/**
 * useChat.js — 聊天状态与发送逻辑（React Hook）
 *
 * 作用：
 * - 维护消息列表（用户 / 助手）
 * - 调用 chatApi 实现流式或非流式回复
 * - 支持中止当前生成、清空会话
 */

import { useCallback, useRef, useState } from 'https://esm.sh/react@18.3.1';
import { chatOnce, rawChatOnce, streamChatMessage } from '../api/chatApi.js';

/** 生成唯一消息 id */
function nextId() {
    return `msg-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
}

/**
 * @typedef {{ id: string, role: 'user'|'assistant', content: string, streaming?: boolean, error?: boolean }} ChatMessage
 */

/**
 * 聊天核心 Hook
 * @returns 供 ChatApp 使用的状态与方法
 */
export function useChat() {
    /** @type {[ChatMessage[], Function]} */
    const [messages, setMessages] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    /** 是否启用流式输出（类似 ChatGPT 打字效果） */
    const [streamEnabled, setStreamEnabled] = useState(true);
    /** 是否使用 RAG 知识库增强 */
    const [ragEnabled, setRagEnabled] = useState(true);
    const abortRef = useRef(null);

    const appendAssistantChunk = useCallback((assistantId, chunk) => {
        setMessages((prev) =>
            prev.map((m) =>
                m.id === assistantId ? { ...m, content: m.content + chunk } : m
            )
        );
    }, []);

    const finishAssistant = useCallback((assistantId) => {
        setMessages((prev) =>
            prev.map((m) =>
                m.id === assistantId ? { ...m, streaming: false } : m
            )
        );
    }, []);

    const markAssistantError = useCallback((assistantId, errorText) => {
        setMessages((prev) =>
            prev.map((m) =>
                m.id === assistantId
                    ? { ...m, content: errorText, streaming: false, error: true }
                    : m
            )
        );
    }, []);

    const sendMessage = useCallback(
        async (text) => {
            const trimmed = text.trim();
            if (!trimmed || isLoading) {
                return;
            }

            const userMsg = { id: nextId(), role: 'user', content: trimmed };
            const assistantId = nextId();
            const assistantMsg = {
                id: assistantId,
                role: 'assistant',
                content: '',
                streaming: true,
            };

            setMessages((prev) => [...prev, userMsg, assistantMsg]);
            setIsLoading(true);

            const controller = new AbortController();
            abortRef.current = controller;

            try {
                if (streamEnabled && ragEnabled) {
                    await streamChatMessage(trimmed, {
                        signal: controller.signal,
                        onChunk: (chunk) => appendAssistantChunk(assistantId, chunk),
                        onDone: () => finishAssistant(assistantId),
                        onError: (err) =>
                            markAssistantError(assistantId, `出错了：${err.message}`),
                    });
                } else if (!streamEnabled && ragEnabled) {
                    const reply = await chatOnce(trimmed, controller.signal);
                    setMessages((prev) =>
                        prev.map((m) =>
                            m.id === assistantId
                                ? { ...m, content: reply, streaming: false }
                                : m
                        )
                    );
                } else {
                    const reply = await rawChatOnce(trimmed, controller.signal);
                    setMessages((prev) =>
                        prev.map((m) =>
                            m.id === assistantId
                                ? { ...m, content: reply, streaming: false }
                                : m
                        )
                    );
                }
            } catch (err) {
                if (err.name !== 'AbortError') {
                    markAssistantError(
                        assistantId,
                        `出错了：${err.message || '未知错误'}`
                    );
                } else {
                    finishAssistant(assistantId);
                }
            } finally {
                setIsLoading(false);
                abortRef.current = null;
            }
        },
        [
            isLoading,
            streamEnabled,
            ragEnabled,
            appendAssistantChunk,
            finishAssistant,
            markAssistantError,
        ]
    );

    const stopGenerating = useCallback(() => {
        abortRef.current?.abort();
        setIsLoading(false);
    }, []);

    const clearMessages = useCallback(() => {
        if (isLoading) {
            abortRef.current?.abort();
        }
        setMessages([]);
        setIsLoading(false);
    }, [isLoading]);

    return {
        messages,
        isLoading,
        streamEnabled,
        setStreamEnabled,
        ragEnabled,
        setRagEnabled,
        sendMessage,
        stopGenerating,
        clearMessages,
    };
}
