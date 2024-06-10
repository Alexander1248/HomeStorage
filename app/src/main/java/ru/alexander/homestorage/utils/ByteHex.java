package ru.alexander.homestorage.utils;

import java.nio.ByteBuffer;

public class ByteHex {
    public static String toHex(byte[] array) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : array)
            hexString.append(String.format("%02X", b));
        return hexString.toString();
    }
    public static byte[] fromHex(String hex) {
        ByteBuffer buffer = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i += 2) {
            String hexByte = hex.substring(i, i + 2);
            buffer.put((byte) Integer.parseInt(hexByte, 16));
        }
        buffer.flip();
        return buffer.array();
    }
}
