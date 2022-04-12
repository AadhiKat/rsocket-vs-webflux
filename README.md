# Rockset-vs-Webflux

Simple Experiment to understand the performance gains of RSocket over Webflux

Results - Benchmarked with Apache Bench - Tested in my local

------------------------------------------------------------------------------------------------------------------

ab -n 10 -c 1 http://localhost:8080/rest/square/10000 -> 

Requests per second:  - 0.84 [#/sec] (mean)

Time per request:     - 366.456 [ms] (mean)

------------------------------------------------------------------------------------------------------------------
ab -n 10 -c 1 http://localhost:8080/rsocket/rr/square/10000 ->


Requests per second:  - 1.98 [#/sec] (mean)

Time per request:     - 189.751 [ms] (mean)

------------------------------------------------------------------------------------------------------------------

ab -n 10 -c 1 http://localhost:8080/rsocket/rc/square/10000 ->

Requests per second:  - 17.12 [#/sec] (mean)

Time per request:     - 100.766 [ms] (mean)

------------------------------------------------------------------------------------------------------------------

