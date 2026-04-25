package com.ruoyi.system.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.ruoyi.system.domain.VegetationAnalysis;
import com.ruoyi.system.service.IVegetationImageService;

/**
 * 植被图像分析服务实现类
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class VegetationImageServiceImpl implements IVegetationImageService 
{
    /**
     * 分析图片植被覆盖率
     * 
     * @param imagePath 图片路径
     * @return 植被覆盖率分析结果
     */
    @Override
    public VegetationAnalysis analyzeVegetationCoverage(String imagePath) 
    {
        try {
            // 使用Java原生图像处理分析植被覆盖率
            return analyzeVegetationCoverageJava(imagePath);
        } catch (Exception e) {
            throw new RuntimeException("图像分析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用Java原生图像处理分析植被覆盖率（备用方案）
     * 
     * @param imagePath 图片路径
     * @return 植被覆盖率分析结果
     */
    public VegetationAnalysis analyzeVegetationCoverageJava(String imagePath) 
    {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            if (image == null) {
                throw new RuntimeException("无法读取图片: " + imagePath);
            }

            int width = image.getWidth();
            int height = image.getHeight();
            long totalPixels = (long) width * height;
            long greenPixels = 0;

            // 遍历每个像素
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // 判断是否为绿色像素（绿色分量大于红色和蓝色分量）
                    if (isGreenPixel(red, green, blue)) {
                        greenPixels++;
                    }
                }
            }

            // 计算植被覆盖率
            double coverageRate = (double) greenPixels / totalPixels * 100.0;

            // 创建分析结果对象
            VegetationAnalysis result = new VegetationAnalysis();
            result.setImageName(new File(imagePath).getName());
            result.setImagePath(imagePath);
            result.setCoverageRate(Math.round(coverageRate * 100.0) / 100.0);
            result.setGreenPixelCount(greenPixels);
            result.setTotalPixelCount(totalPixels);
            result.setAnalysisTime(new Date());
            result.setRemark("基于ExG指数和多特征融合的植被识别算法");

            return result;

        } catch (IOException e) {
            throw new RuntimeException("图像分析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 判断是否为绿色像素（优化算法）
     * 
     * @param red 红色分量
     * @param green 绿色分量
     * @param blue 蓝色分量
     * @return 是否为绿色像素
     */
    private boolean isGreenPixel(int red, int green, int blue) 
    {
        // 使用多种颜色空间进行植被识别
        
        // 1. RGB判断：绿色分量明显大于红色和蓝色
        boolean rgbTest = green > red && green > blue && green > 30;
        
        // 2. 归一化绿色指数 (ExG - Excess Green Index)
        // ExG = 2*g - r - b (归一化到0-1范围)
        double r = red / 255.0;
        double g = green / 255.0;
        double b = blue / 255.0;
        double exg = 2 * g - r - b;
        boolean exgTest = exg > 0.1;
        
        // 3. 避免过暗或过亮的像素
        int brightness = (red + green + blue) / 3;
        boolean brightnessTest = brightness > 20 && brightness < 240;
        
        // 4. 色度和饱和度检查（避免灰色和白色）
        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        double saturation = (max == 0) ? 0 : (max - min) / (double) max;
        boolean saturationTest = saturation > 0.15;
        
        // 5. 排除偏黄色（成熟植被或枯草）
        boolean notYellow = !(green > 120 && red > 100 && blue < 80);
        
        // 综合判断
        return rgbTest && exgTest && brightnessTest && saturationTest && notYellow;
    }
}
