package com.github.eugeniyk.api.counters

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
