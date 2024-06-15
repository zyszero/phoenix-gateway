package io.github.zyszero.phoenix.gateway.filter;

import io.github.zyszero.phoenix.gateway.GatewayFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: zyszero
 * @Date: 2024/6/15 20:45
 */
@Component("demoFilter")
public class DemoFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        System.out.println(" ====>>> filters: demo filter ...");
        exchange.getRequest().getHeaders()
                .toSingleValueMap()
                .forEach((key, value) -> System.out.println(key + " : " + value));
        return Mono.empty();
    }
}
