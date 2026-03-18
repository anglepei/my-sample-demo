# 文件上传下载校验管理系统 - 前端实现任务列表（整合版）

## 技术方案：纯原生HTML/CSS/JavaScript 整合到Spring Boot

### 核心特点
- **零依赖**：不需要Node.js、npm、webpack
- **纯原生**：HTML5 + CSS3 + JavaScript ES6+
- **整合部署**：前端静态资源嵌入Spring Boot项目
- **一启动即用**：启动Spring Boot后直接访问页面

### 项目整合结构

```
src/main/resources/
├── static/                          # 静态资源目录（Spring Boot默认）
│   ├── index.html                   # 首页（重定向到登录）
│   ├── login.html                   # 登录页
│   ├── register.html                # 注册页
│   ├── app.html                     # 主应用框架（单页应用）
│   ├── dashboard.html               # 仪表盘页
│   ├── field-config.html            # 字段配置页
│   ├── template.html                # 模板下载页
│   ├── file-upload.html             # 文件上传页
│   ├── file-list.html               # 文件列表页
│   ├── file-detail.html             # 文件详情页
│   ├── log.html                     # 操作日志页
│   │
│   ├── css/                         # 样式文件
│   │   ├── common.css               # 通用样式
│   │   ├── layout.css               # 布局样式
│   │   ├── form.css                 # 表单样式
│   │   ├── table.css                # 表格样式
│   │   ├── button.css               # 按钮样式
│   │   ├── modal.css                # 弹窗样式
│   │   ├── pagination.css           # 分页样式
│   │   └── toast.css                # 提示框样式
│   │
│   ├── js/                          # JavaScript文件
│   │   ├── config.js                # 配置文件
│   │   ├── utils.js                 # 工具函数
│   │   ├── api.js                   # API封装
│   │   ├── auth.js                  # 认证模块
│   │   ├── router.js                # 路由模块
│   │   ├── ui.js                    # UI组件
│   │   ├── field-config.js          # 字段配置逻辑
│   │   ├── file-upload.js           # 文件上传逻辑
│   │   ├── file-list.js             # 文件列表逻辑
│   │   ├── file-detail.js           # 文件详情逻辑
│   │   ├── log.js                   # 日志查询逻辑
│   │   └── dashboard.js             # 仪表盘逻辑
│   │
│   └── assets/                      # 静态资源
│       ├── logo.png                 # Logo
│       ├── favicon.ico              # 网站图标
│       └── images/                  # 图片目录
│
├── templates/                       # 模板目录（可选，用于服务端渲染）
├── sql/                             # SQL脚本
│   └── init.sql
└── application.yml                  # 应用配置
```

### 访问路径

启动Spring Boot后，直接访问：
- http://localhost:8080/ → 首页
- http://localhost:8080/login.html → 登录页
- http://localhost:8080/register.html → 注册页
- http://localhost:8080/app.html → 主应用

---

## 100个任务清单

### 阶段一：静态资源目录创建 (5个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 001 | 创建static目录结构 | `src/main/resources/static/` | 创建css/js/assets子目录 |
| 002 | 创建通用样式文件 | `static/css/common.css` | CSS变量、重置样式 |
| 003 | 创建布局样式文件 | `static/css/layout.css` | 头部、侧边栏、内容区 |
| 004 | 创建表单样式文件 | `static/css/form.css` | 输入框、下拉框、复选框 |
| 005 | 创建表格样式文件 | `static/css/table.css` | 表格、分页 |

### 阶段二：基础工具函数 (8个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 006 | 创建配置文件 | `static/js/config.js` | API地址、常量配置 |
| 007 | 创建工具函数 | `static/js/utils.js` | DOM、存储、日期格式化 |
| 008 | 创建API封装 | `static/js/api.js` | Fetch封装、请求拦截 |
| 009 | 创建认证模块 | `static/js/auth.js` | Token存储、登录状态 |
| 010 | 创建路由模块 | `static/js/router.js` | Hash路由、页面切换 |
| 011 | 创建UI组件-按钮样式 | `static/css/button.css` | 按钮样式 |
| 012 | 创建UI组件-弹窗样式 | `static/css/modal.css` | 弹窗样式 |
| 013 | 创建UI组件-提示框 | `static/css/toast.css` + `static/js/ui.js` | 提示框 |

