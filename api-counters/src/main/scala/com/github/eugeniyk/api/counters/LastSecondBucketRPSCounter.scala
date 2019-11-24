package com.github.eugeniyk.api.counters

import java.util.concurrent.atomic.LongAdder

class LastSecondBucketRPSCounter() extends RPSCounter {
  private val activeSecondBucket = new LongAdder()
  @volatile private var activeSecond: Long = 0L
  @volatile private var previousSecond: Long = 0
  @volatile private var previousSecondRPS: Int = 0
  private val lock = new Object()

  override def registerRequest(): Unit = {
    val currentSecond = System.currentTimeMillis() / 1000

    if (activeSecond != currentSecond) {
      lock.synchronized {
        val active = activeSecond
        if (active != currentSecond) {
          //  no news since last second - meaning last rps is 0
          previousSecond = currentSecond - 1

          if (active == currentSecond-1) {
            previousSecondRPS = activeSecondBucket.sumThenReset().toInt
          } else {
            previousSecondRPS = 0
            activeSecondBucket.reset()
          }

          activeSecond = currentSecond
        }
      }
    }

    activeSecondBucket.increment()
  }

  override def getRPS: Int = {
    //  current second is the same
    val time = System.currentTimeMillis()
    val currentSecond = time / 1000
    val currentActiveSecond = activeSecond
    val rate = 1 - (time % 1000) / 1000.0   //  what is the percentage we'll use from last second

    //  if we have any data from last second - return it as the most accurate one
    if (currentActiveSecond == currentSecond) {
      val previousRPS = if (previousSecond == currentSecond-1) previousSecondRPS else 0
      (activeSecondBucket.sum() + rate * previousRPS).toInt
    } else if (currentActiveSecond == currentSecond - 1) {
      (rate * activeSecondBucket.sum()).toInt
    } else 0  //  don't have any record about this time
  }
}