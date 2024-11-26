package org.example;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    String ip = "192.168.0.29";
    int port = 62801;

    TerminalFacade terminal = new TerminalFacade(ip, port);

    try {
      terminal.connect();
      if (terminal.initializeTerminal()) {
        System.out.println("Терминал успешно инициализирован!");
      } else {
        System.out.println("Не удалось инициализировать терминал.");
      }
    } catch (IOException e) {
      System.err.println("Ошибка: " + e.getMessage());
    } finally {
      try {
        terminal.disconnect();
      } catch (IOException e) {
        System.err.println("Ошибка при отключении: " + e.getMessage());
      }
    }
  }
}
