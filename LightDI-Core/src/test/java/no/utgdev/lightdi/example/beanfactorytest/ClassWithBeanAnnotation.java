package no.utgdev.lightdi.example.beanfactorytest;

import no.utgdev.lightdi.annotations.Bean;

@Bean
public class ClassWithBeanAnnotation {
    private final String something = "Some property";

    public ClassWithBeanAnnotation() {
    }
}
