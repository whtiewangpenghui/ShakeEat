USE shakeeat;

-- 这份脚本用于初始化一批可直接联调的测试数据。
-- 约定:
-- 1. 食物名称保持唯一, 可重复执行
-- 2. 关系表通过食物名和标签名回填, 不依赖手写主键

INSERT INTO food_item (
    name,
    emoji,
    steps,
    image_url,
    order_url,
    weight,
    reference_price,
    enabled,
    deleted
) VALUES
    ('黄焖鸡米饭', '🍗', '1. 点击下方美团搜索入口 2. 搜索"黄焖鸡米饭 22元" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%BB%84%E7%84%96%E9%B8%A1%E7%B1%B3%E9%A5%AD%2022%E5%85%83', 120, 22.00, 1, 0),
    ('番茄鸡蛋面', '🍜', '1. 煮面 2. 炒番茄鸡蛋 3. 合并出锅', NULL, NULL, 95, 12.00, 1, 0),
    ('轻食鸡胸沙拉', '🥗', '1. 准备生菜和鸡胸肉 2. 淋酱拌匀 3. 直接开吃', NULL, NULL, 90, 28.00, 1, 0),
    ('麻辣香锅外卖', '🌶️', '1. 点击下方美团搜索入口 2. 搜索"麻辣香锅外卖 35元" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%BA%BB%E8%BE%A3%E9%A6%99%E9%94%85%E5%A4%96%E5%8D%96%2035%E5%85%83', 110, 35.00, 1, 0),
    ('冰箱炒饭', '🍚', '1. 翻冰箱找剩饭和配菜 2. 起锅翻炒 3. 调味出锅', NULL, NULL, 100, 10.00, 1, 0),
    ('清汤馄饨', '🥣', '1. 水开下馄饨 2. 调汤底 3. 出锅开吃', NULL, NULL, 80, 16.00, 1, 0),
    ('寿司外卖', '🍣', '1. 点击下方美团搜索入口 2. 搜索"寿司外卖 32元" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E5%AF%BF%E5%8F%B8%E5%A4%96%E5%8D%96%2032%E5%85%83', 88, 32.00, 1, 0),
    ('炸鸡汉堡外卖', '🍔', '1. 点击下方美团搜索入口 2. 搜索"炸鸡汉堡外卖 29元" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1%E5%A4%96%E5%8D%96%2029%E5%85%83', 105, 29.00, 1, 0),
    ('青椒火腿炒饭', '🍛', '1. 翻出剩饭和火腿 2. 下锅翻炒 3. 出锅装盘', NULL, NULL, 92, 11.00, 1, 0),
    ('火腿鸡蛋卷饼', '🌯', '1. 整理冰箱食材 2. 煎蛋卷饼 3. 卷好开吃', NULL, NULL, 86, 13.00, 1, 0),
    ('老干妈拌面', '🥡', '1. 煮面 2. 调老干妈酱汁 3. 拌匀开吃', NULL, NULL, 98, 14.00, 1, 0),
    ('酸辣粉外卖', '🍲', '1. 点击下方美团搜索入口 2. 搜索"酸辣粉外卖 24元" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%85%B8%E8%BE%A3%E7%B2%89%E5%A4%96%E5%8D%96%2024%E5%85%83', 102, 24.00, 1, 0),
    ('凉拌鸡丝荞麦面', '🥙', '1. 煮荞麦面 2. 拌鸡丝和蔬菜 3. 淋酱完成', NULL, NULL, 87, 21.00, 1, 0),
    ('虾仁西兰花饭', '🍱', '1. 焯西兰花 2. 煎虾仁 3. 配米饭装盘', NULL, NULL, 89, 26.00, 1, 0)
ON DUPLICATE KEY UPDATE
    emoji = VALUES(emoji),
    steps = VALUES(steps),
    order_url = VALUES(order_url),
    weight = VALUES(weight),
    reference_price = VALUES(reference_price),
    enabled = VALUES(enabled),
    deleted = VALUES(deleted),
    update_time = CURRENT_TIMESTAMP;

