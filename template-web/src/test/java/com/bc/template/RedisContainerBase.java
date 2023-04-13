package com.bc.template;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisContainerBase implements BeforeAllCallback {

    private static GenericContainer<?> redis = null;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (redis == null) {
            redis = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);
            redis.start();
            System.setProperty("redis.host", redis.getHost());
            System.setProperty("redis.port", redis.getMappedPort(6379).toString());
        }
    }
}
