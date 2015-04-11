package no.utgdev.lightdi;

import no.utgdev.lightdi.bean.BeanFactory;
import no.utgdev.lightdi.exceptions.LightDIAlreadyStartedException;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class LightDI {
    final static Logger logger = getLogger(LightDI.class);

    public static boolean validateConfiguration = true;
    public static boolean initializeBeansOnStartup = true;

    private static LightDI instance;

    private LightDI(String rootPackage) {
        BeanFactory.start(rootPackage);

        if (validateConfiguration) {
            BeanFactory.getInstance().validateConfiguration();
            if (initializeBeansOnStartup) {
                BeanFactory.getInstance().initializeBeans();
            }
        }
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

    public static <T> T getBean(Class<T> cls) {
        return BeanFactory.getInstance().getBean(cls);
    }
}
