/**
 * API封装模块
 */

/**
 * 获取Token
 */
function getToken() {
    return localStorage.getItem(CONFIG.TOKEN_KEY);
}

/**
 * 设置Token
 */
function setToken(token) {
    localStorage.setItem(CONFIG.TOKEN_KEY, token);
}

/**
 * 移除Token
 */
function removeToken() {
    localStorage.removeItem(CONFIG.TOKEN_KEY);
}

/**
 * 通用请求封装
 */
async function request(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json'
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // 添加超时控制
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), CONFIG.REQUEST_TIMEOUT);

    try {
        const response = await fetch(CONFIG.API_BASE + url, {
            ...options,
            headers: { ...headers, ...options.headers },
            signal: controller.signal
        });

        clearTimeout(timeoutId);

        // 处理401未授权
        if (response.status === 401) {
            removeToken();
            localStorage.removeItem(CONFIG.USER_KEY);
            window.location.href = '/login.html';
            throw new Error('登录已过期');
        }

        // 处理403无权限
        if (response.status === 403) {
            throw new Error('无权限访问');
        }

        // 解析JSON响应
        const data = await response.json();

        // 统一响应格式处理
        if (data.code === 0) {
            return data.data;
        } else {
            throw new Error(data.message || '请求失败');
        }
    } catch (error) {
        if (error.name === 'AbortError') {
            throw new Error('请求超时');
        }
        throw error;
    }
}

/**
 * GET请求
 */
function get(url, params = {}) {
    const query = Object.keys(params)
        .filter(key => params[key] !== undefined && params[key] !== null)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&');

    const queryString = query ? '?' + query : '';
    return request(url + queryString, { method: 'GET' });
}

/**
 * POST请求
 */
function post(url, data = {}) {
    return request(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

/**
 * PUT请求
 */
function put(url, data = {}) {
    return request(url, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

/**
 * DELETE请求
 */
function del(url) {
    return request(url, { method: 'DELETE' });
}

/**
 * 文件上传
 */
function uploadFile(url, formData, onProgress) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        const token = getToken();

        // 上传进度
        xhr.upload.addEventListener('progress', (e) => {
            if (e.lengthComputable && onProgress) {
                const percent = Math.round((e.loaded / e.total) * 100);
                onProgress(percent, e.loaded, e.total);
            }
        });

        // 上传完成
        xhr.addEventListener('load', () => {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.code === 0) {
                        resolve(response.data);
                    } else {
                        reject(new Error(response.message || '上传失败'));
                    }
                } catch (e) {
                    reject(new Error('响应解析失败'));
                }
            } else if (xhr.status === 401) {
                removeToken();
                localStorage.removeItem(CONFIG.USER_KEY);
                window.location.href = '/login.html';
                reject(new Error('登录已过期'));
            } else {
                reject(new Error(`上传失败: ${xhr.status}`));
            }
        });

        // 上传错误
        xhr.addEventListener('error', () => {
            reject(new Error('网络错误'));
        });

        // 上传中止
        xhr.addEventListener('abort', () => {
            reject(new Error('上传已取消'));
        });

        // 发送请求
        xhr.open('POST', CONFIG.API_BASE + url);
        if (token) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        }
        xhr.send(formData);
    });
}

/**
 * 文件下载（使用Blob）
 */
async function downloadFile(url, filename, params = {}) {
    console.log('[API下载] 开始下载，url=', url, 'filename=', filename, 'params=', params);
    const token = getToken();
    const query = Object.keys(params)
        .filter(key => params[key] !== undefined && params[key] !== null)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&');

    const queryString = query ? '?' + query : '';
    const downloadUrl = CONFIG.API_BASE + url + queryString;
    console.log('[API下载] 完整URL=', downloadUrl);

    try {
        console.log('[API下载] 发起fetch请求');
        const response = await fetch(downloadUrl, {
            headers: {
                'Authorization': token ? `Bearer ${token}` : '',
                'Cache-Control': 'no-cache',
                'Pragma': 'no-cache'
            },
            cache: 'no-store'
        });
        console.log('[API下载] 收到响应，status=', response.status, 'ok=', response.ok);

        if (response.status === 401) {
            removeToken();
            localStorage.removeItem(CONFIG.USER_KEY);
            window.location.href = '/login.html';
            throw new Error('登录已过期');
        }

        if (!response.ok) {
            console.error('[API下载] 响应不正常，status=', response.status);
            // 尝试读取错误信息
            const errorText = await response.text();
            console.error('[API下载] 错误响应内容:', errorText);
            throw new Error('下载失败');
        }

        console.log('[API下载] 开始转换为Blob');
        const blob = await response.blob();
        console.log('[API下载] Blob生成完成，size=', blob.size, 'type=', blob.type);
        downloadBlob(blob, filename);
        console.log('[API下载] downloadBlob调用完成');
    } catch (error) {
        console.error('[API下载] 下载过程出错:', error);
        throw error;
    }
}

/**
 * 下载Blob文件
 */
function downloadBlob(blob, filename) {
    console.log('[下载Blob] 开始，filename=', filename, 'blob.size=', blob.size);
    try {
        const url = URL.createObjectURL(blob);
        console.log('[下载Blob] ObjectURL创建成功=', url);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        document.body.appendChild(link);
        console.log('[下载Blob] 触发点击下载');
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        console.log('[下载Blob] 下载完成');
    } catch (error) {
        console.error('[下载Blob] 失败:', error);
        throw error;
    }
}

/**
 * API接口定义
 */
const API = {
    // 认证相关
    auth: {
        login: (username, password) => post('/auth/login', { username, password }),
        register: (username, password) => post('/auth/register', { username, password }),
        logout: () => post('/auth/logout'),
        getInfo: () => get('/auth/info')
    },

    // 字段配置
    field: {
        getConfig: () => get('/field/config'),
        saveConfig: (fields) => post('/field/config', { fields }),
        getTypes: () => get('/field/types')
    },

    // 模板
    template: {
        downloadExcel: () => downloadFile('/template/download/excel', 'data_template.xlsx'),
        downloadCsv: () => downloadFile('/template/download/csv', 'data_template.csv')
    },

    // 文件
    file: {
        upload: (formData, onProgress) => uploadFile('/file/upload', formData, onProgress),
        list: (params) => get('/file/list', params),
        detail: (id, params) => get(`/file/detail/${id}`, params),
        download: (id, filename) => downloadFile(`/file/download/${id}?_t=${Date.now()}`, filename),
        delete: (id) => del(`/file/delete/${id}`)
    },

    // 日志
    log: {
        list: (params) => get('/log/list', params)
    },

    // 统计
    statistics: {
        summary: () => get('/statistics/summary')
    }
};
