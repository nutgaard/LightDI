package no.utgdev.lightdi.exampleapp;

import no.utgdev.lightdi.annotations.Bean;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Bean
public class ExampleRepository {
    final static Logger logger = getLogger(ExampleRepository.class);

    public ExampleRepository() {
        logger.debug("ExampleRepository constructor");
    }
}
