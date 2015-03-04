package no.utgdev.lightdi.example.beanfactorytest;

import no.utgdev.lightdi.annotations.Bean;

public class ClassWithMethodBeanAnnotation {
    private final String something = "Some property";

    public ClassWithMethodBeanAnnotation() {
    }

    @Bean
    public String getSomething() {
        return something;
    }

    @Bean
    public Integer getComplexType() {
        return null;
    }
}
