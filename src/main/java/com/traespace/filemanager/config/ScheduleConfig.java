package com.traespace.filemanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务配置
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    private static final Logger log = LoggerFactory.getLogger(ScheduleConfig.class);

    /**
     * 数据清理定时任务
     * 每天凌晨2点执行，删除30天前的软删除数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanDeletedData() {
        log.info("[定时任务] 开始清理已删除数据");
        try {
            // TODO: 实现数据清理逻辑
            // 1. 查询status=0且删除时间超过30天的FileRecord
            // 2. 物理删除对应的DataDetail数据
            // 3. 物理删除FileRecord记录
            log.info("[定时任务] 数据清理完成");
        } catch (Exception e) {
            log.error("[定时任务] 数据清理失败", e);
        }
    }
}
