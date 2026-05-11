package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.IotData;
import com.ruoyi.system.service.IIotDataService;
import com.ruoyi.system.service.IIotDeviceService;
import com.ruoyi.system.mqtt.service.MqttClientService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MQTT数据接收Controller
 */
@RestController
@RequestMapping("/system/iotmqtt")
public class IotMqttController extends BaseController {
    
    @Autowired
    private IIotDeviceService deviceService;

    @Autowired
    private IIotDataService dataService;

    @Autowired
    private MqttClientService mqttClientService;

    /**
     * 接收MQTT数据
     */
    @PostMapping("/receive")
    public AjaxResult receiveData(@RequestBody IotData iotData) {
        return toAjax(dataService.insertIotData(iotData));
    }

    /**
     * 获取最新数据
     */
    @GetMapping("/latest/{deviceId}")
    public AjaxResult getLatestData(@PathVariable("deviceId") Long deviceId) {
        return AjaxResult.success(dataService.selectLatestDataByDeviceId(deviceId));
    }

    /**
     * 发送命令到设备
     */
    @PostMapping("/send/{deviceId}")
    public AjaxResult sendCommand(@PathVariable("deviceId") Long deviceId, 
                                 @RequestParam("command") String command) {
        boolean success = mqttClientService.sendCommandToDevice(deviceId, command);
        if (success) {
            return AjaxResult.success("命令发送成功");
        } else {
            return AjaxResult.error("命令发送失败");
        }
    }

    /**
     * 获取设备各传感器的最新值（聚合）
     */
    @GetMapping("/sensors/{deviceId}")
    public AjaxResult getLatestSensors(@PathVariable("deviceId") Long deviceId) {
        try {
            List<IotData> history = dataService.selectHistoryDataByDeviceId(deviceId, 200);
            Map<String, Object> sensors = new HashMap<>();
            Map<String, Boolean> filled = new HashMap<>();

            String[] types = new String[]{"temperature","humidity","conductivity","ph","nitrogen","phosphorus","potassium","salinity","tds"};
            for (String t : types) {
                filled.put(t, false);
            }

            if (history != null) {
                for (IotData d : history) {
                    String type = d.getSensorType();
                    if (type == null) continue;
                    Boolean already = filled.get(type);
                    if (already != null && !already) {
                        Double val = d.getSensorValueCalculated() != null ? d.getSensorValueCalculated() : d.getSensorValue();
                        sensors.put(type, val);
                        filled.put(type, true);
                    }
                }
            }

            return AjaxResult.success(sensors);
        } catch (Exception e) {
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取设备传感器数据统计信息
     */
    @GetMapping("/statistics/{deviceId}")
    public AjaxResult getSensorStatistics(@PathVariable("deviceId") Long deviceId,
                                        @RequestParam(value = "hours", defaultValue = "24") int hours) {
        try {
            Map<String, Object> statistics = dataService.selectDataStatistics(deviceId, hours);
            return AjaxResult.success(statistics);
        } catch (Exception e) {
            return AjaxResult.error("查询统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取设备传感器历史数据（时间范围）
     */
    @GetMapping("/history/{deviceId}")
    public AjaxResult getSensorHistory(@PathVariable("deviceId") Long deviceId,
                                     @RequestParam(value = "startTime", required = false) String startTime,
                                     @RequestParam(value = "endTime", required = false) String endTime,
                                     @RequestParam(value = "limit", defaultValue = "100") int limit) {
        try {
            List<IotData> history = dataService.selectHistoryDataByTimeRange(deviceId, startTime, endTime, limit);
            return AjaxResult.success(history);
        } catch (Exception e) {
            return AjaxResult.error("查询历史数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取多设备最新数据
     */
    @PostMapping("/latest/multiple")
    public AjaxResult getMultipleDevicesLatest(@RequestBody List<Long> deviceIds) {
        try {
            List<IotData> latestData = dataService.selectLatestDataByDeviceIds(deviceIds);
            return AjaxResult.success(latestData);
        } catch (Exception e) {
            return AjaxResult.error("查询多设备数据失败: " + e.getMessage());
        }
    }
}