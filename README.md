A sample ktor server using Micrometer metrics to illustrate when histogram metrics are enabled, the ktor_http_requests_seconds is changed from the TYPE summary to histogram but is sent without an le label.

Steps to reproduce:
1. In IntelliJ Run the ApplicationKt
2. In a terminal run curl http://localhost:8080/metrics
3. Wait a second and run it again to see ktor_http_server_requests_seconds in the output

```
# HELP ktor_http_server_requests_seconds
# TYPE ktor_http_server_requests_seconds summary
ktor_http_server_requests_seconds{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",quantile="0.5",} 1.744830464
```

4. Stop the running ApplicationKt in IntelliJ.
5. Change the UseHistograms boolean (Monitoring.kt:14) to true.
6. Repeat steps 1-3.
7. Notice the metric changes to a histogram but is still sending ktor_http_server_requests_seconds but without the `le` label.

```
# HELP ktor_http_server_requests_seconds
# TYPE ktor_http_server_requests_seconds histogram
ktor_http_server_requests_seconds{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",quantile="0.5",} 1.409286144
ktor_http_server_requests_seconds{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",quantile="0.9",} 1.409286144
ktor_http_server_requests_seconds{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",quantile="0.95",} 1.409286144
ktor_http_server_requests_seconds{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",quantile="0.99",} 1.409286144
ktor_http_server_requests_seconds_bucket{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",le="0.001",} 0.0
ktor_http_server_requests_seconds_bucket{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",le="0.001048576",} 0.0
ktor_http_server_requests_seconds_bucket{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",le="0.001398101",} 0.0
ktor_http_server_requests_seconds_bucket{address="0:0:0:0:0:0:0:1:8080",method="GET",route="/metrics",status="200",throwable="n/a",le="0.001747626",} 0.0
...
```