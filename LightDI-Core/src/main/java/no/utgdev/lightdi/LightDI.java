package no.utgdev.lightdi;

import no.utgdev.lightdi.bean.BeanFactory;
import no.utgdev.lightdi.exceptions.LightDIAlreadyStartedException;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class LightDI {
    final static Logger logger = getLogger(LightDI.class);

    private static LightDI instance;
    private final String rootPackage;

    private LightDI(String rootPackage) {
        this.rootPackage = rootPackage;
        BeanFactory.start(rootPackage);
    }

    public static LightDI start(String rootPackage) {
        if (rootPackage == null) {
            throw new NullPointerException("rootPackage cannot be null");
        }
        if (instance != null) {
            throw new LightDIAlreadyStartedException();
        }
        instance = new LightDI(rootPackage);
        return instance;
    }
}
