package com.github.eugeniyk.api.counters

import java.util.concurrent.atomic.LongAdder

import com.github.eugeniyk.api.counters.RequestCounter.RequestCounterStats

object RequestCounter {
  /**
   * @param totalRequestSubmitted total number of requests that gatekeeper received, from the last report
   * @param rejectedRequests number of requests that gatekeeper ignored, from the last report
   * @param inflightRequests current number of requests being proceeded
   * @param successResponses success responses count, from the last report
   * @param failureResponses failure responses count, from the last report
   */
  case class RequestCounterStats(totalRequestSubmitted: Long,
                                 rejectedRequests: Long,
                                 inflightRequests: Long,
                                 successResponses: Long,
                                 failureResponses: Long,
                                 currentRPS: Int) {
    /** processed (allowed to pass) to total request ratio; ratio = (total-rejected)/total */
    def processedRatio: Double = if (totalRequestSubmitted > 0) (totalRequestSubmitted - rejectedRequests) / totalRequestSubmitted.toDouble else 1

    private def acceptedRequests: Long = totalRequestSubmitted - rejectedRequests   //  should be equal to successResponses + failureResponses
    def successRatePassed: Double = if (acceptedRequests > 0) successResponses / acceptedRequests.toDouble else 1
    def successRateTotal: Double = if (acceptedRequests + rejectedRequests > 0) successResponses / (acceptedRequests + rejectedRequests).toDouble else 1
  }
}

class RequestCounter() {
  private val totalCounter: LongAdder = new LongAdder()

  private val rejectRequestCounter: LongAdder = new LongAdder()
  private val inflightCounter: LongAdder = new LongAdder()
  private val successResponseCounter: LongAdder = new LongAdder()
  private val failureResponseCounter: LongAdder = new LongAdder()

  private val rpsCounter: RPSCounter = new SlidingWindowRPSCounter()

  def registerRequest(): Unit = {
    rpsCounter.registerRequest()
    totalCounter.increment()
    inflightCounter.increment()
  }

  def rejectRequest(): Unit = {
    totalCounter.increment()
    rejectRequestCounter.increment()
  }

  def registerSuccessResponse(): Unit = {
    successResponseCounter.increment()
    inflightCounter.decrement()
  }

  def registerFailureResponse(): Unit = {
    failureResponseCounter.increment()
    inflightCounter.decrement()
  }

  def inflightRequests: Int = inflightCounter.intValue()
  def rps: Int = rpsCounter.getRPS

  /**
   * @return get [[RequestCounterStats]] statistic
   */
  def getStats: RequestCounterStats = {
    val total = totalCounter.sum()
    val rejected = rejectRequestCounter.sum()
    val inflightRequests = inflightCounter.sum()
    val successResponses = successResponseCounter.sum()
    val failedResponses = failureResponseCounter.sum()

    RequestCounterStats(total, rejected, inflightRequests, successResponses, failedResponses, rpsCounter.getRPS)
  }

  /**
   * @return get [[RequestCounterStats]] statistic
   */
  def getAndCleanStats(): RequestCounterStats = {
    val total = totalCounter.sumThenReset()
    val rejected = rejectRequestCounter.sumThenReset()
    //  We don't need to reset current request counter
    val inflightRequests = inflightCounter.sum()
    val successResponses = successResponseCounter.sumThenReset()
    val failedResponses = failureResponseCounter.sumThenReset()

    RequestCounterStats(total, rejected, inflightRequests, successResponses, failedResponses, rpsCounter.getRPS)
  }
}