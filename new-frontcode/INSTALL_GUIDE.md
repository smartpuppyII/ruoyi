# 移动端前端安装指南

## 📦 文件复制说明

### 第一步：复制静态资源

将 `static/mobile/` 目录复制到RuoYi项目：

```bash
# Windows (PowerShell)
Copy-Item -Path "new-frontcode/static/mobile" -Destination "ruoyi-admin/src/main/resources/static/" -Recurse

# Linux/Mac
cp -r new-frontcode/static/mobile ruoyi-admin/src/main/resources/static/
```

**结果目录结构：**
```
ruoyi-admin/src/main/resources/static/
└── mobile/
    ├── css/
    │   └── mobile.css
    ├── js/
    │   └── mobile.js
    └── img/
```

### 第二步：复制模板文件

将 `templates/mobile/` 目录复制到RuoYi项目：

```bash
# Windows (PowerShell)
Copy-Item -Path "new-frontcode/templates/mobile" -Destination "ruoyi-admin/src/main/resources/templates/" -Recurse

# Linux/Mac
cp -r new-frontcode/templates/mobile ruoyi-admin/src/main/resources/templates/
```

**结果目录结构：**
```
ruoyi-admin/src/main/resources/templates/
└── mobile/
    ├── common/
    │   └── layout.html
    ├── device/
    │   ├── list.html
    │   └── detail.html
    ├── vegetation/
    │   ├── list.html
    │   └── analyze.html
    ├── index.html
    ├── login.html
    └── profile.html
```

### 第三步：复制Java控制器

将控制器文件复制到项目：

```bash
# Windows (PowerShell)
Copy-Item -Path "new-frontcode/java/controller/MobilePageController.java" -Destination "ruoyi-admin/src/main/java/com/ruoyi/web/controller/"

# Linux/Mac
cp new-frontcode/java/controller/MobilePageController.java ruoyi-admin/src/main/java/com/ruoyi/web/controller/
```

**结果位置：**
```
ruoyi-admin/src/main/java/com/ruoyi/web/controller/MobilePageController.java
```

---

## 🚀 快速启动

### 1. 确认后端API已配置

确保您已经完成了后端API的配置（参考 `MOBILE_BACKEND_SUMMARY.md`）。

关键文件应该已存在：
- `ruoyi-framework/src/main/java/com/ruoyi/framework/config/CorsConfig.java`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/mobile/MobileApiController.java`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/mobile/MobileIotController.java`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/mobile/MobileVegetationController.java`

### 2. 启动项目

```bash
cd ruoyi-admin
mvn spring-boot:run
```

或者使用已有的启动脚本：
```bash
# Windows
ry.bat

# Linux
./ry.sh
```

### 3. 访问移动端

打开浏览器或手机访问：

```
http://localhost:8080/m/login
```

**测试账号：**
- 用户名：`admin`
- 密码：`admin123`

---

## 📱 功能页面列表

| 页面 | 路径 | 说明 |
|------|------|------|
| 登录页 | `/m/login` | 移动端登录 |
| 首页 | `/m/index` | 统计数据和快捷入口 |
| 设备列表 | `/m/device/list` | IoT设备列表 |
| 设备详情 | `/m/device/detail?id=1` | 设备详细信息 |
| 植被分析列表 | `/m/vegetation/list` | 分析记录列表 |
| 拍照分析 | `/m/vegetation/analyze` | 上传图片分析 |
| 个人中心 | `/m/profile` | 用户信息和设置 |

---

## 🔧 配置说明

### 1. API地址配置

如果您的后端API不在默认地址，需要修改JS文件：

**编辑：** `static/mobile/js/mobile.js`

```javascript
const MobileApp = {
    baseUrl: window.location.origin,  // 根据实际情况修改
    apiUrl: window.location.origin + '/mobile/api',  // API地址
    ...
};
```

### 2. CDN资源配置

移动端使用了CDN资源（jQuery, Font Awesome），如果需要本地化：

1. 下载jQuery 3.6.4
2. 下载Font Awesome 6.4.0
3. 放到 `static/mobile/lib/` 目录
4. 修改HTML文件中的引用路径

### 3. 主题颜色配置

**编辑：** `static/mobile/css/mobile.css`

```css
:root {
    --primary-color: #007aff;  /* 主色调 */
    --success-color: #52c41a;  /* 成功色 */
    --warning-color: #faad14;  /* 警告色 */
    --danger-color: #f5222d;   /* 危险色 */
    ...
}
```

---

## 📂 完整文件清单

### 静态资源 (3个文件)
```
static/mobile/
├── css/
│   └── mobile.css          ✅ 移动端样式
├── js/
│   └── mobile.js           ✅ 移动端脚本
└── img/
    └── (图片资源，可选)
