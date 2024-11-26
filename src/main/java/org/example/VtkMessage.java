package org.example;

import java.nio.ByteBuffer;

public class VtkMessage {
  public static final int HEADER_LENGTH = 4;
  public static final int PROTOCOL_DISCRIMINATOR_VMC = 0x96FB;
  public static final int PROTOCOL_DISCRIMINATOR_POS = 0x97FB;

  private int length;
  private int protocolDiscriminator;
  private byte[] payload;

  public VtkMessage(int protocolDiscriminator, byte[] payload) {
    this.protocolDiscriminator = protocolDiscriminator;
    this.payload = payload;
    this.length = HEADER_LENGTH + payload.length;
  }

  public byte[] toBytes() {
    ByteBuffer buffer = ByteBuffer.allocate(length);
    buffer.putShort((short) (length - 2)); // Длина без поля длины
    buffer.putShort((short) protocolDiscriminator);
    buffer.put(payload);
    return buffer.array();
  }

  public static VtkMessage fromBytes(byte[] data) {
    ByteBuffer buffer = ByteBuffer.wrap(data);
    int length = buffer.getShort() & 0xFFFF;
    int protocolDiscriminator = buffer.getShort() & 0xFFFF;
    byte[] payload = new byte[length - HEADER_LENGTH];
    buffer.get(payload);
    return new VtkMessage(protocolDiscriminator, payload);
  }
}
