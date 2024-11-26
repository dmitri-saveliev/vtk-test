package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TerminalFacade {
  private VtkClient client;

  public TerminalFacade(String ip, int port) {
    this.client = new VtkClient(ip, port);
  }

  public void connect() throws IOException {
    client.connect();
  }

  public void disconnect() throws IOException {
    client.disconnect();
  }

  public boolean initializeTerminal() {
    try {
      System.out.println("Отправка IDL сообщения для перехода в состояние IDLE...");
      VtkMessage idlMessage = createIdlMessage();
      client.sendMessage(idlMessage);

      VtkMessage response = client.receiveMessage();
      System.out.println("Ответ терминала: " + new String(response.toBytes()));
      return true;
    } catch (IOException e) {
      System.err.println("Ошибка инициализации терминала: " + e.getMessage());
      return false;
    }
  }

  private VtkMessage createIdlMessage() {
    ByteArrayOutputStream payload = new ByteArrayOutputStream();

    try {
      // Код команды (IDL)
      payload.write(0x01); // Тег
      payload.write(0x03); // Длина
      payload.write('I');  // Значение "IDL"
      payload.write('D');
      payload.write('L');

      // Текущий номер события
      payload.write(0x08); // Тег
      payload.write(0x01); // Длина
      payload.write('1');  // Значение (1 в ASCII)

      // Имя события (CSAPP)
      payload.write(0x07); // Тег
      payload.write(0x05); // Длина
      payload.write('C');  // Значение "CSAPP"
      payload.write('S');
      payload.write('A');
      payload.write('P');
      payload.write('P');

      // ID продукта (123)
      payload.write(0x09); // Тег
      payload.write(0x03); // Длина
      payload.write('1');  // Значение "123"
      payload.write('2');
      payload.write('3');

      // Наименование продукта (Кофе)
      payload.write(0x0F); // Тег
      payload.write(0x08); // Длина
      payload.write("Кофе".getBytes("UTF-8")); // UTF-8 строка

      // Сумма (9000 = 90.00)
      payload.write(0x04); // Тег
      payload.write(0x04); // Длина
      payload.write(0x00);
      payload.write(0x00);
      payload.write(0x23); // Сумма (9000 копеек)
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new VtkMessage(VtkMessage.PROTOCOL_DISCRIMINATOR_VMC, payload.toByteArray());
  }
}
