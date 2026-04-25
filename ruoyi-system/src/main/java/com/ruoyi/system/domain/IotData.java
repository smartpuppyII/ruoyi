package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 物联网数据对象 iot_data
 */
public class IotData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 数据ID */
    private Long dataId;

    /** 设备ID */
    @Excel(name = "设备ID")
    private Long deviceId;

    /** 传感器数值 */
    @Excel(name = "传感器数值")
    private Double sensorValue;

    /** 原始数据 */
    @Excel(name = "原始数据")
    private String rawData;

    /** 数据类型 */
    @Excel(name = "数据类型")
    private String dataType;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 状态码 */
    @Excel(name = "状态码")
    private Integer statusCode;

    /** 消息ID */
    @Excel(name = "消息ID")
    private String messageId;

    /** Modbus寄存器地址 */
    @Excel(name = "Modbus寄存器地址")
    private Integer modbusAddress;

    /** Modbus功能码 */
    @Excel(name = "Modbus功能码")
    private Integer modbusFunctionCode;

    /** Modbus数据长度 */
    @Excel(name = "Modbus数据长度")
    private Integer modbusDataLength;

    /** Modbus CRC低字节 */
    @Excel(name = "Modbus CRC低字节")
    private Integer modbusCrcLow;

    /** Modbus CRC高字节 */
    @Excel(name = "Modbus CRC高字节")
    private Integer modbusCrcHigh;

    /** 传感器类型 */
    @Excel(name = "传感器类型")
    private String sensorType;

    /** 传感器原始值 */
    @Excel(name = "传感器原始值")
    private String sensorValueRaw;

    /** 传感器计算后的值 */
    @Excel(name = "传感器计算后的值")
    private Double sensorValueCalculated;

    /** 传感器单位 */
    @Excel(name = "传感器单位")
    private String sensorUnit;

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setSensorValue(Double sensorValue) {
        this.sensorValue = sensorValue;
    }

    public Double getSensorValue() {
        return sensorValue;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getRawData() {
        return rawData;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public Integer getModbusAddress() {
        return modbusAddress;
    }

    public void setModbusAddress(Integer modbusAddress) {
        this.modbusAddress = modbusAddress;
    }

    public Integer getModbusFunctionCode() {
        return modbusFunctionCode;
    }

    public void setModbusFunctionCode(Integer modbusFunctionCode) {
        this.modbusFunctionCode = modbusFunctionCode;
    }

    public Integer getModbusDataLength() {
        return modbusDataLength;
    }

    public void setModbusDataLength(Integer modbusDataLength) {
        this.modbusDataLength = modbusDataLength;
    }

    public Integer getModbusCrcLow() {
        return modbusCrcLow;
    }

    public void setModbusCrcLow(Integer modbusCrcLow) {
        this.modbusCrcLow = modbusCrcLow;
    }

    public Integer getModbusCrcHigh() {
        return modbusCrcHigh;
    }

    public void setModbusCrcHigh(Integer modbusCrcHigh) {
        this.modbusCrcHigh = modbusCrcHigh;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorValueRaw() {
        return sensorValueRaw;
    }

    public void setSensorValueRaw(String sensorValueRaw) {
        this.sensorValueRaw = sensorValueRaw;
    }

    public Double getSensorValueCalculated() {
        return sensorValueCalculated;
    }

    public void setSensorValueCalculated(Double sensorValueCalculated) {
        this.sensorValueCalculated = sensorValueCalculated;
    }

    public String getSensorUnit() {
        return sensorUnit;
    }

    public void setSensorUnit(String sensorUnit) {
        this.sensorUnit = sensorUnit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("dataId", getDataId())
            .append("deviceId", getDeviceId())
            .append("sensorValue", getSensorValue())
            .append("rawData", getRawData())
            .append("dataType", getDataType())
            .append("unit", getUnit())
            .append("statusCode", getStatusCode())
            .append("messageId", getMessageId())
            .append("modbusAddress", getModbusAddress())
            .append("modbusFunctionCode", getModbusFunctionCode())
            .append("modbusDataLength", getModbusDataLength())
            .append("modbusCrcLow", getModbusCrcLow())
            .append("modbusCrcHigh", getModbusCrcHigh())
            .append("sensorType", getSensorType())
            .append("sensorValueRaw", getSensorValueRaw())
            .append("sensorValueCalculated", getSensorValueCalculated())
            .append("sensorUnit", getSensorUnit())
            .append("createTime", getCreateTime())
            .toString();
    }
}