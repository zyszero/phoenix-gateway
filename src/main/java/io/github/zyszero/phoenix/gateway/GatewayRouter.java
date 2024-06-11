package io.github.zyszero.phoenix.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * gateway router
 *
 * @Author: zyszero
 * @Date: 2024/6/7 4:46
 */
@Component
public class GatewayRouter {

    @Autowired
    HelloHandler helloHandler;

    @Autowired
    GatewayHandler gatewayHandler;

    @Bean
    public RouterFunction<?> helloRouterFunction() {
        return route(GET("/hello"), helloHandler::handle);
    }


    @Bean
    public RouterFunction<?> gatewayRouterFunction() {
        return route(GET("/gw").or(POST("/gw/**")),
                gatewayHandler::handle);
    }
}
