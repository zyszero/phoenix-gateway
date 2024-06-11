package io.github.zyszero.phoenix.gateway;

import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.registry.phoenix.PhoenixRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return  new RoundRibonLoadBalancer<>();
    }
}
