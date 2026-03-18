/**
 * 仪表盘页面
 */

/**
 * 页面加载
 */
function dashboardPageLoad() {
    const contentEl = document.getElementById('mainContent');
    contentEl.innerHTML = `
        <div class="page-container">
            <div class="page-header">
                <h1 class="page-title">仪表盘</h1>
                <p class="page-description">欢迎使用文件管理系统</p>
            </div>

            <div class="stats-grid" id="statsGrid">
                <div class="stat-card">
                    <div class="stat-icon primary">📁</div>
                    <div class="stat-content">
                        <div class="stat-label">文件总数</div>
                        <div class="stat-value" id="totalFiles">-</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon success">📊</div>
                    <div class="stat-content">
                        <div class="stat-label">数据总数</div>
                        <div class="stat-value" id="totalRecords">-</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon warning">📤</div>
                    <div class="stat-content">
                        <div class="stat-label">今日上传</div>
                        <div class="stat-value" id="todayUploads">-</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon info">👥</div>
                    <div class="stat-content">
                        <div class="stat-label">活跃用户</div>
                        <div class="stat-value" id="activeUsers">-</div>
                    </div>
                </div>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">快捷操作</h2>
                </div>
                <div class="card-body">
                    <div class="flex" style="gap: var(--spacing-md); flex-wrap: wrap;">
                        <button class="btn btn-primary" onclick="router.navigate('template')">
                            <span>📥</span> 下载模板
                        </button>
                        <button class="btn btn-success" onclick="router.navigate('file-upload')">
                            <span>📤</span> 上传文件
                        </button>
                        <button class="btn btn-secondary" onclick="router.navigate('file-list')">
                            <span>📋</span> 查看文件
                        </button>
                        <button class="btn btn-secondary" onclick="router.navigate('field-config')">
                            <span>⚙️</span> 字段配置
                        </button>
                    </div>
                </div>
            </div>

            <div class="card-container">
                <div class="card-header">
                    <h2 class="card-title">最近活动</h2>
                </div>
                <div class="card-body">
                    <div id="recentActivities">
                        <p class="text-secondary text-center" style="padding: var(--spacing-xl);">
                            加载中...
                        </p>
                    </div>
                </div>
            </div>
        </div>
    `;

    loadStatistics();
    loadRecentActivities();
}

/**
 * 加载统计数据
 */
async function loadStatistics() {
    try {
        const stats = await API.statistics.summary();

        document.getElementById('totalFiles').textContent = formatNumber(stats.totalFiles || 0);
        document.getElementById('totalRecords').textContent = formatNumber(stats.totalDataCount || 0);
        document.getElementById('todayUploads').textContent = formatNumber(stats.todayUploadCount || 0);
        document.getElementById('activeUsers').textContent = formatNumber(stats.todayDataCount || 0);
    } catch (error) {
        console.error('Load statistics error:', error);
        document.getElementById('totalFiles').textContent = '0';
        document.getElementById('totalRecords').textContent = '0';
        document.getElementById('todayUploads').textContent = '0';
        document.getElementById('activeUsers').textContent = '0';
    }
}

/**
 * 加载最近活动
 */
async function loadRecentActivities() {
    try {
        const logs = await API.log.list({ page: 1, size: 5 });

        const container = document.getElementById('recentActivities');
        const logList = logs.records || logs.list || [];

        if (logList.length === 0) {
            container.innerHTML = `
                <p class="text-secondary text-center" style="padding: var(--spacing-xl);">
                    暂无活动记录
                </p>
            `;
            return;
        }

        const operationNames = {
            'UPLOAD': '上传文件',
            'DELETE': '删除文件'
        };

        container.innerHTML = `
            <div class="table-container">
                <table class="table table-sm">
                    <thead>
                        <tr>
                            <th>操作类型</th>
                            <th>操作人</th>
                            <th>操作时间</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${logList.map(log => `
                            <tr>
                                <td>${operationNames[log.operationType] || log.operationType}</td>
                                <td>${log.username || '-'}</td>
                                <td>${formatDate(log.createTime, 'MM-DD HH:mm')}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    } catch (error) {
        console.error('Load recent activities error:', error);
        document.getElementById('recentActivities').innerHTML = `
            <p class="text-danger text-center" style="padding: var(--spacing-xl);">
                加载失败
            </p>
        `;
    }
}
