package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.VegetationAnalysis;

/**
 * 植被覆盖率分析Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Mapper
public interface VegetationAnalysisMapper 
{
    /**
     * 查询植被覆盖率分析
     * 
     * @param id 植被覆盖率分析主键
     * @return 植被覆盖率分析
     */
    public VegetationAnalysis selectVegetationAnalysisById(Long id);

    /**
     * 查询植被覆盖率分析列表
     * 
     * @param vegetationAnalysis 植被覆盖率分析
     * @return 植被覆盖率分析集合
     */
    public List<VegetationAnalysis> selectVegetationAnalysisList(VegetationAnalysis vegetationAnalysis);

    /**
     * 新增植被覆盖率分析
     * 
     * @param vegetationAnalysis 植被覆盖率分析
     * @return 结果
     */
    public int insertVegetationAnalysis(VegetationAnalysis vegetationAnalysis);

    /**
     * 修改植被覆盖率分析
     * 
     * @param vegetationAnalysis 植被覆盖率分析
     * @return 结果
     */
    public int updateVegetationAnalysis(VegetationAnalysis vegetationAnalysis);

    /**
     * 删除植被覆盖率分析
     * 
     * @param id 植被覆盖率分析主键
     * @return 结果
     */
    public int deleteVegetationAnalysisById(Long id);

    /**
     * 批量删除植被覆盖率分析
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVegetationAnalysisByIds(Long[] ids);

    /**
     * 根据设备ID查询最新的植被覆盖率分析
     * 
     * @param deviceId 设备ID
     * @return 植被覆盖率分析
     */
    public VegetationAnalysis selectLatestVegetationAnalysisByDeviceId(Long deviceId);
}

