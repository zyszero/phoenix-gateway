package io.github.zyszero.phoenix.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: zyszero
 * @Date: 2024/6/7 4:58
 */
@Component
public class HelloHandler {

    Mono<ServerResponse> handle(ServerRequest request) {

        String url = "http://localhost:8081/phoenix-rpc";

        String requestJson = """
                {
                  "service": "cn.zyszero.phoenix.rpc.demo.api.UserService",
                  "methodSign": "findById@1_int",
                  "args": [
                    100
                  ]
                }
                """;

        Mono<ResponseEntity<String>> entity = WebClient.create(url)
                .post()
                .header("Content-Type", "application/json")
                .bodyValue(requestJson).retrieve().toEntity(String.class);

        Mono<String> body = entity.map(ResponseEntity::getBody);
        body.subscribe(source -> System.out.println("response: " + source));
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("phoenix.gw.version", "v1.0.0")
                .body(body, String.class);
    }
}
