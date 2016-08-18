package adapter;

import dto.CallerDto;
import dto.TimeConsumingDto;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;

import java.util.concurrent.ExecutionException;

public class CallerDtoAdapter extends ListenableFutureAdapter<CallerDto, ResponseEntity<TimeConsumingDto>> {

  public CallerDtoAdapter(ListenableFuture<ResponseEntity<TimeConsumingDto>> adaptee) {
    super(adaptee);
  }

  @Override
  protected CallerDto adapt(ResponseEntity<TimeConsumingDto> adapteeResult) throws ExecutionException {
    TimeConsumingDto timeConsumingDto = adapteeResult.getBody();
    return new CallerDto(timeConsumingDto.getMessage(), timeConsumingDto.getValue());
  }

}
