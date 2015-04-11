package no.utgdev.lightdi.bean;

import no.utgdev.lightdi.aop.AOPRegistry;
import no.utgdev.lightdi.exceptions.LightDIAlreadyStartedException;
import no.utgdev.lightdi.exceptions.LightDIHasNotBeenStartedException;
import no.utgdev.lightdi.utils.StreamUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static no.utgdev.lightdi.aop.AOPRegistry.AOPConfig.Property;
import static no.utgdev.lightdi.utils.StreamUtils.print;
import static org.slf4j.LoggerFactory.getLogger;

public class BeanFactory {
    final static Logger logger = getLogger(BeanFactory.class);

    private static BeanFactory instance;
    private final String rootPackage;
    private List<BeanDefinition> beanDefinitions;

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
        this.beanDefinitions = new LinkedList<>();
        scanForBeanDefinitions();
    }

    public List<BeanDefinition> getAllBeanDefinitions() {
        return Collections.unmodifiableList(beanDefinitions);
    }

    private void scanForBeanDefinitions() {
        logger.info("Starting scanning for bean definitions.");
        Reflections reflections = new Reflections(rootPackage, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());

        List<Class<? extends Annotation>> registeredAnnotationsTypes = AOPRegistry.getInstance().getAllStream()
                .map(Property.annotationClass)
                .collect(Collectors.toList());

        logger.debug("Annotations to scan for: " + registeredAnnotationsTypes);

        for (Class<? extends Annotation> annotationClass : registeredAnnotationsTypes) {
            beanDefinitions.addAll(
                    reflections.getTypesAnnotatedWith(annotationClass)
                            .stream()
                            .map(BeanDefinition.FromType::new)
                            .collect(Collectors.toList())
            );
            beanDefinitions.addAll(
                    reflections.getMethodsAnnotatedWith(annotationClass)
                            .stream()
                            .map(BeanDefinition.FromMethod::new)
                            .collect(Collectors.toList())
            );
        }

        logger.info("Found bean definitions: " + beanDefinitions);

    }
}
