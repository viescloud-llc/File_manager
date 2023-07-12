package com.vincent.inc.File_manager.config.filter;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.gson.Gson;
import com.vincent.inc.File_manager.model.FileBrowserItem;
import com.vincent.inc.File_manager.service.FileBrowserService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class FileBrowserPostFilter implements GatewayFilter, Ordered {
    @Autowired
    private Gson gson;

    @Autowired
    private FileBrowserService fileBrowserService;

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
        .then(Mono.fromRunnable(() -> {
            var request = exchange.getRequest();
            var response = exchange.getResponse();
            String requestMethod = request.getMethod().name().toUpperCase();
            var status = response.getStatusCode();
            
            if(requestMethod.equals("POST") && status.is2xxSuccessful()) {
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