```

### 模板文件 (9个文件)
```
templates/mobile/
├── common/
│   └── layout.html         ✅ 布局模板
├── device/
│   ├── list.html          ✅ 设备列表
│   └── detail.html        📝 设备详情(待补充)
├── vegetation/
│   ├── list.html          📝 分析列表(待补充)
│   └── analyze.html       ✅ 图片分析
├── index.html             ✅ 首页
├── login.html             ✅ 登录页
└── profile.html           📝 个人中心(待补充)
```

### Java文件 (1个文件)
```
java/controller/
└── MobilePageController.java  ✅ 页面控制器
```

---

## ✅ 验证安装

### 1. 检查文件

确认以下文件已正确复制：

```bash
# 检查静态资源
ls ruoyi-admin/src/main/resources/static/mobile/css/mobile.css
ls ruoyi-admin/src/main/resources/static/mobile/js/mobile.js

# 检查模板
ls ruoyi-admin/src/main/resources/templates/mobile/login.html
ls ruoyi-admin/src/main/resources/templates/mobile/index.html

# 检查控制器
ls ruoyi-admin/src/main/java/com/ruoyi/web/controller/MobilePageController.java
```

### 2. 测试访问

启动项目后，依次测试以下页面：

1. ✅ 登录页: `http://localhost:8080/m/login`
2. ✅ 首页: `http://localhost:8080/m/index`
3. ✅ 设备列表: `http://localhost:8080/m/device/list`
4. ✅ 拍照分析: `http://localhost:8080/m/vegetation/analyze`

### 3. 功能测试

- [ ] 登录功能正常
- [ ] 首页统计数据显示
- [ ] 设备列表加载
- [ ] 搜索和筛选功能
- [ ] 图片上传分析
- [ ] 底部导航切换

---

## 🐛 常见问题

### Q1: 访问 `/m/login` 显示404

**原因：** MobilePageController未正确加载

**解决：**
1. 检查控制器文件是否在正确位置
2. 检查包名是否为 `com.ruoyi.web.controller`
3. 重启项目

### Q2: 页面样式错乱

**原因：** CSS文件未加载

**解决：**
1. 检查 `mobile.css` 是否在 `static/mobile/css/` 目录
2. 清除浏览器缓存
3. 检查浏览器控制台是否有404错误

### Q3: API请求失败

**原因：** 后端API未配置或CORS未开启

**解决：**
1. 确认后端API控制器已创建
2. 检查 `CorsConfig.java` 是否存在
3. 查看浏览器Console的错误信息

### Q4: 图片上传失败

**原因：** 文件上传路径未配置

**解决：**
1. 检查 `application.yml` 中的 `ruoyi.profile` 配置
2. 确保上传目录存在且有写权限
3. 检查文件大小限制

---

## 📱 移动端调试

### 使用Chrome DevTools

1. 打开Chrome浏览器
2. 按 `F12` 打开开发者工具
3. 点击设备模拟按钮（手机图标）
4. 选择设备型号（iPhone 12 Pro等）

### 使用手机访问

1. 确保手机和电脑在同一网络
2. 获取电脑IP地址
   ```bash
   # Windows
   ipconfig
   
   # Linux/Mac
   ifconfig
   ```
3. 手机浏览器访问: `http://电脑IP:8080/m/login`

---

## 🎨 界面截图

访问以下页面查看效果：

- 登录页: `/m/login`
- 首页: `/m/index`
- 设备列表: `/m/device/list`
- 植被分析: `/m/vegetation/analyze`

---

## 📚 相关文档

- [后端改造总结](../MOBILE_BACKEND_SUMMARY.md)
- [API接口文档](../MOBILE_API_GUIDE.md)
- [部署指南](../DEPLOYMENT_GUIDE.md)
- [快速开始](../README_QUICKSTART.md)

---

## 💡 下一步

安装完成后，您可以：

1. 体验基础功能
2. 根据需求定制界面
3. 添加更多功能页面
4. 优化用户体验
5. 考虑打包成原生APP (使用Ionic)

---

**🎉 安装完成！开始使用RuoYi移动端吧！**












