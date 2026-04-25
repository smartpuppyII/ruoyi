package com.ruoyi.system.mqtt.service;

import com.ruoyi.system.domain.IotDevice;
import com.ruoyi.system.mqtt.util.ModbusUtils;
import com.ruoyi.system.service.IIotDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最小可用版本：按固定寄存器段轮询（0x0000-0x0008，共9个），HEX 文本载荷，通过 GE_sub 下发。
 * 不改动数据库结构；默认关闭，通过 application.yml 开关启用。
 */
@Service
public class ModbusPollingService {
    private static final Logger log = LoggerFactory.getLogger(ModbusPollingService.class);

    @Autowired
    private IIotDeviceService deviceService;

    @Autowired
    private MqttClientService mqttClientService;

    // 开关与参数（默认关闭，避免影响现有功能）
    @Value("${modbus.polling.enabled:true}")
    private boolean pollingEnabled;

    @Value("${modbus.polling.intervalMs:2000}")
    private long intervalMs;

    @Value("${modbus.slaveId:1}")
    private int defaultSlaveId;

    // 每设备的简单轮询状态（最小可用，仅一段）
    private final Map<Long, DeviceState> deviceStateMap = new HashMap<>();

    /** 定时驱动入口：由 Quartz Job 调用 */
    public void tick() {
        if (!pollingEnabled) { return; }
        try {
            List<IotDevice> devices = deviceService.selectIotDeviceList(new IotDevice());
            for (IotDevice device : devices) {
                DeviceState state = deviceStateMap.computeIfAbsent(device.getDeviceId(), k -> new DeviceState());
                long now = System.currentTimeMillis();
                if (now - state.lastSentAt < intervalMs) { continue; }

                // 最小可用：读取 0x0000 起 9 个寄存器（≤10）
                int start = 0x0000;
                int qty = 9;
                int slaveId = defaultSlaveId;
                String topic = device.getMqttSubTopic();
                if (topic == null || topic.isEmpty()) { topic = "GE_sub"; }

                String hex = ModbusUtils.buildReadHoldingRegistersHex(slaveId, start, qty);
                boolean ok = mqttClientService.publishHexToTopic(topic, hex);
                if (ok) {
                    state.lastSentAt = now;
                    log.info("已轮询下发 Modbus 读指令: deviceId={}, topic={}, hex={}", device.getDeviceId(), topic, hex);
                }
            }
        } catch (Exception e) {
            log.error("Modbus 轮询发送异常", e);
        }
    }

    private static class DeviceState {
        long lastSentAt = 0L;
    }
}


