package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

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
    socket.setSoTimeout(20000); // Таймаут на чтение
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

    // Читаем длину сообщения
    byte[] lengthBytes = new byte[2];
    inputStream.readFully(lengthBytes);
    int length = ((lengthBytes[0] & 0xFF) << 8) | (lengthBytes[1] & 0xFF);

    // Читаем остальную часть сообщения
    byte[] data = new byte[length];
    inputStream.readFully(data);

    System.out.println("Получено сообщение: " + bytesToHex(data));
    return new VtkMessage(0, data); // Возвращаем сообщение без CRC для проверки
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02X ", b));
    }
    return sb.toString().trim();
  }
}
