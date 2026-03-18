/**
 * 配置文件
 */
const CONFIG = {
    // API基础地址 - 使用相对路径，自动指向同源的后端
    API_BASE: '/api',

    // Token存储key
    TOKEN_KEY: 'auth_token',

    // 用户信息存储key
    USER_KEY: 'user_info',

    // 分页配置
    PAGE_SIZE: 10,
    PAGE_SIZE_OPTIONS: [10, 20, 50, 100],

    // 文件类型白名单
    FILE_TYPES: ['.xlsx', '.xls', '.csv'],

    // 最大文件大小 (200MB)
    MAX_FILE_SIZE: 200 * 1024 * 1024,

    // Token过期时间（毫秒）
    TOKEN_EXPIRE_TIME: 2 * 60 * 60 * 1000,

    // 请求超时时间（毫秒）
    REQUEST_TIMEOUT: 30000,

    // 日期格式
    DATE_FORMAT: 'YYYY-MM-DD',
    DATETIME_FORMAT: 'YYYY-MM-DD HH:mm:ss',

    // 角色类型
    ROLE: {
        USER: 'USER',
        ADMIN: 'ADMIN'
    }
};
