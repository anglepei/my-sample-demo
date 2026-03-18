/**
 * 文件列表页面
 */

let currentFileListParams = {
    page: 1,
    size: 10
};

/**
 * 页面加载
 */
function fileListPageLoad() {
    const contentEl = document.getElementById('mainContent');
    contentEl.innerHTML = `
        <div class="page-container">
            <div class="page-header">
                <h1 class="page-title">文件列表</h1>
            </div>

            <!-- 搜索表单 -->
            <div class="card-container">
                <form id="searchForm" class="search-form" onsubmit="event.preventDefault(); searchFiles();">
                    <div class="form-item">
                        <label class="form-item-label">文件名</label>
                        <input type="text" class="input" name="fileName" placeholder="请输入文件名">
                    </div>
                    <div class="form-item">
                        <label class="form-item-label">上传时间</label>
                        <input type="date" class="input" name="startDate" placeholder="开始日期">
                        <input type="date" class="input" name="endDate" placeholder="结束日期" style="margin-top: var(--spacing-sm);">
                    </div>
                    <div class="search-actions">
                        <button type="submit" class="btn btn-primary">搜索</button>
                        <button type="button" class="btn btn-secondary" onclick="resetSearch()">重置</button>
                    </div>
                </form>
            </div>

            <!-- 文件列表 -->
            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">文件列表</h2>
                    <div class="card-actions">
                        <span id="fileCount" class="text-secondary mr-md">共 0 个文件</span>
                        <button class="btn btn-primary" onclick="router.navigate('file-upload')">
                            <span>📤</span> 上传文件
                        </button>
                    </div>
                </div>

                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>文件名</th>
                                    <th>数据条数</th>
                                    <th>上传时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody id="fileListTableBody">
                                <tr>
                                    <td colspan="4" class="text-center text-secondary" style="padding: var(--spacing-xl);">
                                        加载中...
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 分页 -->
                <div class="pagination" id="fileListPagination">
                    <div class="pagination-info" id="paginationInfo">显示 0-0 条，共 0 条</div>
                    <div class="pagination-controls" id="paginationControls"></div>
                </div>
            </div>
        </div>
    `;

    loadFileList();
}

/**
 * 加载文件列表
 */
