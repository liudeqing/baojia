/**
 * ChatHeader.js — 顶部工具栏
 *
 * 作用：标题展示、流式/RAG 开关、停止生成、清空对话。
 */
import React from 'https://esm.sh/react@18.3.1';

export function ChatHeader({
    streamEnabled,
    onStreamChange,
    ragEnabled,
    onRagChange,
    isLoading,
    onStop,
    onClear,
}) {
    return React.createElement(
        'header',
        { className: 'chat-header' },
        React.createElement(
            'div',
            null,
            React.createElement('div', { className: 'chat-header__title' }, 'AI 知识库助手'),
            React.createElement(
                'div',
                { className: 'chat-header__subtitle' },
                'React 流式聊天 · RAG 增强'
            )
        ),
        React.createElement(
            'div',
            { className: 'chat-header__actions' },
            React.createElement(
                'label',
                { className: 'chat-header__toggle' },
                React.createElement('input', {
                    type: 'checkbox',
                    checked: streamEnabled,
                    disabled: isLoading,
                    onChange: (e) => onStreamChange(e.target.checked),
                }),
                '流式输出'
            ),
            React.createElement(
                'label',
                { className: 'chat-header__toggle' },
                React.createElement('input', {
                    type: 'checkbox',
                    checked: ragEnabled,
                    disabled: isLoading,
                    onChange: (e) => onRagChange(e.target.checked),
                }),
                '知识库 RAG'
            ),
            isLoading
                ? React.createElement(
                      'button',
                      { type: 'button', className: 'chat-header__btn', onClick: onStop },
                      '停止生成'
                  )
                : React.createElement(
                      'button',
                      { type: 'button', className: 'chat-header__btn', onClick: onClear },
                      '清空对话'
                  )
        )
    );
}
