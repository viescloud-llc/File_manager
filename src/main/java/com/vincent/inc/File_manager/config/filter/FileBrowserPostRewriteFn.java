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

        if ((requestMethod.equals("POST") || requestMethod.equals("PUT")) && status.is2xxSuccessful()) {
            int userId = AuthenticationFilter.getUserId(request);
            String path = String.format("/%s%s", userId, request.getPath().subPath(2).value());
            var dbItem = this.fileBrowserService.getItem(path);
            
            if(ObjectUtils.isEmpty(dbItem))
                HttpResponseThrowers.throwServerError("Server file service is having technical difficulty");

            if(!dbItem.getSharedUsers().contains(userId)) {
                dbItem.getSharedUsers().add(userId);
                dbItem = fileBrowserService.getFileBrowserItemService().patchFileBrowserItem(dbItem.getId(), dbItem);
            }

            if(requestMethod.equals("PUT")) {
                var fetchItem = this.fileBrowserService.fetchItem(path);
                fetchItem.getSharedUsers().addAll(dbItem.getSharedUsers());
                fetchItem.setPublic(dbItem.isPublic());
                dbItem = fileBrowserService.getFileBrowserItemService().patchFileBrowserItem(dbItem.getId(), fetchItem);
            }

            var newItemJson = this.gson.toJson(dbItem);
            response.setRawStatusCode(requestMethod.equals("POST") ? 201 : 200);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return Mono.just(newItemJson);
        }

        return Mono.just(u);
    }
}
