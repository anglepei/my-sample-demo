/**
 * 字段配置页面
 */

/**
 * 页面加载
 */
function fieldConfigPageLoad() {
    const contentEl = document.getElementById('mainContent');
    contentEl.innerHTML = `
        <div class="page-container">
            <div class="page-header">
                <h1 class="page-title">字段配置</h1>
                <p class="page-description">配置您的自定义字段，最多可添加10个</p>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">字段列表</h2>
                    <div class="card-actions">
                        <button class="btn btn-primary" onclick="addFieldConfig()">
                            <span>+</span> 新增字段
                        </button>
                    </div>
                </div>

                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>字段名称</th>
                                    <th>字段类型</th>
                                    <th>是否必填</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody id="fieldConfigTableBody">
                                <tr>
                                    <td colspan="5" class="text-center text-secondary">
                                        加载中...
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="card-footer" style="padding: var(--spacing-lg); border-top: 1px solid var(--border-color); text-align: right;">
                    <button class="btn btn-secondary" onclick="router.navigate('template')" style="margin-right: var(--spacing-sm);">
                        下载模板
                    </button>
                </div>
            </div>
        </div>
    `;

    loadFieldConfigList();
}

/**
 * 加载字段配置列表
 */
async function loadFieldConfigList() {
    try {
        const config = await API.field.getConfig();

        const tableBody = document.getElementById('fieldConfigTableBody');

        // 固定字段
        const fixedFields = [
            { name: '序号', type: 'TEXT', required: true, fixed: true },
            { name: '身份证', type: 'TEXT', required: true, fixed: true },
            { name: '手机号', type: 'TEXT', required: true, fixed: true }
        ];

        let html = '';

        // 渲染固定字段
        fixedFields.forEach((field, index) => {
            html += `
                <tr style="background: #fafafa;">
                    <td>${index + 1}</td>
                    <td>${field.name} <span class="text-xs text-secondary">(固定)</span></td>
                    <td>${getFieldTypeName(field.type)}</td>
                    <td>${field.required ? '是' : '否'}</td>
                    <td>-</td>
                </tr>
            `;
        });

        // 渲染自定义字段
        if (config.fields && config.fields.length > 0) {
            config.fields.forEach((field, index) => {
                html += `
                    <tr>
                        <td>${fixedFields.length + index + 1}</td>
                        <td>${field.fieldName}</td>
                        <td>${getFieldTypeName(field.fieldType)}</td>
                        <td>${field.required ? '是' : '否'}</td>
                        <td>
                            <div class="table-actions">
                                <a class="table-action-link" onclick="editFieldConfig('${field.fieldName}', '${field.fieldType}', ${field.required})">编辑</a>
                                <a class="table-action-link danger" onclick="deleteFieldConfig('${field.fieldName}')">删除</a>
                            </div>
                        </td>
                    </tr>
                `;
            });
        }

        if (config.fields && config.fields.length === 0) {
            html += `
                <tr>
                    <td colspan="5" class="text-center text-secondary" style="padding: var(--spacing-xl);">
                        暂无自定义字段，点击上方"新增字段"按钮添加
                    </td>
                </tr>
            `;
        }

        tableBody.innerHTML = html;
    } catch (error) {
        console.error('Load field config error:', error);
        document.getElementById('fieldConfigTableBody').innerHTML = `
            <tr>
                <td colspan="5" class="text-center text-danger" style="padding: var(--spacing-xl);">
                    加载失败: ${error.message}
                </td>
            </tr>
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
        'DATE': '日期'
    };
    return typeNames[type] || type;
}

/**
 * 新增字段配置
 */
function addFieldConfig() {
    const modalHtml = `
        <form id="fieldConfigForm">
            <div class="form-item">
                <label class="form-item-label required">字段名称</label>
                <input type="text" class="input" name="fieldName" placeholder="请输入字段名称" required maxlength="64">
            </div>
            <div class="form-item">
                <label class="form-item-label required">字段类型</label>
                <select class="select" name="fieldType" required>
                    <option value="">请选择字段类型</option>
                    <option value="TEXT">文本</option>
                    <option value="NUMBER">数字</option>
                    <option value="DATE">日期</option>
                </select>
            </div>
            <div class="form-item">
                <label class="checkbox">
                    <input type="checkbox" name="required">
                    <span class="checkbox-label">必填</span>
                </label>
            </div>
        </form>
    `;

    showModal({
        title: '新增字段',
        content: modalHtml,
        onConfirm: async () => {
            const form = document.getElementById('fieldConfigForm');
            const formData = new FormData(form);

            const fieldName = formData.get('fieldName').trim();
            const fieldType = formData.get('fieldType');
            const required = formData.get('required') === 'on';

            if (!fieldName) {
                showToast('请输入字段名称', 'warning');
                return false;
            }

            if (!fieldType) {
                showToast('请选择字段类型', 'warning');
                return false;
            }

            try {
                const config = await API.field.getConfig();
                const fields = config.fields || [];

                if (fields.length >= 10) {
                    showToast('最多只能添加10个自定义字段', 'warning');
                    return false;
                }

                if (fields.some(f => f.fieldName === fieldName)) {
                    showToast('字段名称已存在', 'warning');
                    return false;
                }

                const newField = {
                    fieldName: fieldName,
                    fieldType: fieldType,
                    required: required
                };

                fields.push(newField);

                await API.field.saveConfig(fields);
                showSuccess('添加成功');
                loadFieldConfigList();
                return true;
            } catch (error) {
                showError(error.message || '添加失败');
                return false;
            }
        }
    });

    // 聚焦第一个输入框
    setTimeout(() => {
        const input = document.querySelector('#fieldConfigForm input[name="fieldName"]');
        if (input) input.focus();
    }, 100);
}

/**
 * 编辑字段配置
 */
function editFieldConfig(name, type, required) {
    const modalHtml = `
        <form id="fieldConfigForm">
            <input type="hidden" name="originalName" value="${name}">
            <div class="form-item">
                <label class="form-item-label required">字段名称</label>
                <input type="text" class="input" name="fieldName" value="${name}" required maxlength="64">
            </div>
            <div class="form-item">
                <label class="form-item-label required">字段类型</label>
                <select class="select" name="fieldType" required>
                    <option value="TEXT" ${type === 'TEXT' ? 'selected' : ''}>文本</option>
                    <option value="NUMBER" ${type === 'NUMBER' ? 'selected' : ''}>数字</option>
                    <option value="DATE" ${type === 'DATE' ? 'selected' : ''}>日期</option>
                </select>
            </div>
            <div class="form-item">
                <label class="checkbox">
                    <input type="checkbox" name="required" ${required ? 'checked' : ''}>
                    <span class="checkbox-label">必填</span>
                </label>
            </div>
        </form>
    `;

    showModal({
        title: '编辑字段',
        content: modalHtml,
        onConfirm: async () => {
            const form = document.getElementById('fieldConfigForm');
            const formData = new FormData(form);

            const originalName = formData.get('originalName');
            const fieldName = formData.get('fieldName').trim();
            const fieldType = formData.get('fieldType');
            const isRequired = formData.get('required') === 'on';

            if (!fieldName) {
                showToast('请输入字段名称', 'warning');
                return false;
            }

            if (!fieldType) {
                showToast('请选择字段类型', 'warning');
                return false;
            }

            try {
                const config = await API.field.getConfig();
                const fields = config.fields || [];

                // 检查名称是否与其他字段重复
                if (fieldName !== originalName && fields.some(f => f.fieldName === fieldName)) {
                    showToast('字段名称已存在', 'warning');
                    return false;
                }

                // 更新字段
                const index = fields.findIndex(f => f.fieldName === originalName);
                if (index !== -1) {
                    fields[index] = {
                        fieldName: fieldName,
                        fieldType: fieldType,
                        required: isRequired
                    };
                }

                await API.field.saveConfig(fields);
                showSuccess('保存成功');
                loadFieldConfigList();
                return true;
            } catch (error) {
                showError(error.message || '保存失败');
                return false;
            }
        }
    });
}

/**
 * 删除字段配置
 */
async function deleteFieldConfig(name) {
    const confirmed = await showConfirm({
        title: '确认删除',
        message: `确定要删除字段"${name}"吗？`,
        type: 'danger'
    });

    if (!confirmed) return;

    try {
        const config = await API.field.getConfig();
        const fields = config.fields || [];

        const newFields = fields.filter(f => f.fieldName !== name);

        await API.field.saveConfig(newFields);
        showSuccess('删除成功');
        loadFieldConfigList();
    } catch (error) {
        showError(error.message || '删除失败');
    }
}