### 阶段三：页面路由与布局 (8个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 014 | 创建首页 | `static/index.html` | 重定向到登录页 |
| 015 | 创建登录页 | `static/login.html` | 登录表单 |
| 016 | 创建登录逻辑 | `static/js/login.js` | 登录API调用 |
| 017 | 创建注册页 | `static/register.html` | 注册表单 |
| 018 | 创建注册逻辑 | `static/js/register.js` | 注册API调用 |
| 019 | 创建主应用框架 | `static/app.html` | 布局、导航、菜单 |
| 020 | 创建仪表盘页面 | `static/dashboard.html` | 统计卡片、快捷入口 |
| 021 | 创建仪表盘逻辑 | `static/js/dashboard.js` | 统计数据加载 |

### 阶段四：字段配置模块 (12个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 022 | 创建字段配置页 | `static/field-config.html` | 字段列表、新增按钮 |
| 023 | 创建字段配置逻辑 | `static/js/field-config.js` | CRUD逻辑 |
| 024 | 实现字段列表加载 | - | 调用API渲染列表 |
| 025 | 创建新增字段弹窗 | - | 弹窗HTML结构 |
| 026 | 实现新增字段功能 | - | 表单验证、API调用 |
| 027 | 实现编辑字段功能 | - | 回显、更新 |
| 028 | 实现删除字段功能 | - | 确认、删除 |
| 029 | 实现字段排序 | - | 上移、下移 |
| 030 | 创建字段预览功能 | - | 模板结构预览 |
| 031 | 添加字段校验 | - | 前端校验规则 |
| 032 | 创建字段配置样式 | `static/css/field.css` | 专用样式 |
| 033 | 完善交互细节 | - | 动画、提示 |

### 阶段五：模板管理模块 (6个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 034 | 创建模板下载页 | `static/template.html` | 下载按钮、预览 |
| 035 | 创建模板逻辑 | `static/js/template.js` | 下载逻辑 |
| 036 | 实现Excel模板下载 | - | Blob下载 |
| 037 | 实现CSV模板下载 | - | Blob下载 |
| 038 | 创建字段预览 | - | 当前配置展示 |
| 039 | 添加使用说明 | - | 操作指引 |

### 阶段六：文件上传模块 (15个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 040 | 创建文件上传页 | `static/file-upload.html` | 拖拽区域、表单 |
| 041 | 创建上传逻辑 | `static/js/file-upload.js` | 上传核心逻辑 |
| 042 | 实现文件选择 | - | 点击、拖拽 |
| 043 | 实现文件类型校验 | - | 扩展名校验 |
| 044 | 实现文件上传 | - | XMLHttpRequest进度 |
| 045 | 创建进度条组件 | - | 进度显示 |
| 046 | 实现上传结果处理 | - | 成功/失败跳转 |
| 047 | 创建错误详情弹窗 | - | 错误列表展示 |
| 048 | 实现错误数据展示 | - | 行号、错误原因 |
| 049 | 添加上传历史 | - | 最近上传记录 |
| 050 | 实现批量上传 | - | 队列管理 |
| 051 | 创建上传页面样式 | `static/css/upload.css` | 专用样式 |
| 052 | 实现大文件提示 | - | 文件大小警告 |
| 053 | 添加取消上传功能 | - | 中断请求 |
| 054 | 完善错误提示 | - | 友好错误信息 |

### 阶段七：文件列表模块 (12个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 055 | 创建文件列表页 | `static/file-list.html` | 搜索、表格、分页 |
| 056 | 创建列表逻辑 | `static/js/file-list.js` | 列表加载逻辑 |
| 057 | 创建搜索表单 | - | 文件名、时间范围 |
| 058 | 实现文件列表加载 | - | 分页API调用 |
| 059 | 创建分页组件 | `static/css/pagination.css` | 分页样式+逻辑 |
| 060 | 实现分页切换 | - | 页码跳转 |
| 061 | 实现文件搜索 | - | 条件过滤 |
| 062 | 实现文件详情跳转 | - | 跳转传参 |
| 063 | 实现文件下载 | - | Blob下载 |
| 064 | 实现文件删除 | - | 确认删除 |
| 065 | 添加批量操作 | - | 全选、批量删除 |
| 066 | 实现统计信息 | - | 文件数、数据条数 |

