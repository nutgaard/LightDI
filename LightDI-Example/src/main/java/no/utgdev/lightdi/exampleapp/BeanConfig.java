package no.utgdev.lightdi.exampleapp;

import no.utgdev.lightdi.annotations.Bean;

public class BeanConfig {

    @Bean
    public DBAbstraction dbAbstraction() {
        return new DBImplementation();
    }

}
