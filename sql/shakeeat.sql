/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80300
 Source Host           : localhost:3306
 Source Schema         : shakeeat

 Target Server Type    : MySQL
 Target Server Version : 80300
 File Encoding         : 65001

 Date: 02/04/2026 17:10:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for food_item
-- ----------------------------
DROP TABLE IF EXISTS `food_item`;
CREATE TABLE `food_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '食物名称, 如黄焖鸡米饭',
  `emoji` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '展示用 emoji',
  `steps` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '基础步骤说明, 用于结果页展示',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '食物图片地址',
  `order_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '外卖或购买链接, 用于结果页直达下单',
  `weight` int NOT NULL DEFAULT 100 COMMENT '基础权重, 越大越容易被摇中',
  `reference_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考价格, V1 只做轻度预算修正',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用, 1 启用, 0 停用',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记, 0 未删除, 1 已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_food_item_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 133 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单库主表, 保存可参与推荐的食物' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_item
-- ----------------------------
INSERT INTO `food_item` VALUES (1, '黄焖鸡米饭', '🍗', '1. 点击下方美团搜索入口 2. 搜索\"黄焖鸡米饭 22元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%BB%84%E7%84%96%E9%B8%A1%E7%B1%B3%E9%A5%AD%2022%E5%85%83', 120, 22.00, 1, 0, '2026-03-24 21:45:12', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (2, '番茄鸡蛋面', '🍜', '1. 煮面 2. 炒番茄鸡蛋 3. 合并出锅', NULL, NULL, 95, 12.00, 1, 0, '2026-03-24 21:45:12', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (3, '轻食鸡胸沙拉', '🥗', '1. 准备生菜和鸡胸肉 2. 淋酱拌匀 3. 直接开吃', NULL, NULL, 90, 28.00, 1, 0, '2026-03-24 21:45:12', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (4, '麻辣香锅外卖', '🌶️', '1. 点击下方美团搜索入口 2. 搜索\"麻辣香锅外卖 35元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%BA%BB%E8%BE%A3%E9%A6%99%E9%94%85%E5%A4%96%E5%8D%96%2035%E5%85%83', 110, 35.00, 1, 0, '2026-03-24 21:45:12', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (5, '冰箱炒饭', '🍚', '1. 翻冰箱找剩饭和配菜 2. 起锅翻炒 3. 调味出锅', NULL, NULL, 100, 10.00, 1, 0, '2026-03-24 21:45:12', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (6, '清汤馄饨', '🥣', '1. 水开下馄饨 2. 调汤底 3. 出锅开吃', NULL, NULL, 80, 16.00, 1, 0, '2026-03-24 21:45:12', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (7, '寿司外卖', '🍣', '1. 点击下方美团搜索入口 2. 搜索\"寿司外卖 32元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E5%AF%BF%E5%8F%B8%E5%A4%96%E5%8D%96%2032%E5%85%83', 88, 32.00, 1, 0, '2026-03-25 00:44:04', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (8, '炸鸡汉堡外卖', '🍔', '1. 点击下方美团搜索入口 2. 搜索\"炸鸡汉堡外卖 29元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1%E5%A4%96%E5%8D%96%2029%E5%85%83', 105, 29.00, 1, 0, '2026-03-25 00:44:04', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (9, '青椒火腿炒饭', '🍛', '1. 翻出剩饭和火腿 2. 下锅翻炒 3. 出锅装盘', NULL, NULL, 92, 11.00, 1, 0, '2026-03-25 00:44:04', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (10, '火腿鸡蛋卷饼', '🌯', '1. 整理冰箱食材 2. 煎蛋卷饼 3. 卷好开吃', NULL, NULL, 86, 13.00, 1, 0, '2026-03-25 00:44:04', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (11, '老干妈拌面', '🥡', '1. 煮面 2. 调老干妈酱汁 3. 拌匀开吃', NULL, NULL, 98, 14.00, 1, 0, '2026-03-25 00:44:04', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (12, '酸辣粉外卖', '🍲', '1. 点击下方美团搜索入口 2. 搜索\"酸辣粉外卖 24元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%85%B8%E8%BE%A3%E7%B2%89%E5%A4%96%E5%8D%96%2024%E5%85%83', 102, 24.00, 1, 0, '2026-03-25 00:44:04', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (13, '凉拌鸡丝荞麦面', '🥙', '1. 煮荞麦面 2. 拌鸡丝和蔬菜 3. 淋酱完成', NULL, NULL, 87, 21.00, 1, 0, '2026-03-25 00:44:04', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (14, '虾仁西兰花饭', '🍱', '1. 焯西兰花 2. 煎虾仁 3. 配米饭装盘', NULL, NULL, 89, 26.00, 1, 0, '2026-03-25 00:44:04', '2026-03-25 01:35:11');
INSERT INTO `food_item` VALUES (50, '土豆牛腩饭', '🍛', '1. 炖牛腩 2. 加土豆块 3. 盖饭完成', NULL, NULL, 115, 28.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (51, '蒜蓉生蚝', '🦪', '1. 清洗生蚝 2. 调蒜蓉酱 3. 烤制或蒸制', NULL, NULL, 85, 38.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (52, '牛肉炒河粉', '🍝', '1. 腌制牛肉 2. 炒河粉 3. 混合翻炒', NULL, NULL, 96, 24.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (53, '咖喱鸡肉饭', '🍛', '1. 炒鸡肉 2. 加咖喱块 3. 炖煮收汁', NULL, NULL, 98, 20.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (54, '酸菜鱼外卖', '🐟', '1. 点击下方美团搜索入口 2. 搜索\"酸菜鱼外卖 45元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%85%B8%E8%8F%9C%E9%B1%BC%E5%A4%96%E5%8D%96%2045%E5%85%83', 108, 45.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (55, '手抓饼', '🌮', '1. 煎饼皮 2. 加鸡蛋火腿 3. 卷起开吃', NULL, NULL, 82, 9.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (56, '麻辣烫外卖', '🥘', '1. 点击下方美团搜索入口 2. 搜索\"麻辣烫外卖 28元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%BA%BB%E8%BE%A3%E7%83%AB%E5%A4%96%E5%8D%96%2028%E5%85%83', 99, 28.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (57, '蛋炒饭', '🍳', '1. 打蛋炒散 2. 加米饭翻炒 3. 调味出锅', NULL, NULL, 88, 8.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (58, '葱油拌面', '🍜', '1. 煮面 2. 炸葱油 3. 拌匀开吃', NULL, NULL, 84, 10.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (59, '烤肉拌饭外卖', '🍱', '1. 点击下方美团搜索入口 2. 搜索\"烤肉拌饭外卖 25元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%83%A4%E8%82%89%E6%8B%8C%E9%A5%AD%E5%A4%96%E5%8D%96%2025%E5%85%83', 101, 25.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (60, '蒜蓉空心菜', '🥬', '1. 空心菜洗净 2. 爆香蒜蓉 3. 快速翻炒', NULL, NULL, 78, 12.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (61, '红烧排骨面', '🍜', '1. 炖排骨 2. 煮面条 3. 组合加汤', NULL, NULL, 104, 26.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (62, '韩式炸鸡外卖', '🍗', '1. 点击下方美团搜索入口 2. 搜索\"韩式炸鸡外卖 35元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%9F%A9%E5%BC%8F%E7%82%B8%E9%B8%A1%E5%A4%96%E5%8D%96%2035%E5%85%83', 106, 35.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (63, '皮蛋瘦肉粥', '🥣', '1. 煮粥底 2. 加皮蛋瘦肉 3. 调味出锅', NULL, NULL, 83, 15.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (64, '肉末茄子', '🍆', '1. 茄子切条 2. 炒肉末 3. 炖煮入味', NULL, NULL, 91, 18.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (65, '披萨外卖', '🍕', '1. 点击下方美团搜索入口 2. 搜索\"披萨外卖 48元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E6%8A%AB%E8%90%A8%E5%A4%96%E5%8D%96%2048%E5%85%83', 112, 48.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (66, '凉皮', '🥗', '1. 准备凉皮 2. 加黄瓜丝 3. 淋麻酱辣椒油', NULL, NULL, 79, 12.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (67, '口水鸡', '🐔', '1. 煮鸡腿 2. 调红油酱汁 3. 切块淋汁', NULL, NULL, 94, 22.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (68, '牛肉拉面外卖', '🍜', '1. 点击下方美团搜索入口 2. 搜索\"牛肉拉面外卖 20元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%89%9B%E8%82%89%E6%8B%89%E9%9D%A2%E5%A4%96%E5%8D%96%2020%E5%85%83', 97, 20.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (69, '水煮鱼片', '🐟', '1. 片鱼片 2. 炒底料 3. 煮鱼片泼油', NULL, NULL, 109, 42.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (70, '三明治', '🥪', '1. 准备面包生菜火腿 2. 叠放 3. 切半开吃', NULL, NULL, 81, 14.00, 1, 0, '2026-03-26 21:11:49', '2026-03-26 21:11:49');
INSERT INTO `food_item` VALUES (71, '卤肉饭', '🍛', '1. 炖卤肉 2. 煮鸡蛋 3. 盖饭', NULL, NULL, 103, 19.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (72, '烧烤外卖', '🍢', '1. 点击下方美团搜索入口 2. 搜索\"烧烤外卖 50元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%83%A7%E7%83%A4%E5%A4%96%E5%8D%96%2050%E5%85%83', 118, 50.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (73, '玉米排骨汤', '🍲', '1. 排骨焯水 2. 加玉米炖煮 3. 调味出锅', NULL, NULL, 86, 30.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (74, '炒方便面', '🍝', '1. 煮方便面 2. 加鸡蛋青菜翻炒 3. 调味出锅', NULL, NULL, 93, 8.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (75, '糖醋里脊', '🥩', '1. 里脊肉裹糊 2. 炸至金黄 3. 调糖醋汁翻炒', NULL, NULL, 100, 28.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (76, '饺子外卖', '🥟', '1. 点击下方美团搜索入口 2. 搜索\"饺子外卖 18元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%A5%BA%E5%AD%90%E5%A4%96%E5%8D%96%2018%E5%85%83', 92, 18.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (77, '麻婆豆腐', '🍲', '1. 豆腐焯水 2. 炒肉末豆瓣酱 3. 煮豆腐勾芡', NULL, NULL, 89, 15.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (78, '烤冷面', '🍜', '1. 铁板烤冷面 2. 加鸡蛋火腿 3. 刷酱卷起', NULL, NULL, 87, 12.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (79, '毛血旺外卖', '🌶️', '1. 点击下方美团搜索入口 2. 搜索\"毛血旺外卖 48元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E6%AF%9B%E8%A1%80%E6%97%BA%E5%A4%96%E5%8D%96%2048%E5%85%83', 113, 48.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (80, '蛋包饭', '🍳', '1. 炒饭 2. 摊蛋皮 3. 包饭淋酱', NULL, NULL, 96, 18.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (81, '白灼虾', '🦐', '1. 虾洗净 2. 水中加姜片煮熟 3. 调蘸料', NULL, NULL, 84, 32.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (82, '鸡公煲外卖', '🍗', '1. 点击下方美团搜索入口 2. 搜索\"鸡公煲外卖 30元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%B8%A1%E5%85%AC%E7%85%B2%E5%A4%96%E5%8D%96%2030%E5%85%83', 107, 30.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (83, '番茄龙利鱼', '🐟', '1. 龙利鱼切块 2. 番茄炒出汁 3. 煮鱼片', NULL, NULL, 90, 26.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (84, '扬州炒饭', '🍛', '1. 备虾仁火腿丁 2. 炒饭 3. 加青豆玉米', NULL, NULL, 94, 20.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (85, '部队火锅外卖', '🍲', '1. 点击下方美团搜索入口 2. 搜索\"部队火锅外卖 58元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%83%A8%E9%98%9F%E7%81%AB%E9%94%85%E5%A4%96%E5%8D%96%2058%E5%85%83', 115, 58.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (86, '蒜香排骨', '🍖', '1. 排骨腌制 2. 炸至金黄 3. 爆香蒜末翻炒', NULL, NULL, 102, 34.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (87, '清炒时蔬', '🥬', '1. 蔬菜洗净 2. 蒜蓉爆香 3. 快炒出锅', NULL, NULL, 75, 10.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (88, '鱼香肉丝盖饭', '🍛', '1. 炒肉丝 2. 加木耳胡萝卜 3. 勾鱼香汁盖饭', NULL, NULL, 97, 18.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (89, '湘西外婆菜', '🥗', '1. 备外婆菜 2. 加肉末辣椒炒 3. 出锅', NULL, NULL, 88, 22.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (90, '螺蛳粉外卖', '🍜', '1. 点击下方美团搜索入口 2. 搜索\"螺蛳粉外卖 18元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E8%9E%BA%E8%9B%B3%E7%B2%89%E5%A4%96%E5%8D%96%2018%E5%85%83', 105, 18.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (91, '土豆泥沙拉', '🥔', '1. 土豆蒸熟压泥 2. 拌黄瓜丁 3. 加沙拉酱', NULL, NULL, 80, 12.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (92, '烤鸭外卖', '🦆', '1. 点击下方美团搜索入口 2. 搜索\"烤鸭外卖 45元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%83%A4%E9%B8%AD%E5%A4%96%E5%8D%96%2045%E5%85%83', 110, 45.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (93, '肉夹馍', '🥙', '1. 炖五花肉 2. 剁碎夹馍 3. 加青椒', NULL, NULL, 92, 12.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (94, '酱骨架', '🍖', '1. 骨架焯水 2. 加酱料炖煮 3. 收汁出锅', NULL, NULL, 104, 32.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (95, '肠粉', '🥢', '1. 米浆蒸熟 2. 加肉蛋虾 3. 淋酱油', NULL, NULL, 85, 15.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (96, '石锅拌饭外卖', '🍲', '1. 点击下方美团搜索入口 2. 搜索\"石锅拌饭外卖 25元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%9F%B3%E9%94%85%E6%8B%8C%E9%A5%AD%E5%A4%96%E5%8D%96%2025%E5%85%83', 98, 25.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (97, '蒜蓉粉丝蒸扇贝', '🐚', '1. 扇贝洗净 2. 铺粉丝蒜蓉 3. 蒸熟淋油', NULL, NULL, 87, 38.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (98, '酸豆角肉末', '🥘', '1. 炒肉末 2. 加酸豆角 3. 翻炒出锅', NULL, NULL, 86, 16.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (99, '煎饼果子', '🌯', '1. 摊面糊 2. 加鸡蛋薄脆 3. 刷酱卷起', NULL, NULL, 84, 10.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (100, '冬阴功汤外卖', '🍲', '1. 点击下方美团搜索入口 2. 搜索\"冬阴功汤外卖 42元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E5%86%AC%E9%98%B4%E5%8A%9F%E6%B1%A4%E5%A4%96%E5%8D%96%2042%E5%85%83', 93, 42.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (101, '宫保鸡丁', '🐔', '1. 鸡丁腌制 2. 炸花生米 3. 炒宫保汁', NULL, NULL, 95, 22.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (102, '烧鹅饭外卖', '🍗', '1. 点击下方美团搜索入口 2. 搜索\"烧鹅饭外卖 32元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%83%A7%E9%B9%85%E9%A5%AD%E5%A4%96%E5%8D%96%2032%E5%85%83', 106, 32.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (103, '蚝油生菜', '🥬', '1. 生菜焯水 2. 调蚝油汁 3. 淋汁', NULL, NULL, 77, 8.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (104, '牛肉粉丝汤', '🍲', '1. 煮牛肉汤 2. 泡粉丝 3. 加牛肉片', NULL, NULL, 89, 18.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (105, '炸酱面', '🍜', '1. 炒肉末炸酱 2. 煮面条 3. 码黄瓜丝', NULL, NULL, 91, 16.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (106, '避风塘炒蟹外卖', '🦀', '1. 点击下方美团搜索入口 2. 搜索\"避风塘炒蟹外卖 88元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%81%BF%E9%A3%8E%E5%A1%98%E7%82%92%E8%9F%B9%E5%A4%96%E5%8D%96%2088%E5%85%83', 120, 88.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (107, '日式咖喱乌冬', '🍜', '1. 煮乌冬 2. 炒咖喱鸡肉 3. 混合', NULL, NULL, 88, 24.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (108, '酱牛肉', '🥩', '1. 牛腱子焯水 2. 卤煮 3. 切片', NULL, NULL, 96, 38.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (109, '酸辣土豆丝', '🥔', '1. 土豆切丝 2. 焯水 3. 爆炒加醋', NULL, NULL, 79, 9.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (110, '铁板鱿鱼外卖', '🦑', '1. 点击下方美团搜索入口 2. 搜索\"铁板鱿鱼外卖 25元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%93%81%E6%9D%BF%E9%B1%BF%E9%B1%BC%E5%A4%96%E5%8D%96%2025%E5%85%83', 100, 25.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (111, '清蒸鲈鱼', '🐟', '1. 鲈鱼处理 2. 加葱姜蒸 3. 淋蒸鱼豉油', NULL, NULL, 90, 35.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (112, '麻辣拌', '🥗', '1. 煮各种丸子蔬菜 2. 调麻辣拌酱 3. 拌匀', NULL, NULL, 94, 20.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (113, '羊杂汤外卖', '🥣', '1. 点击下方美团搜索入口 2. 搜索\"羊杂汤外卖 28元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%BE%8A%E6%9D%82%E6%B1%A4%E5%A4%96%E5%8D%96%2028%E5%85%83', 97, 28.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (114, '可乐鸡翅', '🍗', '1. 鸡翅煎黄 2. 加可乐酱油 3. 收汁', NULL, NULL, 92, 22.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (115, '越南春卷', '🌯', '1. 泡米纸 2. 包虾肉蔬菜 3. 蘸鱼露', NULL, NULL, 82, 28.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (116, '红烧牛肉面外卖', '🍜', '1. 点击下方美团搜索入口 2. 搜索\"红烧牛肉面外卖 26元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%BA%A2%E7%83%A7%E7%89%9B%E8%82%89%E9%9D%A2%E5%A4%96%E5%8D%96%2026%E5%85%83', 103, 26.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (117, '蒜蓉西蓝花', '🥦', '1. 西蓝花焯水 2. 蒜蓉爆香 3. 翻炒', NULL, NULL, 76, 12.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (118, '章鱼小丸子外卖', '🐙', '1. 点击下方美团搜索入口 2. 搜索\"章鱼小丸子外卖 18元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%AB%A0%E9%B1%BC%E5%B0%8F%E4%B8%B8%E5%AD%90%E5%A4%96%E5%8D%96%2018%E5%85%83', 86, 18.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (119, '炒米粉', '🍝', '1. 泡米粉 2. 加蛋肉菜炒 3. 调味', NULL, NULL, 91, 14.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (120, '猪脚饭外卖', '🍛', '1. 点击下方美团搜索入口 2. 搜索\"猪脚饭外卖 22元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%8C%AA%E8%84%9A%E9%A5%AD%E5%A4%96%E5%8D%96%2022%E5%85%83', 104, 22.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (121, '地三鲜', '🍆', '1. 土豆茄子青椒切块 2. 分别炸 3. 炒匀', NULL, NULL, 89, 16.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (122, '鳗鱼饭外卖', '🍱', '1. 点击下方美团搜索入口 2. 搜索\"鳗鱼饭外卖 48元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E9%B3%97%E9%B1%BC%E9%A5%AD%E5%A4%96%E5%8D%96%2048%E5%85%83', 95, 48.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (123, '酸梅汤', '🥤', '1. 煮乌梅山楂 2. 加冰糖 3. 冰镇', NULL, NULL, 70, 8.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (124, '鸡米花', '🍗', '1. 鸡胸肉切块 2. 裹粉炸 3. 撒椒盐', NULL, NULL, 93, 15.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (125, '酸辣蕨根粉', '🥢', '1. 煮蕨根粉 2. 调酸辣汁 3. 拌匀', NULL, NULL, 83, 12.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (126, '奶油蘑菇意面', '🍝', '1. 煮意面 2. 炒蘑菇培根 3. 加奶油', NULL, NULL, 90, 28.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (127, '手撕包菜', '🥬', '1. 包菜手撕 2. 爆香干辣椒 3. 快炒', NULL, NULL, 78, 12.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (128, '糯米鸡', '🍙', '1. 糯米浸泡 2. 包鸡肉香菇 3. 荷叶包裹蒸', NULL, NULL, 94, 18.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (129, '番茄牛腩煲外卖', '🍲', '1. 点击下方美团搜索入口 2. 搜索\"番茄牛腩煲外卖 42元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E7%95%AA%E8%8C%84%E7%89%9B%E8%85%A9%E7%85%B2%E5%A4%96%E5%8D%96%2042%E5%85%83', 112, 42.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (130, '韭菜盒子', '🥟', '1. 和面 2. 调韭菜鸡蛋馅 3. 煎至金黄', NULL, NULL, 85, 10.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (131, '蒜蓉小龙虾外卖', '🦞', '1. 点击下方美团搜索入口 2. 搜索\"蒜蓉小龙虾外卖 68元\" 3. 对比店铺价格后下单', NULL, 'https://waimai.meituan.com/?keyword=%E8%92%9C%E8%93%89%E5%B0%8F%E9%BE%99%E8%99%BE%E5%A4%96%E5%8D%96%2068%E5%85%83', 118, 68.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (132, '红豆汤圆', '🍡', '1. 煮红豆汤 2. 下汤圆 3. 加冰糖', NULL, NULL, 76, 12.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');
INSERT INTO `food_item` VALUES (133, '咖喱鱼蛋', '🍢', '1. 鱼蛋解冻 2. 煮咖喱汤 3. 焖煮入味', NULL, NULL, 81, 15.00, 1, 0, '2026-03-26 21:11:50', '2026-03-26 21:11:50');

-- ----------------------------
-- Table structure for food_item_tag_rel
-- ----------------------------
DROP TABLE IF EXISTS `food_item_tag_rel`;
CREATE TABLE `food_item_tag_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `food_id` bigint NOT NULL COMMENT '食物 ID',
  `tag_id` bigint NOT NULL COMMENT '标签 ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_food_item_tag`(`food_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_food_item_tag_rel_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 593 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '食物与标签关系表, 一个食物可关联多个标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_item_tag_rel
