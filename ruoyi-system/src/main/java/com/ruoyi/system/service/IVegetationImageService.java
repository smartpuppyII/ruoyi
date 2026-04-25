package com.ruoyi.system.service;

import com.ruoyi.system.domain.VegetationAnalysis;

/**
 * 植被图像分析服务接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IVegetationImageService 
{
    /**
     * 分析图片植被覆盖率
     * 
     * @param imagePath 图片路径
     * @return 植被覆盖率分析结果
     */
    public VegetationAnalysis analyzeVegetationCoverage(String imagePath);
}

