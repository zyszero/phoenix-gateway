package io.github.zyszero.phoenix.gateway.web.handler;

import io.github.zyszero.phoenix.gateway.DefaultGatewayPluginChain;
import io.github.zyszero.phoenix.gateway.GatewayFilter;
import io.github.zyszero.phoenix.gateway.GatewayPlugin;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * gateway web handler.
 *
 * @Author: zyszero
 * @Date: 2024/6/14 0:42
 */
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    @Autowired
    List<GatewayPlugin> plugins;


    @Autowired
    List<GatewayFilter> filters;


    @NotNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        System.out.println(" ===>>>> Phoenix Gateway web handler ...");

        if (plugins == null || plugins.isEmpty()) {
            String mock = """
                        {"result": "no plugin"}
                    """;
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
        }

        for (GatewayFilter filter : filters) {
            filter.filter(exchange);
        }

        return new DefaultGatewayPluginChain(plugins).handle(exchange);


//        for (GatewayPlugin plugin : plugins) {
//            if (plugin.support(exchange)) {
//                return plugin.handle(exchange);
//            }
//        }

//        String mock = """
//                    {"result": "no supported plugin"}
//                """;
//        return exchange.getResponse()
//                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }
}