-- ----------------------------
INSERT INTO `food_item_tag_rel` VALUES (452, 1, 1);
INSERT INTO `food_item_tag_rel` VALUES (453, 1, 3);
INSERT INTO `food_item_tag_rel` VALUES (454, 1, 6);
INSERT INTO `food_item_tag_rel` VALUES (455, 1, 7);
INSERT INTO `food_item_tag_rel` VALUES (449, 2, 2);
INSERT INTO `food_item_tag_rel` VALUES (450, 2, 6);
INSERT INTO `food_item_tag_rel` VALUES (451, 2, 8);
INSERT INTO `food_item_tag_rel` VALUES (447, 3, 4);
INSERT INTO `food_item_tag_rel` VALUES (448, 3, 5);
INSERT INTO `food_item_tag_rel` VALUES (445, 4, 1);
INSERT INTO `food_item_tag_rel` VALUES (446, 4, 3);
INSERT INTO `food_item_tag_rel` VALUES (443, 5, 2);
INSERT INTO `food_item_tag_rel` VALUES (444, 5, 7);
INSERT INTO `food_item_tag_rel` VALUES (440, 6, 5);
INSERT INTO `food_item_tag_rel` VALUES (441, 6, 6);
INSERT INTO `food_item_tag_rel` VALUES (442, 6, 8);
INSERT INTO `food_item_tag_rel` VALUES (438, 7, 1);
INSERT INTO `food_item_tag_rel` VALUES (439, 7, 5);
INSERT INTO `food_item_tag_rel` VALUES (436, 8, 1);
INSERT INTO `food_item_tag_rel` VALUES (437, 8, 9);
INSERT INTO `food_item_tag_rel` VALUES (433, 9, 2);
INSERT INTO `food_item_tag_rel` VALUES (434, 9, 6);
INSERT INTO `food_item_tag_rel` VALUES (435, 9, 7);
INSERT INTO `food_item_tag_rel` VALUES (431, 10, 2);
INSERT INTO `food_item_tag_rel` VALUES (432, 10, 6);
INSERT INTO `food_item_tag_rel` VALUES (428, 11, 3);
INSERT INTO `food_item_tag_rel` VALUES (429, 11, 6);
INSERT INTO `food_item_tag_rel` VALUES (430, 11, 8);
INSERT INTO `food_item_tag_rel` VALUES (424, 12, 1);
INSERT INTO `food_item_tag_rel` VALUES (425, 12, 3);
INSERT INTO `food_item_tag_rel` VALUES (426, 12, 8);
INSERT INTO `food_item_tag_rel` VALUES (427, 12, 9);
INSERT INTO `food_item_tag_rel` VALUES (421, 13, 4);
INSERT INTO `food_item_tag_rel` VALUES (422, 13, 5);
INSERT INTO `food_item_tag_rel` VALUES (423, 13, 8);
INSERT INTO `food_item_tag_rel` VALUES (418, 14, 4);
INSERT INTO `food_item_tag_rel` VALUES (419, 14, 5);
INSERT INTO `food_item_tag_rel` VALUES (420, 14, 7);
INSERT INTO `food_item_tag_rel` VALUES (456, 50, 7);
INSERT INTO `food_item_tag_rel` VALUES (457, 51, 5);
INSERT INTO `food_item_tag_rel` VALUES (458, 52, 2);
INSERT INTO `food_item_tag_rel` VALUES (459, 52, 8);
INSERT INTO `food_item_tag_rel` VALUES (460, 53, 2);
INSERT INTO `food_item_tag_rel` VALUES (461, 53, 7);
INSERT INTO `food_item_tag_rel` VALUES (462, 54, 1);
INSERT INTO `food_item_tag_rel` VALUES (463, 54, 3);
INSERT INTO `food_item_tag_rel` VALUES (464, 55, 2);
INSERT INTO `food_item_tag_rel` VALUES (465, 55, 6);
INSERT INTO `food_item_tag_rel` VALUES (466, 56, 1);
INSERT INTO `food_item_tag_rel` VALUES (467, 56, 3);
INSERT INTO `food_item_tag_rel` VALUES (468, 57, 2);
INSERT INTO `food_item_tag_rel` VALUES (469, 57, 6);
INSERT INTO `food_item_tag_rel` VALUES (470, 57, 7);
INSERT INTO `food_item_tag_rel` VALUES (471, 58, 2);
INSERT INTO `food_item_tag_rel` VALUES (472, 58, 8);
INSERT INTO `food_item_tag_rel` VALUES (473, 59, 1);
INSERT INTO `food_item_tag_rel` VALUES (474, 59, 7);
INSERT INTO `food_item_tag_rel` VALUES (475, 60, 4);
INSERT INTO `food_item_tag_rel` VALUES (476, 61, 8);
INSERT INTO `food_item_tag_rel` VALUES (477, 62, 1);
INSERT INTO `food_item_tag_rel` VALUES (478, 62, 9);
INSERT INTO `food_item_tag_rel` VALUES (479, 63, 2);
INSERT INTO `food_item_tag_rel` VALUES (480, 63, 8);
INSERT INTO `food_item_tag_rel` VALUES (481, 64, 2);
INSERT INTO `food_item_tag_rel` VALUES (482, 65, 1);
INSERT INTO `food_item_tag_rel` VALUES (483, 65, 9);
INSERT INTO `food_item_tag_rel` VALUES (484, 66, 2);
INSERT INTO `food_item_tag_rel` VALUES (485, 66, 8);
INSERT INTO `food_item_tag_rel` VALUES (486, 67, 3);
INSERT INTO `food_item_tag_rel` VALUES (487, 68, 1);
INSERT INTO `food_item_tag_rel` VALUES (488, 68, 8);
INSERT INTO `food_item_tag_rel` VALUES (489, 69, 3);
INSERT INTO `food_item_tag_rel` VALUES (490, 69, 5);
INSERT INTO `food_item_tag_rel` VALUES (491, 70, 2);
INSERT INTO `food_item_tag_rel` VALUES (492, 70, 4);
INSERT INTO `food_item_tag_rel` VALUES (493, 71, 2);
INSERT INTO `food_item_tag_rel` VALUES (494, 71, 6);
INSERT INTO `food_item_tag_rel` VALUES (495, 71, 7);
INSERT INTO `food_item_tag_rel` VALUES (496, 72, 1);
INSERT INTO `food_item_tag_rel` VALUES (497, 73, 4);
INSERT INTO `food_item_tag_rel` VALUES (498, 74, 2);
INSERT INTO `food_item_tag_rel` VALUES (499, 74, 6);
INSERT INTO `food_item_tag_rel` VALUES (500, 74, 8);
INSERT INTO `food_item_tag_rel` VALUES (501, 75, 9);
INSERT INTO `food_item_tag_rel` VALUES (502, 76, 1);
INSERT INTO `food_item_tag_rel` VALUES (503, 76, 8);
INSERT INTO `food_item_tag_rel` VALUES (504, 77, 3);
INSERT INTO `food_item_tag_rel` VALUES (505, 78, 2);
INSERT INTO `food_item_tag_rel` VALUES (506, 78, 6);
INSERT INTO `food_item_tag_rel` VALUES (507, 79, 1);
INSERT INTO `food_item_tag_rel` VALUES (508, 79, 3);
INSERT INTO `food_item_tag_rel` VALUES (509, 80, 2);
INSERT INTO `food_item_tag_rel` VALUES (510, 80, 6);
INSERT INTO `food_item_tag_rel` VALUES (511, 80, 7);
INSERT INTO `food_item_tag_rel` VALUES (512, 81, 4);
INSERT INTO `food_item_tag_rel` VALUES (513, 81, 5);
INSERT INTO `food_item_tag_rel` VALUES (514, 82, 1);
INSERT INTO `food_item_tag_rel` VALUES (515, 82, 3);
INSERT INTO `food_item_tag_rel` VALUES (516, 83, 4);
INSERT INTO `food_item_tag_rel` VALUES (517, 83, 5);
INSERT INTO `food_item_tag_rel` VALUES (518, 84, 2);
INSERT INTO `food_item_tag_rel` VALUES (519, 84, 5);
INSERT INTO `food_item_tag_rel` VALUES (520, 84, 7);
INSERT INTO `food_item_tag_rel` VALUES (521, 85, 1);
INSERT INTO `food_item_tag_rel` VALUES (522, 85, 3);
INSERT INTO `food_item_tag_rel` VALUES (523, 86, 9);
INSERT INTO `food_item_tag_rel` VALUES (524, 87, 4);
INSERT INTO `food_item_tag_rel` VALUES (525, 88, 2);
INSERT INTO `food_item_tag_rel` VALUES (526, 88, 7);
INSERT INTO `food_item_tag_rel` VALUES (527, 89, 3);
INSERT INTO `food_item_tag_rel` VALUES (528, 90, 1);
INSERT INTO `food_item_tag_rel` VALUES (529, 90, 3);
INSERT INTO `food_item_tag_rel` VALUES (530, 90, 8);
INSERT INTO `food_item_tag_rel` VALUES (531, 91, 4);
INSERT INTO `food_item_tag_rel` VALUES (532, 92, 1);
INSERT INTO `food_item_tag_rel` VALUES (533, 93, 2);
INSERT INTO `food_item_tag_rel` VALUES (534, 95, 2);
INSERT INTO `food_item_tag_rel` VALUES (535, 95, 5);
INSERT INTO `food_item_tag_rel` VALUES (536, 95, 6);
INSERT INTO `food_item_tag_rel` VALUES (537, 96, 1);
INSERT INTO `food_item_tag_rel` VALUES (538, 96, 7);
INSERT INTO `food_item_tag_rel` VALUES (539, 97, 5);
INSERT INTO `food_item_tag_rel` VALUES (540, 98, 2);
INSERT INTO `food_item_tag_rel` VALUES (541, 99, 2);
INSERT INTO `food_item_tag_rel` VALUES (542, 99, 6);
INSERT INTO `food_item_tag_rel` VALUES (543, 100, 1);
INSERT INTO `food_item_tag_rel` VALUES (544, 100, 5);
INSERT INTO `food_item_tag_rel` VALUES (545, 101, 3);
INSERT INTO `food_item_tag_rel` VALUES (546, 102, 1);
INSERT INTO `food_item_tag_rel` VALUES (547, 102, 7);
INSERT INTO `food_item_tag_rel` VALUES (548, 103, 4);
INSERT INTO `food_item_tag_rel` VALUES (549, 104, 2);
INSERT INTO `food_item_tag_rel` VALUES (550, 104, 8);
INSERT INTO `food_item_tag_rel` VALUES (551, 105, 2);
INSERT INTO `food_item_tag_rel` VALUES (552, 105, 8);
INSERT INTO `food_item_tag_rel` VALUES (553, 106, 1);
INSERT INTO `food_item_tag_rel` VALUES (554, 106, 5);
INSERT INTO `food_item_tag_rel` VALUES (555, 107, 2);
INSERT INTO `food_item_tag_rel` VALUES (556, 107, 8);
INSERT INTO `food_item_tag_rel` VALUES (557, 109, 2);
INSERT INTO `food_item_tag_rel` VALUES (558, 109, 3);
INSERT INTO `food_item_tag_rel` VALUES (559, 110, 1);
INSERT INTO `food_item_tag_rel` VALUES (560, 110, 5);
INSERT INTO `food_item_tag_rel` VALUES (561, 111, 4);
INSERT INTO `food_item_tag_rel` VALUES (562, 111, 5);
INSERT INTO `food_item_tag_rel` VALUES (563, 112, 3);
INSERT INTO `food_item_tag_rel` VALUES (564, 113, 1);
INSERT INTO `food_item_tag_rel` VALUES (565, 114, 2);
INSERT INTO `food_item_tag_rel` VALUES (566, 115, 4);
INSERT INTO `food_item_tag_rel` VALUES (567, 115, 5);
INSERT INTO `food_item_tag_rel` VALUES (568, 116, 1);
INSERT INTO `food_item_tag_rel` VALUES (569, 116, 8);
INSERT INTO `food_item_tag_rel` VALUES (570, 117, 4);
INSERT INTO `food_item_tag_rel` VALUES (571, 118, 1);
INSERT INTO `food_item_tag_rel` VALUES (572, 118, 5);
INSERT INTO `food_item_tag_rel` VALUES (573, 119, 2);
INSERT INTO `food_item_tag_rel` VALUES (574, 119, 6);
INSERT INTO `food_item_tag_rel` VALUES (575, 119, 8);
INSERT INTO `food_item_tag_rel` VALUES (576, 120, 1);
INSERT INTO `food_item_tag_rel` VALUES (577, 120, 7);
INSERT INTO `food_item_tag_rel` VALUES (578, 121, 2);
INSERT INTO `food_item_tag_rel` VALUES (579, 122, 1);
INSERT INTO `food_item_tag_rel` VALUES (580, 122, 5);
INSERT INTO `food_item_tag_rel` VALUES (581, 122, 7);
INSERT INTO `food_item_tag_rel` VALUES (582, 124, 9);
INSERT INTO `food_item_tag_rel` VALUES (583, 125, 3);
INSERT INTO `food_item_tag_rel` VALUES (584, 125, 8);
INSERT INTO `food_item_tag_rel` VALUES (585, 126, 8);
INSERT INTO `food_item_tag_rel` VALUES (586, 127, 3);
INSERT INTO `food_item_tag_rel` VALUES (587, 128, 2);
INSERT INTO `food_item_tag_rel` VALUES (588, 129, 1);
INSERT INTO `food_item_tag_rel` VALUES (589, 130, 2);
INSERT INTO `food_item_tag_rel` VALUES (590, 130, 6);
INSERT INTO `food_item_tag_rel` VALUES (591, 131, 1);
INSERT INTO `food_item_tag_rel` VALUES (592, 131, 5);
INSERT INTO `food_item_tag_rel` VALUES (593, 133, 5);

