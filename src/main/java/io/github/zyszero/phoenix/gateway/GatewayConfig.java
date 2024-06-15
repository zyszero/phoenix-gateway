package io.github.zyszero.phoenix.gateway;

import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.registry.phoenix.PhoenixRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

import static io.github.zyszero.phoenix.gateway.GatewayPlugin.GATEWAY_PREFIX;

/**
 * gateway config
 *
 * @Author: zyszero
 * @Date: 2024/6/12 0:28
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter registryCenter() {
        return new PhoenixRegistryCenter();
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
        return new RoundRibonLoadBalancer<>();
    }

    @Bean
    ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties properties = new Properties();
            properties.put(GATEWAY_PREFIX + "/**", "gatewayWebHandler");
            handlerMapping.setMappings(properties);
            handlerMapping.initApplicationContext();
            System.out.println("phoenix gateway start");
        };
    }
}
