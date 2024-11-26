package org.example;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    TerminalFacade terminal = new TerminalFacade("192.168.0.29", 62801);

    try {
      terminal.connect();

      if (terminal.checkHealth()) {
        System.out.println("Терминал доступен.");
        boolean paymentResult = terminal.processPayment(100);
        if (paymentResult) {
          System.out.println("Платеж успешно проведен.");
        } else {
          System.out.println("Платеж отклонен.");
        }
      } else {
        System.out.println("Терминал недоступен.");
      }

      terminal.disconnect();
    } catch (IOException e) {
      System.err.println("Ошибка работы с терминалом: " + e.getMessage());
    }
  }
}

