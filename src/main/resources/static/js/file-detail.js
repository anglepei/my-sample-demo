/**
 * 文件详情页面
 */

let currentFileId = null;
let currentDetailParams = {
    page: 1,
    size: 10
};

/**
 * 页面加载
 */
function fileDetailPageLoad() {
    const params = router.getPageParams();
    currentFileId = params.param;

    if (!currentFileId) {
        showError('缺少文件ID参数');
        router.navigate('file-list');
        return;
    }

    const contentEl = document.getElementById('mainContent');
    contentEl.innerHTML = `
        <div class="page-container">
            <div class="page-header">
                <div class="flex-center" style="gap: var(--spacing-md); justify-content: flex-start;">
                    <button class="btn btn-secondary btn-sm" onclick="router.navigate('file-list')">
                        ← 返回
                    </button>
                    <h1 class="page-title">文件详情</h1>
                </div>
            </div>

            <!-- 文件基本信息 -->
            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">文件信息</h2>
                </div>
                <div class="card-body">
                    <div id="fileInfo">加载中...</div>
                </div>
            </div>

            <!-- 字段配置快照 -->
            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">字段配置</h2>
                </div>
                <div class="card-body">
                    <div id="fieldConfigSnapshot">加载中...</div>
                </div>
            </div>

            <!-- 数据明细 -->
            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">数据明细</h2>
                </div>

                <!-- 搜索表单 -->
                <div style="padding: var(--spacing-md); border-bottom: 1px solid var(--border-color);">
                    <form id="detailSearchForm" class="search-form" onsubmit="event.preventDefault(); searchDataDetails();" style="box-shadow: none; padding: 0; background: transparent; margin-bottom: 0;">
                        <div class="form-item">
                            <label class="form-item-label">序号</label>
                            <input type="text" class="input input-sm" name="seqNo" placeholder="请输入序号">
                        </div>
                        <div class="form-item">
                            <label class="form-item-label">身份证</label>
                            <input type="text" class="input input-sm" name="idCard" placeholder="请输入身份证号">
                        </div>
                        <div class="search-actions">
                            <button type="submit" class="btn btn-primary btn-sm">搜索</button>
                            <button type="button" class="btn btn-secondary btn-sm" onclick="resetDetailSearch()">重置</button>
                        </div>
                    </form>
                </div>

                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="table table-sm">
                            <thead id="detailTableHead">
                                <tr>
                                    <th>序号</th>
                                    <th>身份证</th>
                                    <th>手机号</th>
                                </tr>
                            </thead>
                            <tbody id="detailTableBody">
                                <tr>
                                    <td colspan="3" class="text-center text-secondary" style="padding: var(--spacing-xl);">
                                        加载中...
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 分页 -->
                <div class="pagination" id="detailPagination">
                    <div class="pagination-info" id="detailPaginationInfo">显示 0-0 条，共 0 条</div>
                    <div class="pagination-controls" id="detailPaginationControls"></div>
                </div>

                <div class="card-footer" style="padding: var(--spacing-lg); border-top: 1px solid var(--border-color); text-align: right;">
                    <button class="btn btn-secondary" onclick="exportData()">
                        <span>📥</span> 导出数据
                    </button>
                </div>
            </div>
        </div>
    `;

    loadFileInfo();
}

/**
 * 加载文件信息
 */
async function loadFileInfo() {
    try {
        const result = await API.file.detail(currentFileId, { page: 1, size: 1 });

        // 显示文件信息
        document.getElementById('fileInfo').innerHTML = `
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: var(--spacing-md);">
                <div>
                    <span class="text-secondary">文件名：</span>
                    <span>${escapeHtml(result.file.originalName)}</span>
                </div>
                <div>
                    <span class="text-secondary">文件类型：</span>
                    <span>${result.file.fileType}</span>
                </div>
                <div>
                    <span class="text-secondary">数据条数：</span>
                    <span>${formatNumber(result.file.rowCount)} 条</span>
                </div>
                <div>
                    <span class="text-secondary">上传时间：</span>
                    <span>${formatDate(result.file.uploadTime)}</span>
                </div>
                <div>
                    <span class="text-secondary">上传人：</span>
                    <span>${result.file.username || '-'}</span>
                </div>
            </div>
        `;

        // 显示字段配置快照
        renderFieldConfigSnapshot(result.file.fieldConfigSnapshot);

        // 加载数据明细
        currentDetailParams = { page: 1, size: 10 };
        loadDataDetails();

    } catch (error) {
        console.error('Load file info error:', error);
        document.getElementById('fileInfo').innerHTML = `<p class="text-danger">加载失败: ${error.message}</p>`;
    }
}

/**
 * 渲染字段配置快照
 */
