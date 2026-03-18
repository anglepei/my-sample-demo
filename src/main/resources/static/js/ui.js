/**
 * UI组件模块
 */

/**
 * 提示框容器
 */
let toastContainer = null;

/**
 * 显示提示框
 */
function showToast(message, type = 'info', duration = 3000) {
    // 创建容器
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }

    // 创建提示框
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    // 图标
    const icons = {
        success: '✓',
        error: '✕',
        warning: '⚠',
        info: 'ℹ',
        loading: '◐'
    };

    toast.innerHTML = `
        <span class="toast-icon">${icons[type] || icons.info}</span>
        <span class="toast-content">${message}</span>
        <button class="toast-close" onclick="this.parentElement.remove()">×</button>
    `;

    toastContainer.appendChild(toast);

    // 自动关闭
    if (duration > 0 && type !== 'loading') {
        setTimeout(() => {
            toast.classList.add('hiding');
            setTimeout(() => toast.remove(), 300);
        }, duration);
    }

    return toast;
}

/**
 * 显示成功提示
 */
function showSuccess(message, duration = 3000) {
    return showToast(message, 'success', duration);
}

/**
 * 显示错误提示
 */
function showError(message, duration = 3000) {
    return showToast(message, 'error', duration);
}

/**
 * 显示警告提示
 */
function showWarning(message, duration = 3000) {
    return showToast(message, 'warning', duration);
}

/**
 * 显示信息提示
 */
function showInfo(message, duration = 3000) {
    return showToast(message, 'info', duration);
}

/**
 * 显示加载提示
 */
function showLoading(message = '加载中...') {
    return showToast(message, 'loading', 0);
}

/**
 * 弹窗容器
 */
let modalContainer = null;

/**
 * 显示确认对话框
 */
function showConfirm(options) {
    const {
        title = '确认',
        message = '',
        type = 'info',
        onConfirm = null,
        onCancel = null,
        confirmText = '确定',
        cancelText = '取消'
    } = options;

    return new Promise((resolve) => {
        // 创建容器
        if (!modalContainer) {
            modalContainer = document.createElement('div');
            document.body.appendChild(modalContainer);
        }

        // 创建遮罩和弹窗
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay show';

        const icons = {
            warning: '⚠',
            danger: '✕',
            info: 'ℹ',
            success: '✓'
        };

        overlay.innerHTML = `
            <div class="modal confirm-modal">
                <div class="modal-body">
                    <div class="confirm-icon ${type}">${icons[type] || icons.info}</div>
                    <div class="confirm-title">${title}</div>
                    <div class="confirm-message">${message}</div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" data-action="cancel">${cancelText}</button>
                    <button class="btn btn-${type === 'danger' ? 'danger' : 'primary'}" data-action="confirm">${confirmText}</button>
                </div>
            </div>
        `;

        modalContainer.appendChild(overlay);

        // 绑定事件
        overlay.querySelectorAll('[data-action]').forEach(btn => {
            btn.addEventListener('click', () => {
                const action = btn.getAttribute('data-action');
                overlay.classList.remove('show');
                setTimeout(() => overlay.remove(), 300);

                if (action === 'confirm') {
                    if (onConfirm) onConfirm();
                    resolve(true);
                } else {
                    if (onCancel) onCancel();
                    resolve(false);
                }
            });
        });

        // 点击遮罩关闭
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                overlay.classList.remove('show');
                setTimeout(() => overlay.remove(), 300);
                if (onCancel) onCancel();
                resolve(false);
            }
        });
    });
}

/**
 * 显示弹窗
 */
