package config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  public AsyncRestTemplate getAsyncRestTemplate() {
    return new AsyncRestTemplate();
  }

  @Bean(destroyMethod = "shutdown")
  public ExecutorService getExecutorService() {
    return Executors.newFixedThreadPool(3);
  }

}
