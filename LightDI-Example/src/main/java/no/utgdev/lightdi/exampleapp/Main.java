package no.utgdev.lightdi.exampleapp;

import no.utgdev.lightdi.LightDI;

import javax.inject.Inject;

public class Main {
    @Inject
    private ExampleService service;

    @Inject
    private ExampleSessionInterface session;

    public static void main(String[] args) {
        LightDI.start("no.utgdev.lightdi.exampleapp");

        Main main = LightDI.getBean(Main.class);
        System.out.println("Main: "+ main);
        System.out.println("Service: "+ main.service);
    }
}
