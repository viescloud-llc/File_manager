package com.vincent.inc.File_manager.config.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
        String path = getModifiedAuthPath(request);

        if (requestMethod.equals("GET")) {

            if(!this.fileBrowserService.isItemExist(path))
                HttpResponseThrowers.throwBadRequest("Item does not exist");

            path = String.format("%s%s", FileBrowserService.DOWNLOAD_PATH, path);
        } 
        else if (requestMethod.equals("POST")) {
            overrideItem(path);
            path = String.format("%s%s", FileBrowserService.UPLOAD_PATH, path);
        } 
        else
            HttpResponseThrowers.throwForbidden("Not Allow");

        return request.mutate().path(path).build();
    }

    private void overrideItem(String path) {
        var item = this.fileBrowserService.getItem(path);
        if(!ObjectUtils.isEmpty(item))
            this.fileBrowserService.getFileBrowserItemService().deleteFileBrowserItem(item.getId());
    }

    public static int getUserId(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().getOrEmpty("user_id");

        if (headers.isEmpty())
            return (int) HttpResponseThrowers.throwUnauthorized("user not login");

        String id = headers.get(0);
        int userId = Integer.parseInt(id);

        return userId;
    }

    public static String getModifiedAuthPath(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        int userId = getUserId(request);
        return String.format("/%s%s", userId, path);
    }
}
