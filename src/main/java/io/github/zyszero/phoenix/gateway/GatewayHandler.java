package io.github.zyszero.phoenix.gateway;

import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: zyszero
 * @Date: 2024/6/7 5:01
 */
@Component
public class GatewayHandler {

    @Autowired
    RegistryCenter registryCenter;

    @Autowired
    LoadBalancer<InstanceMeta> loadBalancer;

    Mono<ServerResponse> handle(ServerRequest request) {
        // 1. 通过请求路径获取服务名
        String service = request.path().substring(4);
        ServiceMeta serviceMeta = ServiceMeta.builder().name(service)
                .app("app1").env("dev").namespace("public").build();
        // 2. 通过 registryCenter 获取所有或者的服务实例
        List<InstanceMeta> instanceMetas = registryCenter.fetchAll(serviceMeta);

        // 3. 通过负载均衡算法选择一个服务实例
        InstanceMeta instance = loadBalancer.choose(instanceMetas);
        String url = instance.toUrl();

        // 4. 获取请求的报文
        Mono<String> requestMono = request.bodyToMono(String.class);

        return requestMono.flatMap(reqMono -> invokeFromRegistry(reqMono, url));

    }

    @NotNull
    private static Mono<ServerResponse> invokeFromRegistry(String reqMono, String url) {
        // 5. 通过 webClient 发送 post 请求
        WebClient client = WebClient.create(url);

        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(reqMono)
                .retrieve()
                .toEntity(String.class);

        // 6. 通过 entity 获取响应的报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        body.subscribe(source -> System.out.println("response: " + source));

        // 7. 组装响应报文
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("phoenix.gw.version", "v1.0.0")
                .body(body, String.class);
    }
}
