package io.github.zyszero.phoenix.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: zyszero
 * @Date: 2024/6/15 18:58
 */
public abstract class AbstractGatewayPlugin implements GatewayPlugin {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        boolean supported = support(exchange);
        System.out.println("====>>> plugin[" + this.getName() + "] support=" + supported);
        return supported ? doHandle(exchange) : Mono.empty();
    }


    @Override
    public boolean support(ServerWebExchange exchange) {
        return doSupport(exchange);
    }

    public abstract boolean doSupport(ServerWebExchange exchange);

    public abstract Mono<Void> doHandle(ServerWebExchange exchange);
}
