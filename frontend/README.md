# ShakeEat Frontend

当前前端已经切成 `uni-app` 小程序工程, 目标是让 HBuilderX 直接识别并编译到微信小程序。

## 打开方式

1. 使用 HBuilderX 打开 `D:/ShakeEat/frontend`
2. 等待工程索引完成
3. 点击 `运行 -> 运行到小程序模拟器 -> 微信开发者工具`

## 关键文件

- `pages.json`: 小程序页面入口配置
- `manifest.json`: uni-app / 微信小程序配置
- `App.vue`: 应用入口
- `pages/index/index.vue`: 主页面, 包含摇一摇、偏好、菜单、历史 4 个模块
- `pages/webview/index.vue`: 外部平台搜索页承载页
- `common/api.js`: 后端接口封装

## 本地联调

当前接口基地址写在 `common/config.js`:

```js
export const BASE_URL = 'http://127.0.0.1:8080';
```

如果微信开发者工具里请求不到本地后端, 处理方式有 2 个:

- 开发阶段先关闭“校验合法域名”
- 把 `BASE_URL` 改成你电脑的局域网 IP, 比如 `http://192.168.1.10:8080`

## 外链说明

微信小程序不能像普通网页那样随便直接打开外部链接。

当前处理是:

- 默认支持复制平台搜索链接
- 默认支持复制搜索词
- 提供 `web-view` 打开能力, 但目标域名需要你在小程序后台配置业务域名
