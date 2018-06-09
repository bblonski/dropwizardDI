package com.bblonski.dropwizard.ext;

import com.codahale.metrics.health.HealthCheck;

import javax.inject.Singleton;

@Singleton
public class MyHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
