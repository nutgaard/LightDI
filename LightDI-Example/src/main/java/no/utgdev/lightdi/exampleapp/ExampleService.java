package no.utgdev.lightdi.exampleapp;

import no.utgdev.lightdi.annotations.Bean;
import org.slf4j.Logger;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@Bean
public class ExampleService {
    final static Logger logger = getLogger(ExampleService.class);

    @Inject
    private ExampleRepository repository;

    @Inject DBAbstraction db;

    public ExampleService() {
        logger.debug("ExampleService constructor");
    }
}
