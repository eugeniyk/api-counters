package com.github.eugeniyk.api.counters

import java.util.concurrent.atomic.LongAdder

import com.github.eugeniyk.api.counters.SlidingWindowRPSCounter.{BucketStat, RPSStats}

object SlidingWindowRPSCounter {
  class BucketStat(millisecondResolution: Int) {
    @volatile private var bucketTimestamp: Long = 0L
    private val requestCounter: LongAdder = new LongAdder()
    private val lock = new Object()

    def registerRequest(timestamp: Long): Unit = {
      if (bucketTimestamp != timestamp) {
        //  we need to use lock here since there is race condition during reset / increment
        lock.synchronized {
          if (bucketTimestamp != timestamp) {
            requestCounter.reset()
            bucketTimestamp = timestamp
          }
        }
      }

      requestCounter.increment()
    }

    def currentRPS(timestamp: Long): Int = {
      if (timestamp - 1000 - millisecondResolution > bucketTimestamp) {
        //  our stats are obsolete now
        0
      } else {
        requestCounter.sum().toInt
      }
    }
  }

  case class RPSStats(timestamp: Long, rps: Int)
}

class SlidingWindowRPSCounter() extends RPSCounter {
  /**
   * Determines precision of the counter within second
   * As average, it's allowed to lost 1 / (2*bucketsPerSecond)
   */
  private val bucketsPerSecond = 40
  private val millisecondResolution = 1000 / bucketsPerSecond

  private val buckets = new Array[BucketStat](bucketsPerSecond)
  0.until(bucketsPerSecond).foreach { i =>
    buckets(i) = new BucketStat(millisecondResolution)
  }

  //  No need for Atomic reference here - we are allowed to calculate stats multiple times
  @volatile private var lastRPSStats = RPSStats(0L, 0)

  /**
   * Register incoming request
   */
  override def registerRequest(): Unit = {                                    //  example for bucketsPerSecond = 40:
    val time = System.currentTimeMillis()                                     //  1574578556239
    val normalizedBucketTime = time - time % millisecondResolution            //  1574578556200
    val bucket = (normalizedBucketTime % 1000 / millisecondResolution).toInt  //  200 / 25 = 8

    buckets(bucket).registerRequest(normalizedBucketTime)
  }

  /**
   * Return current rate of requests (requests per second)
   */
  override def getRPS: Int = {
    //  no need to recalculate it every time since we have millisecondResolution precision
    val stats = lastRPSStats
    val timestamp = System.currentTimeMillis()

    if (stats.timestamp + millisecondResolution < timestamp) {
      //  don't need to lock here - current rps should not be more expensive than postponing the thread
      var sum = 0
      buckets.foreach(sum += _.currentRPS(timestamp))
      lastRPSStats = RPSStats(timestamp, sum)
      sum
    } else stats.rps
  }
}
