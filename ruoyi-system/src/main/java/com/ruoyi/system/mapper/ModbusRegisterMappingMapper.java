package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ModbusRegisterMapping;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * Modbus寄存器映射Mapper接口
 */
public interface ModbusRegisterMappingMapper {
    
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
    ModbusRegisterMapping selectByRegisterAddress(@Param("deviceId") Long deviceId, @Param("registerAddress") Integer registerAddress);
    
    /**
     * 查询所有启用的寄存器映射配置
     * 
     * @return 寄存器映射配置列表
     */
    List<ModbusRegisterMapping> selectAllActive();
}
