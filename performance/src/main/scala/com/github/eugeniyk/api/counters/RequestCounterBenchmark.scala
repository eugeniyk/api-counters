package com.github.eugeniyk.api.counters

import java.util.concurrent.TimeUnit

import com.github.eugeniyk.api.counters.RequestCounterBenchmark.TestState
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Group, Mode, OutputTimeUnit, Scope, State}

object RequestCounterBenchmark {
  @State(Scope.Benchmark)
  class TestState {
    val requestCounter = new RequestCounter()
  }
}

/**
  * sbt "performance/jmh:run -i 10 -wi 5 -f1 -t1 RequestCounterBenchmark"
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class RequestCounterBenchmark {
  @Benchmark
  @Group("requestCounter")
  def registerRequest(state: TestState) = {
    state.requestCounter.registerRequest()
  }

  @Benchmark
  @Group("requestCounter")
  def registerSuccessResponse(state: TestState) = {
    state.requestCounter.registerSuccessResponse()
  }

  @Benchmark
  @Group("requestCounter")
  def inflightRequests(state: TestState) = {
    state.requestCounter.inflightRequests
  }
}

