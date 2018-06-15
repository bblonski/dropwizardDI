package com.bblonski.dropwizard.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.ArrayList;

public class WeldDropwizardExtension implements Extension {
    private static final Logger logger = LoggerFactory.getLogger(WeldDropwizardExtension.class);
    private final ArrayList<String> names = new ArrayList<String>();

    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
        logger.error("============================> beginning the scanning process");
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
        logger.error("============================> finished the scanning process");
    }

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        final String name = pat.getAnnotatedType().getJavaClass().getName();
        logger.debug("============================> scanning type: {}", name);
        names.add(name);
    }
}
