/**
 * main.js — React 应用启动入口
 *
 * 作用：将 ChatApp 挂载到 index.html 中的 #root 节点。
 * 使用 esm.sh 提供的 React 18，无需 webpack/vite 构建步骤。
 */
import React from 'https://esm.sh/react@18.3.1';
import { createRoot } from 'https://esm.sh/react-dom@18.3.1/client';
import { ChatApp } from './components/ChatApp.js';

const rootEl = document.getElementById('root');
if (rootEl) {
    createRoot(rootEl).render(
        React.createElement(React.StrictMode, null, React.createElement(ChatApp))
    );
}
