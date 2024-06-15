package io.github.zyszero.phoenix.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway filter
 *
 * @Author: zyszero
 * @Date: 2024/6/15 20:42
 */
public interface GatewayFilter {
    Mono<Void> filter(ServerWebExchange exchange);
}
