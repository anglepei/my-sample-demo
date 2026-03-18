/**
 * 文件上传页面
 */

/**
 * 页面加载
 */
function fileUploadPageLoad() {
    const contentEl = document.getElementById('mainContent');
    contentEl.innerHTML = `
        <div class="page-container">
            <div class="page-header">
                <h1 class="page-title">文件上传</h1>
                <p class="page-description">支持 Excel (.xlsx, .xls) 和 CSV (.csv) 格式，最大 200MB</p>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">上传文件</h2>
                </div>

                <div class="card-body">
                    <div id="uploadArea" class="upload-area">
                        <div class="upload-placeholder">
                            <div class="upload-icon">📁</div>
                            <p class="upload-title">点击或拖拽文件到此处上传</p>
                            <p class="upload-hint">支持 .xlsx、.xls、.csv 格式，最大 200MB</p>
                        </div>
                        <input type="file" id="fileInput" accept=".xlsx,.xls,.csv" style="display: none;">
                    </div>

                    <div id="selectedFile" class="selected-file hidden" style="margin-top: var(--spacing-lg); padding: var(--spacing-md); background: #f5f5f5; border-radius: var(--border-radius-md);">
                        <div class="flex flex-between">
                            <div class="flex-center" style="gap: var(--spacing-sm);">
                                <span>📄</span>
                                <span id="selectedFileName"></span>
                                <span id="selectedFileSize" class="text-secondary text-sm"></span>
                            </div>
                            <button class="btn btn-sm btn-text" onclick="clearSelectedFile()">✕</button>
                        </div>
                    </div>

                    <div id="uploadProgress" class="upload-progress hidden" style="margin-top: var(--spacing-lg);">
                        <div class="flex flex-between" style="margin-bottom: var(--spacing-sm);">
                            <span id="uploadStatus">上传中...</span>
                            <span id="uploadPercent">0%</span>
                        </div>
                        <div class="progress-bar">
                            <div class="progress-bar-fill" id="progressBarFill" style="width: 0%;"></div>
                        </div>
                    </div>

                    <div id="uploadResult" class="upload-result hidden" style="margin-top: var(--spacing-lg); padding: var(--spacing-md); border-radius: var(--border-radius-md);"></div>
                </div>

                <div class="card-footer" style="padding: var(--spacing-lg); border-top: 1px solid var(--border-color);">
                    <div class="flex flex-between">
                        <button class="btn btn-secondary" onclick="router.navigate('template')">
                            <span>📥</span> 下载模板
                        </button>
                        <button class="btn btn-primary" id="uploadBtn" onclick="startUpload()" disabled>
                            <span>📤</span> 开始上传
                        </button>
                    </div>
                </div>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">上传说明</h2>
                </div>
                <div class="card-body">
                    <div class="text-secondary" style="line-height: 1.8;">
                        <p>1. 请先下载模板并按模板格式填写数据</p>
                        <p>2. 第一行为表头，请勿修改</p>
                        <p>3. 固定字段（序号、身份证、手机号）必须填写且格式正确</p>
                        <p>4. 身份证号：18位且校验码正确</p>
                        <p>5. 手机号：11位且以1开头</p>
                        <p>6. 自定义字段中的必填项不能为空</p>
                        <p>7. 同一文件内身份证号不能重复</p>
                        <p>8. 空行将被自动跳过</p>
                    </div>
                </div>
            </div>
        </div>

        <style>
            .upload-area {
                border: 2px dashed var(--border-color);
                border-radius: var(--border-radius-lg);
                padding: var(--spacing-xl);
                text-align: center;
                cursor: pointer;
                transition: all 0.3s;
            }
            .upload-area:hover {
                border-color: var(--primary-color);
                background: #f5f5f5;
            }
            .upload-area.dragover {
                border-color: var(--primary-color);
                background: #e6f7ff;
            }
            .upload-icon {
                font-size: 48px;
                margin-bottom: var(--spacing-md);
            }
            .upload-title {
                font-size: var(--font-size-lg);
                color: var(--text-primary);
                margin-bottom: var(--spacing-sm);
            }
            .upload-hint {
                color: var(--text-secondary);
                font-size: var(--font-size-sm);
            }
            .progress-bar {
                width: 100%;
                height: 8px;
                background: var(--bg-secondary);
                border-radius: 4px;
                overflow: hidden;
            }
            .progress-bar-fill {
                height: 100%;
                background: var(--primary-color);
                transition: width 0.3s;
            }
            .upload-result.success {
                background: #f6ffed;
                border: 1px solid #b7eb8f;
                color: #52c41a;
            }
            .upload-result.error {
                background: #fff2f0;
                border: 1px solid #ffccc7;
                color: #f5222d;
            }
        </style>
    `;

    setupUploadArea();
}

