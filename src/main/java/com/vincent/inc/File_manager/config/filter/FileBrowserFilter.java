package com.vincent.inc.File_manager.config.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import com.vincent.inc.File_manager.model.FileBrowserToken;
import com.vincent.inc.File_manager.service.FileBrowserService;
import com.vincent.inc.File_manager.util.Http.HttpResponseThrowers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
@Slf4j
public class FileBrowserFilter implements GatewayFilter {

    @Autowired
    private FileBrowserService fileBrowserService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();

            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, request.getURI());
            ServerWebExchange modExchange = populateRequestWithHeadersAndQuery(exchange);
            
            return chain.filter(modExchange);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            HttpResponseThrowers.throwServerError("server is experiencing some error");
        }

        return chain.filter(exchange);
    }

    public ServerWebExchange populateRequestWithHeadersAndQuery(ServerWebExchange exchange) {
        FileBrowserToken token = this.fileBrowserService.getToken();
        ServerHttpRequest request = exchange.getRequest();
        
        var uri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
            .queryParam("auth", token.getXAuth())
            .queryParam("override", "true")
            .build()
            .toUri();

        request = request.mutate()
                .header("Cookie", token.getCookie())
                .header("X-Auth", token.getXAuth())
                .uri(uri)
                .build();

        return exchange.mutate().request(request).build();
    }
}
