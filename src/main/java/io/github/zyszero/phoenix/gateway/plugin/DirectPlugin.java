package io.github.zyszero.phoenix.gateway.plugin;

import io.github.zyszero.phoenix.gateway.AbstractGatewayPlugin;
import io.github.zyszero.phoenix.gateway.GatewayPluginChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * direct proxy plugin.
 *
 * @Author: zyszero
 * @Date: 2024/6/15 19:08
 */
@Component("directPlugin")
public class DirectPlugin extends AbstractGatewayPlugin {

    public static final String NAME = "direct";

    private String PREFIX = GATEWAY_PREFIX + "/" + NAME + "/";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        System.out.println("=======>>>>>>> [DirectPlugin] ...");
        String backend = exchange.getRequest().getQueryParams().getFirst("backend");
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("phoenix.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("phoenix.gw.plugin", getName());

        if (backend == null || backend.isEmpty()) {
            return requestBody.flatMap(body -> exchange.getResponse().writeWith(Mono.just(body)))
                    .then(chain.handle(exchange));
        }

        WebClient client = WebClient.create(backend);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class)
                .retrieve()
                .toEntity(String.class);

        Mono<String> body = entity.map(ResponseEntity::getBody);

        return body.flatMap(data -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(data.getBytes()))))
                .then(chain.handle(exchange));

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(PREFIX);
    }
}