### 阶段八：文件详情模块 (10个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 067 | 创建文件详情页 | `static/file-detail.html` | 信息卡片、数据表格 |
| 068 | 创建详情逻辑 | `static/js/file-detail.js` | 详情加载逻辑 |
| 069 | 显示文件基本信息 | - | 文件名、上传时间等 |
| 070 | 显示字段配置快照 | - | 历史字段展示 |
| 071 | 创建数据明细表格 | - | 固定+动态列 |
| 072 | 实现数据明细加载 | - | 分页加载 |
| 073 | 实现数据搜索 | - | 序号、身份证 |
| 074 | 实现数据导出 | - | CSV导出 |
| 075 | 实现返回功能 | - | 返回列表 |
| 076 | 创建详情页样式 | `static/css/detail.css` | 专用样式 |

### 阶段九：操作日志模块 (8个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 077 | 创建日志页面 | `static/log.html` | 搜索、列表 |
| 078 | 创建日志逻辑 | `static/js/log.js` | 日志加载逻辑 |
| 079 | 实现日志列表加载 | - | 分页API |
| 080 | 创建日志搜索 | - | 类型、时间范围 |
| 081 | 创建日志详情弹窗 | - | 详情展示 |
| 082 | 实现日志详情查看 | - | API调用 |
| 083 | 添加操作类型标签 | - | 颜色区分 |
| 084 | 实现日志导出 | - | CSV导出 |

### 阶段十：UI组件完善 (8个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 085 | 完善提示框组件 | `static/js/ui.js` | 成功/错误/警告 |
| 086 | 完善确认对话框 | `static/js/ui.js` | 确认/取消 |
| 087 | 完善弹窗组件 | `static/js/ui.js` | 通用弹窗 |
| 088 | 完善加载组件 | `static/css/loading.css` | 加载动画 |
| 089 | 创建空状态组件 | `static/css/empty.css` | 暂无数据 |
| 090 | 创建表单验证组件 | `static/js/ui.js` | 实时验证 |
| 091 | 创建日期选择器 | - | 原生date input |
| 092 | 完善通用样式 | `static/css/common.css` | 工具类补充 |

### 阶段十一：权限控制 (4个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 093 | 实现菜单权限控制 | `static/js/auth.js` | 根据角色显示菜单 |
| 094 | 实现按钮权限控制 | `static/js/auth.js` | 按钮显示/隐藏 |
| 095 | 创建403页面 | `static/403.html` | 无权限提示 |
| 096 | 实现权限校验拦截 | `static/js/router.js` | 页面权限检查 |

### 阶段十二：响应式设计 (4个任务)

| ID | 任务 | 文件路径 | 说明 |
|----|------|----------|------|
| 097 | 添加移动端适配 | `static/css/responsive.css` | 媒体查询 |
| 098 | 实现侧边栏抽屉 | `static/js/ui.js` | 移动端菜单 |
| 099 | 实现表格横向滚动 | `static/css/table.css` | 小屏幕适配 |
| 100 | 测试跨浏览器兼容 | - | Chrome/Edge/Firefox |

---

## 核心代码示例

### 1. 配置文件 (static/js/config.js)

```javascript
const CONFIG = {
    // API基础地址 - 使用相对路径，自动指向同源的后端
    API_BASE: '/api',

    // Token存储key
    TOKEN_KEY: 'auth_token',

    // 用户信息存储key
    USER_KEY: 'user_info',

    // 分页配置
    PAGE_SIZE: 10,

    // 文件类型白名单
    FILE_TYPES: ['.xlsx', '.xls', '.csv'],

    // 最大文件大小 (200MB)
    MAX_FILE_SIZE: 200 * 1024 * 1024
};
```

