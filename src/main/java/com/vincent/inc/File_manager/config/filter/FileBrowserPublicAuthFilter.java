package com.vincent.inc.File_manager.config.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.vincent.inc.File_manager.service.FileBrowserService;
import com.vincent.inc.File_manager.util.Http.HttpResponseThrowers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class FileBrowserPublicAuthFilter implements GatewayFilter {

    @Autowired
    private FileBrowserService fileBrowserService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        try {
            ServerHttpRequest request = exchange.getRequest();

            var newRequest = validateRequest(request);
            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            HttpResponseThrowers.throwServerError("server is experiencing some error");
        }

        return chain.filter(exchange);
    }

    private ServerHttpRequest validateRequest(ServerHttpRequest request) {
        String requestMethod = request.getMethod().name().toUpperCase();
        String path = request.getPath().value();
        int headerUserId = AuthenticationFilter.tryGetUserId(request);

        if(requestMethod.equals("GET")) {
            var item = this.fileBrowserService.getItem(path);

            if (ObjectUtils.isEmpty(item))
                HttpResponseThrowers.throwBadRequest("Item does not exist");

            if(item.isPublic() || item.getSharedUsers().contains(headerUserId))
                path = String.format("%s%s", FileBrowserService.DOWNLOAD_PATH, path);
            else
                HttpResponseThrowers.throwForbidden("Not allow to access this item");
        }
        else
            HttpResponseThrowers.throwForbidden("Not Allow");

        return request.mutate().path(path).build();
    }
}
