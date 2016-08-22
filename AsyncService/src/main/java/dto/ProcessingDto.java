package dto;

public class ProcessingDto {

  private String message;
  private int processingTime;

  public ProcessingDto() {
  }

  public ProcessingDto(String message, int processingTime) {
    this.message = message;
    this.processingTime = processingTime;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getProcessingTime() {
    return processingTime;
  }

  public void setProcessingTime(int processingTime) {
    this.processingTime = processingTime;
  }

}
