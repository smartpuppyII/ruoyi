package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Modbus寄存器映射对象 modbus_register_mapping
 */
public class ModbusRegisterMapping extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 设备ID */
    @Excel(name = "设备ID")
    private Long deviceId;

    /** 寄存器地址(十六进制) */
    @Excel(name = "寄存器地址(十六进制)")
    private String registerAddressHex;

    /** 寄存器地址(十进制) */
    @Excel(name = "寄存器地址(十进制)")
    private Integer registerAddressDec;

    /** 传感器名称 */
    @Excel(name = "传感器名称")
    private String sensorName;

    /** 传感器类型 */
    @Excel(name = "传感器类型")
    private String sensorType;

    /** 数据类型 */
    @Excel(name = "数据类型")
    private String dataType;

    /** 缩放因子 */
    @Excel(name = "缩放因子")
    private Double scaleFactor;

    /** 偏移值 */
    @Excel(name = "偏移值")
    private Double offsetValue;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 描述说明 */
    private String description;

    /** 是否启用 */
    @Excel(name = "是否启用")
    private Integer isActive;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setRegisterAddressHex(String registerAddressHex) {
        this.registerAddressHex = registerAddressHex;
    }

    public String getRegisterAddressHex() {
        return registerAddressHex;
    }

    public void setRegisterAddressDec(Integer registerAddressDec) {
        this.registerAddressDec = registerAddressDec;
    }

    public Integer getRegisterAddressDec() {
        return registerAddressDec;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setScaleFactor(Double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public Double getScaleFactor() {
        return scaleFactor;
    }

    public void setOffsetValue(Double offsetValue) {
        this.offsetValue = offsetValue;
    }

    public Double getOffsetValue() {
        return offsetValue;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getIsActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceId", getDeviceId())
            .append("registerAddressHex", getRegisterAddressHex())
            .append("registerAddressDec", getRegisterAddressDec())
            .append("sensorName", getSensorName())
            .append("sensorType", getSensorType())
            .append("dataType", getDataType())
            .append("scaleFactor", getScaleFactor())
            .append("offsetValue", getOffsetValue())
            .append("unit", getUnit())
            .append("description", getDescription())
            .append("isActive", getIsActive())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
