USE shakeeat;

SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'shakeeat'
      AND TABLE_NAME = 'food_item'
      AND COLUMN_NAME = 'order_url'
);

SET @add_sql = IF(
    @column_exists = 0,
    'ALTER TABLE food_item ADD COLUMN order_url VARCHAR(500) DEFAULT NULL COMMENT ''外卖或购买链接, 用于结果页直达下单'' AFTER image_url',
    'SELECT 1'
);
PREPARE add_stmt FROM @add_sql;
EXECUTE add_stmt;
DEALLOCATE PREPARE add_stmt;

ALTER TABLE food_item
    MODIFY COLUMN order_url VARCHAR(500) DEFAULT NULL COMMENT '外卖或购买链接, 用于结果页直达下单';
