package com.github.eugeniyk.api.counters

import com.github.eugeniyk.api.counters.RequestCounter.RequestCounterStats
import org.specs2.mutable.Specification

class RequestCounterSpec extends Specification {
  "RequestCounter" should {
    "basic tests" in {
      val counter = new RequestCounter()
      val total = 1000000

      1.to(total).foreach { i =>
        if (i % 10 != 0) {
          counter.registerAcceptedRequest()

          if (i % 5 == 0) counter.registerFailureResponse()
          else counter.registerSuccessResponse()
        } else {
          counter.registerRejectedRequest()
        }
      }

      //  assume everything with 1 sec
      counter.rps should_== total
      counter.inflightRequests should_== 0

      val expectedRejected = total / 10
      val expectFailure = total / 5 - expectedRejected

      counter.getStats should_== RequestCounterStats(
        totalRequestSubmitted = total,
        rejectedRequests = expectedRejected,
        inflightRequests = 0,
        successResponses = total - expectFailure - expectedRejected,
        failureResponses = expectFailure,
        currentRPS = total
      )
    }
  }
}
