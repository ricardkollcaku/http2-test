package com.example.server;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class BenchmarkProvider {

  private AtomicInteger currentProcessed;
  private AtomicInteger previewsProcessed;
  @Value("${benchmark.enabled}")
  private Boolean isBenchmarkEnabled;

  @PostConstruct
  private void onInit() {
    if (!isBenchmarkEnabled) {
      return;
    }
    currentProcessed = new AtomicInteger(0);
    previewsProcessed = new AtomicInteger(0);
    initBenchmark();
  }

  private void initBenchmark() {
    Flux.interval(Duration.ofSeconds(1))
        .subscribeOn(Schedulers.newSingle("banchmark"))
        .map(aLong -> calculateEventSendPerSecond())
        .filter(integer -> integer > 0)
        .onErrorContinue((throwable, o) -> onError(throwable))
        .retry()
        .subscribe(this::printBenchmark);

  }

  private void printBenchmark(Integer eventPerSecond) {
    long memoryUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    memoryUsed = memoryUsed / 1000000;
    int threadNumber = Thread.activeCount();
    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
        OperatingSystemMXBean.class);
    System.out.println(
        "Processed:\t" + eventPerSecond + "\titems/s" + "\tMemory used:\t" + memoryUsed + "MB\t"
            + "CPU:\t" + ((int) (osBean.getProcessCpuLoad() * 100)) + "%\t"
            + "Active threads\t" + threadNumber);
  }

  public void incrementProcessed() {
    if (isBenchmarkEnabled) {
      currentProcessed.incrementAndGet();
    }
  }


  private Integer calculateEventSendPerSecond() {
    int currentProcessed = this.currentProcessed.get();
    int previewsProcessed = this.previewsProcessed.get();
    int currentCalculation = currentProcessed - previewsProcessed;
    this.previewsProcessed.set(currentProcessed);
    return currentCalculation;
  }

  private void onError(Throwable throwable) {
    throwable.printStackTrace();
  }
}
