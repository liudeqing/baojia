/**
 * ChatInput.js — 底部输入框与发送区
 *
 * 作用：多行输入、Enter 发送 / Shift+Enter 换行、生成中禁用输入。
 */
import React, { useCallback, useRef, useState } from 'https://esm.sh/react@18.3.1';

/** @param {{ onSend: Function, disabled: boolean }} props */
export function ChatInput({ onSend, disabled }) {
    const [value, setValue] = useState('');
    const textareaRef = useRef(null);

    const adjustHeight = useCallback(() => {
        const el = textareaRef.current;
        if (!el) return;
        el.style.height = 'auto';
        el.style.height = `${Math.min(el.scrollHeight, 200)}px`;
    }, []);

    const handleSubmit = useCallback(() => {
        const trimmed = value.trim();
        if (!trimmed || disabled) return;
        onSend(trimmed);
        setValue('');
        if (textareaRef.current) {
            textareaRef.current.style.height = 'auto';
        }
    }, [value, disabled, onSend]);

    const handleKeyDown = useCallback(
        (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSubmit();
            }
        },
        [handleSubmit]
    );

    return React.createElement(
        'div',
        { className: 'chat-input-area' },
        React.createElement(
            'div',
            { className: 'chat-input-area__inner' },
            React.createElement('textarea', {
                ref: textareaRef,
                className: 'chat-input-area__textarea',
                placeholder: '输入消息，Enter 发送，Shift+Enter 换行',
                rows: 1,
                value,
                disabled,
                onChange: (e) => {
                    setValue(e.target.value);
                    adjustHeight();
                },
                onKeyDown: handleKeyDown,
            }),
            React.createElement(
                'button',
                {
                    type: 'button',
                    className: 'chat-input-area__send',
                    disabled: disabled || !value.trim(),
                    onClick: handleSubmit,
                    title: '发送',
                    'aria-label': '发送消息',
                },
                '↑'
            )
        ),
        React.createElement(
            'p',
            { className: 'chat-input-area__hint' },
            '流式输出由后端 Ollama + Spring AI 提供 · 端口默认 8085'
        )
    );
}
