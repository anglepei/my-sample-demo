/**
 * 模板下载页面
 */

/**
 * 页面加载
 */
function templatePageLoad() {
    const contentEl = document.getElementById('mainContent');
    contentEl.innerHTML = `
        <div class="page-container">
            <div class="page-header">
                <h1 class="page-title">模板下载</h1>
                <p class="page-description">下载当前字段配置的模板文件</p>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">下载模板</h2>
                </div>

                <div class="card-body">
                    <div class="flex flex-column" style="gap: var(--spacing-lg); align-items: center; padding: var(--spacing-xl) 0;">
                        <div style="font-size: 64px;">📥</div>
                        <div class="text-center">
                            <p class="text-primary" style="font-size: var(--font-size-lg); margin-bottom: var(--spacing-sm);">
                                选择模板类型下载
                            </p>
                            <p class="text-secondary">
                                模板包含固定字段（序号、身份证、手机号）和您配置的自定义字段
                            </p>
                        </div>
                        <div class="flex" style="gap: var(--spacing-md);">
                            <button class="btn btn-primary btn-lg" onclick="downloadExcelTemplate()">
                                <span>📊</span> 下载 Excel 模板
                            </button>
                            <button class="btn btn-success btn-lg" onclick="downloadCsvTemplate()">
                                <span>📄</span> 下载 CSV 模板
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">下载带测试数据的模板</h2>
                </div>

                <div class="card-body">
                    <div class="flex flex-column" style="gap: var(--spacing-lg); align-items: center; padding: var(--spacing-xl) 0;">
                        <div style="font-size: 48px;">🧪</div>
                        <div class="text-center">
                            <p class="text-primary" style="font-size: var(--font-size-lg); margin-bottom: var(--spacing-sm);">
                                生成测试数据模板
                            </p>
                            <p class="text-secondary">
                                模板包含随机生成的测试数据，可用于系统测试
                            </p>
                        </div>
                        <div class="flex flex-column" style="gap: var(--spacing-sm); width: 100%; max-width: 300px;">
                            <label class="form-label">数据条数：</label>
                            <input type="number" id="dataCount" class="form-control" value="10" min="1" max="1000000"
                                   placeholder="请输入数据条数（1-1000000）">
                            <small class="text-secondary">最大支持 1,000,000 条数据</small>
                        </div>
                        <div class="flex" style="gap: var(--spacing-md);">
                            <button class="btn btn-warning btn-lg" onclick="downloadExcelTemplateWithData()">
                                <span>📊</span> 下载带数据 Excel
                            </button>
                            <button class="btn btn-info btn-lg" onclick="downloadCsvTemplateWithData()">
                                <span>📄</span> 下载带数据 CSV
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">字段预览</h2>
                </div>

                <div class="card-body">
                    <div id="fieldPreview">
                        <p class="text-center text-secondary">加载中...</p>
                    </div>
                </div>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">使用说明</h2>
                </div>

                <div class="card-body">
                    <div class="text-secondary" style="line-height: 1.8;">
                        <p>1. 下载模板前，请先在"字段配置"页面配置您的自定义字段</p>
                        <p>2. 模板第一行为表头，请勿修改</p>
                        <p>3. 固定字段（序号、身份证、手机号）为必填项</p>
                        <p>4. 身份证号必须为18位且格式正确（最后一位校验码必须正确）</p>
                        <p>5. 手机号必须为11位且以1开头</p>
                        <p>6. 自定义字段中的必填项不能为空</p>
                        <p>7. 同一文件内身份证号不能重复</p>
                        <p>8. 填写完成后保存文件，然后到"文件上传"页面上传</p>
                        <p style="color: var(--primary-color); font-weight: 500;">9. CSV模板所有单元格已设置为文本格式，可直接填写数据</p>
                    </div>
                </div>
            </div>
        </div>
    `;

    loadFieldPreview();
}

/**
 * 加载字段预览
 */
async function loadFieldPreview() {
    try {
        const config = await API.field.getConfig();
        const container = document.getElementById('fieldPreview');

        const columns = [
            { name: '序号', type: 'TEXT', required: true, fixed: true },
            { name: '身份证', type: 'TEXT', required: true, fixed: true },
            { name: '手机号', type: 'TEXT', required: true, fixed: true }
        ];

        if (config.fields && config.fields.length > 0) {
            columns.push(...config.fields.map(f => ({
                name: f.fieldName,
                type: f.fieldType,
                required: f.required,
                fixed: false
            })));
        }

        container.innerHTML = `
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>序号</th>
                            <th>字段名称</th>
                            <th>字段类型</th>
                            <th>是否必填</th>
                            <th>说明</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${columns.map((col, index) => `
                            <tr ${col.fixed ? 'style="background: #fafafa;"' : ''}>
                                <td>${index + 1}</td>
                                <td>${col.name} ${col.fixed ? '<span class="text-xs text-secondary">(固定)</span>' : ''}</td>
                                <td>${getFieldTypeName(col.type)}</td>
                                <td>${col.required ? '<span class="text-success">是</span>' : '<span class="text-secondary">否</span>'}</td>
                                <td>${col.fixed ? '系统固定字段，不可删除' : '自定义字段'}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    } catch (error) {
        console.error('Load field preview error:', error);
        document.getElementById('fieldPreview').innerHTML = `
            <p class="text-center text-danger">加载失败: ${error.message}</p>
        `;
    }
}

/**
 * 获取字段类型名称
 */
