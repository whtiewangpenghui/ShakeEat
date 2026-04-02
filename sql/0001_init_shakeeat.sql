CREATE DATABASE IF NOT EXISTS shakeeat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shakeeat;

CREATE TABLE IF NOT EXISTS food_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name VARCHAR(100) NOT NULL COMMENT '食物名称, 如黄焖鸡米饭',
    emoji VARCHAR(20) DEFAULT NULL COMMENT '展示用 emoji',
    steps TEXT DEFAULT NULL COMMENT '基础步骤说明, 用于结果页展示',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '食物图片地址',
    order_url VARCHAR(500) DEFAULT NULL COMMENT '外卖或购买链接, 用于结果页直达下单',
    weight INT NOT NULL DEFAULT 100 COMMENT '基础权重, 越大越容易被摇中',
    reference_price DECIMAL(10, 2) DEFAULT NULL COMMENT '参考价格, V1 只做轻度预算修正',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用, 1 启用, 0 停用',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记, 0 未删除, 1 已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_food_item_name (name)
) COMMENT='菜单库主表, 保存可参与推荐的食物';

CREATE TABLE IF NOT EXISTS food_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称, 如外卖, 辣, 轻食',
    icon VARCHAR(50) DEFAULT NULL COMMENT '标签图标或 emoji',
    tag_type VARCHAR(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '标签类型, SYSTEM 系统标签, CUSTOM 自定义标签',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序值, 越小越靠前',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用, 1 启用, 0 停用',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记, 0 未删除, 1 已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_food_tag_name (name)
) COMMENT='标签字典表, 用于模式筛选和偏好设置';

CREATE TABLE IF NOT EXISTS food_item_tag_rel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    food_id BIGINT NOT NULL COMMENT '食物 ID',
    tag_id BIGINT NOT NULL COMMENT '标签 ID',
    UNIQUE KEY uk_food_item_tag (food_id, tag_id),
    KEY idx_food_item_tag_rel_tag_id (tag_id)
) COMMENT='食物与标签关系表, 一个食物可关联多个标签';

CREATE TABLE IF NOT EXISTS user_preference (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID, 当前 V1 固定为 1',
    favorite_tag_ids JSON DEFAULT NULL COMMENT '喜欢标签 ID 数组, 用于提高命中权重',
    avoid_tag_ids JSON DEFAULT NULL COMMENT '避免标签 ID 数组, 用于直接排除候选',
    max_budget DECIMAL(10, 2) DEFAULT NULL COMMENT '最高预算, V1 不做硬过滤',
    last_mode VARCHAR(20) DEFAULT 'LAZY' COMMENT '上次使用模式, 用于首页默认态恢复',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_preference_user_id (user_id)
) COMMENT='用户偏好表, 当前单用户只有一条记录';

CREATE TABLE IF NOT EXISTS shake_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID, 当前 V1 固定为 1',
    food_id BIGINT NOT NULL COMMENT '命中的食物 ID',
    food_name VARCHAR(100) NOT NULL COMMENT '冗余食物名称, 防止后续改名影响历史展示',
    mode VARCHAR(20) NOT NULL COMMENT '摇一摇模式, 如 LAZY, CLEAR, SPICY, LIGHT',
    trigger_type VARCHAR(20) NOT NULL COMMENT '触发方式, SHAKE 或 CLICK',
    result_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '结果状态, PENDING, ACCEPTED, REJECTED',
    ai_tip TEXT DEFAULT NULL COMMENT 'AI 或兜底文案',
    ai_source VARCHAR(20) NOT NULL DEFAULT 'FALLBACK' COMMENT '文案来源, OLLAMA 或 FALLBACK',
    prompt_snapshot TEXT DEFAULT NULL COMMENT '生成文案时的 prompt 快照, 便于排查',
    score_snapshot JSON DEFAULT NULL COMMENT '命中时的权重明细快照, 便于调权重',
    shake_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '摇出结果的时间',
    confirm_time DATETIME DEFAULT NULL COMMENT '用户点击就这样吃的确认时间',
    KEY idx_shake_history_user_time (user_id, shake_time),
    KEY idx_shake_history_food_id (food_id)
) COMMENT='摇一摇历史表, 每次摇一摇都会落一条记录';

INSERT INTO food_tag (name, icon, tag_type, sort)
VALUES
    ('外卖', '🍔', 'SYSTEM', 10),
    ('清冰箱', '🥬', 'SYSTEM', 20),
    ('辣', '🌶️', 'SYSTEM', 30),
    ('轻食', '🥗', 'SYSTEM', 40),
    ('健康', '🍃', 'SYSTEM', 50),
    ('10分钟', '⏱️', 'SYSTEM', 60),
    ('米饭', '🍚', 'SYSTEM', 70),
    ('面食', '🍜', 'SYSTEM', 80),
    ('夜宵', '🌙', 'SYSTEM', 90)
ON DUPLICATE KEY UPDATE
    icon = VALUES(icon),
    sort = VALUES(sort),
    enabled = 1,
    deleted = 0;

INSERT INTO user_preference (user_id, favorite_tag_ids, avoid_tag_ids, max_budget, last_mode)
VALUES (1, JSON_ARRAY(), JSON_ARRAY(), 50.00, 'LAZY')
ON DUPLICATE KEY UPDATE
    update_time = CURRENT_TIMESTAMP;

