package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VtkMessage {
  public static final int HEADER_LENGTH = 4; // 2 байта длины + 2 байта идентификатора
  public static final int PROTOCOL_DISCRIMINATOR_VMC = 0x96FB;

  private byte[] data;

  public VtkMessage(int protocolDiscriminator, byte[] payload) {
    ByteArrayOutputStream message = new ByteArrayOutputStream();
    try {
      // Длина сообщения (длина полезной нагрузки + 2 байта CRC)
      int length = payload.length + 2;
      message.write((length >> 8) & 0xFF);
      message.write(length & 0xFF);

      // Протокольный идентификатор
      message.write((protocolDiscriminator >> 8) & 0xFF);
      message.write(protocolDiscriminator & 0xFF);

      // Полезная нагрузка
      message.write(payload);

      // CRC16
      int crc = CRC16.calculate(message.toByteArray());
      message.write((crc >> 8) & 0xFF);
      message.write(crc & 0xFF);

      this.data = message.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Ошибка формирования сообщения", e);
    }
  }

  public byte[] toBytes() {
    return data;
  }
}