let selectedFile = null;

/**
 * 设置上传区域
 */
function setupUploadArea() {
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('fileInput');

    // 点击上传
    uploadArea.addEventListener('click', () => {
        fileInput.click();
    });

    // 文件选择
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            handleFileSelect(file);
        }
    });

    // 拖拽上传
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
    });

    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('dragover');

        const file = e.dataTransfer.files[0];
        if (file) {
            handleFileSelect(file);
        }
    });
}

/**
 * 处理文件选择
 */
function handleFileSelect(file) {
    // 验证文件类型
    const ext = getFileExtension(file.name);
    if (!['.xlsx', '.xls', '.csv'].includes(ext)) {
        showError('不支持的文件格式，请上传 Excel 或 CSV 文件');
        return;
    }

    // 验证文件大小
    if (file.size > CONFIG.MAX_FILE_SIZE) {
        showError('文件大小超过 200MB');
        return;
    }

    selectedFile = file;

    // 显示选中的文件
    document.getElementById('selectedFile').classList.remove('hidden');
    document.getElementById('selectedFileName').textContent = file.name;
    document.getElementById('selectedFileSize').textContent = formatFileSize(file.size);

    // 启用上传按钮
    document.getElementById('uploadBtn').disabled = false;
}

/**
 * 清除选中的文件
 */
function clearSelectedFile() {
    selectedFile = null;
    document.getElementById('fileInput').value = '';
    document.getElementById('selectedFile').classList.add('hidden');
    document.getElementById('uploadBtn').disabled = true;
    hideUploadResult();
    hideUploadProgress();
}

/**
 * 开始上传
 */
async function startUpload() {
    if (!selectedFile) {
        showError('请先选择文件');
        return;
    }

    const formData = new FormData();
    formData.append('file', selectedFile);

    // 显示进度条
    showUploadProgress();
    updateUploadProgress(0, '上传中...');

    // 禁用上传按钮
    const uploadBtn = document.getElementById('uploadBtn');
    uploadBtn.disabled = true;

    try {
        await API.file.upload(formData, (percent) => {
            updateUploadProgress(percent, '上传中...');
        });

        // 上传成功
        updateUploadProgress(100, '上传成功');
        showUploadResult('success', `文件"${selectedFile.name}"上传成功！共处理数据，请到文件列表查看。`);

        // 3秒后跳转到文件列表
        setTimeout(() => {
            router.navigate('file-list');
        }, 3000);

    } catch (error) {
        // 上传失败
        showUploadResult('error', `上传失败：${error.message}`);

        // 解析错误详情
        try {
            const errorData = JSON.parse(error.message);
            if (errorData.details) {
                showErrorDetails(errorData.details);
            }
        } catch (e) {
            // 不是JSON格式的错误，直接显示
        }
    } finally {
        uploadBtn.disabled = false;
    }
}

/**
 * 显示上传进度
 */
function showUploadProgress() {
    document.getElementById('uploadProgress').classList.remove('hidden');
}

/**
 * 隐藏上传进度
 */
function hideUploadProgress() {
    document.getElementById('uploadProgress').classList.add('hidden');
}

/**
 * 更新上传进度
 */
function updateUploadProgress(percent, status) {
    document.getElementById('uploadPercent').textContent = percent + '%';
    document.getElementById('uploadStatus').textContent = status;
    document.getElementById('progressBarFill').style.width = percent + '%';
}

/**
 * 显示上传结果
 */
function showUploadResult(type, message) {
    const resultDiv = document.getElementById('uploadResult');
    resultDiv.className = `upload-result ${type}`;
    resultDiv.classList.remove('hidden');
    resultDiv.innerHTML = `
        <div class="flex-center" style="gap: var(--spacing-sm);">
            <span>${type === 'success' ? '✓' : '✕'}</span>
            <span>${message}</span>
        </div>
    `;
}

/**
 * 隐藏上传结果
 */
function hideUploadResult() {
    document.getElementById('uploadResult').classList.add('hidden');
}

/**
 * 显示错误详情
 */
function showErrorDetails(details) {
    const contentEl = document.getElementById('mainContent');

    // 创建错误详情弹窗
    const modalHtml = `
        <div style="max-height: 400px; overflow-y: auto;">
            <p class="mb-md">数据校验失败，请检查以下错误：</p>
            <table class="table table-bordered table-sm">
                <thead>
                    <tr>
                        <th>行号</th>
                        <th>字段</th>
                        <th>错误信息</th>
                    </tr>
                </thead>
                <tbody>
                    ${details.map(err => `
                        <tr>
                            <td>${err.row || '-'}</td>
                            <td>${err.field || '-'}</td>
                            <td>${err.message}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;

    showModal({
        title: '数据校验错误',
        content: modalHtml,
        width: '600px'
    });
}
