package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 植被覆盖率分析对象 vegetation_analysis
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public class VegetationAnalysis extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 图片文件名 */
    @Excel(name = "图片文件名")
    private String imageName;

    /** 图片路径 */
    @Excel(name = "图片路径")
    private String imagePath;

    /** 植被覆盖率（百分比） */
    @Excel(name = "植被覆盖率", readConverterExp = "百=分比")
    private Double coverageRate;

    /** 绿色像素数量 */
    @Excel(name = "绿色像素数量")
    private Long greenPixelCount;

    /** 总像素数量 */
    @Excel(name = "总像素数量")
    private Long totalPixelCount;

    /** 分析时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "分析时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date analysisTime;

    /** 设备ID（关联IoT设备） */
    @Excel(name = "设备ID")
    private Long deviceId;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setImageName(String imageName) 
    {
        this.imageName = imageName;
    }

    public String getImageName() 
    {
        return imageName;
    }
    public void setImagePath(String imagePath) 
    {
        this.imagePath = imagePath;
    }

    public String getImagePath() 
    {
        return imagePath;
    }
    public void setCoverageRate(Double coverageRate) 
    {
        this.coverageRate = coverageRate;
    }

    public Double getCoverageRate() 
    {
        return coverageRate;
    }
    public void setGreenPixelCount(Long greenPixelCount) 
    {
        this.greenPixelCount = greenPixelCount;
    }

    public Long getGreenPixelCount() 
    {
        return greenPixelCount;
    }
    public void setTotalPixelCount(Long totalPixelCount) 
    {
        this.totalPixelCount = totalPixelCount;
    }

    public Long getTotalPixelCount() 
    {
        return totalPixelCount;
    }
    public void setAnalysisTime(Date analysisTime) 
    {
        this.analysisTime = analysisTime;
    }

    public Date getAnalysisTime() 
    {
        return analysisTime;
    }
    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }
    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("imageName", getImageName())
            .append("imagePath", getImagePath())
            .append("coverageRate", getCoverageRate())
            .append("greenPixelCount", getGreenPixelCount())
            .append("totalPixelCount", getTotalPixelCount())
            .append("analysisTime", getAnalysisTime())
            .append("deviceId", getDeviceId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

