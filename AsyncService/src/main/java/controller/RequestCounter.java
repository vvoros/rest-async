package controller;

public class RequestCounter {

  private static int requestCounter = 0;

  protected int getRequestCounter() {
    return ++requestCounter;
  }

}
