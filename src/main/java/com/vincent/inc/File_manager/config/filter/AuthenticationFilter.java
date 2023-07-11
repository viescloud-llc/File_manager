package com.vincent.inc.File_manager.config.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import com.vincent.inc.File_manager.service.FileBrowserService;
import com.vincent.inc.File_manager.util.Http.HttpResponseThrowers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
@Slf4j
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private FileBrowserService fileBrowserService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();

            String requestMethod = request.getMethod().name().toUpperCase();
            String path = request.getURI().getPath();
            int userId = this.getUserId(request);

            if(requestMethod.equals("GET")) {
                
                path = String.format("%s/%s%s", FileBrowserService.DOWNLOAD_PATH, userId, path);
            }
            else if(requestMethod.equals("POST")) {
                path = String.format("%s/%s%s", FileBrowserService.UPLOAD_PATH, userId, path);

            }
            else 
                HttpResponseThrowers.throwForbidden("Not Allow");

            var newRequest = request.mutate().path(path).build();
            return chain.filter(exchange.mutate().request(newRequest).build());
            
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            HttpResponseThrowers.throwServerError("server is experiencing some error");
        }

        return chain.filter(exchange);
    }

    public int getUserId(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().getOrEmpty("user_id");

        if (headers.isEmpty())
            return (int) HttpResponseThrowers.throwUnauthorized("user not login");

        String id = headers.get(0);
        int userId = Integer.parseInt(id);

        return userId;
    }


}
