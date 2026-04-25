package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.IotDevice;

/**
 * 物联网设备Mapper接口
 */
public interface IotDeviceMapper {
    /**
     * 查询物联网设备
     * 
     * @param deviceId 物联网设备主键
     * @return 物联网设备
     */
    public IotDevice selectIotDeviceByDeviceId(Long deviceId);

    /**
     * 查询物联网设备列表
     * 
     * @param iotDevice 物联网设备
     * @return 物联网设备集合
     */
    public List<IotDevice> selectIotDeviceList(IotDevice iotDevice);

    /**
     * 新增物联网设备
     * 
     * @param iotDevice 物联网设备
     * @return 结果
     */
    public int insertIotDevice(IotDevice iotDevice);

    /**
     * 修改物联网设备
     * 
     * @param iotDevice 物联网设备
     * @return 结果
     */
    public int updateIotDevice(IotDevice iotDevice);

    /**
     * 删除物联网设备
     * 
     * @param deviceId 物联网设备主键
     * @return 结果
     */
    public int deleteIotDeviceByDeviceId(Long deviceId);

    /**
     * 批量删除物联网设备
     * 
     * @param deviceIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIotDeviceByDeviceIds(Long[] deviceIds);
}