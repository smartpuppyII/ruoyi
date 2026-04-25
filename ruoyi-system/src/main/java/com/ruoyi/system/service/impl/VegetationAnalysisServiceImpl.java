package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.VegetationAnalysisMapper;
import com.ruoyi.system.domain.VegetationAnalysis;
import com.ruoyi.system.service.IVegetationAnalysisService;
import com.ruoyi.system.service.IVegetationImageService;

/**
 * 植被覆盖率分析Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class VegetationAnalysisServiceImpl implements IVegetationAnalysisService 
{
    @Autowired
    private VegetationAnalysisMapper vegetationAnalysisMapper;

    @Autowired
    private IVegetationImageService vegetationImageService;

    /**
     * 查询植被覆盖率分析
     * 
     * @param id 植被覆盖率分析主键
     * @return 植被覆盖率分析
     */
    @Override
    public VegetationAnalysis selectVegetationAnalysisById(Long id)
    {
        return vegetationAnalysisMapper.selectVegetationAnalysisById(id);
    }

    /**
     * 查询植被覆盖率分析列表
     * 
     * @param vegetationAnalysis 植被覆盖率分析
     * @return 植被覆盖率分析
     */
    @Override
    public List<VegetationAnalysis> selectVegetationAnalysisList(VegetationAnalysis vegetationAnalysis)
    {
        return vegetationAnalysisMapper.selectVegetationAnalysisList(vegetationAnalysis);
    }

    /**
     * 新增植被覆盖率分析
     * 
     * @param vegetationAnalysis 植被覆盖率分析
     * @return 结果
     */
    @Override
    public int insertVegetationAnalysis(VegetationAnalysis vegetationAnalysis)
    {
        vegetationAnalysis.setCreateTime(DateUtils.getNowDate());
        return vegetationAnalysisMapper.insertVegetationAnalysis(vegetationAnalysis);
    }

    /**
     * 修改植被覆盖率分析
     * 
     * @param vegetationAnalysis 植被覆盖率分析
     * @return 结果
     */
    @Override
    public int updateVegetationAnalysis(VegetationAnalysis vegetationAnalysis)
    {
        vegetationAnalysis.setUpdateTime(DateUtils.getNowDate());
        return vegetationAnalysisMapper.updateVegetationAnalysis(vegetationAnalysis);
    }

    /**
     * 批量删除植被覆盖率分析
     * 
     * @param ids 需要删除的植被覆盖率分析主键
     * @return 结果
     */
    @Override
    public int deleteVegetationAnalysisByIds(Long[] ids)
    {
        return vegetationAnalysisMapper.deleteVegetationAnalysisByIds(ids);
    }

    /**
     * 删除植被覆盖率分析信息
     * 
     * @param id 植被覆盖率分析主键
     * @return 结果
     */
    @Override
    public int deleteVegetationAnalysisById(Long id)
    {
        return vegetationAnalysisMapper.deleteVegetationAnalysisById(id);
    }

    /**
     * 根据设备ID查询最新的植被覆盖率分析
     * 
     * @param deviceId 设备ID
     * @return 植被覆盖率分析
     */
    @Override
    public VegetationAnalysis selectLatestVegetationAnalysisByDeviceId(Long deviceId)
    {
        return vegetationAnalysisMapper.selectLatestVegetationAnalysisByDeviceId(deviceId);
    }

    /**
     * 分析图片植被覆盖率
     * 
     * @param imagePath 图片路径
     * @param deviceId 设备ID
     * @return 植被覆盖率分析结果
     */
    @Override
    public VegetationAnalysis analyzeVegetationCoverage(String imagePath, Long deviceId)
    {
        try {
            // 调用图像处理服务分析植被覆盖率
            VegetationAnalysis result = vegetationImageService.analyzeVegetationCoverage(imagePath);
            
            if (result != null) {
                // 如果没有设备ID，使用默认值0
                result.setDeviceId(deviceId != null ? deviceId : 0L);
                result.setAnalysisTime(new Date());
                result.setCreateTime(DateUtils.getNowDate());
                
                // 保存分析结果到数据库
                insertVegetationAnalysis(result);
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("植被覆盖率分析失败: " + e.getMessage(), e);
        }
    }
}

