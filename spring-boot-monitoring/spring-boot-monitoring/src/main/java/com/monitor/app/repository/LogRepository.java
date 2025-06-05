package com.monitor.app.repository;

import com.monitor.app.entity.LogRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 日志记录仓库，用于测试数据库监控功能
 */
@Repository
public interface LogRepository extends JpaRepository<LogRecord, Long> {
}
