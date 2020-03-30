package com.anyservice.config;

import com.hazelcast.config.*;
import com.hazelcast.map.merge.PassThroughMergePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class HazelcastConfig extends CachingConfigurerSupport {

    @Value("${security.jwt.uuid.live.seconds}")
    private int uuidLive;

    @Value("${security.jwt.uuid.live.seconds}")
    private int verificationCodeLive;

    @Bean
    public Config hazelCastConfig() {
        return new Config()
                .setInstanceName("gw-hazelcast")
                .setGroupConfig(new GroupConfig("GW_HZ_GROUP", "GW_HZ_GROUP_PSSWRD"))
                .setNetworkConfig(
                        new NetworkConfig()
                                .setPort(5600)
                                .setPortAutoIncrement(true)
                )
                .addMapConfig(
                        new MapConfig()
                                .setName("uuidTokenMap")
                                .setBackupCount(2)
                                .setMaxSizeConfig(new MaxSizeConfig(10000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                                .setReadBackupData(true)
                                .setEvictionPolicy(EvictionPolicy.NONE)
                                .setMergePolicyConfig(new MergePolicyConfig()
                                        .setPolicy(PassThroughMergePolicy.class.getName()))
                                .setTimeToLiveSeconds(uuidLive))
                .addMapConfig(
                        // Map for keeping the verification codes
                        new MapConfig()
                                .setName("verificationCodeMap")
                                .setBackupCount(2)
                                .setMaxSizeConfig(new MaxSizeConfig(10000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                                .setReadBackupData(true)
                                .setEvictionPolicy(EvictionPolicy.NONE)
                                .setMergePolicyConfig(new MergePolicyConfig()
                                        .setPolicy(PassThroughMergePolicy.class.getName()))
                                .setTimeToLiveSeconds(verificationCodeLive));
    }
}
