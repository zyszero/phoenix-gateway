package io.github.zyszero.phoenix.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway plugin
 *
 * @Author: zyszero
 * @Date: 2024/6/15 18:56
 */
public interface GatewayPlugin {

    String GATEWAY_PREFIX = "/gw";

    void start();

    void stop();

    String getName();

    boolean support(ServerWebExchange exchange);
    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);

}
