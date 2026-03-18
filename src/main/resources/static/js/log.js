/**
 * 操作日志页面
 */

let currentLogParams = {
    page: 1,
    size: 10
};

/**
 * 页面加载
 */
function logPageLoad() {
    const contentEl = document.getElementById('mainContent');
    contentEl.innerHTML = `
        <div class="page-container">
            <div class="page-header">
                <h1 class="page-title">操作日志</h1>
            </div>

            <!-- 搜索表单 -->
            <div class="card-container">
                <form id="logSearchForm" class="search-form" onsubmit="event.preventDefault(); searchLogs();">
                    <div class="form-item">
                        <label class="form-item-label">用户名</label>
                        <input type="text" class="input" name="username" placeholder="请输入用户名">
                    </div>
                    <div class="form-item">
                        <label class="form-item-label">操作类型</label>
                        <select class="select" name="operationType">
                            <option value="">全部</option>
                            <option value="UPLOAD">上传文件</option>
                            <option value="DELETE">删除文件</option>
                        </select>
                    </div>
                    <div class="form-item">
                        <label class="form-item-label">操作时间</label>
                        <input type="date" class="input" name="startDate" placeholder="开始日期">
                        <input type="date" class="input" name="endDate" placeholder="结束日期" style="margin-top: var(--spacing-sm);">
                    </div>
                    <div class="search-actions">
                        <button type="submit" class="btn btn-primary">搜索</button>
                        <button type="button" class="btn btn-secondary" onclick="resetLogSearch()">重置</button>
                    </div>
                </form>
            </div>

            <!-- 日志列表 -->
            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">日志列表</h2>
                    <div class="card-actions">
                        <button class="btn btn-secondary" onclick="exportLogs()">
                            <span>📥</span> 导出日志
                        </button>
                    </div>
                </div>

                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>操作人</th>
                                    <th>操作类型</th>
                                    <th>操作描述</th>
                                    <th>IP地址</th>
                                    <th>操作时间</th>
                                    <th>耗时</th>
                                </tr>
                            </thead>
                            <tbody id="logTableBody">
                                <tr>
                                    <td colspan="7" class="text-center text-secondary" style="padding: var(--spacing-xl);">
                                        加载中...
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 分页 -->
                <div class="pagination" id="logPagination">
                    <div class="pagination-info" id="logPaginationInfo">显示 0-0 条，共 0 条</div>
                    <div class="pagination-controls" id="logPaginationControls"></div>
                </div>
            </div>
        </div>
    `;

    loadLogList();
}

/**
 * 加载日志列表
 */
async function loadLogList(params = {}) {
    const mergedParams = { ...currentLogParams, ...params };

    try {
        const result = await API.log.list(mergedParams);
        currentLogParams = mergedParams;

        renderLogList(result);
        renderLogPagination(result);

    } catch (error) {
        console.error('Load log list error:', error);
        document.getElementById('logTableBody').innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-danger" style="padding: var(--spacing-xl);">
                    加载失败: ${error.message}
                </td>
            </tr>
        `;
    }
}

/**
 * 渲染日志列表
 */
function renderLogList(result) {
    const tableBody = document.getElementById('logTableBody');

    if (!result.records || result.records.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-secondary" style="padding: var(--spacing-xl);">
                    暂无日志记录
                </td>
            </tr>
        `;
        return;
    }

    const operationNames = {
        'UPLOAD': '上传文件',
        'DELETE': '删除文件',
        'DOWNLOAD': '下载文件'
    };

    const operationTypes = {
        'UPLOAD': 'success',
        'DELETE': 'danger',
        'DOWNLOAD': 'info'
    };

    tableBody.innerHTML = result.records.map(log => {
        // 处理操作类型枚举
        let operationKey = log.operationType || log.operation || 'OTHER';
        if (typeof operationKey === 'object' && operationKey.name) {
            operationKey = operationKey.name;
        }

        return `
        <tr>
            <td>${log.username || '-'}</td>
            <td><span class="text-${operationTypes[operationKey] || 'info'}">${operationNames[operationKey] || operationKey}</span></td>
            <td>${log.description || '-'}</td>
            <td>${log.requestIp || '-'}</td>
            <td>${formatDate(log.createTime, 'YYYY-MM-DD HH:mm:ss')}</td>
            <td>${log.costTime ? log.costTime + 'ms' : '-'}</td>
        </tr>
        `;
    }).join('');
}

/**
 * 渲染分页
 */
function renderLogPagination(result) {
    const { page, size, total } = result;
    const totalPages = Math.ceil(total / size);
    const start = (page - 1) * size + 1;
    const end = Math.min(page * size, total);

    document.getElementById('logPaginationInfo').textContent = `显示 ${start}-${end} 条，共 ${total} 条`;

    let controls = '';

    controls += `<button class="pagination-btn" ${page === 1 ? 'disabled' : ''} onclick="goToLogPage(${page - 1})">上一页</button>`;

    const maxVisiblePages = 5;
    let startPage = Math.max(1, page - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    if (startPage > 1) {
        controls += `<button class="pagination-btn" onclick="goToLogPage(1)">1</button>`;
        if (startPage > 2) {
            controls += `<span class="pagination-btn" disabled>...</span>`;
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        controls += `<button class="pagination-btn ${i === page ? 'active' : ''}" onclick="goToLogPage(${i})">${i}</button>`;
    }

    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            controls += `<span class="pagination-btn" disabled>...</span>`;
        }
        controls += `<button class="pagination-btn" onclick="goToLogPage(${totalPages})">${totalPages}</button>`;
    }

    controls += `<button class="pagination-btn" ${page === totalPages ? 'disabled' : ''} onclick="goToLogPage(${page + 1})">下一页</button>`;

    document.getElementById('logPaginationControls').innerHTML = controls;
}

/**
 * 跳转页码
 */
function goToLogPage(page) {
    loadLogList({ page });
}

/**
 * 搜索日志
 */
function searchLogs() {
    const form = document.getElementById('logSearchForm');
    const formData = new FormData(form);

    const params = {
        page: 1,
        size: currentLogParams.size,
        username: formData.get('username') || undefined,
        operationType: formData.get('operationType') || undefined,
        startDate: formData.get('startDate') || undefined,
        endDate: formData.get('endDate') || undefined
    };

    Object.keys(params).forEach(key => {
        if (params[key] === undefined) {
            delete params[key];
        }
    });

    loadLogList(params);
}

/**
 * 重置搜索
 */
function resetLogSearch() {
    document.getElementById('logSearchForm').reset();
    loadLogList({ page: 1, size: currentLogParams.size });
}

/**
 * 导出日志
 */
function exportLogs() {
    showInfo('导出功能开发中...');
}