-- ----------------------------
-- Table structure for food_tag
-- ----------------------------
DROP TABLE IF EXISTS `food_tag`;
CREATE TABLE `food_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称, 如外卖, 辣, 轻食',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签图标或 emoji',
  `tag_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SYSTEM' COMMENT '标签类型, SYSTEM 系统标签, CUSTOM 自定义标签',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值, 越小越靠前',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用, 1 启用, 0 停用',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记, 0 未删除, 1 已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_food_tag_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签字典表, 用于模式筛选和偏好设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_tag
-- ----------------------------
INSERT INTO `food_tag` VALUES (1, '外卖', '🍔', 'SYSTEM', 10, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');
INSERT INTO `food_tag` VALUES (2, '清冰箱', '🥬', 'SYSTEM', 20, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');
INSERT INTO `food_tag` VALUES (3, '辣', '🌶️', 'SYSTEM', 30, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');
INSERT INTO `food_tag` VALUES (4, '轻食', '🥗', 'SYSTEM', 40, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');
INSERT INTO `food_tag` VALUES (5, '健康', '🍃', 'SYSTEM', 50, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');
INSERT INTO `food_tag` VALUES (6, '10分钟', '⏱️', 'SYSTEM', 60, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');
INSERT INTO `food_tag` VALUES (7, '米饭', '🍚', 'SYSTEM', 70, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');
INSERT INTO `food_tag` VALUES (8, '面食', '🍜', 'SYSTEM', 80, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');
INSERT INTO `food_tag` VALUES (9, '夜宵', '🌙', 'SYSTEM', 90, 1, 0, '2026-03-24 21:00:52', '2026-03-24 21:00:52');

-- ----------------------------
-- Table structure for shake_history
-- ----------------------------
DROP TABLE IF EXISTS `shake_history`;
CREATE TABLE `shake_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID, 当前 V1 固定为 1',
  `food_id` bigint NOT NULL COMMENT '命中的食物 ID',
  `food_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '冗余食物名称, 防止后续改名影响历史展示',
  `mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '摇一摇模式, 如 LAZY, CLEAR, SPICY, LIGHT',
  `trigger_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发方式, SHAKE 或 CLICK',
  `result_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '结果状态, PENDING, ACCEPTED, REJECTED',
  `ai_tip` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI 或兜底文案',
  `ai_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'FALLBACK' COMMENT '文案来源, OLLAMA 或 FALLBACK',
  `prompt_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '生成文案时的 prompt 快照, 便于排查',
  `score_snapshot` json NULL COMMENT '命中时的权重明细快照, 便于调权重',
  `steps_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '摇出当时的步骤快照',
  `order_url_snapshot` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '摇出当时的下单或搜索链接快照',
  `reference_price_snapshot` decimal(10, 2) NULL DEFAULT NULL COMMENT '摇出当时的参考价格快照',
  `tag_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '摇出当时的标签名称快照',
  `reject_feedback_code` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拒绝本次推荐的反馈原因',
  `shake_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '摇出结果的时间',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '用户点击就这样吃的确认时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_shake_history_user_time`(`user_id` ASC, `shake_time` ASC) USING BTREE,
  INDEX `idx_shake_history_food_id`(`food_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '摇一摇历史表, 每次摇一摇都会落一条记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shake_history
-- ----------------------------
INSERT INTO `shake_history` VALUES (1, 1, 4, '麻辣香锅外卖', 'LAZY', 'CLICK', 'PENDING', '今晚懒得做饭？打开外卖 App 点份麻辣香锅吧，自选爱吃的菜，辣得过瘾送到家，轻松解决晚餐烦恼！', 'OLLAMA', NULL, '{\"score\": 198.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-24 23:25:01', NULL);
INSERT INTO `shake_history` VALUES (2, 1, 4, '麻辣香锅外卖', 'LAZY', 'CLICK', 'PENDING', '今晚懒得做饭？点份麻辣香锅外卖吧，打开手机选些喜欢的菜，热辣鲜香一锅送到家，轻松解决晚餐烦恼。', 'OLLAMA', NULL, '{\"score\": 198.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-24 23:30:03', NULL);
INSERT INTO `shake_history` VALUES (3, 1, 4, '麻辣香锅外卖', 'LAZY', 'CLICK', 'PENDING', '今晚懒得做饭？打开外卖 App 点份麻辣香锅吧，自选喜欢的菜，辣得过瘾送到家，轻松解决晚餐烦恼。', 'OLLAMA', NULL, '{\"score\": 198.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-24 23:43:01', NULL);
INSERT INTO `shake_history` VALUES (4, 1, 1, '黄焖鸡米饭', 'LAZY', 'CLICK', 'PENDING', '今晚来份热辣过瘾的黄焖鸡米饭吧！下单后10分钟就能开动，香浓酱汁拌着米饭，外卖到家正合适。', 'OLLAMA', NULL, '{\"score\": 216.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-24 23:43:07', NULL);
INSERT INTO `shake_history` VALUES (5, 1, 4, '麻辣香锅外卖', 'LAZY', 'CLICK', 'PENDING', '今晚懒得做饭？打开外卖App点份麻辣香锅吧，自选喜欢的食材，辣得过瘾又方便，在家就能享受热辣美味。', 'OLLAMA', NULL, '{\"score\": 198.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-24 23:58:00', NULL);
INSERT INTO `shake_history` VALUES (6, 1, 1, '黄焖鸡米饭', 'LAZY', 'CLICK', 'PENDING', '今晚懒得下厨？不如点份热辣鲜香的黄焖鸡米饭外卖吧，十分钟就能送到，软嫩鸡肉配上浓郁汤汁，轻松解决晚餐选择困难！', 'OLLAMA', NULL, '{\"score\": 216.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-24 23:58:44', NULL);
INSERT INTO `shake_history` VALUES (7, 1, 4, '麻辣香锅外卖', 'LAZY', 'CLICK', 'PENDING', '今晚懒得下厨？不如点份麻辣香锅外卖吧，打开 App 选几样爱吃的食材，辣得过瘾又省心，坐等美味上门就好啦！', 'OLLAMA', NULL, '{\"score\": 198.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-24 23:58:55', NULL);
INSERT INTO `shake_history` VALUES (8, 1, 1, '黄焖鸡米饭', 'LAZY', 'CLICK', 'PENDING', '今晚懒得做饭？来份热辣鲜香的黄焖鸡米饭外卖吧，十分钟就能送到，香浓汤汁配上软糯米饭，轻松解决晚餐烦恼！', 'OLLAMA', NULL, '{\"score\": 216.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-24 23:59:47', NULL);
INSERT INTO `shake_history` VALUES (9, 1, 1, '黄焖鸡米饭', 'LAZY', 'CLICK', 'PENDING', '今晚试试黄焖鸡米饭吧，香辣下饭的外卖十分钟就能送到，热乎乎地拌上米饭，简单又满足。', 'OLLAMA', NULL, '{\"score\": 216.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:28:26', NULL);
INSERT INTO `shake_history` VALUES (10, 1, 2, '番茄鸡蛋面', 'CLEAR', 'CLICK', 'PENDING', '冰箱里剩的番茄和鸡蛋别浪费，十分钟就能煮碗热乎的番茄鸡蛋面，酸甜开胃，简单又治愈。', 'OLLAMA', NULL, '{\"score\": 171.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:28:38', NULL);
INSERT INTO `shake_history` VALUES (11, 1, 1, '黄焖鸡米饭', 'SPICY', 'CLICK', 'PENDING', '想吃点辣又不想等太久？试试黄焖鸡米饭外卖吧，下单后十分钟就能享受这份香辣过瘾的晚餐！', 'OLLAMA', NULL, '{\"score\": 216.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:29:16', NULL);
INSERT INTO `shake_history` VALUES (12, 1, 4, '麻辣香锅外卖', 'SPICY', 'CLICK', 'PENDING', '今晚懒得下厨？不如点份麻辣香锅外卖吧，自选鲜香麻辣的食材，热辣过瘾送到家，轻松解决晚餐烦恼！', 'OLLAMA', NULL, '{\"score\": 198.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:29:47', NULL);
INSERT INTO `shake_history` VALUES (13, 1, 4, '麻辣香锅外卖', 'SPICY', 'CLICK', 'PENDING', '今晚试试麻辣香锅外卖吧，打开手机点几样爱吃的菜，热辣鲜香送到家，让晚餐变得轻松又过瘾！', 'OLLAMA', NULL, '{\"score\": 198.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:29:49', NULL);
INSERT INTO `shake_history` VALUES (14, 1, 4, '麻辣香锅外卖', 'SPICY', 'CLICK', 'PENDING', '今晚试试麻辣香锅外卖吧，打开 App 自选食材，热辣过瘾的香气送到家，轻松解决晚餐选择困难！', 'OLLAMA', NULL, '{\"score\": 198.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:29:58', NULL);
INSERT INTO `shake_history` VALUES (15, 1, 1, '黄焖鸡米饭', 'SPICY', 'CLICK', 'PENDING', '今晚来份热辣过瘾的黄焖鸡米饭外卖吧！十分钟就能送到，香浓酱汁包裹嫩滑鸡肉，配上热腾腾的米饭，辣得恰到好处，瞬间点亮你的味蕾～', 'OLLAMA', NULL, '{\"score\": 216.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:30:15', NULL);
INSERT INTO `shake_history` VALUES (16, 1, 3, '轻食鸡胸沙拉', 'LIGHT', 'CLICK', 'REJECTED', '今晚来份轻食鸡胸沙拉吧，清爽的生菜搭配嫩滑鸡胸肉，简单拌一拌就能享受健康美味，让晚餐变得轻盈无负担。', 'OLLAMA', NULL, '{\"score\": 162.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:32:21', NULL);
INSERT INTO `shake_history` VALUES (17, 1, 6, '清汤馄饨', 'LIGHT', 'CLICK', 'ACCEPTED', '今晚来碗热乎乎的清汤馄饨吧，十分钟就能搞定，清淡鲜美又暖胃，简单几步就能享受健康美味！', 'OLLAMA', NULL, '{\"score\": 144.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:32:29', '2026-03-25 00:34:02');
INSERT INTO `shake_history` VALUES (18, 1, 12, '酸辣粉外卖', 'LAZY', 'CLICK', 'REJECTED', '懒得做饭的夜晚，不如来一碗酸辣开胃的粉，外卖送到家，暖呼呼的正好当夜宵。', 'OLLAMA', NULL, '{\"score\": 183.6}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:47:11', NULL);
INSERT INTO `shake_history` VALUES (19, 1, 7, '寿司外卖', 'LAZY', 'CLICK', 'REJECTED', '今晚懒得下厨？不如点份新鲜寿司外卖吧！健康又美味，打开 App 选个套餐，剩下的就交给骑手啦～', 'OLLAMA', NULL, '{\"score\": 158.4}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:47:33', NULL);
INSERT INTO `shake_history` VALUES (20, 1, 4, '麻辣香锅外卖', 'LAZY', 'CLICK', 'PENDING', '今晚懒得下厨？不如点份麻辣香锅外卖吧，热辣鲜香一锅端，打开 App 选几样爱吃的菜，轻松下单就能在家享受美味啦！', 'OLLAMA', NULL, '{\"score\": 128.7}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 00:54:21', NULL);
INSERT INTO `shake_history` VALUES (21, 1, 1, '黄焖鸡米饭', 'LAZY', 'CLICK', 'PENDING', '今晚懒得下厨？不如点份热辣过瘾的黄焖鸡米饭外卖吧！十分钟就能送到，香浓汤汁配上软糯米饭，轻松解决一顿美味晚餐。', 'OLLAMA', NULL, '{\"score\": 140.4}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 01:07:50', NULL);
INSERT INTO `shake_history` VALUES (22, 1, 8, '炸鸡汉堡外卖', 'LAZY', 'CLICK', 'PENDING', '夜深了，不如点个金黄酥脆的炸鸡汉堡吧，热乎乎的外卖马上到家，轻松搞定美味夜宵！', 'OLLAMA', NULL, '{\"score\": 189.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 01:10:42', NULL);
INSERT INTO `shake_history` VALUES (23, 1, 12, '酸辣粉外卖', 'LAZY', 'CLICK', 'PENDING', '这碗酸辣粉来得正是时候，酸辣开胃驱散疲惫，热乎乎的面条暖到心里，是慰藉夜晚的完美选择。', 'OLLAMA', NULL, '{\"score\": 119.34}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 01:51:11', NULL);
INSERT INTO `shake_history` VALUES (24, 1, 4, '麻辣香锅外卖', 'LAZY', 'CLICK', 'PENDING', '今晚懒得下厨？一份热辣鲜香的麻辣香锅正合适，食材丰富又下饭，轻松驱散一天的疲惫。', 'OLLAMA', NULL, '{\"score\": 69.3}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 01:51:18', NULL);
INSERT INTO `shake_history` VALUES (25, 1, 11, '老干妈拌面', 'SPICY', 'CLICK', 'PENDING', '深夜饿了吗？一碗香辣浓郁的老干妈拌面，热腾腾的面条裹满酱汁，十分钟就能抚慰你的胃和心。', 'OLLAMA', NULL, '{\"score\": 176.4}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:17:14', NULL);
INSERT INTO `shake_history` VALUES (26, 1, 1, '黄焖鸡米饭', 'LAZY', 'CLICK', 'PENDING', '今晚来份热腾腾的黄焖鸡米饭吧，香辣汤汁裹着嫩滑鸡肉，十分钟就能送到，配着米饭吃暖心又满足。', 'OLLAMA', NULL, '{\"score\": 140.4}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:17:36', NULL);
INSERT INTO `shake_history` VALUES (27, 1, 12, '酸辣粉外卖', 'SPICY', 'CLICK', 'ACCEPTED', '今晚来碗酸辣粉吧！热腾腾的红油汤底酸辣开胃，滑溜的粉条嚼劲十足，特别适合现在有点疲惫的你，瞬间唤醒味蕾～', 'OLLAMA', NULL, '{\"score\": 119.34}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:18:00', '2026-03-25 14:29:50');
INSERT INTO `shake_history` VALUES (28, 1, 4, '麻辣香锅外卖', 'LAZY', 'CLICK', 'PENDING', '忙了一天，让热辣丰盛的香锅点亮夜晚吧，暖暖地犒劳自己。', 'OLLAMA', NULL, '{\"score\": 128.7}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:38:39', NULL);
INSERT INTO `shake_history` VALUES (29, 1, 7, '寿司外卖', 'LAZY', 'CLICK', 'PENDING', '傍晚来份清爽的寿司吧，新鲜鱼生搭配醋饭，低脂又饱腹，正好抚慰忙碌一天的身心。', 'OLLAMA', NULL, '{\"score\": 158.4}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:38:42', NULL);
INSERT INTO `shake_history` VALUES (30, 1, 8, '炸鸡汉堡外卖', 'LAZY', 'CLICK', 'PENDING', '今晚就让酥脆多汁的炸鸡汉堡来犒劳自己吧，热乎乎的炸鸡配上松软面包，轻松驱散一天的疲惫，正是此刻完美的治愈选择。', 'OLLAMA', NULL, '{\"score\": 122.85}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:38:44', NULL);
INSERT INTO `shake_history` VALUES (31, 1, 1, '黄焖鸡米饭', 'LAZY', 'CLICK', 'PENDING', '今晚来份热腾腾的黄焖鸡米饭吧，香辣汤汁包裹着嫩滑鸡肉，十分钟就能送到，配着米饭吃特别暖胃解馋。', 'OLLAMA', NULL, '{\"score\": 75.6}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:38:45', NULL);
INSERT INTO `shake_history` VALUES (32, 1, 7, '寿司外卖', 'LAZY', 'CLICK', 'PENDING', '清爽的寿司搭配鲜嫩鱼生，既轻盈无负担又满满营养，正适合犒劳忙碌一天的自己。', 'OLLAMA', NULL, '{\"score\": 158.4}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:39:04', NULL);
INSERT INTO `shake_history` VALUES (33, 1, 7, '寿司外卖', 'LAZY', 'CLICK', 'PENDING', '傍晚时分，来一份清爽的寿司外卖吧！食材新鲜，搭配均衡，既满足口腹之欲又无负担，正好为忙碌的一天画上轻盈的句点。', 'OLLAMA', NULL, '{\"score\": 158.4}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:39:05', NULL);
INSERT INTO `shake_history` VALUES (34, 1, 8, '炸鸡汉堡外卖', 'LAZY', 'CLICK', 'PENDING', '酥脆炸鸡配上松软面包，深夜的满足感就在这一口，29元就能点亮今晚的快乐。', 'OLLAMA', NULL, '{\"score\": 189.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:39:09', NULL);
INSERT INTO `shake_history` VALUES (35, 1, 11, '老干妈拌面', 'SPICY', 'CLICK', 'PENDING', '夜深了，来碗香辣过瘾的老干妈拌面吧，十分钟就能让舌尖醒过来，暖暖地安抚疲惫的胃。', 'OLLAMA', NULL, '{\"score\": 114.66}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:39:12', NULL);
INSERT INTO `shake_history` VALUES (36, 1, 8, '炸鸡汉堡外卖', 'LAZY', 'CLICK', 'PENDING', '今晚就犒劳自己一份香脆多汁的炸鸡汉堡吧，热乎乎的送到手，轻松治愈一天的疲惫，正是完美的宵夜选择。', 'OLLAMA', NULL, '{\"score\": 122.85}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:39:17', NULL);
INSERT INTO `shake_history` VALUES (37, 1, 12, '酸辣粉外卖', 'SPICY', 'CLICK', 'PENDING', '酸辣的汤头配上滑溜红薯粉，一口下去暖胃又开胃，正好抚慰下班后的疲惫。', 'OLLAMA', NULL, '{\"score\": 119.34}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:39:37', NULL);
INSERT INTO `shake_history` VALUES (38, 1, 4, '麻辣香锅外卖', 'SPICY', 'CLICK', 'PENDING', '傍晚的凉风配上这份麻辣香锅，热腾腾的香辣瞬间唤醒味蕾，正好驱散一天的疲惫。', 'OLLAMA', NULL, '{\"score\": 128.7}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:39:54', NULL);
INSERT INTO `shake_history` VALUES (39, 1, 1, '黄焖鸡米饭', 'LAZY', 'CLICK', 'PENDING', '今晚来份热腾腾的黄焖鸡米饭吧，香辣汤汁包裹着嫩滑鸡肉，十分钟就能送到，配着米饭吃特别暖胃解馋。', 'OLLAMA', NULL, '{\"score\": 216.0}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 14:59:46', NULL);
INSERT INTO `shake_history` VALUES (40, 1, 13, '凉拌鸡丝荞麦面', 'LIGHT', 'CLICK', 'REJECTED', '夏夜闷热没胃口？这碗凉拌鸡丝荞麦面清爽又饱腹，低卡酱汁裹着弹滑面条，拌上丝丝鸡肉和脆嫩蔬菜，吃起来毫无负担。', 'OLLAMA', NULL, '{\"score\": 156.6}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 15:15:22', NULL);
INSERT INTO `shake_history` VALUES (41, 1, 8, '炸鸡汉堡外卖', 'LAZY', 'CLICK', 'PENDING', '酥脆炸鸡配上松软面包，深夜的热量满足最能抚慰一天的疲惫。', 'OLLAMA', NULL, '{\"score\": 122.85}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 17:05:21', NULL);
INSERT INTO `shake_history` VALUES (42, 1, 12, '酸辣粉外卖', 'LAZY', 'CLICK', 'PENDING', '今晚来碗酸辣粉吧，热乎乎的酸辣汤底配上滑溜红薯粉，又开胃又解乏，正适合犒劳忙碌了一天的自己。', 'OLLAMA', NULL, '{\"score\": 119.34}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 17:26:16', NULL);
INSERT INTO `shake_history` VALUES (43, 1, 3, '轻食鸡胸沙拉', 'LIGHT', 'CLICK', 'PENDING', '晚上来份清爽的鸡胸沙拉吧，十分钟就能搞定，低卡又饱腹，加点辣酱提味，还能清空冰箱里的蔬菜，健康无负担！', 'OLLAMA', NULL, '{\"score\": 210.6}', NULL, NULL, NULL, NULL, NULL, '2026-03-25 18:40:28', NULL);
INSERT INTO `shake_history` VALUES (44, 1, 13, '凉拌鸡丝荞麦面', 'LIGHT', 'CLICK', 'PENDING', '夏夜闷热没胃口？这碗凉拌鸡丝荞麦面正清爽，微辣酱汁裹着弹滑面条，十分钟就拌好，吃得舒服又没负担。', 'OLLAMA', NULL, '{\"score\": 132.33}', NULL, NULL, NULL, NULL, NULL, '2026-03-26 16:47:23', NULL);
INSERT INTO `shake_history` VALUES (45, 1, 14, '虾仁西兰花饭', 'LIGHT', 'CLICK', 'PENDING', '今晚就来份虾仁西兰花饭吧，清爽的西兰花配上弹嫩虾仁，健康又饱腹，十分钟就能轻松搞定，正适合不想让肠胃有负担的夜晚。', 'OLLAMA', NULL, '{\"score\": 208.26}', NULL, NULL, NULL, NULL, NULL, '2026-03-26 16:58:23', NULL);
INSERT INTO `shake_history` VALUES (46, 1, 4, '麻辣香锅外卖', 'SPICY', 'CLICK', 'PENDING', '忙碌一天后，来份麻辣香锅犒劳自己吧！热辣的香气瞬间唤醒味蕾，多种食材一锅满足，既过足辣瘾又清空冰箱库存，轻松享受美味一刻。', 'OLLAMA', NULL, '{\"score\": 257.4}', NULL, NULL, NULL, NULL, NULL, '2026-03-26 20:36:56', NULL);
INSERT INTO `shake_history` VALUES (47, 1, 56, '麻辣烫外卖', 'LAZY', 'CLICK', 'PENDING', '今晚来碗麻辣烫吧，热辣开胃又过瘾，还能清空冰箱库存，十分钟就能享用健康轻食啦！', 'OLLAMA', NULL, '{\"modeHit\": true, \"baseWeight\": 99, \"finalScore\": 231.66, \"modeFactor\": 1.8, \"favoriteHit\": true, \"budgetFactor\": 1.0, \"reasonLabels\": [\"命中当前模式, 权重 x1.80\", \"命中喜欢标签, 权重 x1.30\", \"预算友好, 不降权\", \"近期未重复出现, 不降权\"], \"favoriteFactor\": 1.3, \"diversityFactor\": 1.0}', '1. 点击下方美团搜索入口 2. 搜索\"麻辣烫外卖 28元\" 3. 对比店铺价格后下单', 'https://waimai.meituan.com/?keyword=%E9%BA%BB%E8%BE%A3%E7%83%AB%E5%A4%96%E5%8D%96%2028%E5%85%83', 28.00, '[\"外卖\",\"辣\"]', NULL, '2026-03-31 23:23:35', NULL);

-- ----------------------------
-- Table structure for user_preference
-- ----------------------------
DROP TABLE IF EXISTS `user_preference`;
CREATE TABLE `user_preference`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID, 当前 V1 固定为 1',
  `favorite_tag_ids` json NULL COMMENT '喜欢标签 ID 数组, 用于提高命中权重',
  `avoid_tag_ids` json NULL COMMENT '避免标签 ID 数组, 用于直接排除候选',
  `max_budget` decimal(10, 2) NULL DEFAULT NULL COMMENT '最高预算, V1 不做硬过滤',
  `last_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'LAZY' COMMENT '上次使用模式, 用于首页默认态恢复',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_preference_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户偏好表, 当前单用户只有一条记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_preference
-- ----------------------------
INSERT INTO `user_preference` VALUES (1, 1, '[2, 3, 4, 5, 6]', '[9]', 50.00, 'LIGHT', '2026-03-24 21:00:52', '2026-03-24 21:00:52');

SET FOREIGN_KEY_CHECKS = 1;
