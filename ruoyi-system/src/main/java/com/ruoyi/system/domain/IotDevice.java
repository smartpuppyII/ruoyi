package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 物联网设备对象 iot_device
 */
public class IotDevice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 设备ID */
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 设备类型 */
    @Excel(name = "设备类型")
    private String deviceType;

    /** MQTT主题 */
    @Excel(name = "MQTT发布主题")
    private String mqttPubTopic;
    
    /** MQTT订阅主题 */
    @Excel(name = "MQTT订阅主题")
    private String mqttSubTopic;

    /** MQTT Broker */
    @Excel(name = "MQTT Broker")
    private String mqttBroker;

    /** 设备状态（0正常 1停用） */
    @Excel(name = "设备状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 设备IP地址 */
    @Excel(name = "设备IP地址")
    private String ipAddress;

    /** 最后通信时间 */
    @Excel(name = "最后通信时间")
    private String lastCommunication;

    /** 信号强度 */
    @Excel(name = "信号强度")
    private Integer signalStrength;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setMqttPubTopic(String mqttPubTopic) {
        this.mqttPubTopic = mqttPubTopic;
    }

    public String getMqttPubTopic() {
        return mqttPubTopic;
    }

    public void setMqttSubTopic(String mqttSubTopic) {
        this.mqttSubTopic = mqttSubTopic;
    }

    public String getMqttSubTopic() {
        return mqttSubTopic;
    }

    public void setMqttBroker(String mqttBroker) {
        this.mqttBroker = mqttBroker;
    }

    public String getMqttBroker() {
        return mqttBroker;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setLastCommunication(String lastCommunication) {
        this.lastCommunication = lastCommunication;
    }

    public String getLastCommunication() {
        return lastCommunication;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("deviceType", getDeviceType())
            .append("mqttPubTopic", getMqttPubTopic())
            .append("mqttSubTopic", getMqttSubTopic())
            .append("mqttBroker", getMqttBroker())
            .append("status", getStatus())
            .append("ipAddress", getIpAddress())
            .append("lastCommunication", getLastCommunication())
            .append("signalStrength", getSignalStrength())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}