/**
 * 认证模块
 */

/**
 * 存储Token和用户信息
 */
function login(token, user) {
    setToken(token);
    localStorage.setItem(CONFIG.USER_KEY, JSON.stringify(user));
    // 记录登录时间
    localStorage.setItem(CONFIG.LOGIN_TIME_KEY, Date.now().toString());
}

/**
 * 退出登录
 */
function logout() {
    removeToken();
    localStorage.removeItem(CONFIG.USER_KEY);
    localStorage.removeItem(CONFIG.LOGIN_TIME_KEY);
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
    const token = getToken();
    if (!token) return false;

    // 检查Token是否过期
    const loginTime = localStorage.getItem(CONFIG.LOGIN_TIME_KEY);
    if (loginTime) {
        const elapsed = Date.now() - parseInt(loginTime);
        if (elapsed > CONFIG.TOKEN_EXPIRE_TIME) {
            logout();
            return false;
        }
    }

    return true;
}

/**
 * 检查是否是管理员
 */
function isAdmin() {
    const user = getCurrentUser();
    return user && user.role === CONFIG.ROLE.ADMIN;
}

/**
 * 检查权限
 */
function hasRole(role) {
    const user = getCurrentUser();
    return user && user.role === role;
}

/**
 * 权限检查 - 显示/隐藏元素
 */
function applyPermissions() {
    const user = getCurrentUser();
    if (!user) {
        // 未登录，跳转到登录页
        if (!window.location.pathname.endsWith('login.html') &&
            !window.location.pathname.endsWith('register.html')) {
            window.location.href = '/login.html';
        }
        return;
    }

    // 隐藏管理员专用元素
    if (user.role !== CONFIG.ROLE.ADMIN) {
        document.querySelectorAll('[data-role="admin"]').forEach(el => {
            el.style.display = 'none';
        });
    }

    // 根据角色显示/隐藏元素
    document.querySelectorAll('[data-role]').forEach(el => {
        const requiredRole = el.getAttribute('data-role');
        if (requiredRole && requiredRole !== user.role) {
            el.style.display = 'none';
        }
    });
}

/**
 * 检查页面访问权限
 */
function checkPagePermission(requiredRole) {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return false;
    }

    if (requiredRole && !hasRole(requiredRole)) {
        window.location.href = '/403.html';
        return false;
    }

    return true;
}

/**
 * 更新用户信息显示
 */
function updateUserInfo() {
    const user = getCurrentUser();
    const usernameEl = document.getElementById('username');
    const roleEl = document.getElementById('userRole');

    if (usernameEl && user) {
        usernameEl.textContent = user.username;
    }

    if (roleEl && user) {
        roleEl.textContent = user.role === CONFIG.ROLE.ADMIN ? '管理员' : '普通用户';
    }
}

/**
 * 登录状态过期检查
 */
function checkLoginStatus() {
    if (!isLoggedIn()) {
        showToast('登录已过期，请重新登录', 'warning');
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 1500);
        return false;
    }
    return true;
}

// 扩展配置
CONFIG.LOGIN_TIME_KEY = 'login_time';

// 导出
window.auth = {
    login,
    logout,
    getCurrentUser,
    isLoggedIn,
    isAdmin,
    hasRole,
    applyPermissions,
    checkPagePermission,
    updateUserInfo,
    checkLoginStatus
};
