package com.github.eugeniyk.api.counters

import java.util.concurrent.TimeUnit

import com.github.eugeniyk.api.counters.PRSCountersBenchmark.TestState
import org.openjdk.jmh.annotations._

object PRSCountersBenchmark {
  @State(Scope.Benchmark)
  class TestState {
    val lastSecondCounter = new LastSecondBucketRPSCounter()
    val slidingWindowCounter = new SlidingWindowRPSCounter()
  }
}

/**
 * sbt "performance/jmh:run -i 7 -wi 3 -f1 PRSCountersBenchmark"
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class PRSCountersBenchmark {
  @Benchmark
  @Group("LastSecondBucket")
  @GroupThreads(4)
  def lastSecondBucketRPSCounterRegister(state: TestState) = {
    state.lastSecondCounter.registerRequest()
  }

  @Benchmark
  @Group("LastSecondBucket")
  @GroupThreads(4)
  def lastSecondBucketRPSCounterGetRPS(state: TestState) = {
    state.lastSecondCounter.getRPS
  }

  @Benchmark
  @Group("SlidingWindow")
  @GroupThreads(4)
  def slidingWindowCounterRegister(state: TestState) = {
    state.slidingWindowCounter.registerRequest()
  }

  @Benchmark
  @Group("SlidingWindow")
  @GroupThreads(4)
  def slidingWindowCounterGetRPS(state: TestState) = {
    state.slidingWindowCounter.getRPS
  }
}
