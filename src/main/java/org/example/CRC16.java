package org.example;

import java.util.Arrays;

public class CRC16 {
  private static final int[] CRC_TABLE = new int[256];

  static {
    for (int i = 0; i < 256; i++) {
      int crc = i << 8;
      for (int j = 0; j < 8; j++) {
        if ((crc & 0x8000) != 0) {
          crc = (crc << 1) ^ 0x1021;
        } else {
          crc <<= 1;
        }
      }
      CRC_TABLE[i] = crc & 0xFFFF;
    }
  }

  public static int calculate(byte[] data) {
    int crc = 0xFFFF;

    for (byte b : data) {
      int tmp = ((crc >> 8) ^ (b & 0xFF)) & 0xFF;
      crc = (crc << 8) ^ CRC_TABLE[tmp];
      crc &= 0xFFFF; // Ограничение до 16 бит
    }

    return crc;
  }
}