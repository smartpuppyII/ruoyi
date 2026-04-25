package com.ruoyi.system.mqtt.util;

/**
 * Modbus RTU 辅助工具：生成读保持寄存器请求（HEX 文本）与 CRC16(Modbus) 计算。
 */
public class ModbusUtils {

    /**
     * 生成 Modbus-RTU 读保持寄存器(0x03) 请求帧的 HEX 文本。
     * 结构：地址(1) 功能码(1) 起始地址(2) 寄存器数量(2) CRC(2, 低字节在前)
     * @param slaveId 从站地址 0-247
     * @param startAddress 起始寄存器地址 0-65535
     * @param quantity 寄存器数量 1-125（本项目建议≤10）
     * @return 大写 HEX 字符串（不含空格）
     */
    public static String buildReadHoldingRegistersHex(int slaveId, int startAddress, int quantity) {
        byte addr = (byte) (slaveId & 0xFF);
        byte func = 0x03;
        byte startHi = (byte) ((startAddress >> 8) & 0xFF);
        byte startLo = (byte) (startAddress & 0xFF);
        byte qtyHi = (byte) ((quantity >> 8) & 0xFF);
        byte qtyLo = (byte) (quantity & 0xFF);

        byte[] frameNoCrc = new byte[] { addr, func, startHi, startLo, qtyHi, qtyLo };
        int crc = crc16Modbus(frameNoCrc);
        byte crcLo = (byte) (crc & 0xFF);
        byte crcHi = (byte) ((crc >> 8) & 0xFF);

        StringBuilder sb = new StringBuilder();
        appendHex(sb, addr); appendHex(sb, func);
        appendHex(sb, startHi); appendHex(sb, startLo);
        appendHex(sb, qtyHi); appendHex(sb, qtyLo);
        appendHex(sb, crcLo); appendHex(sb, crcHi);
        return sb.toString();
    }

    /**
     * 计算 CRC16(Modbus) 多项式 0xA001，初始值 0xFFFF，低字节在前。
     */
    public static int crc16Modbus(byte[] data) {
        int crc = 0xFFFF;
        for (byte b : data) {
            crc ^= (b & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ 0xA001;
                } else {
                    crc = (crc >>> 1);
                }
            }
        }
        return crc & 0xFFFF;
    }

    private static void appendHex(StringBuilder sb, byte b) {
        sb.append(String.format("%02X", b & 0xFF));
    }
}


