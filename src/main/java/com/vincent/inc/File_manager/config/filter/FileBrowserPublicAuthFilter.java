package com.vincent.inc.File_manager.config.filter;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.gson.Gson;
import com.vincent.inc.File_manager.service.FileBrowserService;

import reactor.core.publisher.Mono;

@Component
public class FileBrowserPublicAuthFilter implements GatewayFilter {
    @Autowired
    private Gson gson;

    @Autowired
    private FileBrowserService fileBrowserService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    var request = exchange.getRequest();
                    var response = exchange.getResponse();
                    String requestMethod = request.getMethod().name().toUpperCase();
                    var status = response.getStatusCode();

                    if (requestMethod.equals("POST") && status.is2xxSuccessful()) {
                        String path = request.getPath().subPath(4).value();
                        var newItem = this.fileBrowserService.getItem(path);
                        var newItemJson = this.gson.toJson(newItem);
                        response.setRawStatusCode(201);
                        DataBuffer dataBuffer = response.bufferFactory().wrap(newItemJson.getBytes(StandardCharsets.UTF_8));
                        response.writeWith(Mono.just(dataBuffer));
                        exchange.mutate().response(response).build();
                    }
                }));
    }
}