UPDATE food_item
SET order_url = CASE name
    WHEN '黄焖鸡米饭' THEN 'https://waimai.meituan.com/?keyword=%E9%BB%84%E7%84%96%E9%B8%A1%E7%B1%B3%E9%A5%AD%2022%E5%85%83'
    WHEN '麻辣香锅外卖' THEN 'https://waimai.meituan.com/?keyword=%E9%BA%BB%E8%BE%A3%E9%A6%99%E9%94%85%E5%A4%96%E5%8D%96%2035%E5%85%83'
    WHEN '寿司外卖' THEN 'https://waimai.meituan.com/?keyword=%E5%AF%BF%E5%8F%B8%E5%A4%96%E5%8D%96%2032%E5%85%83'
    WHEN '炸鸡汉堡外卖' THEN 'https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1%E5%A4%96%E5%8D%96%2029%E5%85%83'
    WHEN '酸辣粉外卖' THEN 'https://waimai.meituan.com/?keyword=%E9%85%B8%E8%BE%A3%E7%B2%89%E5%A4%96%E5%8D%96%2024%E5%85%83'
    ELSE order_url
END
WHERE name IN ('黄焖鸡米饭', '麻辣香锅外卖', '寿司外卖', '炸鸡汉堡外卖', '酸辣粉外卖');

UPDATE food_item
SET steps = CASE name
    WHEN '黄焖鸡米饭' THEN '1. 点击下方美团搜索入口 2. 搜索"黄焖鸡米饭 22元" 3. 对比店铺价格后下单'
    WHEN '麻辣香锅外卖' THEN '1. 点击下方美团搜索入口 2. 搜索"麻辣香锅外卖 35元" 3. 对比店铺价格后下单'
    WHEN '寿司外卖' THEN '1. 点击下方美团搜索入口 2. 搜索"寿司外卖 32元" 3. 对比店铺价格后下单'
    WHEN '炸鸡汉堡外卖' THEN '1. 点击下方美团搜索入口 2. 搜索"炸鸡汉堡外卖 29元" 3. 对比店铺价格后下单'
    WHEN '酸辣粉外卖' THEN '1. 点击下方美团搜索入口 2. 搜索"酸辣粉外卖 24元" 3. 对比店铺价格后下单'
    ELSE steps
END
WHERE name IN ('黄焖鸡米饭', '麻辣香锅外卖', '寿司外卖', '炸鸡汉堡外卖', '酸辣粉外卖');

-- 先清理测试食物已有关系, 再重新回填, 保证脚本可重复执行。
DELETE rel
FROM food_item_tag_rel rel
INNER JOIN food_item fi ON fi.id = rel.food_id
WHERE fi.name IN (
    '黄焖鸡米饭',
    '番茄鸡蛋面',
    '轻食鸡胸沙拉',
    '麻辣香锅外卖',
    '冰箱炒饭',
    '清汤馄饨',
    '寿司外卖',
    '炸鸡汉堡外卖',
    '青椒火腿炒饭',
    '火腿鸡蛋卷饼',
    '老干妈拌面',
    '酸辣粉外卖',
    '凉拌鸡丝荞麦面',
    '虾仁西兰花饭'
);

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '外卖'
WHERE fi.name IN ('黄焖鸡米饭', '麻辣香锅外卖', '寿司外卖', '炸鸡汉堡外卖', '酸辣粉外卖');

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '辣'
WHERE fi.name IN ('麻辣香锅外卖', '黄焖鸡米饭', '老干妈拌面', '酸辣粉外卖');

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '轻食'
WHERE fi.name IN ('轻食鸡胸沙拉', '凉拌鸡丝荞麦面', '虾仁西兰花饭');

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '健康'
WHERE fi.name IN ('轻食鸡胸沙拉', '清汤馄饨', '寿司外卖', '凉拌鸡丝荞麦面', '虾仁西兰花饭');

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '清冰箱'
WHERE fi.name IN ('冰箱炒饭', '番茄鸡蛋面', '青椒火腿炒饭', '火腿鸡蛋卷饼');

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '10分钟'
WHERE fi.name IN ('黄焖鸡米饭', '番茄鸡蛋面', '清汤馄饨', '青椒火腿炒饭', '火腿鸡蛋卷饼', '老干妈拌面');

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '米饭'
WHERE fi.name IN ('黄焖鸡米饭', '冰箱炒饭', '青椒火腿炒饭', '虾仁西兰花饭');

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '面食'
WHERE fi.name IN ('番茄鸡蛋面', '清汤馄饨', '老干妈拌面', '酸辣粉外卖', '凉拌鸡丝荞麦面');

INSERT INTO food_item_tag_rel (food_id, tag_id)
SELECT fi.id, ft.id
FROM food_item fi
INNER JOIN food_tag ft ON ft.name = '夜宵'
WHERE fi.name IN ('炸鸡汉堡外卖', '酸辣粉外卖');
