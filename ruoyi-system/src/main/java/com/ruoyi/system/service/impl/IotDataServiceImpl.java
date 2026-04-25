package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.IotDataMapper;
import com.ruoyi.system.domain.IotData;
import com.ruoyi.system.service.IIotDataService;

/**
 * 物联网数据Service业务层处理
 */
@Service
public class IotDataServiceImpl implements IIotDataService {
    @Autowired
    private IotDataMapper iotDataMapper;

    /**
     * 查询物联网数据
     * 
     * @param dataId 物联网数据主键
     * @return 物联网数据
     */
    @Override
    public IotData selectIotDataByDataId(Long dataId) {
        return iotDataMapper.selectIotDataByDataId(dataId);
    }

    /**
     * 查询物联网数据列表
     * 
     * @param iotData 物联网数据
     * @return 物联网数据
     */
    @Override
    public List<IotData> selectIotDataList(IotData iotData) {
        return iotDataMapper.selectIotDataList(iotData);
    }

    /**
     * 新增物联网数据
     * 
     * @param iotData 物联网数据
     * @return 结果
     */
    @Override
    public int insertIotData(IotData iotData) {
        return iotDataMapper.insertIotData(iotData);
    }

    /**
     * 查询设备最新数据
     * 
     * @param deviceId 设备ID
     * @return 物联网数据
     */
    @Override
    public IotData selectLatestDataByDeviceId(Long deviceId) {
        return iotDataMapper.selectLatestDataByDeviceId(deviceId);
    }

    /**
     * 查询设备历史数据
     * 
     * @param deviceId 设备ID
     * @param limit 限制条数
     * @return 物联网数据集合
     */
    @Override
    public List<IotData> selectHistoryDataByDeviceId(Long deviceId, int limit) {
        return iotDataMapper.selectHistoryDataByDeviceId(deviceId, limit);
    }

    /**
     * 查询设备时间范围内的历史数据
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 物联网数据集合
     */
    @Override
    public List<IotData> selectHistoryDataByTimeRange(Long deviceId, String startTime, String endTime, int limit) {
        return iotDataMapper.selectHistoryDataByTimeRange(deviceId, startTime, endTime, limit);
    }

    /**
     * 查询多设备的最新数据
     * 
     * @param deviceIds 设备ID列表
     * @return 物联网数据集合
     */
    @Override
    public List<IotData> selectLatestDataByDeviceIds(List<Long> deviceIds) {
        return iotDataMapper.selectLatestDataByDeviceIds(deviceIds);
    }

    /**
     * 查询设备数据统计信息
     * 
     * @param deviceId 设备ID
     * @param hours 最近几小时
     * @return 统计信息Map
     */
    @Override
    public Map<String, Object> selectDataStatistics(Long deviceId, int hours) {
        return iotDataMapper.selectDataStatistics(deviceId, hours);
    }
}