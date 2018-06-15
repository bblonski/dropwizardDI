package com.bblonski.dropwizard.ext;

import com.codahale.metrics.health.HealthCheck;

public class DBHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
