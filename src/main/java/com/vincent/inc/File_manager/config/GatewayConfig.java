package com.vincent.inc.File_manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vincent.inc.File_manager.config.filter.AuthenticationFilter;
import com.vincent.inc.File_manager.config.filter.FileBrowserFilter;
import com.vincent.inc.File_manager.config.filter.FileBrowserPostFilter;
import com.vincent.inc.File_manager.service.FileBrowserService;

@Configuration
public class GatewayConfig 
{
    public static final String BROWSER = "/browser";
    public static final String FETCH = "/fetch";

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private FileBrowserFilter fileBrowserFilter;

    @Autowired
    private FileBrowserPostFilter fileBrowserPostFilter;

    @Autowired
    private FileBrowserService fileBrowserService;

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder)
    {
        return builder.routes()
            //-----------------------------TESTING---------------------------------------
            .route(r -> r
                .path(BROWSER + "/**")
				.filters(f -> f.stripPrefix(1)
                               .filter(authenticationFilter)
                               .filter(fileBrowserFilter)
                               .filter(fileBrowserPostFilter))
                .uri(fileBrowserService.getFileBrowserUrl()))
            .route(r -> r
                .path(FETCH + "/**")
				.filters(f -> f.stripPrefix(1)
                               .filter(authenticationFilter)
                               .filter(fileBrowserFilter))
                .uri(fileBrowserService.getFileBrowserUrl()))
            .build();
    }
}
