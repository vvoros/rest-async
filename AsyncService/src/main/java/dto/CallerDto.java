package dto;

public class CallerDto {

  private String message;
  private int consume;

  public CallerDto(String message, int consume) {
    this.message = message;
    this.consume = consume;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getConsume() {
    return consume;
  }

  public void setConsume(int consume) {
    this.consume = consume;
  }

}
