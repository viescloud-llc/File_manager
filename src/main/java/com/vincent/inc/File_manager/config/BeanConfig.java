package com.vincent.inc.File_manager.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurerComposite;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.google.gson.Gson;

@Configuration
public class BeanConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {

        final CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("*"));
        corsConfig.addAllowedHeader("*");

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public WebFluxConfigurer corsConfigurer() {
        return new WebFluxConfigurerComposite() {

            @Override
            public void addCorsMappings(org.springframework.web.reactive.config.CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedHeaders("*")
                        .allowedOrigins("*")
                        .allowedMethods("*");
            }
        };
    }

    // @Bean
    // public WebMvcConfigurer CORSConfigurer() {
    // return new WebMvcConfigurer() {
    // @Override
    // public void
    // addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry
    // registry) {
    // registry.addMapping("/**")
    // .allowedOrigins("*")
    // .allowedHeaders("*")
    // .allowedMethods("*");
    // }
    // };
    // }

    @Bean
    public HttpMessageConverters messageConverter(ObjectProvider<HttpMessageConverter<?>> converter) {
        return new HttpMessageConverters(converter.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public static RestTemplate restTemplate() {
        // RestTemplate restTemplate = restTemplateBuilder
        // .setConnectTimeout(Duration.ofMillis(connectTimeout))
        // .setReadTimeout(Duration.ofMillis(connectTimeout))
        // .build();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }

    private static class RestTemplateErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            var inputStream = response.getBody();

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1;) {
                result.write(buffer, 0, length);
            }

            System.out.println(result.toString(StandardCharsets.UTF_8));
        }
    }
}