### 2. API封装 (static/js/api.js)

```javascript
/**
 * 获取Token
 */
function getToken() {
    return localStorage.getItem(CONFIG.TOKEN_KEY);
}

/**
 * 通用请求封装
 */
async function request(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json'
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(CONFIG.API_BASE + url, {
            ...options,
            headers: { ...headers, ...options.headers }
        });

        // 处理401未授权
        if (response.status === 401) {
            localStorage.removeItem(CONFIG.TOKEN_KEY);
            localStorage.removeItem(CONFIG.USER_KEY);
            window.location.href = '/login.html';
            throw new Error('登录已过期');
        }

        const data = await response.json();

        // 统一响应格式处理
        if (data.code === 0) {
            return data.data;
        } else {
            throw new Error(data.message || '请求失败');
        }
    } catch (error) {
        showToast(error.message, 'error');
        throw error;
    }
}

/**
 * GET请求
 */
function get(url, params) {
    const query = params ? '?' + new URLSearchParams(params).toString() : '';
    return request(url + query, { method: 'GET' });
}

/**
 * POST请求
 */
function post(url, data) {
    return request(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

/**
 * 文件上传
 */
function uploadFile(url, formData, onProgress) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        const token = getToken();

        xhr.upload.addEventListener('progress', (e) => {
            if (e.lengthComputable && onProgress) {
                const percent = Math.round((e.loaded / e.total) * 100);
                onProgress(percent);
            }
        });

        xhr.addEventListener('load', () => {
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.code === 0) {
                    resolve(response.data);
                } else {
                    reject(new Error(response.message));
                }
            } else {
                reject(new Error('上传失败'));
            }
        });

        xhr.addEventListener('error', () => {
            reject(new Error('网络错误'));
        });

        xhr.open('POST', CONFIG.API_BASE + url);
        if (token) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        }
        xhr.send(formData);
    });
}

/**
 * 文件下载
 */
function downloadFile(url, filename) {
    const token = getToken();
    const link = document.createElement('a');
    link.href = CONFIG.API_BASE + url + (url.includes('?') ? '&' : '?') + 'token=' + encodeURIComponent(token);
    link.download = filename;
    link.click();
}
```

### 3. 认证模块 (static/js/auth.js)

```javascript
/**
 * 存储Token和用户信息
 */
function login(token, user) {
    localStorage.setItem(CONFIG.TOKEN_KEY, token);
    localStorage.setItem(CONFIG.USER_KEY, JSON.stringify(user));
}

/**
 * 退出登录
 */
function logout() {
    localStorage.removeItem(CONFIG.TOKEN_KEY);
    localStorage.removeItem(CONFIG.USER_KEY);
    window.location.href = '/login.html';
}

/**
 * 获取当前用户信息
 */
function getCurrentUser() {
    const userStr = localStorage.getItem(CONFIG.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
}

/**
 * 检查是否登录
 */
function isLoggedIn() {
    return !!getToken();
}

/**
 * 检查是否是管理员
 */
function isAdmin() {
    const user = getCurrentUser();
    return user && user.role === 'ADMIN';
}

/**
 * 权限检查 - 显示/隐藏元素
 */
function applyPermissions() {
    const user = getCurrentUser();
    if (!user) return;

    // 隐藏管理员专用元素
    if (user.role !== 'ADMIN') {
        document.querySelectorAll('[data-role="admin"]').forEach(el => {
            el.style.display = 'none';
        });
    }
}
```

### 4. 路由模块 (static/js/router.js)

```javascript
/**
 * 路由配置
 */
const routes = {
    '': '/login.html',
    'login': '/login.html',
    'register': '/register.html',
    'dashboard': '/app.html?page=dashboard',
    'field-config': '/app.html?page=field-config',
    'template': '/app.html?page=template',
    'file-upload': '/app.html?page=file-upload',
    'file-list': '/app.html?page=file-list',
    'file-detail': '/app.html?page=file-detail&id=',
    'log': '/app.html?page=log'
};

/**
 * 路由跳转
 */
function navigate(route, params = {}) {
    const hash = params.id ? `${route}:${params.id}` : route;
    window.location.hash = hash;
}

/**
 * 获取当前页面参数
 */
function getPageParams() {
    const hash = window.location.hash.slice(1);
    const [page, param] = hash.split(':');
    return { page, param };
}
```

