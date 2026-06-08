/**
 * MessageList.js — 消息列表容器
 *
 * 作用：展示全部历史消息，新消息到达时自动滚动到底部。
 */
import React, { useEffect, useRef } from 'https://esm.sh/react@18.3.1';
import { MessageBubble } from './MessageBubble.js';

/** @param {{ messages: object[], isLoading: boolean }} props */
export function MessageList({ messages, isLoading }) {
    const bottomRef = useRef(null);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, isLoading]);

    return React.createElement(
        'div',
        { className: 'message-list', role: 'log', 'aria-live': 'polite' },
        React.createElement(
            'div',
            { className: 'message-list__inner' },
            messages.length === 0 &&
                React.createElement(
                    'div',
                    { className: 'message-list__empty' },
                    React.createElement('p', null, '你好，我是基于知识库的 AI 助手。'),
                    React.createElement(
                        'p',
                        null,
                        '在下方输入问题，支持流式输出；可先通过 POST /knowledge 加载知识。'
                    )
                ),
            messages.map((msg) =>
                React.createElement(MessageBubble, { key: msg.id, message: msg })
            ),
            React.createElement('div', { ref: bottomRef })
        )
    );
}
