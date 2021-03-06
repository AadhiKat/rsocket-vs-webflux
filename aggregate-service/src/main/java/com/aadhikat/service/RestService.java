package com.aadhikat.service;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RestService {

    private WebClient webClient;

    @Value("${rest.service.url}")
    private String url;

    private HttpClient getHttpClientFactory() {
        final ReactorResourceFactory reactorResourceFactory = new ReactorResourceFactory();
        reactorResourceFactory.setConnectionProvider(
                ConnectionProvider.builder("prism-cp")
                        .maxConnections(1000)
                        .build());

        return HttpClient.create(reactorResourceFactory.getConnectionProvider())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .responseTimeout(Duration.ofMillis(1000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(1000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(1000, TimeUnit.MILLISECONDS)));
    }

    @PostConstruct
    private void init() {
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(getHttpClientFactory()))
                .baseUrl(url)
                .build();
    }

    public Mono<Void> requestResponseV1(int input) {
        return Flux.range(1, input)
                .flatMap(i -> this.webClient
                        .get()
                        .uri("v1/{input}", i)
                        .retrieve()
                        .bodyToMono(Integer.class)
                        .map(k -> Tuples.of(i, k))
                )
                .then()
                .doOnNext(System.out::println);
    }

//    public Mono<Void> requestResponseV2(int input) {
//        return Flux.range(1, input)
//                .flatMap(i -> this.webClient
//                        .get()
//                        .uri("v2/{input}", i)
//                        .retrieve()
//                        .bodyToMono(Integer.class)
//                        .map(k -> Tuples.of(i, k))
//                )
//                .then()
//                .doOnNext(System.out::println);
//    }
}
