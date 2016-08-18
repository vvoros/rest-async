package controller;

import dto.TimeConsumingDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TimeConsumingController {

  @RequestMapping(value = "/timeConsumer", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<TimeConsumingDto> timeConsumer(@RequestParam(defaultValue = "0") int consume) throws InterruptedException {
    TimeUnit.SECONDS.sleep(consume);

    return new ResponseEntity<>(new TimeConsumingDto("This is THE response", consume), HttpStatus.OK);
  }

}
