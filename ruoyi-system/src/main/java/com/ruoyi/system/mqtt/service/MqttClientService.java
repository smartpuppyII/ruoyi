package com.ruoyi.system.mqtt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.IotData;
import com.ruoyi.system.domain.IotDevice;
import com.ruoyi.system.mqtt.config.MqttConfig;
import com.ruoyi.system.service.IIotDataService;
import com.ruoyi.system.service.IIotDeviceService;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class MqttClientService {
    private static final Logger log = LoggerFactory.getLogger(MqttClientService.class);

    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private MqttConnectOptions mqttConnectOptions;

    @Autowired
    private IIotDeviceService deviceService;

    @Autowired
    private IIotDataService dataService;



    private MqttClient mqttClient;
    
    // 存储设备ID与MQTT主题的映射关系
    private Map<String, Long> topicDeviceMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            // 创建MQTT客户端，使用随机客户端ID
            String clientId = mqttConfig.getClientId() + "_" + UUID.randomUUID().toString();
            mqttClient = new MqttClient(mqttConfig.getBrokerUrl(), clientId);

            // 设置回调
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.error("MQTT连接丢失", cause);
                    reconnect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    handleMessage(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // 消息发送完成回调
                }
            });

            // 连接MQTT服务器
            mqttClient.connect(mqttConnectOptions);

            // 订阅所有设备主题
            subscribeToAllDevices();

        } catch (MqttException e) {
            log.error("MQTT客户端初始化失败", e);
        }
    }

    private void subscribeToAllDevices() {
        try {
            // 订阅默认主题
            String defaultTopic = mqttConfig.getDefaultTopic();
            if (defaultTopic != null && !defaultTopic.isEmpty()) {
                mqttClient.subscribe(defaultTopic);
                log.info("已订阅默认主题: {}", defaultTopic);
            }
            
            // 只订阅GE_pub主题（从broker.emqx.io接收传感器数据）
            mqttClient.subscribe("GE_pub");
            log.info("已订阅GE_pub主题");
            
            // 不再订阅GE_sub主题
            log.info("不订阅GE_sub主题，只接收传感器响应数据");
            
            // 订阅设备特定主题
            List<IotDevice> devices = deviceService.selectIotDeviceList(new IotDevice());
            for (IotDevice device : devices) {
                if (device.getMqttPubTopic() != null && !device.getMqttPubTopic().isEmpty()) {
                    // 存储主题与设备ID的映射关系
                    topicDeviceMap.put(device.getMqttPubTopic(), device.getDeviceId());
                    log.info("映射主题到设备: {} -> {}", device.getMqttPubTopic(), device.getDeviceId());
                }
            }
        } catch (MqttException e) {
            log.error("订阅设备主题失败", e);
        }
    }

    private void reconnect() {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.reconnect();
                subscribeToAllDevices();
                log.info("MQTT客户端重连成功");
            }
        } catch (MqttException e) {
            log.error("MQTT客户端重连失败", e);
        }
    }

    private void handleMessage(String topic, MqttMessage message) {
        try {
            byte[] payloadBytes = message.getPayload();
            
            // 首先尝试将二进制数据转换为HEX字符串
            String hexPayload = bytesToHexString(payloadBytes);
            
            // 尝试将字节数组转换为字符串（用于JSON等文本数据）
            String textPayload;
            try {
                textPayload = new String(payloadBytes, "UTF-8");
                // 检查是否为有效的UTF-8文本
                if (!isValidUtf8(textPayload)) {
                    textPayload = hexPayload; // 如果不是有效UTF-8，使用HEX字符串
                }
            } catch (Exception e) {
                textPayload = hexPayload; // 转换失败，使用HEX字符串
            }
            
            log.info("收到MQTT消息: topic={}, hexPayload={}, textPayload={}, payloadLength={}", 
                    topic, hexPayload, textPayload, payloadBytes.length);
            
            // 获取设备ID - 支持GE_pub和GE_sub主题
            Long deviceId = topicDeviceMap.get(topic);
            if (deviceId == null) {
                // 如果是GE_pub或GE_sub主题，使用第一个可用设备
                if ("GE_pub".equals(topic) || "GE_sub".equals(topic)) {
                    List<IotDevice> devices = deviceService.selectIotDeviceList(new IotDevice());
                    if (!devices.isEmpty()) {
                        deviceId = devices.get(0).getDeviceId();
                        log.info("{}主题消息分配给设备ID: {}", topic, deviceId);
                    }
                }
                
                if (deviceId == null) {
                    log.warn("未找到主题对应的设备: {}", topic);
                    return;
                }
            }
            
            // 首先尝试解析Modbus数据（支持HEX字符串和二进制格式）
            if (isModbusData(hexPayload, payloadBytes)) {
                handleModbusData(deviceId, hexPayload, payloadBytes);
                return;
            }
            
            // 尝试解析JSON数据
            try {
                JSONObject jsonData = JSON.parseObject(textPayload);
                
                // 保存数据
                IotData data = new IotData();
                data.setDeviceId(deviceId);
                data.setRawData(textPayload);
                data.setCreateTime(DateUtils.getNowDate());
                
                // 尝试提取常见字段
                if (jsonData.containsKey("test")) {
                    // 处理测试数据格式: {"test":"hello","value":83,"timestamp":1754900243678}
                    data.setDataType("test");
                    if (jsonData.containsKey("value")) {
                        data.setSensorValue(jsonData.getDoubleValue("value"));
                    } else {
                        data.setSensorValue(0.0);
                    }
                } else if (jsonData.containsKey("msg")) {
                    data.setDataType("message");
                    data.setSensorValue(0.0);
                } else if (jsonData.containsKey("data")) {
                    // 处理嵌套的数据结构
                    JSONObject dataObj = jsonData.getJSONObject("data");
                    if (dataObj.containsKey("temperature")) {
                        data.setDataType("temperature");
                        data.setSensorValue(dataObj.getDoubleValue("temperature"));
                        data.setUnit("°C");
                    } else if (dataObj.containsKey("humidity")) {
                        data.setDataType("humidity");
                        data.setSensorValue(dataObj.getDoubleValue("humidity"));
                        data.setUnit("%");
                    } else if (dataObj.containsKey("pressure")) {
                        data.setDataType("pressure");
                        data.setSensorValue(dataObj.getDoubleValue("pressure"));
                        data.setUnit("hPa");
                    }
                } else if (jsonData.containsKey("value")) {
                    // 处理直接包含value字段的数据
                    data.setDataType("sensor");
                    data.setSensorValue(jsonData.getDoubleValue("value"));
                } else {
                    // 默认处理
                    data.setDataType("json");
                    data.setSensorValue(0.0);
                }
                
                // 提取消息ID和状态
                if (jsonData.containsKey("messageId")) {
                    data.setMessageId(jsonData.getString("messageId"));
                }
                if (jsonData.containsKey("status")) {
                    data.setStatusCode(jsonData.getIntValue("status"));
                }
                
                // 保存数据
                dataService.insertIotData(data);
                
                // 更新设备状态
                updateDeviceStatus(deviceId, jsonData);
                
                // WebSocket推送已禁用 - 使用定时器刷新数据
                log.debug("数据已保存到数据库，设备ID: {}", deviceId);
                
            } catch (Exception e) {
                // 非JSON格式或解析失败，作为普通文本处理
                IotData data = new IotData();
                data.setDeviceId(deviceId);
                data.setRawData(textPayload);
                data.setDataType("text");
                data.setSensorValue(0.0);
                data.setCreateTime(DateUtils.getNowDate());
                
                // 保存数据
                dataService.insertIotData(data);
                
                // WebSocket推送已禁用 - 使用定时器刷新数据
                log.debug("数据已保存到数据库，设备ID: {}", deviceId);
            }
            
        } catch (Exception e) {
            log.error("处理MQTT消息失败", e);
        }
    }
    
    /**
     * 判断是否为Modbus数据
     */
    private boolean isModbusData(String payload, byte[] payloadBytes) {
        if (payload == null || payload.trim().isEmpty()) {
            return false;
        }
        
        // 方法1：检查是否为十六进制字符串格式
        String cleanPayload = payload.replaceAll("\\s+", "").toUpperCase();
        if (cleanPayload.matches("^[0-9A-F]+$")) {
            // 检查长度是否合理（至少6字节：地址+功能码+数据+CRC）
            if (cleanPayload.length() < 12) {
                return false;
            }
            
            // 检查是否为Modbus RTU格式（功能码03、06、10等）
            if (cleanPayload.length() >= 4) {
                try {
                    String functionCode = cleanPayload.substring(2, 4);
                    int funcCode = Integer.parseInt(functionCode, 16);
                    return funcCode == 0x03 || funcCode == 0x06 || funcCode == 0x10;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        
        // 方法2：检查是否为二进制格式（直接字节数组）
        if (payloadBytes != null && payloadBytes.length >= 6) {
            // 检查功能码（第2个字节）
            int functionCode = payloadBytes[1] & 0xFF;
            return functionCode == 0x03 || functionCode == 0x06 || functionCode == 0x10;
        }
        
        return false;
    }
    
    /**
     * 处理Modbus数据
     */
    private void handleModbusData(Long deviceId, String payload, byte[] payloadBytes) {
        try {
            log.info("开始解析Modbus数据: payload={}, payloadLength={}", payload, payloadBytes != null ? payloadBytes.length : 0);
            
            String cleanPayload;
            ModbusFrame frame;
            
            // 判断数据格式并解析
            if (payload.replaceAll("\\s+", "").toUpperCase().matches("^[0-9A-F]+$")) {
                // HEX字符串格式
                cleanPayload = payload.replaceAll("\\s+", "").toUpperCase();
                frame = parseModbusFrame(cleanPayload);
            } else if (payloadBytes != null && payloadBytes.length >= 6) {
                // 二进制格式
                cleanPayload = bytesToHexString(payloadBytes);
                frame = parseModbusFrameFromBytes(payloadBytes);
            } else {
                log.warn("不支持的Modbus数据格式: {}", payload);
                return;
            }
            
            if (frame == null) {
                log.warn("Modbus数据解析失败: {}", payload);
                return;
            }
            
            // 如果是功能码03（读保持寄存器），解析并保存传感器数据
            if (frame.functionCode == 0x03) {
                if (frame.isRequest) {
                    // 这是请求帧，只保存请求信息
                    IotData data = new IotData();
                    data.setDeviceId(deviceId);
                    data.setRawData(cleanPayload);
                    data.setDataType("modbus_request");
                    data.setSensorValue(0.0);
                    data.setCreateTime(DateUtils.getNowDate());
                    
                    // 设置Modbus相关字段
                    data.setModbusAddress(frame.startAddress);
                    data.setModbusFunctionCode(frame.functionCode);
                    data.setModbusDataLength(frame.dataLength);
                    data.setModbusCrcLow(frame.crcLow);
                    data.setModbusCrcHigh(frame.crcHigh);
                    
                    // 保存数据
                    dataService.insertIotData(data);
                    
                    log.info("Modbus请求数据保存完成，设备ID: {}, 起始地址: {}, 寄存器数量: {}", 
                            deviceId, frame.startAddress, frame.dataLength);
                } else {
                    // 这是响应帧，解析并保存传感器数据
                    parseModbusReadResponse(deviceId, frame, cleanPayload, payloadBytes);
                    
                    // 保存原始Modbus数据（包含解析后的传感器信息）
                    IotData data = new IotData();
                    data.setDeviceId(deviceId);
                    data.setRawData(cleanPayload);
                    data.setDataType("modbus_response");
                    data.setSensorValue(0.0);
                    data.setCreateTime(DateUtils.getNowDate());
                    
                    // 设置Modbus相关字段
                    data.setModbusAddress(frame.startAddress);
                    data.setModbusFunctionCode(frame.functionCode);
                    data.setModbusDataLength(frame.dataLength);
                    data.setModbusCrcLow(frame.crcLow);
                    data.setModbusCrcHigh(frame.crcHigh);
                    
                    // 保存数据
                    dataService.insertIotData(data);
                    
                    log.info("Modbus响应数据解析完成，设备ID: {}, 地址: {}, 功能码: {}, 数据长度: {}", 
                            deviceId, frame.startAddress, frame.functionCode, frame.dataLength);
                }
            } else {
                // 其他功能码，只保存原始数据
                IotData data = new IotData();
                data.setDeviceId(deviceId);
                data.setRawData(cleanPayload);
                data.setDataType("modbus");
                data.setSensorValue(0.0);
                data.setCreateTime(DateUtils.getNowDate());
                
                // 设置Modbus相关字段
                data.setModbusAddress(frame.startAddress);
                data.setModbusFunctionCode(frame.functionCode);
                data.setModbusDataLength(frame.dataLength);
                data.setModbusCrcLow(frame.crcLow);
                data.setModbusCrcHigh(frame.crcHigh);
                
                // 保存数据
                dataService.insertIotData(data);
                
                log.info("Modbus数据保存完成，设备ID: {}, 地址: {}, 功能码: {}, 数据长度: {}", 
                        deviceId, frame.startAddress, frame.functionCode, frame.dataLength);
            }
            
        } catch (Exception e) {
            log.error("处理Modbus数据失败: {}", payload, e);
        }
    }
    
    /**
     * 解析Modbus帧结构
     */
    private ModbusFrame parseModbusFrame(String hexData) {
        try {
            if (hexData.length() < 6) {
                log.warn("Modbus数据长度不足: {}", hexData);
                return null;
            }
            
            ModbusFrame frame = new ModbusFrame();
            
            // 地址码（1字节）
            frame.address = Integer.parseInt(hexData.substring(0, 2), 16);
            
            // 功能码（1字节）
            frame.functionCode = Integer.parseInt(hexData.substring(2, 4), 16);
            
            if (frame.functionCode == 0x03) {
                // 检查是请求还是响应
                if (hexData.length() == 16) {
                    // 这是读保持寄存器请求: 地址(1) + 功能码(1) + 起始地址(2) + 寄存器数量(2) + CRC(2) = 8字节 = 16个HEX字符
                    frame.isRequest = true;
                    frame.startAddress = Integer.parseInt(hexData.substring(4, 8), 16); // 起始地址
                    frame.dataLength = Integer.parseInt(hexData.substring(8, 12), 16); // 寄存器数量
                    
                    // CRC校验码（2字节）
                    frame.crcLow = Integer.parseInt(hexData.substring(12, 14), 16);
                    frame.crcHigh = Integer.parseInt(hexData.substring(14, 16), 16);
                    
                    log.info("Modbus请求帧解析成功: 地址={}, 功能码={}, 起始地址={}, 寄存器数量={}, CRC={:02X}{:02X}", 
                            frame.address, frame.functionCode, frame.startAddress, frame.dataLength, frame.crcLow, frame.crcHigh);
                    
                } else if (hexData.length() >= 8) {
                    // 这是读保持寄存器响应: 地址(1) + 功能码(1) + 字节数(1) + 数据(N*2) + CRC(2)
                    frame.isRequest = false;
                    
                    // 字节数（1字节）
                    frame.byteCount = Integer.parseInt(hexData.substring(4, 6), 16);
                    
                    // 数据长度（寄存器数量）
                    frame.dataLength = frame.byteCount / 2;
                    
                    // 起始地址（从数据中推断，这里假设从0开始）
                    frame.startAddress = 0;
                    
                    // 数据区域
                    int dataStart = 6;
                    int dataEnd = dataStart + frame.byteCount * 2; // 每个字节对应2个HEX字符
                    if (hexData.length() >= dataEnd + 4) {
                        frame.data = hexData.substring(dataStart, dataEnd);
                        
                        // CRC校验码（2字节）
                        frame.crcLow = Integer.parseInt(hexData.substring(dataEnd, dataEnd + 2), 16);
                        frame.crcHigh = Integer.parseInt(hexData.substring(dataEnd + 2, dataEnd + 4), 16);
                        
                        log.info("Modbus响应帧解析成功: 地址={}, 功能码={}, 字节数={}, 数据长度={}, 数据={}, CRC={:02X}{:02X}", 
                                frame.address, frame.functionCode, frame.byteCount, frame.dataLength, frame.data, frame.crcLow, frame.crcHigh);
                    } else {
                        log.warn("Modbus响应数据CRC部分缺失: 数据长度={}, 需要长度={}", hexData.length(), dataEnd + 4);
                    }
                }
            }
            
            return frame;
            
        } catch (Exception e) {
            log.error("解析Modbus帧失败: {}", hexData, e);
            return null;
        }
    }
    
    /**
     * 解析Modbus读响应数据
     */
    private void parseModbusReadResponse(Long deviceId, ModbusFrame frame, String hexData, byte[] payloadBytes) {
        try {
            if (frame.data == null || frame.data.isEmpty()) {
                log.warn("Modbus数据区域为空");
                return;
            }
            
            log.info("开始解析Modbus读响应数据: 数据长度={}, 数据内容={}", frame.dataLength, frame.data);
            
            // 根据数据长度，解析不同的传感器数据
            int dataLength = frame.dataLength;
            int dataIndex = 0;
            
            // 解析传感器数据（每个寄存器2字节=4个HEX字符）
            for (int registerIndex = 0; registerIndex < dataLength && (dataIndex + 4) <= frame.data.length(); registerIndex++) {
                String registerData = frame.data.substring(dataIndex, dataIndex + 4);
                int registerValue = Integer.parseInt(registerData, 16);
                
                // 使用改进的传感器类型识别和值计算
                String sensorType = getSensorTypeByRegisterIndex(registerIndex);
                String sensorUnit = getSensorUnitByType(sensorType);
                double calculatedValue = calculateSensorValue(registerValue, sensorType);
                
                log.info("解析寄存器{}: 原始值=0x{} ({}), 传感器类型={}, 计算值={}, 单位={}", 
                        registerIndex, registerData, registerValue, sensorType, calculatedValue, sensorUnit);
                
                saveSensorData(deviceId, sensorType, registerValue, calculatedValue, sensorUnit, hexData, registerIndex, payloadBytes);
                
                dataIndex += 4;
            }
            
        } catch (Exception e) {
            log.error("解析Modbus读响应数据失败", e);
        }
    }
    
    /**
     * 根据寄存器索引获取传感器类型
     */
    private String getSensorTypeByRegisterIndex(int index) {
        switch (index) {
            case 0: return "humidity";      // 含水率 0000H
            case 1: return "temperature";   // 温度值 0001H
            case 2: return "conductivity";  // 电导率 0002H
            case 3: return "ph";            // PH值 0003H
            case 4: return "nitrogen";      // 氮含量 0004H
            case 5: return "phosphorus";    // 磷含量 0005H
            case 6: return "potassium";     // 钾含量 0006H
            case 7: return "salinity";      // 盐度 0007H
            case 8: return "tds";           // TDS 0008H
            default: return "unknown";
        }
    }
    
    /**
     * 根据传感器类型获取单位
     */
    private String getSensorUnitByType(String sensorType) {
        switch (sensorType) {
            case "humidity": return "%";
            case "temperature": return "°C";
            case "conductivity": return "μS/cm";
            case "ph": return "";
            case "nitrogen": return "mg/kg";
            case "phosphorus": return "mg/kg";
            case "potassium": return "mg/kg";
            case "salinity": return "‰";
            case "tds": return "mg/L";
            default: return "unknown";
        }
    }
    
    /**
     * 计算传感器值 - 改进版本
     */
    private double calculateSensorValue(int rawValue, String sensorType) {
        try {
            switch (sensorType) {
                case "temperature":
                    // 温度：负值以补码形式，需要特殊处理
                    if (rawValue > 0x7FFF) {
                        // 16位有符号整数的补码转换
                        rawValue = rawValue - 0x10000;
                    }
                    return rawValue * 0.1; // 扩大10倍
                case "humidity":
                    return rawValue * 0.1; // 扩大10倍
                case "ph":
                    return rawValue * 0.1; // 扩大10倍
                case "conductivity":
                case "nitrogen":
                case "phosphorus":
                case "potassium":
                case "salinity":
                case "tds":
                    return rawValue * 1.0; // 原始值
                default:
                    return rawValue * 1.0;
            }
        } catch (Exception e) {
            log.error("计算传感器值失败: rawValue={}, sensorType={}", rawValue, sensorType, e);
            return 0.0;
        }
    }
    
    /**
     * 保存传感器数据 - 改进版本
     */
    private void saveSensorData(Long deviceId, String sensorType, int rawValue, 
                               double calculatedValue, String unit, String rawData, int registerIndex, byte[] payloadBytes) {
        try {
            IotData data = new IotData();
            data.setDeviceId(deviceId);
            data.setRawData(rawData);
            data.setDataType("modbus_sensor");
            data.setSensorValue(calculatedValue);
            data.setCreateTime(DateUtils.getNowDate());
            
            // 设置传感器相关字段
            data.setSensorType(sensorType);
            data.setSensorValueRaw(String.format("0x%04X", rawValue));
            data.setSensorValueCalculated(calculatedValue);
            data.setSensorUnit(unit);
            
            // 设置Modbus相关字段
            data.setModbusAddress(registerIndex);
            data.setModbusFunctionCode(0x03);
            data.setModbusDataLength(1);
            
            // 保存数据
            dataService.insertIotData(data);
            
            log.info("保存传感器数据: 设备ID={}, 类型={}, 原始值=0x{} ({}), 计算值={}, 单位={}", 
                     deviceId, sensorType, String.format("%04X", rawValue), rawValue, calculatedValue, unit);
            
        } catch (Exception e) {
            log.error("保存传感器数据失败: 设备ID={}, 类型={}", deviceId, sensorType, e);
        }
    }
    
    /**
     * Modbus帧结构类
     */
    private static class ModbusFrame {
        int address;           // 地址码
        int functionCode;      // 功能码
        int byteCount;         // 字节数
        int dataLength;        // 数据长度（寄存器数量）
        int startAddress;      // 起始地址
        String data;           // 数据区域
        int crcLow;            // CRC低字节
        int crcHigh;           // CRC高字节
        boolean isRequest;     // 是否为请求帧
    }
    
    /**
     * 检查字符串是否为有效的UTF-8编码
     */
    private boolean isValidUtf8(String text) {
        if (text == null) {
            return false;
        }
        
        // 检查是否包含控制字符或不可打印字符
        for (char c : text.toCharArray()) {
            if (c < 32 && c != 9 && c != 10 && c != 13) { // 排除制表符、换行符、回车符
                return false;
            }
            if (c > 126 && c < 160) { // 排除一些特殊字符
                return false;
            }
        }
        
        // 检查是否包含常见的乱码字符
        if (text.contains("") || text.contains("□") || text.contains("")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 将字节数组转换为十六进制字符串
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
    
    /**
     * 从字节数组解析Modbus帧结构
     */
    private ModbusFrame parseModbusFrameFromBytes(byte[] bytes) {
        try {
            if (bytes.length < 6) {
                log.warn("Modbus字节数据长度不足: {}", bytes.length);
                return null;
            }
            
            ModbusFrame frame = new ModbusFrame();
            
            // 地址码（1字节）
            frame.address = bytes[0] & 0xFF;
            
            // 功能码（1字节）
            frame.functionCode = bytes[1] & 0xFF;
            
            if (frame.functionCode == 0x03) {
                // 读保持寄存器响应
                if (bytes.length < 8) {
                    log.warn("读保持寄存器响应数据长度不足: {}", bytes.length);
                    return null;
                }
                
                // 字节数（1字节）
                frame.byteCount = bytes[2] & 0xFF;
                
                // 数据长度（寄存器数量）
                frame.dataLength = frame.byteCount / 2;
                
                // 起始地址（从数据中推断，这里假设从0开始）
                frame.startAddress = 0;
                
                // 数据区域
                int dataStart = 3;
                int dataEnd = dataStart + frame.byteCount;
                if (bytes.length >= dataEnd + 2) {
                    StringBuilder dataHex = new StringBuilder();
                    for (int i = dataStart; i < dataEnd; i++) {
                        dataHex.append(String.format("%02X", bytes[i] & 0xFF));
                    }
                    frame.data = dataHex.toString();
                    
                    // CRC校验码（2字节）
                    frame.crcLow = bytes[dataEnd] & 0xFF;
                    frame.crcHigh = bytes[dataEnd + 1] & 0xFF;
                    
                    log.debug("Modbus帧解析成功: 地址={}, 功能码={}, 字节数={}, 数据长度={}, 数据={}, CRC={:02X}{:02X}", 
                            frame.address, frame.functionCode, frame.byteCount, frame.dataLength, frame.data, frame.crcLow, frame.crcHigh);
                } else {
                    log.warn("Modbus数据CRC部分缺失: 数据长度={}, 需要长度={}", bytes.length, dataEnd + 2);
                }
            }
            
            return frame;
            
        } catch (Exception e) {
            log.error("解析Modbus字节帧失败", e);
            return null;
        }
    }
    
    // 更新设备状态信息
    private void updateDeviceStatus(Long deviceId, JSONObject jsonData) {
        try {
            IotDevice device = deviceService.selectIotDeviceByDeviceId(deviceId);
            if (device != null) {
                boolean needUpdate = false;
                
                // 更新最后通信时间
                device.setLastCommunication(DateUtils.getTime());
                needUpdate = true;
                
                // 更新信号强度
                if (jsonData.containsKey("signal")) {
                    device.setSignalStrength(jsonData.getIntValue("signal"));
                    needUpdate = true;
                }
                
                // 如果需要更新，则保存设备信息
                if (needUpdate) {
                    deviceService.updateIotDevice(device);
                }
            }
        } catch (Exception e) {
            log.error("更新设备状态失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                mqttClient.close();
            }
        } catch (MqttException e) {
            log.error("MQTT客户端关闭失败", e);
        }
    }

    // 订阅新设备主题
    public void subscribeToDevice(IotDevice device) {
        try {
            if (device.getMqttPubTopic() != null && !device.getMqttPubTopic().isEmpty()) {
                mqttClient.subscribe(device.getMqttPubTopic());
                // 存储主题与设备ID的映射关系
                topicDeviceMap.put(device.getMqttPubTopic(), device.getDeviceId());
                log.info("已订阅新设备主题: {}", device.getMqttPubTopic());
            }
        } catch (MqttException e) {
            log.error("订阅新设备主题失败", e);
        }
    }

    // 取消订阅设备主题
    public void unsubscribeFromDevice(IotDevice device) {
        try {
            if (device.getMqttPubTopic() != null && !device.getMqttPubTopic().isEmpty()) {
                mqttClient.unsubscribe(device.getMqttPubTopic());
                // 移除主题与设备ID的映射关系
                topicDeviceMap.remove(device.getMqttPubTopic());
                log.info("已取消订阅设备主题: {}", device.getMqttPubTopic());
            }
        } catch (MqttException e) {
            log.error("取消订阅设备主题失败", e);
        }
    }
    
    // 发送命令到设备
    public boolean sendCommandToDevice(Long deviceId, String command) {
        try {
            IotDevice device = deviceService.selectIotDeviceByDeviceId(deviceId);
            if (device == null || device.getMqttSubTopic() == null || device.getMqttSubTopic().isEmpty()) {
                log.error("设备不存在或未配置订阅主题: {}", deviceId);
                return false;
            }
            
            MqttMessage message = new MqttMessage(command.getBytes());
            message.setQos(0);
            mqttClient.publish(device.getMqttSubTopic(), message);
            log.info("已发送命令到设备: deviceId={}, topic={}, command={}", deviceId, device.getMqttSubTopic(), command);
            return true;
        } catch (Exception e) {
            log.error("发送命令到设备失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 直接向指定主题发送 HEX 文本指令（最小可用版本轮询使用）。
     * 不改变现有 sendCommandToDevice 行为。
     */
    public boolean publishHexToTopic(String topic, String hexPayload) {
        try {
            if (topic == null || topic.isEmpty()) { topic = "GE_sub"; }
            if (!mqttClient.isConnected()) { reconnect(); }
            
            // 将 HEX 字符串转换为真正的二进制数据
            byte[] binaryData = hexStringToByteArray(hexPayload);
            MqttMessage message = new MqttMessage(binaryData);
            message.setQos(0);
            mqttClient.publish(topic, message);
            log.debug("MQTT publish HEX: topic={}, payload={}, binaryLength={}", topic, hexPayload, binaryData.length);
            return true;
        } catch (Exception e) {
            log.error("MQTT 发布 HEX 指令失败: topic={}, payload={}", topic, hexPayload, e);
            return false;
        }
    }
    
    /**
     * 将 HEX 字符串转换为字节数组
     */
    private byte[] hexStringToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }
        
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}