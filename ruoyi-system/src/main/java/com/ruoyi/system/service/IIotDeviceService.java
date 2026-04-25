package com.ruoyi.system.service;

import com.ruoyi.system.domain.IotDevice;
import java.util.List;

/**
 * 物联网设备Service接口
 */
public interface IIotDeviceService {
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
     * 批量删除物联网设备
     * 
     * @param deviceIds 需要删除的物联网设备主键集合
     * @return 结果
     */
    public int deleteIotDeviceByDeviceIds(Long[] deviceIds);

    /**
     * 删除物联网设备信息
     * 
     * @param deviceId 物联网设备主键
     * @return 结果
     */
    public int deleteIotDeviceByDeviceId(Long deviceId);
}