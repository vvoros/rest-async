package controller;

import adapter.CallerDtoAdapter;
import dto.CallerDto;
import dto.TimeConsumingDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CallerController extends RequestCounter {

  private static final Logger LOGGER = LoggerFactory.getLogger(CallerController.class);
  private static final String TIME_CONSUMING_SERVICE_URL = "http://localhost:9001/timeConsumer?consume={consume}";

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private AsyncRestTemplate asyncRestTemplate;

  @RequestMapping(value = "/caller", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<CallerDto> caller(@RequestParam(defaultValue = "0") int consume) {
    int requestId = getRequestCounter();

    LOGGER.info("Request sent to service, req={}.", requestId);
    ResponseEntity<TimeConsumingDto> timeConsumingResponse =
        restTemplate.getForEntity(TIME_CONSUMING_SERVICE_URL, TimeConsumingDto.class, consume);


    LOGGER.info("Response returned, converting DTO, req={}.", requestId);
    TimeConsumingDto timeConsumingDto = timeConsumingResponse.getBody();
    CallerDto callerDto = new CallerDto(timeConsumingDto.getMessage(), timeConsumingDto.getValue());

    LOGGER.info("Exiting, req={}.", requestId);
    return new ResponseEntity<>(callerDto, HttpStatus.OK);
  }

  @RequestMapping(value = "/asyncCaller", method = RequestMethod.GET, produces = "application/json")
  public DeferredResult<ResponseEntity<CallerDto>> asyncCaller(@RequestParam(defaultValue = "0") int consume) {
    int requestId = getRequestCounter();

    LOGGER.info("Async request sent to service, req={}.", requestId);
    ListenableFuture<ResponseEntity<TimeConsumingDto>> asyncResponse =
        asyncRestTemplate.getForEntity(TIME_CONSUMING_SERVICE_URL, TimeConsumingDto.class, consume);

    LOGGER.info("Async response returned, converting DTO, req={}.", requestId);
    DeferredResult<ResponseEntity<CallerDto>> deferredResult = new DeferredResult<>();
    ListenableFuture<CallerDto> callerDto = new CallerDtoAdapter(asyncResponse);

    callerDto.addCallback(
        new ListenableFutureCallback<CallerDto>() {

          @Override
          public void onSuccess(CallerDto result) {
            LOGGER.info("Callback success, req={}.", requestId);
            deferredResult.setResult(new ResponseEntity<>(result, HttpStatus.OK));
          }

          @Override
          public void onFailure(Throwable ex) {
            LOGGER.info("Callback failed, req={}.", requestId);
            deferredResult.setResult(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
          }
        });

    /*
     * // callback with lambdas
     * callerDto.addCallback(result -> deferredResult.setResult(new ResponseEntity<>(result, HttpStatus.OK)),
     * exception -> deferredResult.setResult(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE)));
     */

    LOGGER.info("Exiting, req={}.", requestId);
    return deferredResult;
  }

  @RequestMapping(value = "/asyncCallerSimpler", method = RequestMethod.GET, produces = "application/json")
  public ListenableFuture<CallerDto> asyncCallerSimpler(@RequestParam(defaultValue = "0") int consume) {
    int requestId = getRequestCounter();

    LOGGER.info("Async request sent to service, req={}.", requestId);
    ListenableFuture<ResponseEntity<TimeConsumingDto>> asyncResponse =
        asyncRestTemplate.getForEntity(TIME_CONSUMING_SERVICE_URL, TimeConsumingDto.class, consume);

    LOGGER.info("Async response returned, converting DTO, req={}.", requestId);
    ListenableFuture<CallerDto> callerDto = new CallerDtoAdapter(asyncResponse);

    LOGGER.info("Exiting, req={}.", requestId);
    return callerDto;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<CallerDto> handleException(Exception e) {
    return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
  }

}
