package io.github.zyszero.phoenix.gateway;

import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
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
    RegistryCenter registryCenter;

    @Autowired
    LoadBalancer<InstanceMeta> loadBalancer;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {

        System.out.println("===>>>> Phoenix Gateway web handler ...");

        // 1. 通过请求路径获取服务名
        String service = exchange.getRequest().getPath().value().substring(4);
        ServiceMeta serviceMeta = ServiceMeta.builder().name(service)
                .app("app1").env("dev").namespace("public").build();
        // 2. 通过 registryCenter 获取所有或者的服务实例
        List<InstanceMeta> instanceMetas = registryCenter.fetchAll(serviceMeta);

        // 3. 通过负载均衡算法选择一个服务实例
        InstanceMeta instance = loadBalancer.choose(instanceMetas);
        String url = instance.toUrl();

        // 4. 获取请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 5. 通过 webClient 发送 post 请求
        WebClient client = WebClient.create(url);

        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class)
                .retrieve()
                .toEntity(String.class);

        // 6. 通过 entity 获取响应的报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
//        body.subscribe(source -> System.out.println("response: " + source));

        // 7. 组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("phoenix.gw.version", "v1.0.0");


        return body.flatMap(data -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(data.getBytes()))));

    }
}