function showModal(options) {
    const {
        title = '',
        content = '',
        width = '500px',
        onConfirm = null,
        onCancel = null,
        confirmText = '确定',
        cancelText = '取消',
        showCancel = true
    } = options;

    return new Promise((resolve) => {
        // 创建容器
        if (!modalContainer) {
            modalContainer = document.createElement('div');
            document.body.appendChild(modalContainer);
        }

        // 创建遮罩和弹窗
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay show';

        overlay.innerHTML = `
            <div class="modal" style="max-width: ${width}">
                <div class="modal-header">
                    <h3 class="modal-title">${title}</h3>
                    <button class="modal-close" data-action="cancel">×</button>
                </div>
                <div class="modal-body">${content}</div>
                <div class="modal-footer">
                    ${showCancel ? `<button class="btn btn-secondary" data-action="cancel">${cancelText}</button>` : ''}
                    <button class="btn btn-primary" data-action="confirm">${confirmText}</button>
                </div>
            </div>
        `;

        modalContainer.appendChild(overlay);

        // 绑定事件
        overlay.querySelectorAll('[data-action]').forEach(btn => {
            btn.addEventListener('click', () => {
                const action = btn.getAttribute('data-action');
                overlay.classList.remove('show');
                setTimeout(() => overlay.remove(), 300);

                if (action === 'confirm') {
                    if (onConfirm) onConfirm();
                    resolve(true);
                } else {
                    if (onCancel) onCancel();
                    resolve(false);
                }
            });
        });

        // 点击遮罩关闭
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                overlay.classList.remove('show');
                setTimeout(() => overlay.remove(), 300);
                if (onCancel) onCancel();
                resolve(false);
            }
        });
    });
}

/**
 * 关闭弹窗
 */
function closeModal() {
    // 关闭模态弹窗
    const overlay = document.querySelector('.modal-overlay.show');
    if (overlay) {
        overlay.classList.remove('show');
        setTimeout(() => overlay.remove(), 300);
    }

    // 关闭加载提示 (toast toast-loading)
    const loadingToasts = document.querySelectorAll('.toast.toast-loading');
    loadingToasts.forEach(toast => {
        toast.classList.add('hiding');
        setTimeout(() => toast.remove(), 300);
    });

    // 关闭加载遮罩
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.classList.remove('show');
        setTimeout(() => loadingOverlay.remove(), 300);
    }
}

/**
 * 显示加载遮罩
 */
function showLoadingOverlay(message = '加载中...') {
    let overlay = document.getElementById('loadingOverlay');

    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.className = 'loading-overlay';
        overlay.innerHTML = `
            <div class="loading-content">
                <div class="loading-spinner"></div>
                <div class="loading-message"></div>
            </div>
        `;
        document.body.appendChild(overlay);
    }

    overlay.querySelector('.loading-message').textContent = message;
    overlay.style.display = 'flex';

    return overlay;
}

/**
 * 隐藏加载遮罩
 */
function hideLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
}

/**
 * 表单验证
 */
function validateForm(formEl) {
    const inputs = formEl.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;

    inputs.forEach(input => {
        const formItem = input.closest('.form-item');
        if (!input.value.trim()) {
            formItem.classList.add('has-error');
            isValid = false;
        } else {
            formItem.classList.remove('has-error');
        }
    });

    return isValid;
}

/**
 * 清空表单
 */
function clearForm(formEl) {
    formEl.reset();
    formEl.querySelectorAll('.form-item').forEach(item => {
        item.classList.remove('has-error', 'has-success');
    });
}

/**
 * 禁用表单
 */
function disableForm(formEl) {
    formEl.querySelectorAll('input, select, textarea, button').forEach(el => {
        el.disabled = true;
    });
}

/**
 * 启用表单
 */
function enableForm(formEl) {
    formEl.querySelectorAll('input, select, textarea, button').forEach(el => {
        el.disabled = false;
    });
}

/**
 * 渲染空状态
 */
function renderEmptyState(container, message = '暂无数据') {
    container.innerHTML = `
        <div class="table-empty">
            <div class="table-empty-icon">📭</div>
            <div class="table-empty-text">${message}</div>
        </div>
    `;
}

/**
 * 渲染加载状态
 */
function renderLoadingState(container, message = '加载中...') {
    container.innerHTML = `
        <div class="table-loading">
            <div class="loading-spinner"></div>
            <div>${message}</div>
        </div>
    `;
}

// 导出
window.ui = {
    showToast,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    showLoading,
    showConfirm,
    showModal,
    closeModal,
    showLoadingOverlay,
    hideLoadingOverlay,
    validateForm,
    clearForm,
    disableForm,
    enableForm,
    renderEmptyState,
    renderLoadingState
};

// 添加快捷方式
window.showToast = showToast;
window.showSuccess = showSuccess;
window.showError = showError;
window.showWarning = showWarning;
window.showInfo = showInfo;
window.showLoading = showLoading;
window.showConfirm = showConfirm;
window.showModal = showModal;
window.closeModal = closeModal;
window.showInfo = showInfo;
window.showConfirm = showConfirm;
