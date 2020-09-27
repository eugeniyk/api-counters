# Benchmark results

Note: the baselines below I've got by running JMH benchmarks on my 8core Mac

### PRSCountersBenchmark

`sbt "performance/jmh:run -i 7 -wi 3 -f1 PRSCountersBenchmark"`:

| Benchmark                                                                   | Mode | Cnt |   Score  |  Error  | Units |
|-----------------------------------------------------------------------------|------|-----|----------|---------|-------|
| PRSCountersBenchmark\.LastSecondBucket                                      | avgt | 10  | 124\.530 | 9\.199  | ns/op |
| PRSCountersBenchmark\.LastSecondBucket:lastSecondBucketRPSCounterGetRPS     | avgt | 10  | 153\.001 | 11\.379 | ns/op |
| PRSCountersBenchmark\.LastSecondBucket:lastSecondBucketRPSCounterRegister   | avgt | 10  | 96\.060  | 7\.934  | ns/op |
| PRSCountersBenchmark\.SlidingWindow                                         | avgt | 10  | 70\.598  | 7\.099  | ns/op |
| PRSCountersBenchmark\.SlidingWindow:slidingWindowCounterGetRPS              | avgt | 10  | 43\.707  | 4\.414  | ns/op |
| PRSCountersBenchmark\.SlidingWindow:slidingWindowCounterRegister            | avgt | 10  | 97\.489  | 9\.785  | ns/op |

### RequestCounterBenchmark

` sbt "performance/jmh:run -i 10 -wi 5 -f1 -t8 RequestCounterBenchmark"`

| Benchmark                                                                  | Mode | Cnt |   Score  |  Error  | Units |
|----------------------------------------------------------------------------|------|-----|----------|---------|-------|
| RequestCounterBenchmark\.requestCounter                                    | avgt | 10  | 133\.648 | 8\.944  | ns/op |
| RequestCounterBenchmark\.requestCounter:inflightRequests                   | avgt | 10  | 191\.595 | 12\.475 | ns/op |
| RequestCounterBenchmark\.requestCounter:registerRequest                    | avgt | 10  | 149\.83  | 10\.692 | ns/op |
| RequestCounterBenchmark\.requestCounter:registerSuccessResponse            | avgt | 10  | 59\.519  | 4\.02   | ns/op |