function renderFieldConfigSnapshot(snapshot) {
    const container = document.getElementById('fieldConfigSnapshot');

    if (!snapshot || !snapshot.fields || snapshot.fields.length === 0) {
        container.innerHTML = '<p class="text-secondary">暂无字段配置信息</p>';
        return;
    }

    // 构建完整的字段列表（固定字段 + 自定义字段）
    const allFields = [
        { name: '序号', type: 'TEXT', required: true, fixed: true },
        { name: '身份证', type: 'TEXT', required: true, fixed: true },
        { name: '手机号', type: 'TEXT', required: true, fixed: true },
        ...snapshot.fields.map(f => ({
            name: f.fieldName || f.name,
            type: f.fieldType || f.type,
            required: f.required,
            fixed: false
        }))
    ];

    container.innerHTML = `
        <div class="table-responsive">
            <table class="table table-sm">
                <thead>
                    <tr>
                        <th>序号</th>
                        <th>字段名称</th>
                        <th>字段类型</th>
                        <th>是否必填</th>
                    </tr>
                </thead>
                <tbody>
                    ${allFields.map((field, index) => `
                        <tr ${field.fixed ? 'style="background: #fafafa;"' : ''}>
                            <td>${index + 1}</td>
                            <td>${field.name} ${field.fixed ? '<span class="text-xs text-secondary">(固定)</span>' : ''}</td>
                            <td>${getFieldTypeName(field.type)}</td>
                            <td>${field.required ? '<span class="text-success">是</span>' : '<span class="text-secondary">否</span>'}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

/**
 * 获取字段类型名称
 */
function getFieldTypeName(type) {
    const typeNames = {
        'TEXT': '文本',
        'NUMBER': '数字',
        'DATE': '日期'
    };
    return typeNames[type] || type;
}

/**
 * 加载数据明细
 */
async function loadDataDetails(params = {}) {
    try {
        const mergedParams = { ...currentDetailParams, ...params };
        const result = await API.file.detail(currentFileId, mergedParams);

        currentDetailParams = mergedParams;

        renderDataDetails(result);
        renderDetailPagination(result);

    } catch (error) {
        console.error('Load data details error:', error);
        document.getElementById('detailTableBody').innerHTML = `
            <tr>
                <td colspan="3" class="text-center text-danger" style="padding: var(--spacing-xl);">
                    加载失败: ${error.message}
                </td>
            </tr>
        `;
    }
}

/**
 * 渲染数据明细
 */
function renderDataDetails(result) {
    const tableHead = document.getElementById('detailTableHead');
    const tableBody = document.getElementById('detailTableBody');

    if (!result.list || result.list.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="3" class="text-center text-secondary" style="padding: var(--spacing-xl);">
                    暂无数据
                </td>
            </tr>
        `;
        return;
    }

    // 渲染表头（包含自定义字段）
    const firstItem = result.list[0];
    const customFields = firstItem.customFields ? Object.keys(firstItem.customFields) : [];

    tableHead.innerHTML = `
        <tr>
            <th>序号</th>
            <th>身份证</th>
            <th>手机号</th>
            ${customFields.map(field => `<th>${escapeHtml(field)}</th>`).join('')}
        </tr>
    `;

    // 渲染数据
    tableBody.innerHTML = result.list.map(item => `
        <tr>
            <td>${escapeHtml(item.seqNo || '')}</td>
            <td>${escapeHtml(item.idCard || '')}</td>
            <td>${escapeHtml(item.phone || '')}</td>
            ${customFields.map(field => `<td>${escapeHtml(item.customFields?.[field] || '')}</td>`).join('')}
        </tr>
    `).join('');
}

/**
 * 渲染分页
 */
function renderDetailPagination(result) {
    const { page, size, total } = result;
    const totalPages = Math.ceil(total / size);
    const start = (page - 1) * size + 1;
    const end = Math.min(page * size, total);

    document.getElementById('detailPaginationInfo').textContent = `显示 ${start}-${end} 条，共 ${total} 条`;

    let controls = '';

    controls += `<button class="pagination-btn" ${page === 1 ? 'disabled' : ''} onclick="goToDetailPage(${page - 1})">上一页</button>`;

    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || (i >= page - 2 && i <= page + 2)) {
            controls += `<button class="pagination-btn ${i === page ? 'active' : ''}" onclick="goToDetailPage(${i})">${i}</button>`;
        } else if (i === page - 3 || i === page + 3) {
            controls += `<span class="pagination-btn" disabled>...</span>`;
        }
    }

    controls += `<button class="pagination-btn" ${page === totalPages ? 'disabled' : ''} onclick="goToDetailPage(${page + 1})">下一页</button>`;

    document.getElementById('detailPaginationControls').innerHTML = controls;
}

/**
 * 跳转页码
 */
function goToDetailPage(page) {
    loadDataDetails({ page });
}

/**
 * 搜索数据明细
 */
function searchDataDetails() {
    const form = document.getElementById('detailSearchForm');
    const formData = new FormData(form);

    const params = {
        page: 1,
        size: currentDetailParams.size,
        seqNo: formData.get('seqNo') || undefined,
        idCard: formData.get('idCard') || undefined
    };

    Object.keys(params).forEach(key => {
        if (params[key] === undefined) {
            delete params[key];
        }
    });

    loadDataDetails(params);
}

/**
 * 重置搜索
 */
function resetDetailSearch() {
    document.getElementById('detailSearchForm').reset();
    loadDataDetails({ page: 1, size: currentDetailParams.size });
}

/**
 * 导出数据
 */
function exportData() {
    showInfo('导出功能开发中...');
}
