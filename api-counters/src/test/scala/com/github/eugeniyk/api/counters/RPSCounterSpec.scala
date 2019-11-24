package com.github.eugeniyk.api.counters

import java.util.concurrent.Executors

import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.Result
import org.specs2.mutable.Specification

import scala.concurrent.{ExecutionContext, Future}

class RPSCounterSpec(implicit env: ExecutionEnv) extends Specification {
  "RPS counter" should {

    "be able to accumulate requests" in {
      val total = 100000

      def counterTest(counter: RPSCounter) = {
        1.to(total).foreach(_ => counter.registerRequest())

        counter.getRPS should_== total
      }

      "SlidingWindow implementation" in {
        val counter = new SlidingWindowRPSCounter()
        counterTest(counter)

        Thread.sleep(1100)  //  avoid cache result

        counter.getRPS should_== 0
      }

      "LastSecondBucket implementation" in {
        //  we need to wait since the actual RPS we've got from last second
        val counter = new LastSecondBucketRPSCounter()
        counterTest(counter)

        Thread.sleep(1010)

        counter.getRPS should beLessThan(total) //  but could be more than 0

        Thread.sleep(500)
        counter.getRPS should beLessThan(total / 2)

        Thread.sleep(500)
        counter.getRPS should_== 0
      }
    }

    "concurrency test" in {
      val concurrency = 40
      val total = 10000

      def concurrentTest(counter: RPSCounter): Result = {
        implicit val ec = ExecutionContext.fromExecutor(Executors.newWorkStealingPool())

        val start = System.currentTimeMillis()
        val futures = 1.to(concurrency).map { _ => Future {
          1.to(total).foreach(_ => counter.registerRequest())
        }(ec)}

        val result = Future.sequence(futures).map { _ =>
          counter.getRPS should_== total * concurrency
        }.await

        println(s"Time taken: ${System.currentTimeMillis() - start}")
        result
      }

      "SlidingWindow implementation" in {
        concurrentTest(new SlidingWindowRPSCounter())
      }

      "LastSecondBucket implementation" in {
        concurrentTest(new LastSecondBucketRPSCounter())
      }
    }
  }
}