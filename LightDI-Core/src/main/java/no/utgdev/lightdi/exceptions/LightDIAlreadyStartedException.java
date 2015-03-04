package no.utgdev.lightdi.exceptions;

public class LightDIAlreadyStartedException extends RuntimeException {
    public LightDIAlreadyStartedException() {
        this("The LightDI library was already started.");
    }

    public LightDIAlreadyStartedException(String message) {
        super(message);
    }
}
