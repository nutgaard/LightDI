package no.utgdev.lightdi.exceptions;

public class LightDIHasNotBeenStartedException extends RuntimeException {
    public LightDIHasNotBeenStartedException(String message) {
        super(message);
    }
}
