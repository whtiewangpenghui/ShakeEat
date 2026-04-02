USE shakeeat;

SET @steps_snapshot_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'shakeeat'
      AND TABLE_NAME = 'shake_history'
      AND COLUMN_NAME = 'steps_snapshot'
);

SET @add_steps_snapshot_sql = IF(
    @steps_snapshot_exists = 0,
    'ALTER TABLE shake_history ADD COLUMN steps_snapshot TEXT DEFAULT NULL COMMENT ''摇出当时的步骤快照'' AFTER score_snapshot',
    'SELECT 1'
);
PREPARE add_steps_snapshot_stmt FROM @add_steps_snapshot_sql;
EXECUTE add_steps_snapshot_stmt;
DEALLOCATE PREPARE add_steps_snapshot_stmt;

SET @order_url_snapshot_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'shakeeat'
      AND TABLE_NAME = 'shake_history'
      AND COLUMN_NAME = 'order_url_snapshot'
);

SET @add_order_url_snapshot_sql = IF(
    @order_url_snapshot_exists = 0,
    'ALTER TABLE shake_history ADD COLUMN order_url_snapshot VARCHAR(500) DEFAULT NULL COMMENT ''摇出当时的下单或搜索链接快照'' AFTER steps_snapshot',
    'SELECT 1'
);
PREPARE add_order_url_snapshot_stmt FROM @add_order_url_snapshot_sql;
EXECUTE add_order_url_snapshot_stmt;
DEALLOCATE PREPARE add_order_url_snapshot_stmt;

SET @reference_price_snapshot_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'shakeeat'
      AND TABLE_NAME = 'shake_history'
      AND COLUMN_NAME = 'reference_price_snapshot'
);

SET @add_reference_price_snapshot_sql = IF(
    @reference_price_snapshot_exists = 0,
    'ALTER TABLE shake_history ADD COLUMN reference_price_snapshot DECIMAL(10, 2) DEFAULT NULL COMMENT ''摇出当时的参考价格快照'' AFTER order_url_snapshot',
    'SELECT 1'
);
PREPARE add_reference_price_snapshot_stmt FROM @add_reference_price_snapshot_sql;
EXECUTE add_reference_price_snapshot_stmt;
DEALLOCATE PREPARE add_reference_price_snapshot_stmt;

SET @tag_snapshot_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'shakeeat'
      AND TABLE_NAME = 'shake_history'
      AND COLUMN_NAME = 'tag_snapshot'
);

SET @add_tag_snapshot_sql = IF(
    @tag_snapshot_exists = 0,
    'ALTER TABLE shake_history ADD COLUMN tag_snapshot TEXT DEFAULT NULL COMMENT ''摇出当时的标签名称快照'' AFTER reference_price_snapshot',
    'SELECT 1'
);
PREPARE add_tag_snapshot_stmt FROM @add_tag_snapshot_sql;
EXECUTE add_tag_snapshot_stmt;
DEALLOCATE PREPARE add_tag_snapshot_stmt;

ALTER TABLE shake_history
    MODIFY COLUMN steps_snapshot TEXT DEFAULT NULL COMMENT '摇出当时的步骤快照',
    MODIFY COLUMN order_url_snapshot VARCHAR(500) DEFAULT NULL COMMENT '摇出当时的下单或搜索链接快照',
    MODIFY COLUMN reference_price_snapshot DECIMAL(10, 2) DEFAULT NULL COMMENT '摇出当时的参考价格快照',
    MODIFY COLUMN tag_snapshot TEXT DEFAULT NULL COMMENT '摇出当时的标签名称快照';
