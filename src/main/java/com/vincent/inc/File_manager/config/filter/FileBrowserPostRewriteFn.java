package com.vincent.inc.File_manager.config.filter;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;

import com.google.gson.Gson;
import com.vincent.inc.File_manager.service.FileBrowserService;
import com.vincent.inc.File_manager.util.Http.HttpResponseThrowers;

import reactor.core.publisher.Mono;

@Component
public class FileBrowserPostRewriteFn implements RewriteFunction<String, String> {

    @Autowired
    private Gson gson;

    @Autowired
    private FileBrowserService fileBrowserService;

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String u) {
        var request = exchange.getRequest();
        var response = exchange.getResponse();
        String requestMethod = request.getMethod().name().toUpperCase();
        var status = response.getStatusCode();

        if (requestMethod.equals("POST") && status.is2xxSuccessful()) {
            int userId = AuthenticationFilter.getUserId(request);
            String path = String.format("/%s%s", userId, request.getPath().subPath(2).value());
            var newItem = this.fileBrowserService.getItem(path);

            if(ObjectUtils.isEmpty(newItem))
                HttpResponseThrowers.throwServerError("Server file service is having technical difficulty");

            if(!newItem.getSharedUsers().contains(userId))
                newItem.getSharedUsers().add(userId);
            newItem = fileBrowserService.getFileBrowserItemService().patchFileBrowserItem(newItem.getId(), newItem);
            var newItemJson = this.gson.toJson(newItem);
            response.setRawStatusCode(201);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return Mono.just(newItemJson);
        }

        return Mono.just(u);
    }
}