### 5. 登录页 (static/login.html)

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - 文件管理系统</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/form.css">
    <link rel="stylesheet" href="/css/button.css">
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .login-box {
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 400px;
        }
        .login-box h1 {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
        }
        .form-item {
            margin-bottom: 20px;
        }
        .form-item label {
            display: block;
            margin-bottom: 8px;
            color: #555;
        }
        .form-item input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-item input:focus {
            outline: none;
            border-color: #667eea;
        }
        .btn-login {
            width: 100%;
            padding: 12px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .btn-login:hover {
            background: #5568d3;
        }
        .register-link {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        .register-link a {
            color: #667eea;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="login-box">
        <h1>文件管理系统</h1>
        <form id="loginForm">
            <div class="form-item">
                <label>用户名</label>
                <input type="text" name="username" required placeholder="请输入用户名">
            </div>
            <div class="form-item">
                <label>密码</label>
                <input type="password" name="password" required placeholder="请输入密码">
            </div>
            <button type="submit" class="btn-login">登录</button>
        </form>
        <p class="register-link">
            还没有账号？ <a href="/register.html">立即注册</a>
        </p>
    </div>

    <script src="/js/config.js"></script>
    <script src="/js/utils.js"></script>
    <script src="/js/api.js"></script>
    <script src="/js/auth.js"></script>
    <script src="/js/ui.js"></script>
    <script>
        $('#loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const username = formData.get('username');
            const password = formData.get('password');

            try {
                const result = await post('/auth/login', {
                    username: username,
                    password: password
                });
                login(result.token, result.user);
                showToast('登录成功', 'success');
                setTimeout(() => {
                    window.location.href = '/app.html?page=dashboard';
                }, 500);
            } catch (error) {
                // 错误已在api.js中处理
            }
        });

        // 检查是否已登录
        if (isLoggedIn()) {
            window.location.href = '/app.html?page=dashboard';
        }
    </script>
</body>
</html>
```

### 6. 主应用框架 (static/app.html)

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>文件管理系统</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/table.css">
    <link rel="stylesheet" href="/css/button.css">
    <link rel="stylesheet" href="/css/form.css">
    <link rel="stylesheet" href="/css/modal.css">
    <link rel="stylesheet" href="/css/toast.css">
</head>
<body>
    <div class="app">
        <header class="header">
            <div class="logo">
                <span>📁</span>
                <span>文件管理系统</span>
            </div>
            <div class="user-info">
                <span id="username"></span>
                <button onclick="logout()" class="btn btn-sm">退出</button>
            </div>
        </header>

        <div class="main">
            <aside class="sidebar">
                <nav class="nav">
                    <a href="#dashboard" class="nav-item" data-page="dashboard">
                        <span>📊</span> 仪表盘
                    </a>
                    <a href="#field-config" class="nav-item" data-page="field-config">
                        <span>⚙️</span> 字段配置
                    </a>
                    <a href="#template" class="nav-item" data-page="template">
                        <span>📥</span> 模板下载
                    </a>
                    <a href="#file-upload" class="nav-item" data-page="file-upload">
                        <span>📤</span> 文件上传
                    </a>
                    <a href="#file-list" class="nav-item" data-page="file-list">
                        <span>📋</span> 文件列表
                    </a>
                    <a href="#log" class="nav-item" data-page="log">
                        <span>📝</span> 操作日志
                    </a>
                </nav>
            </aside>

            <main class="content" id="mainContent">
                <!-- 动态内容区域 -->
            </main>
        </div>
    </div>

    <div id="toastContainer"></div>
    <div id="modalContainer"></div>

    <script src="/js/config.js"></script>
    <script src="/js/utils.js"></script>
    <script src="/js/api.js"></script>
    <script src="/js/auth.js"></script>
    <script src="/js/router.js"></script>
    <script src="/js/ui.js"></script>
    <script src="/js/dashboard.js"></script>
    <script src="/js/field-config.js"></script>
    <script src="/js/template.js"></script>
    <script src="/js/file-upload.js"></script>
    <script src="/js/file-list.js"></script>
    <script src="/js/log.js"></script>
    <script>
        // 检查登录状态
        if (!isLoggedIn()) {
            window.location.href = '/login.html';
        }

        // 显示用户信息
        const user = getCurrentUser();
        if (user) {
            $('#username').textContent = user.username;
        }

        // 应用权限控制
        applyPermissions();

        // 路由处理
        const pages = {
            'dashboard': renderDashboard,
            'field-config': renderFieldConfig,
            'template': renderTemplate,
            'file-upload': renderFileUpload,
            'file-list': renderFileList,
            'log': renderLog
        };

        function loadPage() {
            const params = getPageParams();
            const page = pages[params.page] || pages['dashboard'];
            page(params.param);
            updateNav(params.page);
            document.title = (getPageTitle(params.page) || '仪表盘') + ' - 文件管理系统';
        }

        function getPageTitle(page) {
            const titles = {
                'dashboard': '仪表盘',
                'field-config': '字段配置',
                'template': '模板下载',
                'file-upload': '文件上传',
                'file-list': '文件列表',
                'log': '操作日志'
            };
            return titles[page];
        }

        function updateNav(activePage) {
            document.querySelectorAll('.nav-item').forEach(item => {
                item.classList.remove('active');
                if (item.dataset.page === activePage) {
                    item.classList.add('active');
                }
            });
        }

        // 监听hash变化
        window.addEventListener('hashchange', loadPage);

        // 初始加载
        if (!window.location.hash) {
            window.location.hash = '#dashboard';
        } else {
            loadPage();
        }
    </script>
</body>
</html>
```

### 7. 通用样式 (static/css/common.css)

```css
:root {
    /* 主题色 */
    --primary-color: #1890ff;
    --success-color: #52c41a;
    --warning-color: #faad14;
    --danger-color: #f5222d;
    --info-color: #13c2c2;

    /* 文字颜色 */
    --text-primary: #262626;
    --text-secondary: #595959;
    --text-disabled: #bfbfbf;

    /* 背景色 */
    --bg-primary: #ffffff;
    --bg-secondary: #f5f5f5;
    --bg-disabled: #f5f5f5;

    /* 边框色 */
    --border-color: #d9d9d9;
    --border-color-hover: #40a9ff;

    /* 间距 */
    --spacing-xs: 4px;
    --spacing-sm: 8px;
    --spacing-md: 16px;
    --spacing-lg: 24px;
    --spacing-xl: 32px;

    /* 圆角 */
    --border-radius-sm: 2px;
    --border-radius-md: 4px;
    --border-radius-lg: 8px;

    /* 阴影 */
    --shadow-sm: 0 1px 2px rgba(0,0,0,0.03);
    --shadow-md: 0 4px 6px rgba(0,0,0,0.1);
    --shadow-lg: 0 10px 20px rgba(0,0,0,0.15);
}

/* 重置样式 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
    font-size: 14px;
    line-height: 1.5;
    color: var(--text-primary);
    background: var(--bg-secondary);
}

/* 通用类 */
.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 var(--spacing-md);
}

.text-center { text-align: center; }
.text-right { text-align: right; }
.text-left { text-align: left; }

.mt-xs { margin-top: var(--spacing-xs); }
.mt-sm { margin-top: var(--spacing-sm); }
.mt-md { margin-top: var(--spacing-md); }
.mt-lg { margin-top: var(--spacing-lg); }

.mb-xs { margin-bottom: var(--spacing-xs); }
.mb-sm { margin-bottom: var(--spacing-sm); }
.mb-md { margin-bottom: var(--spacing-md); }
.mb-lg { margin-bottom: var(--spacing-lg); }

.hidden { display: none !important; }
```

### 8. 布局样式 (static/css/layout.css)

```css
.app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

/* 头部 */
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 var(--spacing-lg);
    height: 60px;
    background: #001529;
    color: white;
    box-shadow: var(--shadow-md);
}

.logo {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    font-size: 18px;
    font-weight: 500;
}

.user-info {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
}

/* 主区域 */
.main {
    display: flex;
    flex: 1;
}

/* 侧边栏 */
.sidebar {
    width: 200px;
    background: #001529;
    color: rgba(255,255,255,0.65);
}

.nav {
    padding: var(--spacing-md) 0;
}

.nav-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    padding: 12px 24px;
    color: inherit;
    text-decoration: none;
    transition: all 0.3s;
}

.nav-item:hover {
    color: white;
    background: rgba(255,255,255,0.08);
}

.nav-item.active {
    color: white;
    background: var(--primary-color);
}

/* 内容区 */
.content {
    flex: 1;
    padding: var(--spacing-lg);
    overflow-y: auto;
}
```

---

## API对接清单

### 认证接口
```javascript
// 登录
POST /api/auth/login
Body: { "username": "admin", "password": "123456" }

// 注册
POST /api/auth/register
Body: { "username": "test", "password": "123456" }

// 登出
POST /api/auth/logout

// 获取当前用户
GET /api/auth/info
```

### 字段配置接口
```javascript
// 获取字段配置
GET /api/field/config

// 保存字段配置
POST /api/field/config
Body: { "fields": [...] }
```

### 模板接口
```javascript
// 下载Excel模板
GET /api/template/download/excel

// 下载CSV模板
GET /api/template/download/csv
```

### 文件接口
```javascript
// 上传文件
POST /api/file/upload
Body: FormData (file字段)

// 文件列表
GET /api/file/list?page=1&size=10

// 文件详情
GET /api/file/detail?id=123&page=1&size=10

// 下载文件
GET /api/file/download?id=123

// 删除文件
DELETE /api/file/delete?id=123
```

### 日志接口
```javascript
// 日志列表
GET /api/log/list?page=1&size=10
```

### 统计接口
```javascript
// 获取统计
GET /api/statistics/summary
```

---

## 启动访问

### 1. 启动Spring Boot应用

```bash
mvn spring-boot:run
# 或
java -jar target/file-manager.jar
```

### 2. 访问页面

```
http://localhost:8080/          → 首页（重定向到登录）
http://localhost:8080/login.html → 登录页
http://localhost:8080/app.html  → 主应用
```

### 3. 默认账号

```
用户名: admin
密码: admin123 (需在数据库初始化时创建)
```

---

## 任务执行优先级

| 优先级 | 阶段 | 说明 |
|--------|------|------|
| P0 | 阶段一-三 | 基础架构、登录、主框架 |
| P1 | 阶段四-七 | 字段配置、模板、文件管理 |
| P2 | 阶段八-九 | 文件详情、操作日志 |
| P3 | 阶段十-十二 | UI优化、权限、响应式 |

---

## 100个任务统计

| 阶段 | 任务数 | 说明 |
|------|--------|------|
| 阶段一：静态资源目录创建 | 5 | 目录、基础样式 |
| 阶段二：基础工具函数 | 8 | 配置、API、认证、路由 |
| 阶段三：页面路由与布局 | 8 | 登录、注册、主框架 |
| 阶段四：字段配置模块 | 12 | CRUD、排序、预览 |
| 阶段五：模板管理模块 | 6 | Excel/CSV下载 |
| 阶段六：文件上传模块 | 15 | 上传、进度、错误处理 |
| 阶段七：文件列表模块 | 12 | 列表、搜索、分页 |
| 阶段八：文件详情模块 | 10 | 详情、数据明细、导出 |
| 阶段九：操作日志模块 | 8 | 日志查询、统计 |
| 阶段十：UI组件完善 | 8 | 提示、弹窗、表单 |
| 阶段十一：权限控制 | 4 | 菜单、按钮权限 |
| 阶段十二：响应式设计 | 4 | 移动端适配 |
| **合计** | **100** | **前端任务总数** |
