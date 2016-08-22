package task;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import dto.ProcessingDto;

public class ProcessingTask implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingTask.class);

  private DeferredResult<ResponseEntity<ProcessingDto>> deferredResult;
  private int consume;
  private int requestId;

  public ProcessingTask(DeferredResult<ResponseEntity<ProcessingDto>> deferredResult, int consume, int requestId) {
    this.deferredResult = deferredResult;
    this.consume = consume;
    this.requestId = requestId;
  }

  @Override
  public void run() {
    heavyCalculation();
    if (deferredResult.isSetOrExpired()) {
      LOGGER.info("Processing of request has already expired, req={}.", requestId);
    } else {
      deferredResult.setResult(new ResponseEntity<>(new ProcessingDto("Ok", consume), HttpStatus.OK));
      LOGGER.info("Processing of request is done, req={}.", requestId);
    }
  }

  private void heavyCalculation() {
    try {
      TimeUnit.SECONDS.sleep(consume);
    } catch (InterruptedException e) {
      LOGGER.error("InterruptedException, req={}.", requestId, e);
    }
  }

}
