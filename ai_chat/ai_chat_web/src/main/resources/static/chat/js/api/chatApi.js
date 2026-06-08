/**
 * chatApi.js — 聊天相关 HTTP API 封装
 *
 * 作用：统一对接后端接口，前端组件不直接拼 URL。
 * - 流式：优先 POST /chat-ui/stream（ChatStreamUiController，适合长文本）
 * - 备用：GET  /chatstream（ChatAiController 已有接口）
 * - 非流式：GET /chat、/raw
 */

/** UI 专用流式接口（POST + SSE） */
const STREAM_POST_URL = '/chat-ui/stream';

/** 原有 GET 流式接口（ChatAiController，未修改） */
const STREAM_GET_URL = '/chatstream';

/**
 * 以流式方式发送消息（RAG + 逐 token 返回）。
 *
 * @param {string} message 用户输入
 * @param {object} handlers 回调
 * @param {(chunk: string) => void} handlers.onChunk 每收到一段文本
 * @param {() => void} [handlers.onDone] 流结束
 * @param {(err: Error) => void} [handlers.onError] 出错
 * @param {AbortSignal} [handlers.signal] 用于中止请求
 * @param {boolean} [handlers.useGetFallback=false] 是否改用 GET /chatstream
 */
export async function streamChatMessage(message, handlers) {
    const { onChunk, onDone, onError, signal, useGetFallback = false } = handlers;

    try {
        let response;
        if (useGetFallback) {
            const url = `${STREAM_GET_URL}?msg=${encodeURIComponent(message)}`;
            response = await fetch(url, { method: 'GET', signal });
        } else {
            response = await fetch(STREAM_POST_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message }),
                signal,
            });
        }

        if (!response.ok) {
            throw new Error(`请求失败：HTTP ${response.status}`);
        }
        if (!response.body) {
            throw new Error('浏览器不支持流式读取');
        }

        const contentType = response.headers.get('Content-Type') || '';
        const isSse = contentType.includes('text/event-stream');
        await readStreamBody(response.body, onChunk, isSse);
        onDone?.();
    } catch (err) {
        if (err.name === 'AbortError') {
            onDone?.();
            return;
        }
        onError?.(err instanceof Error ? err : new Error(String(err)));
    }
}

/**
 * 非流式 RAG 对话（一次性返回完整文本）。
 * 对接 ChatAiController GET /chat
 */
export async function chatOnce(message, signal) {
    const url = `/chat?msg=${encodeURIComponent(message)}`;
    const response = await fetch(url, { signal });
    if (!response.ok) {
        throw new Error(`请求失败：HTTP ${response.status}`);
    }
    return response.text();
}

/**
 * 非流式普通对话（无 RAG）。
 * 对接 ChatAiController GET /raw
 */
export async function rawChatOnce(message, signal) {
    const url = `/raw?msg=${encodeURIComponent(message)}`;
    const response = await fetch(url, { signal });
    if (!response.ok) {
        throw new Error(`请求失败：HTTP ${response.status}`);
    }
    return response.text();
}

/**
 * 从 ReadableStream 中增量读取并解析 SSE / 纯文本流。
 * Spring AI Flux<String> + TEXT_EVENT_STREAM 可能输出 "data:xxx\n\n" 或连续字符串。
 */
async function readStreamBody(body, onChunk, isSse) {
    const reader = body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';

    while (true) {
        const { done, value } = await reader.read();
        if (done) {
            if (isSse) {
                flushBuffer(buffer, onChunk, true);
            } else if (buffer) {
                onChunk(buffer);
            }
            break;
        }
        const chunk = decoder.decode(value, { stream: true });
        if (isSse) {
            buffer += chunk;
            buffer = flushBuffer(buffer, onChunk, false);
        } else {
            onChunk(chunk);
        }
    }
}

/**
 * 解析缓冲区：提取 SSE data 行或整段文本。
 * @returns {string} 剩余未处理缓冲区
 */
function flushBuffer(buffer, onChunk, isFinal) {
    // SSE 事件以双换行分隔
    const parts = buffer.split('\n\n');
    if (!isFinal && parts.length > 1) {
        const remainder = parts.pop();
        for (const part of parts) {
            emitPart(part, onChunk);
        }
        return remainder ?? '';
    }
    if (isFinal && buffer.length > 0) {
        emitPart(buffer, onChunk);
        return '';
    }
    return buffer;
}

function emitPart(part, onChunk) {
    const lines = part.split('\n');
    for (const line of lines) {
        if (line.startsWith('data:')) {
            const payload = line.slice(5).trimStart();
            if (payload && payload !== '[DONE]') {
                onChunk(payload);
            }
        } else if (line.trim()) {
            onChunk(line);
        }
    }
}
