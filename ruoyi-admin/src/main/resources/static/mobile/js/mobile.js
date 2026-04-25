/**
 * RuoYi 移动端JavaScript
 */

// 全局配置
const MobileApp = {
    baseUrl: window.location.origin,
    apiUrl: window.location.origin + '/mobile/api',
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
};

// ================================
// 工具函数
// ================================

// 显示加载动画
function showLoading(text = '加载中...') {
    const loading = document.createElement('div');
    loading.className = 'loading-overlay';
    loading.id = 'loading-overlay';
    loading.innerHTML = `
        <div style="text-align: center;">
            <div class="loading-spinner"></div>
            <div class="loading-text">${text}</div>
        </div>
    `;
    document.body.appendChild(loading);
}

// 隐藏加载动画
function hideLoading() {
    const loading = document.getElementById('loading-overlay');
    if (loading) {
        loading.remove();
    }
}

// 显示提示信息
function showToast(message, duration = 2000) {
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, duration);
}

// HTTP请求封装
async function request(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${MobileApp.token}`
        }
    };
    
    const config = { ...defaultOptions, ...options };
    
    // 合并headers
    if (options.headers) {
        config.headers = { ...defaultOptions.headers, ...options.headers };
    }
    
    try {
        const response = await fetch(url, config);
        
        // 打印响应状态用于调试
        console.log('API Response Status:', response.status);
        console.log('API URL:', url);
        
        const data = await response.json();
        console.log('API Response Data:', data);
        
        // 判断响应码，0表示成功（RuoYi框架标准）
        if (data.code === 0 || data.code === 200) {
            return data;
        } else if (data.code === 401) {
            // Token过期，跳转登录
            showToast('登录已过期，请重新登录');
            setTimeout(() => {
                window.location.href = '/m/login';
            }, 1500);
            throw new Error('Unauthorized');
        } else {
            // 其他错误码才是真正的错误
            const errorMsg = data.msg || '请求失败';
            console.error('API Error:', errorMsg, data);
            throw new Error(errorMsg);
        }
    } catch (error) {
        // 如果error.message是'操作成功'，说明这不是真正的错误
        if (error.message === '操作成功') {
            console.warn('Ignored false error:', error);
            return; // 忽略这个"错误"
        }
        console.error('Request error:', error);
        throw error;
    }
}

// GET请求
async function get(url) {
    return request(url, { method: 'GET' });
}

// POST请求
async function post(url, data) {
    return request(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

// PUT请求
async function put(url, data) {
    return request(url, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

// DELETE请求
async function del(url) {
    return request(url, { method: 'DELETE' });
}

// 格式化日期
function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!date) return '-';
    
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hour = String(d.getHours()).padStart(2, '0');
    const minute = String(d.getMinutes()).padStart(2, '0');
    const second = String(d.getSeconds()).padStart(2, '0');
    
    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hour)
        .replace('mm', minute)
        .replace('ss', second);
}

// 格式化文件大小
function formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
}

// 防抖函数
function debounce(func, wait = 300) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 节流函数
function throttle(func, limit = 300) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// ================================
// 登录相关
// ================================

// 保存登录信息
function saveLoginInfo(token, user) {
    MobileApp.token = token;
    MobileApp.user = user;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
}

// 清除登录信息
function clearLoginInfo() {
    MobileApp.token = '';
    MobileApp.user = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
}

// 检查登录状态
function checkLogin() {
    if (!MobileApp.token) {
        window.location.href = '/m/login';
        return false;
    }
    return true;
}

// 退出登录
async function logout() {
    if (confirm('确定要退出登录吗？')) {
        try {
            await post(MobileApp.apiUrl + '/logout');
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            clearLoginInfo();
            window.location.href = '/m/login';
        }
    }
}

// ================================
// 图片处理
// ================================

// 压缩图片
function compressImage(file, maxWidth = 1024, quality = 0.8) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        
        reader.onload = (e) => {
            const img = new Image();
            img.src = e.target.result;
            
            img.onload = () => {
                const canvas = document.createElement('canvas');
                let width = img.width;
                let height = img.height;
                
                // 等比例缩放
                if (width > maxWidth) {
                    height = (maxWidth * height) / width;
                    width = maxWidth;
                }
                
                canvas.width = width;
                canvas.height = height;
                
                const ctx = canvas.getContext('2d');
                ctx.drawImage(img, 0, 0, width, height);
                
                canvas.toBlob((blob) => {
                    resolve(new File([blob], file.name, {
                        type: 'image/jpeg',
                        lastModified: Date.now()
                    }));
                }, 'image/jpeg', quality);
            };
            
            img.onerror = reject;
        };
        
        reader.onerror = reject;
    });
}

// 选择图片
function selectImage(callback) {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    
    input.onchange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            try {
                // 压缩图片
                const compressed = await compressImage(file);
                callback(compressed);
            } catch (error) {
                console.error('Image compression error:', error);
                callback(file); // 压缩失败则使用原图
            }
        }
    };
    
    input.click();
}

// 拍照
function captureImage(callback) {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.capture = 'environment'; // 使用后置摄像头
    
    input.onchange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            try {
                const compressed = await compressImage(file);
                callback(compressed);
            } catch (error) {
                console.error('Image compression error:', error);
                callback(file);
            }
        }
    };
    
    input.click();
}

// ================================
// 下拉刷新和上拉加载
// ================================

// 简单的下拉刷新实现
function enablePullRefresh(container, callback) {
    let startY = 0;
    let moveY = 0;
    let isRefreshing = false;
    
    container.addEventListener('touchstart', (e) => {
        if (container.scrollTop === 0) {
            startY = e.touches[0].pageY;
        }
    });
    
    container.addEventListener('touchmove', (e) => {
        if (isRefreshing) return;
        
        moveY = e.touches[0].pageY;
        const diff = moveY - startY;
        
        if (diff > 0 && container.scrollTop === 0) {
            e.preventDefault();
            // 可以在这里添加下拉提示UI
        }
    });
    
    container.addEventListener('touchend', async () => {
        const diff = moveY - startY;
        
        if (diff > 100 && !isRefreshing) {
            isRefreshing = true;
            showToast('刷新中...');
            
            try {
                await callback();
                showToast('刷新成功');
            } catch (error) {
                showToast('刷新失败');
            } finally {
                isRefreshing = false;
                startY = 0;
                moveY = 0;
            }
        }
    });
}

// ================================
// 页面初始化
// ================================

document.addEventListener('DOMContentLoaded', () => {
    // 返回按钮
    const backBtn = document.querySelector('.back-btn');
    if (backBtn) {
        backBtn.addEventListener('click', () => {
            if (window.history.length > 1) {
                window.history.back();
            } else {
                window.location.href = '/m/index';
            }
        });
    }
    
    // 底部导航高亮
    const currentPath = window.location.pathname;
    const footerItems = document.querySelectorAll('.footer-item');
    footerItems.forEach(item => {
        if (item.getAttribute('href') === currentPath) {
            item.classList.add('active');
        }
    });
    
    // 阻止双击缩放
    let lastTouchEnd = 0;
    document.addEventListener('touchend', (e) => {
        const now = Date.now();
        if (now - lastTouchEnd <= 300) {
            e.preventDefault();
        }
        lastTouchEnd = now;
    }, false);
});

// ================================
// API接口封装
// ================================

const API = {
    // 登录
    login: (username, password) => {
        return post(MobileApp.apiUrl + '/login', {
            username,
            password,
            code: '',
            uuid: ''
        });
    },
    
    // 获取用户信息
    getUserInfo: () => {
        return get(MobileApp.apiUrl + '/getInfo');
    },
    
    // 获取设备列表
    getDevices: (params = {}) => {
        const query = new URLSearchParams(params).toString();
        return get(MobileApp.apiUrl + '/iot/devices' + (query ? '?' + query : ''));
    },
    
    // 获取设备详情
    getDeviceDetail: (deviceId) => {
        return get(MobileApp.apiUrl + '/iot/device/' + deviceId);
    },
    
    // 获取设备统计
    getDeviceStatistics: () => {
        return get(MobileApp.apiUrl + '/iot/devices/statistics');
    },
    
    // 获取植被分析列表
    getVegetationList: (params = {}) => {
        const query = new URLSearchParams(params).toString();
        return get(MobileApp.apiUrl + '/vegetation/list' + (query ? '?' + query : ''));
    },
    
    // 上传图片分析
    analyzeImage: async (file, deviceId = null) => {
        const formData = new FormData();
        formData.append('file', file);
        if (deviceId) {
            formData.append('deviceId', deviceId);
        }
        
        const response = await fetch(MobileApp.apiUrl + '/vegetation/analyze', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${MobileApp.token}`
            },
            body: formData
        });
        
        return response.json();
    },
    
    // 获取植被统计
    getVegetationStatistics: () => {
        return get(MobileApp.apiUrl + '/vegetation/statistics');
    }
};

// 导出全局变量
window.MobileApp = MobileApp;
window.API = API;
window.showLoading = showLoading;
window.hideLoading = hideLoading;
window.showToast = showToast;
window.formatDate = formatDate;
window.selectImage = selectImage;
window.captureImage = captureImage;
window.logout = logout;
window.checkLogin = checkLogin;



