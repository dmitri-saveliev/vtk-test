package org.example;

import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class VtkClient {
  private String ip;
  private int port;
  private Socket socket;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;

  public VtkClient(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public void connect() throws IOException {
    System.out.println("Подключение к " + ip + ":" + port);
    socket = new Socket();
    socket.connect(new InetSocketAddress(ip, port), 15000); // Таймаут подключения
    socket.setSoTimeout(10000); // Таймаут чтения
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
  }

  public void disconnect() throws IOException {
    if (socket != null) {
      socket.close();
    }
  }

  public void sendMessage(VtkMessage message) throws IOException {
    byte[] data = message.toBytes();
    System.out.println("Отправка сообщения: " + bytesToHex(data));
    outputStream.write(data);
    outputStream.flush();
  }

  public VtkMessage receiveMessage() throws IOException {
    System.out.println("Ожидание ответа от терминала...");
    byte[] header = new byte[VtkMessage.HEADER_LENGTH];

    int readBytes = inputStream.read(header);
    if (readBytes < VtkMessage.HEADER_LENGTH) {
      throw new IOException("Не удалось прочитать заголовок сообщения.");
    }
    System.out.println("Получен заголовок: " + bytesToHex(header));

    int length = ByteBuffer.wrap(header).getShort() & 0xFFFF;
    byte[] data = new byte[length];

    readBytes = inputStream.read(data);
    if (readBytes < length) {
      throw new IOException("Не удалось прочитать полное сообщение.");
    }
    System.out.println("Получено сообщение: " + bytesToHex(data));

    return VtkMessage.fromBytes(data);
  }


  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02X ", b));
    }
    return sb.toString().trim();
  }
}
