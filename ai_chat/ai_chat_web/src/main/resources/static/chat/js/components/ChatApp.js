/**
 * ChatApp.js — 聊天应用根组件
 *
 * 作用：组合 Header、MessageList、ChatInput，并通过 useChat 管理全局聊天状态。
 */
import React from 'https://esm.sh/react@18.3.1';
import { useChat } from '../hooks/useChat.js';
import { ChatHeader } from './ChatHeader.js';
import { ChatInput } from './ChatInput.js';
import { MessageList } from './MessageList.js';

export function ChatApp() {
    const {
        messages,
        isLoading,
        streamEnabled,
        setStreamEnabled,
        ragEnabled,
        setRagEnabled,
        sendMessage,
        stopGenerating,
        clearMessages,
    } = useChat();

    return React.createElement(
        'div',
        { className: 'chat-app' },
        React.createElement(ChatHeader, {
            streamEnabled,
            onStreamChange: setStreamEnabled,
            ragEnabled,
            onRagChange: setRagEnabled,
            isLoading,
            onStop: stopGenerating,
            onClear: clearMessages,
        }),
        React.createElement(MessageList, { messages, isLoading }),
        React.createElement(ChatInput, { onSend: sendMessage, disabled: isLoading })
    );
}
