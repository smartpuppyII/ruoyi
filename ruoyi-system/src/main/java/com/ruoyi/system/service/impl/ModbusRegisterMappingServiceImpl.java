package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ModbusRegisterMapping;
import com.ruoyi.system.mapper.ModbusRegisterMappingMapper;
import com.ruoyi.system.service.IModbusRegisterMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modbus寄存器映射Service业务层处理
 */
@Service
public class ModbusRegisterMappingServiceImpl implements IModbusRegisterMappingService {
    
    @Autowired
    private ModbusRegisterMappingMapper modbusRegisterMappingMapper;

    /**
     * 根据设备ID查询寄存器映射配置
     * 
     * @param deviceId 设备ID
     * @return 寄存器映射配置列表
     */
    @Override
    public List<ModbusRegisterMapping> selectByDeviceId(Long deviceId) {
        return modbusRegisterMappingMapper.selectByDeviceId(deviceId);
    }

    /**
     * 根据寄存器地址查询映射配置
     * 
     * @param deviceId 设备ID
     * @param registerAddress 寄存器地址
     * @return 寄存器映射配置
     */
    @Override
    public ModbusRegisterMapping selectByRegisterAddress(Long deviceId, Integer registerAddress) {
        return modbusRegisterMappingMapper.selectByRegisterAddress(deviceId, registerAddress);
    }

    /**
     * 获取设备的所有传感器类型映射
     * 
     * @param deviceId 设备ID
     * @return 传感器类型映射Map
     */
    @Override
    public Map<String, ModbusRegisterMapping> getSensorTypeMapping(Long deviceId) {
        List<ModbusRegisterMapping> mappings = selectByDeviceId(deviceId);
        Map<String, ModbusRegisterMapping> result = new HashMap<>();
        
        for (ModbusRegisterMapping mapping : mappings) {
            result.put(mapping.getSensorType(), mapping);
        }
        
        return result;
    }

    /**
     * 根据寄存器索引获取传感器类型
     * 
     * @param deviceId 设备ID
     * @param registerIndex 寄存器索引
     * @return 传感器类型
     */
    @Override
    public String getSensorTypeByRegisterIndex(Long deviceId, int registerIndex) {
        List<ModbusRegisterMapping> mappings = selectByDeviceId(deviceId);
        
        if (registerIndex >= 0 && registerIndex < mappings.size()) {
            return mappings.get(registerIndex).getSensorType();
        }
        
        return "unknown";
    }
}
