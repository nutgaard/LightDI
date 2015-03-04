package no.utgdev.lightdi.bean;

import no.utgdev.lightdi.annotations.Bean;
import no.utgdev.lightdi.exceptions.LightDIAlreadyStartedException;
import no.utgdev.lightdi.exceptions.LightDIHasNotBeenStartedException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

public class BeanFactory {
    final static Logger logger = getLogger(BeanFactory.class);

    private static BeanFactory instance;
    private final String rootPackage;

    public static BeanFactory getInstance() {
        if (instance == null) {
            throw new LightDIHasNotBeenStartedException("LightDI has not been started, yet you tried to get the BeanFactory. Call LightDI.start(rootPackage) before calling getInstance()");
        }
        return instance;
    }

    public static void start(String rootPackage) {
        if (instance != null) {
            throw new LightDIAlreadyStartedException();
        }
        instance = new BeanFactory(rootPackage);
    }

    public BeanFactory(String rootPackage) {
        this.rootPackage = rootPackage;
        scanForBeanDefinitions();
    }

    private void scanForBeanDefinitions() {
        Reflections reflections = new Reflections(rootPackage, new MethodAnnotationsScanner(), new TypeAnnotationsScanner());

        Set<Method> methods = reflections.getMethodsAnnotatedWith(Bean.class);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(Bean.class);

    }
}
