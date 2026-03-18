/**
 * 路由模块（Hash路由）
 */

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
    'file-detail': '/app.html?page=file-detail',
    'log': '/app.html?page=log'
};

/**
 * 页面名称到函数名的映射
 */
const pageFunctionMap = {
    'dashboard': 'dashboardPageLoad',
    'field-config': 'fieldConfigPageLoad',
    'template': 'templatePageLoad',
    'file-upload': 'fileUploadPageLoad',
    'file-list': 'fileListPageLoad',
    'file-detail': 'fileDetailPageLoad',
    'log': 'logPageLoad'
};

/**
 * 页面标题映射
 */
const pageTitles = {
    'login': '登录',
    'register': '注册',
    'dashboard': '仪表盘',
    'field-config': '字段配置',
    'template': '模板下载',
    'file-upload': '文件上传',
    'file-list': '文件列表',
    'file-detail': '文件详情',
    'log': '操作日志'
};

/**
 * 路由跳转
 */
function navigate(route, params = {}) {
    const hash = params.id ? `${route}:${params.id}` : route;

    // 构建查询参数
    const query = Object.keys(params)
        .filter(key => key !== 'id')
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&');

    window.location.hash = hash + (query ? '&' + query : '');
}

/**
 * 获取当前页面参数
 */
function getPageParams() {
    const hash = window.location.hash.slice(1);
    const [pagePath, ...queryParts] = hash.split('&');
    const [page, param] = pagePath.split(':');

    // 解析查询参数
    const query = {};
    queryParts.forEach(part => {
        const [key, value] = part.split('=');
        if (key && value) {
            query[decodeURIComponent(key)] = decodeURIComponent(value);
        }
    });

    return { page, param, query };
}

/**
 * 获取当前页面名称
 */
function getCurrentPage() {
    const params = getPageParams();
    return params.page || 'dashboard';
}

/**
 * 更新页面标题
 */
function updatePageTitle(page) {
    const title = pageTitles[page] || '文件管理系统';
    document.title = title + ' - 文件管理系统';
}

/**
 * 路由守卫
 */
const routeGuards = {
    // 登录检查白名单
    whitelist: ['login', 'register'],

    // 需要管理员权限的页面
    adminOnly: [],

    // 检查路由权限
    check(page) {
        // 白名单直接通过
        if (this.whitelist.includes(page)) {
            return true;
        }

        // 检查登录状态
        if (!isLoggedIn()) {
            navigate('login');
            return false;
        }

        // 检查管理员权限
        if (this.adminOnly.includes(page) && !isAdmin()) {
            showToast('需要管理员权限', 'error');
            return false;
        }

        return true;
    }
};

/**
 * 初始化路由
 */
function initRouter() {
    const params = getPageParams();
    const page = params.page || 'dashboard';

    // 检查权限
    if (!routeGuards.check(page)) {
        return;
    }

    // 更新标题
    updatePageTitle(page);

    // 更新导航高亮
    updateNavHighlight(page);

    // 返回页面函数名
    return pageFunctionMap[page] || page + 'PageLoad';
}

/**
 * 更新导航高亮
 */
function updateNavHighlight(activePage) {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
        const itemPage = item.getAttribute('data-page');
        if (itemPage === activePage) {
            item.classList.add('active');
        }
    });
}

/**
 * 监听路由变化
 */
function setupRouterListener(callback) {
    window.addEventListener('hashchange', () => {
        const functionName = initRouter();
        if (functionName && window[functionName] && callback) {
            callback(functionName);
        }
    });
}

/**
 * 返回上一页
 */
function goBack() {
    window.history.back();
}

/**
 * 刷新当前页面
 */
function refresh() {
    window.location.reload();
}

// 导出
window.router = {
    navigate,
    getPageParams,
    getCurrentPage,
    updatePageTitle,
    initRouter,
    setupRouterListener,
    goBack,
    refresh,
    routeGuards
};
