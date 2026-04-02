# ShakeEat

一个“摇一摇决定吃什么”的单用户应用。

后端基于 Spring Boot 3 + Java 17，前端目录 `frontend/` 同时包含：

- `uni-app` / 微信小程序工程（当前主前端）
- React + Vite 管理台/原型界面

项目目标是把“今晚吃什么”从纠结变成可执行：支持菜单管理、标签管理、偏好设置、摇一摇推荐、历史记录、推荐理由与外卖跳转辅助。

## 功能特性

- 摇一摇推荐吃什么
- 支持 4 种快捷模式
  - 懒人外卖
  - 清冰箱
  - 解馋重口
  - 轻食健康
- 菜单库 CRUD
- 标签管理
- 偏好设置（喜欢 / 避免 / 预算）
- 历史记录列表、详情、统计
- 每日次数限制
- AI 推荐文案 + 失败兜底
- 外卖搜索链接与步骤快照保存

## 技术栈

### 后端

- Java 17
- Spring Boot 3.5.12
- MyBatis-Plus 3.5.7
- MySQL
- Redis
- Maven Wrapper

### 前端

- uni-app / 微信小程序
- React 18
- Vite 5

## 目录结构

```text
.
├─ src/                      # Spring Boot 后端
├─ sql/                      # 数据库初始化与增量脚本
├─ frontend/                 # 前端工程
│  ├─ App.vue                # uni-app 入口
│  ├─ main.js
│  ├─ pages/**               # uni-app 页面
│  ├─ common/**              # uni-app 公共配置与 API
│  ├─ src/**                 # React/Vite 原型代码
│  ├─ package.json
│  └─ vite.config.js
├─ pom.xml
├─ mvnw
└─ README.md
```

## 核心业务说明

### 单用户模型

当前版本是单用户应用，默认使用固定用户：

- `user_id = 1`

未引入账号体系和多用户隔离。

### 摇一摇推荐逻辑

核心流程：

1. 校验每日次数上限
2. 读取用户偏好
3. 加载启用菜单与标签关系
4. 排除避免标签命中的食物
5. 结合模式、喜欢标签、预算、最近历史计算权重
6. 尽量避免与最近一次结果连续重复
7. 生成 AI 文案，失败则使用模板兜底
8. 写入历史记录及展示快照

### 历史快照

历史记录并不是简单查当前菜品，而是会保存：

- `steps_snapshot`
- `order_url_snapshot`
- `reference_price_snapshot`
- `tag_snapshot`
- `score_snapshot`

这样即使后续菜名、标签、外卖链接变更，旧历史也尽量保持当时的展示效果。

## 环境要求

- JDK 17
- MySQL 8+
- Redis
- Node.js 18+
- HBuilderX（如果运行 uni-app / 微信小程序）
- 微信开发者工具（如果调试小程序）

## 本地配置

默认配置在：`src/main/resources/application.yml`

### 后端默认依赖

- MySQL: `127.0.0.1:3306/shakeeat`
- 用户名: `root`
- 密码: `root`
- Redis: `127.0.0.1:6379`
- Redis 密码: `123456`
- 服务端口: `8080`
- Ollama: `http://127.0.0.1:11434`

### AI 默认配置

- `app.ai.ollama.enabled: true`
- `app.ai.deepseek.enabled: false`

说明：AI 是增强项，不是主流程依赖。即使 AI 不可用，摇一摇也应继续返回兜底文案。

## 数据库初始化

按顺序执行 `sql/` 目录脚本：

```bash
mysql -h 127.0.0.1 -P 3306 -u root -proot < sql/0001_init_shakeeat.sql
mysql -h 127.0.0.1 -P 3306 -u root -proot < sql/0003_seed_demo_data.sql
mysql -h 127.0.0.1 -P 3306 -u root -proot < sql/0004_add_order_url.sql
mysql -h 127.0.0.1 -P 3306 -u root -proot < sql/0005_add_history_snapshots.sql
mysql -h 127.0.0.1 -P 3306 -u root -proot < sql/0006_add_reject_feedback.sql
```

补充说明：

- `sql/0002_comment_shakeeat.sql` 主要用于补充注释，不影响核心功能。

## 启动方式

### 1. 启动后端

在仓库根目录执行：

```bash
./mvnw spring-boot:run
```

### 2. 运行后端测试

```bash
./mvnw test
```

单测示例：

```bash
./mvnw -Dtest=ShakeServiceImplTest test
./mvnw -Dtest=ShakeServiceImplTest#shakeShouldReturnFallbackResult test
```

### 3. 打包后端

```bash
./mvnw clean package
```

## 前端运行说明

## uni-app / 微信小程序（当前主前端）

使用 HBuilderX 打开 `frontend/` 目录：

1. 打开 `frontend/`
2. 运行 -> 运行到小程序模拟器 -> 微信开发者工具

联调后端地址在：

- `frontend/common/config.js`

默认值：

```js
export const BASE_URL = 'http://127.0.0.1:8080';
```

如果小程序访问不到本地服务：

- 开发阶段关闭合法域名校验
- 或把 `BASE_URL` 改成你电脑的局域网 IP

## React / Vite 原型界面

在 `frontend/` 目录执行：

```bash
npm install
npm run dev
```

构建与预览：

```bash
npm run build
npm run preview
```

Vite 开发服务器会将 `/api` 代理到：

- `http://127.0.0.1:8080`

## API 返回约定

所有后端接口统一返回：

```json
{
  "success": true,
  "message": "ok",
  "data": {}
}
```

前端会按 `success / message / data` 解包。

## 主要模块

### 后端模块

- `module/food`：菜单管理、导入导出、启停
- `module/tag`：标签字典管理
- `module/preference`：用户偏好
- `module/shake`：摇一摇主流程、确认/拒绝、次数额度
- `module/history`：历史分页、详情、统计
- `support/ai`：Ollama / DeepSeek 文案生成
- `support/order`：外卖链接解析
- `support/template`：AI 失败兜底文案

### 前端关键入口

#### uni-app

- `frontend/App.vue`
- `frontend/main.js`
- `frontend/pages/index/index.vue`
- `frontend/pages/webview/index.vue`
- `frontend/common/api.js`

#### React/Vite

- `frontend/src/main.jsx`
- `frontend/src/App.jsx`
- `frontend/src/api.js`
- `frontend/src/components/**`

## 测试关注点

当前测试主要集中在：

- `ShakeServiceImplTest`
- `HistoryServiceImplTest`
- `FoodServiceImplTest`
- `TagServiceImplTest`
- `OllamaAiTipServiceTest`

如果改动推荐、历史、AI 兜底、标签或偏好权重，建议优先更新这些测试。

## 注意事项

- 当前是单用户模型，不要默认扩展为多用户设计。
- 预算是轻量降权，不是硬过滤。
- AI 文案失败不能阻断主流程。
- 改前端前先区分：要改的是 `uni-app` 还是 `React/Vite`。

## 仓库地址

- GitHub: <https://github.com/whtiewangpenghui/ShakeEat.git>
