package com.vincent.inc.File_manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vincent.inc.File_manager.config.filter.AuthenticationFilter;
import com.vincent.inc.File_manager.config.filter.FileBrowserFilter;
import com.vincent.inc.File_manager.config.filter.FileBrowserPostRewriteFn;
import com.vincent.inc.File_manager.service.FileBrowserService;

@Configuration
public class GatewayConfig 
{
    public static final String BROWSE = "/browse";
    public static final String UPLOAD = "/upload";
    public static final String PUBLIC = "/public";

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private FileBrowserFilter fileBrowserFilter;

    @Autowired
    private FileBrowserService fileBrowserService;

    @Autowired
    private FileBrowserPostRewriteFn fileBrowserPostRewriteFn;

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder)
    {
        return builder.routes()
            //-----------------------------TESTING---------------------------------------
            .route(r -> r
                .path(BROWSE + "/**")
				.filters(f -> f.stripPrefix(1)
                               .filter(authenticationFilter)
                               .filter(fileBrowserFilter))
                .uri(fileBrowserService.getFileBrowserUrl()))
            .route(r -> r
                .path(UPLOAD + "/**")
				.filters(f -> f.stripPrefix(1)
                               .filter(authenticationFilter)
                               .filter(fileBrowserFilter)
                               .modifyResponseBody(String.class, String.class, fileBrowserPostRewriteFn))
                .uri(fileBrowserService.getFileBrowserUrl()))
            .route(r -> r
                .path(PUBLIC + "/**")
				.filters(f -> f.stripPrefix(1)
                               .filter(authenticationFilter)
                               .filter(fileBrowserFilter))
                .uri(fileBrowserService.getFileBrowserUrl()))
            .build();
    }
}
