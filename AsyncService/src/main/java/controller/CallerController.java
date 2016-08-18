package controller;

import adapter.CallerDtoAdapter;
import dto.CallerDto;
import dto.TimeConsumingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class CallerController {

  private static final String TIME_CONSUMING_SERVICE_URL = "http://localhost:9001/timeConsumer?consume={consume}";

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private AsyncRestTemplate asyncRestTemplate;

  @RequestMapping(value = "/caller", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<CallerDto> caller(@RequestParam(defaultValue = "0") int consume) {
    ResponseEntity<TimeConsumingDto> timeConsumingResponse =
        restTemplate.getForEntity(TIME_CONSUMING_SERVICE_URL, TimeConsumingDto.class, consume);

    TimeConsumingDto timeConsumingDto = timeConsumingResponse.getBody();
    CallerDto callerDto = new CallerDto(timeConsumingDto.getMessage(), timeConsumingDto.getValue());

    return new ResponseEntity<CallerDto>(callerDto, HttpStatus.OK);
  }

  @RequestMapping(value = "/asyncCaller", method = RequestMethod.GET, produces = "application/json")
  public DeferredResult<ResponseEntity<CallerDto>> asyncCaller(@RequestParam(defaultValue = "0") int consume) {
    ListenableFuture<ResponseEntity<TimeConsumingDto>> asyncResponse =
        asyncRestTemplate.getForEntity(TIME_CONSUMING_SERVICE_URL, TimeConsumingDto.class, consume);

    DeferredResult<ResponseEntity<CallerDto>> deferredResult = new DeferredResult<>();
    ListenableFuture<CallerDto> callerDto = new CallerDtoAdapter(asyncResponse);

    callerDto.addCallback(
        new ListenableFutureCallback<CallerDto>() {

          @Override
          public void onSuccess(CallerDto result) {
            deferredResult.setResult(new ResponseEntity<>(result, HttpStatus.OK));
          }

          @Override
          public void onFailure(Throwable ex) {
            deferredResult.setResult(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
          }
        });

    /*
     * // callback with lambdas
     * callerDto.addCallback(result -> deferredResult.setResult(new ResponseEntity<>(result, HttpStatus.OK)),
     * exception -> deferredResult.setResult(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE)));
     */

    return deferredResult;
  }

  @RequestMapping(value = "/asyncCallerSimpler", method = RequestMethod.GET, produces = "application/json")
  public ListenableFuture<CallerDto> asyncCallerSimpler(@RequestParam(defaultValue = "0") int consume) {
    ListenableFuture<ResponseEntity<TimeConsumingDto>> asyncResponse =
        asyncRestTemplate.getForEntity(TIME_CONSUMING_SERVICE_URL, TimeConsumingDto.class, consume);

    ListenableFuture<CallerDto> callerDto = new CallerDtoAdapter(asyncResponse);

    return callerDto;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<CallerDto> handleException(Exception e) {
    return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
  }

}
