package com.ruoyi.quartz.task;

import com.ruoyi.system.mqtt.service.ModbusPollingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 简单轮询任务：驱动 ModbusPollingService.tick()
 * 默认不会自动加入数据库任务，仅提供可在"定时任务"中配置的调用入口：invokeTarget = modbusPollingJob.tick
 */
@Component("modbusPollingJob")
public class ModbusPollingJob {
    private static final Logger log = LoggerFactory.getLogger(ModbusPollingJob.class);

    @Autowired
    private ModbusPollingService modbusPollingService;

    public void tick() {
        try {
            modbusPollingService.tick();
        } catch (Exception e) {
            log.error("ModbusPollingJob 执行失败", e);
        }
    }
}


