package io.github.zyszero.phoenix.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway plugin chain.
 *
 * @Author: zyszero
 * @Date: 2024/6/15 19:45
 */
public interface GatewayPluginChain {

    Mono<Void> handle(ServerWebExchange exchange);

}
