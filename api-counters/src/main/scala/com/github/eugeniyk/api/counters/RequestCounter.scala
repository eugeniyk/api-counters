package com.github.eugeniyk.api.counters

import java.util.concurrent.atomic.LongAdder

import com.github.eugeniyk.api.counters.RequestCounter.RequestCounterStats

object RequestCounter {
  /**
   * @param totalRequestSubmitted total number of requests that gatekeeper received, from the last report
   * @param rejectedRequests number of requests that gatekeeper ignored, from the last report
   * @param inflightRequests current number of requests being proceeded
   * @param successResponses success responses count, from the last report
   * @param rejectedRPS number of incoming requests for last second
   * @param acceptedRPS number of processing requests for last second
   */
  case class RequestCounterStats(totalRequestSubmitted: Long,
                                 rejectedRequests: Long,
                                 inflightRequests: Long,
                                 successResponses: Long,
                                 failureResponses: Long,
                                 acceptedRPS: Int,
                                 rejectedRPS: Int) {
    def incomingRPS: Int = acceptedRPS + rejectedRPS

    /** processed (allowed to pass) to total request ratio; ratio = (total-rejected)/total */
    def processedRatio: Double = if (totalRequestSubmitted > 0) (totalRequestSubmitted - rejectedRequests) / totalRequestSubmitted.toDouble else 1

    private def acceptedRequests: Long = totalRequestSubmitted - rejectedRequests   //  should be equal to successResponses + failureResponses
    def successRatePassed: Double = if (acceptedRequests > 0) successResponses / acceptedRequests.toDouble else 1
    def successRateTotal: Double = if (acceptedRequests + rejectedRequests > 0) successResponses / (acceptedRequests + rejectedRequests).toDouble else 1
  }
}

class RequestCounter() {
  private val totalCounter: LongAdder = new LongAdder()

  private val inflightCounter: LongAdder = new LongAdder()
  private val rejectRequestCounter: LongAdder = new LongAdder()
  private val successResponseCounter: LongAdder = new LongAdder()
  private val failureResponseCounter: LongAdder = new LongAdder()

  private val acceptRpsCounter: RPSCounter = RPSCounter()
  private val rejectRpsCounter: RPSCounter = RPSCounter()

  /**
   * Register request that we accept
   */
  def registerAcceptedRequest(): Unit = {
    acceptRpsCounter.registerRequest()
    totalCounter.increment()
    inflightCounter.increment()
  }

  /**
   * Register request that we reject
   */
  def registerRejectedRequest(): Unit = {
    rejectRpsCounter.registerRequest()
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

  /** Returns request per second stats for all incoming requests including rejected one */
  def incomingRPS: Int = acceptedRPS + rejectedRPS
  /** Returns request per second stats for only accepted requests, without rejected one */
  def acceptedRPS: Int = acceptRpsCounter.getRPS
  /** Returns request per second stats for only rejected requests */
  def rejectedRPS: Int = rejectRpsCounter.getRPS

  /**
   * @return get [[RequestCounterStats]] statistic
   */
  def getStats: RequestCounterStats = {
    val total = totalCounter.sum()
    val rejected = rejectRequestCounter.sum()
    val inflightRequests = inflightCounter.sum()
    val successResponses = successResponseCounter.sum()
    val failedResponses = failureResponseCounter.sum()

    RequestCounterStats(
      totalRequestSubmitted = total,
      rejectedRequests = rejected,
      inflightRequests = inflightRequests,
      successResponses = successResponses,
      failureResponses = failedResponses,
      acceptedRPS = acceptedRPS,
      rejectedRPS = rejectedRPS)
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

    RequestCounterStats(total, rejected, inflightRequests, successResponses, failedResponses, acceptedRPS, rejectedRPS)
  }
}