# GSH课程表 GitHub Pages 主页 — 设计文档

## 概述

为 GSH课程表（Great Schedule Helper）制作静态 HTML 主页，部署到 GitHub Pages，作为 App 的官方展示页面。

## 技术选型

- **纯手写 HTML/CSS**，零外部依赖
- 现代 CSS 特性：`backdrop-filter`（毛玻璃）、CSS Grid/Flexbox、CSS 变量、`@keyframes` 动画
- 少量原生 JS 处理导航高亮和滚动交互
- 目标：三页静态站点，文件小、加载快

## 文件结构

```
docs/
├── index.html          # 首页
├── download.html       # 下载页
├── about.html          # 关于页
├── css/
│   └── style.css       # 全局样式
├── js/
│   └── main.js         # 导航交互
└── assets/
    ├── logo.png        # App 图标（已有）
    ├── 001.png         # 截图占位
    └── 002.png         # 截图占位
```

## 视觉风格

### 整体方向

清新校园风 + 毛玻璃（Glassmorphism）。明亮柔和的渐变背景，半透明模糊面板，圆角元素，轻阴影。

### 配色

| 用途 | 色值 | 说明 |
|------|------|------|
| 主色 | `#4A90D9` | 天空蓝，按钮、链接、高亮 |
| 辅色 | `#7EC8A0` | 薄荷绿，卡片点缀、渐变辅色 |
| 背景 | `#F0F4F8` | 浅灰蓝基底，配合柔光渐变 |
| 玻璃面板 | `rgba(255,255,255,0.5~0.6)` + `blur(16~20px)` | 毛玻璃卡片和导航 |
| 文字主色 | `#2C3E50` | 深蓝灰，标题和正文 |
| 文字辅色 | `#7F8C8D` | 中灰，说明文字 |

### 毛玻璃实现

- `background: rgba(255, 255, 255, 0.5)` 半透明白色
- `backdrop-filter: blur(20px)` 背景模糊
- `-webkit-backdrop-filter: blur(20px)` Safari 兼容
- `border: 1px solid rgba(255, 255, 255, 0.7)` 微秒边缘
- `box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06)` 柔和阴影

## 页面设计

### 1. 首页（index.html）

| 区域 | 内容 | 细节 |
|------|------|------|
| 导航栏 | Logo + 标题 + 三个链接 | 毛玻璃固定顶部，当前页高亮 |
| Hero | App 图标、标题"GSH课程表"、标语、下载按钮 | 按钮渐变蓝，悬停放大 |
| 亮点卡片 | 3 张毛玻璃卡片 | 一键导入 / M3 现代化 UI / 轻量省电 |
| 截图预览 | 2 张 App 截图 | 圆角卡片容器，毛玻璃边框 |
| 页脚 | © 2026 GSH课程表 · Apache 2.0 · Powered by WakeUpSchedule | 居中灰色小字 |

### 2. 下载页（download.html）

| 区域 | 内容 |
|------|------|
| 版本信息卡片 | 版本号 v1.0.1-beta、发布日期、文件大小 |
| 下载按钮 | 链接到 GitHub Release APK |
| 更新日志 | 本版本主要改动（适配银杏教务系统、M3 迁移、作息时间适配等） |
| 历史版本 | 链接到 GitHub Releases 页面 |

### 3. 关于页（about.html）

| 区域 | 内容 |
|------|------|
| 项目简介 | GSH课程表是什么，基于 WakeUpSchedule 二次开发 |
| 致谢 | WakeUpSchedule (YZune)、北邮适配 (王衔飞)、xianfei |
| 技术栈 | Kotlin / Jetpack Compose / Material 3 / Room / Retrofit |
| License | Apache License 2.0 |

## 导航行为

- 所有页面共享同一导航栏，通过 `<a>` 链接跳转
- 当前页面对应导航项高亮为蓝色（`#4A90D9`），其余灰色
- 页面切换无动画，纯链接跳转

## 响应式设计

- 桌面端（>768px）：三列卡片、Nav 横向排列
- 平板（480px–768px）：卡片两列或单列，间距缩小
- 手机端（<480px）：卡片单列堆叠，Hero 字号缩小，导航精简

使用 CSS Grid `auto-fit` / `minmax` 实现弹性布局，断点使用 `@media` 查询。

## 浏览器兼容性

- `backdrop-filter` 在 Chrome 76+、Edge 79+、Safari 9+、Firefox 103+ 均支持
- 对不支持的浏览器（如旧版 Firefox），降级为纯白半透明背景（`opacity` fallback）

## 截图命名约定

- 截图文件放在 `docs/assets/` 目录
- 第 1 张：`001.png` 或 `001.jpg`
- 第 2 张：`002.png` 或 `002.jpg`
- HTML 中使用 `<picture>` 或先用 `<img>` 指向 `.png`，用户自行替换

## 边界情况

- 截图缺失时显示占位虚线框 + 文件名提示
- 导航链接 404 时浏览器默认处理
- 无 JS 环境：导航高亮通过 body class 静态处理（每个页面 body 带 `data-page` 属性，CSS 据此高亮对应导航项），核心展示不受影响
