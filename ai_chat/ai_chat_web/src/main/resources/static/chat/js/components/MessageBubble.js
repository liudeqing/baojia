/**
 * MessageBubble.js — 单条聊天气泡组件
 *
 * 作用：根据 role 渲染用户 / 助手消息样式；流式输出时显示闪烁光标。
 */
import React from 'https://esm.sh/react@18.3.1';

/** @param {{ message: object }} props */
export function MessageBubble({ message }) {
    const isUser = message.role === 'user';
    const bubbleClass = [
        'message-bubble',
        isUser ? 'message-bubble--user' : 'message-bubble--assistant',
        message.error ? 'message-bubble--error' : '',
        !isUser && message.streaming ? 'streaming-cursor' : '',
    ]
        .filter(Boolean)
        .join(' ');

    return React.createElement(
        'div',
        { className: `message-row message-row--${message.role}` },
        React.createElement(
            'div',
            {
                className: `message-avatar message-avatar--${isUser ? 'user' : 'assistant'}`,
                'aria-hidden': true,
            },
            isUser ? '我' : 'AI'
        ),
        React.createElement(
            'div',
            { className: bubbleClass },
            message.content || (message.streaming ? '思考中…' : '')
        )
    );
}
