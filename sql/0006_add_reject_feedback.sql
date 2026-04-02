USE shakeeat;

SET @reject_feedback_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'shakeeat'
      AND TABLE_NAME = 'shake_history'
      AND COLUMN_NAME = 'reject_feedback_code'
);

SET @add_reject_feedback_sql = IF(
    @reject_feedback_exists = 0,
    'ALTER TABLE shake_history ADD COLUMN reject_feedback_code VARCHAR(40) DEFAULT NULL COMMENT ''拒绝本次推荐的反馈原因'' AFTER tag_snapshot',
    'SELECT 1'
);
PREPARE add_reject_feedback_stmt FROM @add_reject_feedback_sql;
EXECUTE add_reject_feedback_stmt;
DEALLOCATE PREPARE add_reject_feedback_stmt;

ALTER TABLE shake_history
    MODIFY COLUMN reject_feedback_code VARCHAR(40) DEFAULT NULL COMMENT '拒绝本次推荐的反馈原因';
