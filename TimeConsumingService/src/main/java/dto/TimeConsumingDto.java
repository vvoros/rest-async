package dto;

public class TimeConsumingDto {

  private String message;
  private int value;

  public TimeConsumingDto(String message, int value) {
    this.message = message;
    this.value = value;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

}
