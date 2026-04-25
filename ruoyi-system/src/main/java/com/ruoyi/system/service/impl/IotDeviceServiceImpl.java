package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.mqtt.service.MqttClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.IotDeviceMapper;
import com.ruoyi.system.domain.IotDevice;
import com.ruoyi.system.service.IIotDeviceService;

/**
 * 物联网设备Service业务层处理
 */
@Service
public class IotDeviceServiceImpl implements IIotDeviceService {
    @Autowired
    private IotDeviceMapper iotDeviceMapper;

    @Autowired
    private MqttClientService mqttClientService;

    /**
     * 查询物联网设备
     * 
     * @param deviceId 物联网设备主键
     * @return 物联网设备
     */
    @Override
    public IotDevice selectIotDeviceByDeviceId(Long deviceId) {
        return iotDeviceMapper.selectIotDeviceByDeviceId(deviceId);
    }

    /**
     * 查询物联网设备列表
     * 
     * @param iotDevice 物联网设备
     * @return 物联网设备
     */
    @Override
    public List<IotDevice> selectIotDeviceList(IotDevice iotDevice) {
        return iotDeviceMapper.selectIotDeviceList(iotDevice);
    }

    /**
     * 新增物联网设备
     * 
     * @param iotDevice 物联网设备
     * @return 结果
     */
    @Override
    public int insertIotDevice(IotDevice iotDevice) {
        iotDevice.setCreateTime(DateUtils.getNowDate());
        int rows = iotDeviceMapper.insertIotDevice(iotDevice);
        if (rows > 0) {
            // 订阅新设备的MQTT主题
            mqttClientService.subscribeToDevice(iotDevice);
        }
        return rows;
    }

    /**
     * 修改物联网设备
     * 
     * @param iotDevice 物联网设备
     * @return 结果
     */
    @Override
    public int updateIotDevice(IotDevice iotDevice) {
        IotDevice oldDevice = iotDeviceMapper.selectIotDeviceByDeviceId(iotDevice.getDeviceId());
        iotDevice.setUpdateTime(DateUtils.getNowDate());
        int rows = iotDeviceMapper.updateIotDevice(iotDevice);
        if (rows > 0 && oldDevice != null && 
            !iotDevice.getMqttPubTopic().equals(oldDevice.getMqttPubTopic())) {
            // 如果MQTT发布主题发生变化，需要重新订阅
            mqttClientService.unsubscribeFromDevice(oldDevice);
            mqttClientService.subscribeToDevice(iotDevice);
        }
        return rows;
    }

    /**
     * 批量删除物联网设备
     * 
     * @param deviceIds 需要删除的物联网设备主键
     * @return 结果
     */
    @Override
    public int deleteIotDeviceByDeviceIds(Long[] deviceIds) {
        // 取消订阅要删除的设备的MQTT主题
        for (Long deviceId : deviceIds) {
            IotDevice device = iotDeviceMapper.selectIotDeviceByDeviceId(deviceId);
            if (device != null) {
                mqttClientService.unsubscribeFromDevice(device);
            }
        }
        return iotDeviceMapper.deleteIotDeviceByDeviceIds(deviceIds);
    }

    /**
     * 删除物联网设备信息
     * 
     * @param deviceId 物联网设备主键
     * @return 结果
     */
    @Override
    public int deleteIotDeviceByDeviceId(Long deviceId) {
        IotDevice device = iotDeviceMapper.selectIotDeviceByDeviceId(deviceId);
        if (device != null) {
            mqttClientService.unsubscribeFromDevice(device);
        }
        return iotDeviceMapper.deleteIotDeviceByDeviceId(deviceId);
    }
}