async function loadFileList(params = {}) {
    const mergedParams = { ...currentFileListParams, ...params };

    try {
        const result = await API.file.list(mergedParams);

        // 保存当前参数
        currentFileListParams = mergedParams;

        renderFileList(result);
        renderPagination(result);
        document.getElementById('fileCount').textContent = `共 ${result.total} 个文件`;

    } catch (error) {
        console.error('Load file list error:', error);
        document.getElementById('fileListTableBody').innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-danger" style="padding: var(--spacing-xl);">
                    加载失败: ${error.message}
                </td>
            </tr>
        `;
    }
}

/**
 * 渲染文件列表
 */
function renderFileList(result) {
    const tableBody = document.getElementById('fileListTableBody');

    if (!result.records || result.records.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-secondary" style="padding: var(--spacing-xl);">
                    暂无文件，<a href="#" onclick="router.navigate('file-upload')" style="color: var(--primary-color);">立即上传</a>
                </td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = result.records.map(file => `
        <tr>
            <td>
                <div class="flex-center" style="gap: var(--spacing-sm); justify-content: flex-start;">
                    <span>${getFileIcon(file.fileType)}</span>
                    <span>${escapeHtml(file.fileName)}</span>
                </div>
            </td>
            <td>${formatNumber(file.dataCount)} 条</td>
            <td>${formatDate(file.uploadTime, 'YYYY-MM-DD HH:mm')}</td>
            <td>
                <div class="table-actions">
                    <a class="table-action-link" onclick="viewFileDetail(${file.id})">详情</a>
                    <a class="table-action-link" onclick="downloadFile(${file.id}, '${escapeHtml(file.fileName)}')">下载</a>
                    ${isAdmin() ? `<a class="table-action-link danger" onclick="deleteFile(${file.id})">删除</a>` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

/**
 * 获取文件图标
 */
function getFileIcon(fileType) {
    const icons = {
        'XLSX': '📊',
        'XLS': '📊',
        'CSV': '📄'
    };
    return icons[fileType] || '📁';
}

/**
 * 渲染分页
 */
function renderPagination(result) {
    const { page, size, total } = result;
    const totalPages = Math.ceil(total / size);
    const start = (page - 1) * size + 1;
    const end = Math.min(page * size, total);

    // 更新分页信息
    document.getElementById('paginationInfo').textContent = `显示 ${start}-${end} 条，共 ${total} 条`;

    // 生成分页按钮
    let controls = '';

    // 上一页
    controls += `<button class="pagination-btn" ${page === 1 ? 'disabled' : ''} onclick="goToPage(${page - 1})">上一页</button>`;

    // 页码
    const maxVisiblePages = 5;
    let startPage = Math.max(1, page - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    if (startPage > 1) {
        controls += `<button class="pagination-btn" onclick="goToPage(1)">1</button>`;
        if (startPage > 2) {
            controls += `<span class="pagination-btn" disabled>...</span>`;
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        controls += `<button class="pagination-btn ${i === page ? 'active' : ''}" onclick="goToPage(${i})">${i}</button>`;
    }

    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            controls += `<span class="pagination-btn" disabled>...</span>`;
        }
        controls += `<button class="pagination-btn" onclick="goToPage(${totalPages})">${totalPages}</button>`;
    }

    // 下一页
    controls += `<button class="pagination-btn" ${page === totalPages ? 'disabled' : ''} onclick="goToPage(${page + 1})">下一页</button>`;

    document.getElementById('paginationControls').innerHTML = controls;
}

/**
 * 跳转页面
 */
function goToPage(page) {
    loadFileList({ page });
}

/**
 * 搜索文件
 */
function searchFiles() {
    const form = document.getElementById('searchForm');
    const formData = new FormData(form);

    const params = {
        page: 1,
        size: currentFileListParams.size,
        fileName: formData.get('fileName') || undefined,
        startDate: formData.get('startDate') || undefined,
        endDate: formData.get('endDate') || undefined
    };

    // 移除undefined值
    Object.keys(params).forEach(key => {
        if (params[key] === undefined) {
            delete params[key];
        }
    });

    loadFileList(params);
}

/**
 * 重置搜索
 */
function resetSearch() {
    document.getElementById('searchForm').reset();
    loadFileList({ page: 1, size: currentFileListParams.size });
}

/**
 * 查看文件详情
 */
function viewFileDetail(fileId) {
    router.navigate('file-detail', { id: fileId });
}

/**
 * 下载文件
 */
async function downloadFile(fileId, filename) {
    console.log('[前端下载] 开始下载，fileId=', fileId, 'filename=', filename);
    showLoading('正在下载...');

    const token = getToken();
    const downloadUrl = `/api/file/download/${fileId}?_t=${Date.now()}`;

    console.log('[前端下载] 下载URL=', downloadUrl, 'token存在=', !!token);

    try {
        // 使用原生fetch，添加更多缓存控制
        const response = await fetch(downloadUrl, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Cache-Control': 'no-cache',
                'Pragma': 'no-cache',
                'Expires': '0'
            },
            cache: 'no-store'
        });

        console.log('[前端下载] 响应status=', response.status, 'ok=', response.ok);

        if (response.status === 401) {
            window.location.href = '/login.html';
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            console.error('[前端下载] 错误响应:', errorText);
            throw new Error(`下载失败: ${response.status}`);
        }

        const blob = await response.blob();
        console.log('[前端下载] Blob size=', blob.size, 'type=', blob.type);

        if (blob.size === 0) {
            throw new Error('下载文件为空');
        }

        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);

        showSuccess('下载成功');
    } catch (error) {
        console.error('[前端下载] 错误:', error);
        showError(error.message || '下载失败');
    } finally {
        closeModal();
    }
}

/**
 * 删除文件
 */
async function deleteFile(fileId) {
    const confirmed = await showConfirm({
        title: '确认删除',
        message: '确定要删除此文件吗？删除后将无法恢复。',
        type: 'danger'
    });

    if (!confirmed) return;

    try {
        await API.file.delete(fileId);
        showSuccess('删除成功');
        loadFileList();
    } catch (error) {
        showError(error.message || '删除失败');
    }
}