function getFieldTypeName(type) {
    const typeNames = {
        'TEXT': '文本',
        'NUMBER': '数字',
        'DATE': '日期 (YYYY-MM-DD)'
    };
    return typeNames[type] || type;
}

/**
 * 下载Excel模板
 */
async function downloadExcelTemplate() {
    const loadingToast = showLoading('正在生成模板...');

    try {
        // 添加超时控制
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 30000); // 30秒超时

        const token = localStorage.getItem('auth_token');

        const response = await fetch('/api/template/download/excel', {
            headers: {
                'Authorization': token ? `Bearer ${token}` : ''
            },
            signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (response.status === 401) {
            console.error('[下载] 401 未授权');
            showError('登录已过期，请重新登录');
            setTimeout(() => window.location.href = '/login.html', 1500);
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            console.error('[下载] 请求失败, status:', response.status, 'response:', errorText);
            throw new Error(`下载失败 (${response.status})`);
        }

        const blob = await response.blob();

        // 下载文件
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'data_template.xlsx';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);

        showSuccess('Excel模板下载成功');
    } catch (error) {
        console.error('[下载] 异常:', error);
        if (error.name === 'AbortError') {
            showError('下载超时，请稍后重试');
        } else {
            showError(error.message || '下载失败，请检查网络连接');
        }
    } finally {
        if (loadingToast) {
            loadingToast.remove();
        }
    }
}

/**
 * 下载CSV模板
 */
async function downloadCsvTemplate() {
    const loadingToast = showLoading('正在生成模板...');

    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 30000);

        const token = localStorage.getItem('auth_token');

        const response = await fetch('/api/template/download/csv', {
            headers: {
                'Authorization': token ? `Bearer ${token}` : ''
            },
            signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (response.status === 401) {
            console.error('[下载] 401 未授权');
            showError('登录已过期，请重新登录');
            setTimeout(() => window.location.href = '/login.html', 1500);
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            console.error('[下载] 请求失败, status:', response.status, 'response:', errorText);
            throw new Error(`下载失败 (${response.status})`);
        }

        const blob = await response.blob();

        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'data_template.csv';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);

        showSuccess('CSV模板下载成功');
    } catch (error) {
        console.error('[下载] 异常:', error);
        if (error.name === 'AbortError') {
            showError('下载超时，请稍后重试');
        } else {
            showError(error.message || '下载失败，请检查网络连接');
        }
    } finally {
        if (loadingToast) {
            loadingToast.remove();
        }
    }
}

/**
 * 下载带数据的Excel模板
 */
async function downloadExcelTemplateWithData() {
    const countInput = document.getElementById('dataCount');
    const count = parseInt(countInput.value);

    // 验证输入
    if (isNaN(count) || count < 1) {
        showError('数据条数必须大于等于1');
        return;
    }
    if (count > 1000000) {
        showError('数据条数不能超过1,000,000');
        return;
    }

    const loadingToast = showLoading(`正在生成 ${count} 条测试数据...`);

    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 60000);

        const token = localStorage.getItem('auth_token');

        const response = await fetch(`/api/template/download/excelWithData?count=${count}`, {
            headers: {
                'Authorization': token ? `Bearer ${token}` : ''
            },
            signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (response.status === 401) {
            console.error('[下载] 401 未授权');
            showError('登录已过期，请重新登录');
            setTimeout(() => window.location.href = '/login.html', 1500);
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            console.error('[下载] 请求失败, status:', response.status, 'response:', errorText);
            throw new Error(`下载失败 (${response.status})`);
        }

        const blob = await response.blob();

        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'data_template_with_data.xlsx';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);

        showSuccess(`Excel模板下载成功（${count}条数据）`);
    } catch (error) {
        console.error('[下载] 异常:', error);
        if (error.name === 'AbortError') {
            showError('下载超时，请减少数据条数后重试');
        } else {
            showError(error.message || '下载失败，请检查网络连接');
        }
    } finally {
        if (loadingToast) {
            loadingToast.remove();
        }
    }
}

/**
 * 下载带数据的CSV模板
 */
async function downloadCsvTemplateWithData() {
    const countInput = document.getElementById('dataCount');
    const count = parseInt(countInput.value);

    // 验证输入
    if (isNaN(count) || count < 1) {
        showError('数据条数必须大于等于1');
        return;
    }
    if (count > 1000000) {
        showError('数据条数不能超过1,000,000');
        return;
    }

    const loadingToast = showLoading(`正在生成 ${count} 条测试数据...`);

    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 60000);

        const token = localStorage.getItem('auth_token');

        const response = await fetch(`/api/template/download/csvWithData?count=${count}`, {
            headers: {
                'Authorization': token ? `Bearer ${token}` : ''
            },
            signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (response.status === 401) {
            console.error('[下载] 401 未授权');
            showError('登录已过期，请重新登录');
            setTimeout(() => window.location.href = '/login.html', 1500);
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            console.error('[下载] 请求失败, status:', response.status, 'response:', errorText);
            throw new Error(`下载失败 (${response.status})`);
        }

        const blob = await response.blob();

        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'data_template_with_data.csv';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);

        showSuccess(`CSV模板下载成功（${count}条数据）`);
    } catch (error) {
        console.error('[下载] 异常:', error);
        if (error.name === 'AbortError') {
            showError('下载超时，请减少数据条数后重试');
        } else {
            showError(error.message || '下载失败，请检查网络连接');
        }
    } finally {
        if (loadingToast) {
            loadingToast.remove();
        }
    }
}
