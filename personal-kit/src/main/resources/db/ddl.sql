-- 在dml.sql最后添加
CREATE TABLE IF NOT EXISTS db_init_status (
    id INT PRIMARY KEY,
    initialized BOOLEAN DEFAULT FALSE,
    init_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
