# Benchmark results

Note: the baselines below I've got by running JMH benchmarks on my 8core Mac

### PRSCountersBenchmark

`sbt "performance/jmh:run -i 10 -wi 5 -f1 -t16 PRSCountersBenchmark"`:

| Benchmark                                                                   | Mode | Cnt |   Score  |  Error  | Units |
|-----------------------------------------------------------------------------|------|-----|----------|---------|-------|
| PRSCountersBenchmark\.LastSecondBucket                                      | avgt | 10  | 218\.128 | 52\.965 | ns/op |
| PRSCountersBenchmark\.LastSecondBucket:lastSecondBucketRPSCounterGetRPS     | avgt | 10  | 321\.294 | 80\.202 | ns/op |
| PRSCountersBenchmark\.LastSecondBucket:lastSecondBucketRPSCounterRegister   | avgt | 10  | 114\.962 | 25\.787 | ns/op |
| PRSCountersBenchmark\.SlidingWindow                                         | avgt | 10  | 66\.142  | 1\.414  | ns/op |
| PRSCountersBenchmark\.SlidingWindow:slidingWindowCounterGetRPS              | avgt | 10  | 42\.742  | 0\.943  | ns/op |
| PRSCountersBenchmark\.SlidingWindow:slidingWindowCounterRegister            | avgt | 10  | 89\.543  | 1\.887  | ns/op |

### RequestCounterBenchmark

` sbt "performance/jmh:run -i 10 -wi 5 -f1 -t8 RequestCounterBenchmark"`

| Benchmark                                                                  | Mode | Cnt |   Score  |  Error  | Units |
|----------------------------------------------------------------------------|------|-----|----------|---------|-------|
| RequestCounterBenchmark\.requestCounter                                    | avgt | 10  | 133\.648 | 8\.944  | ns/op |
| RequestCounterBenchmark\.requestCounter:inflightRequests                   | avgt | 10  | 191\.595 | 12\.475 | ns/op |
| RequestCounterBenchmark\.requestCounter:registerRequest                    | avgt | 10  | 149\.83  | 10\.692 | ns/op |
| RequestCounterBenchmark\.requestCounter:registerSuccessResponse            | avgt | 10  | 59\.519  | 4\.02   | ns/op |