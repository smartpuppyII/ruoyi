package com.ruoyi.system.service;

import com.ruoyi.system.domain.IotData;
import java.util.List;
import java.util.Map;

/**
 * 物联网数据Service接口
 */
public interface IIotDataService {
    /**
     * 查询物联网数据
     * 
     * @param dataId 物联网数据主键
     * @return 物联网数据
     */
    public IotData selectIotDataByDataId(Long dataId);

    /**
     * 查询物联网数据列表
     * 
     * @param iotData 物联网数据
     * @return 物联网数据集合
     */
    public List<IotData> selectIotDataList(IotData iotData);

    /**
     * 新增物联网数据
     * 
     * @param iotData 物联网数据
     * @return 结果
     */
    public int insertIotData(IotData iotData);

    /**
     * 查询设备最新数据
     * 
     * @param deviceId 设备ID
     * @return 物联网数据
     */
    public IotData selectLatestDataByDeviceId(Long deviceId);

    /**
     * 查询设备历史数据
     * 
     * @param deviceId 设备ID
     * @param limit 限制条数
     * @return 物联网数据集合
     */
    public List<IotData> selectHistoryDataByDeviceId(Long deviceId, int limit);

    /**
     * 查询设备时间范围内的历史数据
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 物联网数据集合
     */
    public List<IotData> selectHistoryDataByTimeRange(Long deviceId, String startTime, String endTime, int limit);

    /**
     * 查询多设备的最新数据
     * 
     * @param deviceIds 设备ID列表
     * @return 物联网数据集合
     */
    public List<IotData> selectLatestDataByDeviceIds(List<Long> deviceIds);

    /**
     * 查询设备数据统计信息
     * 
     * @param deviceId 设备ID
     * @param hours 最近几小时
     * @return 统计信息Map
     */
    public Map<String, Object> selectDataStatistics(Long deviceId, int hours);
}