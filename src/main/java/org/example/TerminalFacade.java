package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TerminalFacade {
  private VtkClient client;
  private int operationNumber = 1;

  public TerminalFacade(String ip, int port) {
    this.client = new VtkClient(ip, port);
  }

  public void connect() throws IOException {
    client.connect();
  }

  public void disconnect() throws IOException {
    client.disconnect();
  }

  public boolean checkHealth() {
    try {
      System.out.println("Отправка сообщения IDL для проверки состояния...");
      VtkMessage idlMessage = createIdlMessage();
      client.sendMessage(idlMessage);

      System.out.println("Сообщение отправлено. Ожидание ответа...");
      VtkMessage response = client.receiveMessage();

      if (response == null || response.toBytes().length == 0) {
        System.err.println("Ответ отсутствует или пустой.");
        return false;
      }

      System.out.println("Ответ получен: " + new String(response.toBytes()));
      return true;
    } catch (IOException e) {
      System.err.println("Ошибка проверки терминала: " + e.getMessage());
      return false;
    }
  }


  public boolean processPayment(int amount) {
    try {
      VtkMessage vrpMessage = createVrpMessage(amount);
      client.sendMessage(vrpMessage);

      VtkMessage response = client.receiveMessage();
      return isPaymentApproved(response);
    } catch (IOException e) {
      System.err.println("Ошибка проведения платежа: " + e.getMessage());
      return false;
    }
  }

  private VtkMessage createIdlMessage() {
    ByteArrayOutputStream payload = new ByteArrayOutputStream();

    try {
      // Тег 01h: Код команды
      payload.write(0x01); // Тег
      payload.write(0x03); // Длина
      payload.write('I');  // IDL
      payload.write('D');
      payload.write('L');

      // Тег 03h: Номер операции
      payload.write(0x03); // Тег
      payload.write(0x01); // Длина
      payload.write(0x01); // Значение

      // Тег 06h: Таймаут
      payload.write(0x06); // Тег
      payload.write(0x03); // Длина
      payload.write("120".getBytes()); // Значение (120 секунд)

      // Тег 08h: Номер события
      payload.write(0x08); // Тег
      payload.write(0x01); // Длина
      payload.write(0x01); // Значение
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new VtkMessage(VtkMessage.PROTOCOL_DISCRIMINATOR_POS, payload.toByteArray());
  }


  private VtkMessage createVrpMessage(int amount) {
    String payload = String.format("01VRP0301%04d040%d", operationNumber++, amount);
    return new VtkMessage(VtkMessage.PROTOCOL_DISCRIMINATOR_POS, payload.getBytes());
  }

  private boolean isPaymentApproved(VtkMessage response) {
    String payload = new String(response.toBytes());
    return payload.contains("APPROVED");
  }
}
