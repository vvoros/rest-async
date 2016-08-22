package controller;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import dto.ProcessingDto;
import task.ProcessingTask;

@RestController
public class ProcessingController extends RequestCounter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingController.class);

  @Autowired
  private ExecutorService executorService;

  @RequestMapping(value = "/asyncProcessing", method = RequestMethod.GET, produces = "application/json")
  public DeferredResult<ResponseEntity<ProcessingDto>> asyncProcessing(@RequestParam(defaultValue = "0") int consume) {
    int requestId = getRequestCounter();

    DeferredResult<ResponseEntity<ProcessingDto>> deferredResult = new DeferredResult<>();

    LOGGER.info("Processing task scheduled, req={}.", requestId);
    ProcessingTask processingTask = new ProcessingTask(deferredResult, consume, requestId);
    executorService.execute(processingTask);

    LOGGER.info("Exiting, req={}.", requestId);
    return deferredResult;
  }

}
