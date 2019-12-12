package com.github.eugeniyk.api.counters

object RPSCounter {
  /**
   * Instantiate default implementation of [[RPSCounter]] - [[SlidingWindowRPSCounter]]
   */
  def apply(): RPSCounter = new SlidingWindowRPSCounter()
}

/**
  * Requests per second counter
  */
trait RPSCounter {
  /**
    * Register incoming request
    */
  def registerRequest(): Unit

  /**
    * Return current rate of requests (requests per second)
    */
  def getRPS: Int
}
