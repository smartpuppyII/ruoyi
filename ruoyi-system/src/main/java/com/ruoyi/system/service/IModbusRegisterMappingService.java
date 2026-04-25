package com.ruoyi.system.service;

import com.ruoyi.system.domain.ModbusRegisterMapping;
import java.util.List;
import java.util.Map;

/**
 * Modbus寄存器映射Service接口
 */
public interface IModbusRegisterMappingService {
    
    /**
     * 根据设备ID查询寄存器映射配置
     * 
     * @param deviceId 设备ID
     * @return 寄存器映射配置列表
     */
    List<ModbusRegisterMapping> selectByDeviceId(Long deviceId);
    
    /**
     * 根据寄存器地址查询映射配置
     * 
     * @param deviceId 设备ID
     * @param registerAddress 寄存器地址
     * @return 寄存器映射配置
     */
    ModbusRegisterMapping selectByRegisterAddress(Long deviceId, Integer registerAddress);
    
    /**
     * 获取设备的所有传感器类型映射
     * 
     * @param deviceId 设备ID
     * @return 传感器类型映射Map
     */
    Map<String, ModbusRegisterMapping> getSensorTypeMapping(Long deviceId);
    
    /**
     * 根据寄存器索引获取传感器类型
     * 
     * @param deviceId 设备ID
     * @param registerIndex 寄存器索引
     * @return 传感器类型
     */
    String getSensorTypeByRegisterIndex(Long deviceId, int registerIndex);
}
