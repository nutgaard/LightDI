package no.utgdev.lightdi.bean;

import no.utgdev.lightdi.aop.AOPRegistry;
import no.utgdev.lightdi.exceptions.LightDIAlreadyStartedException;
import no.utgdev.lightdi.exceptions.LightDIFoundUnfulfilledBeans;
import no.utgdev.lightdi.exceptions.LightDIHasNotBeenStartedException;
import no.utgdev.lightdi.utils.StreamUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static no.utgdev.lightdi.aop.AOPRegistry.AOPConfig.Property;
import static org.slf4j.LoggerFactory.getLogger;

public class BeanFactory {
    final static Logger logger = getLogger(BeanFactory.class);

    private static BeanFactory instance;
    private final String rootPackage;
    private List<BeanDefinition> beanDefinitions;
    private Predicate<Field> cannotBeFulfilledByBeanDefinitions = field -> !canBeFulfilled(field.getType());
    private Map<BeanDefinition, Object> beans = new HashMap<>();


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

    private boolean canBeFulfilled(Class<?> type) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition.canFulfill(type)) {
                return true;
            }
        }
        return false;
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
            Set<Method> annotatedMethod = reflections.getMethodsAnnotatedWith(annotationClass);
            beanDefinitions.addAll(
                    annotatedMethod
                            .stream()
                            .map(Method::getDeclaringClass)
                            .map(BeanDefinition.FromType::new)
                            .collect(Collectors.toList())
            );
            beanDefinitions.addAll(
                    annotatedMethod
                            .stream()
                            .map(BeanDefinition.FromMethod::new)
                            .collect(Collectors.toList())
            );

        }
        logger.info("Found bean definitions: " + beanDefinitions);
    }

    public void findBeandefinitonDependencies() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            beanDefinition.findBeanDependencies();
        }
    }

    public void validateConfiguration() {
        Reflections reflections = new Reflections(rootPackage, new MethodAnnotationsScanner(), new FieldAnnotationsScanner());
        List<Field> failedBeans = reflections.getFieldsAnnotatedWith(Inject.class)
                .stream()
                .map(StreamUtils.print("DI Inject: "))
                .filter(cannotBeFulfilledByBeanDefinitions)
                .collect(Collectors.toList());

        if (!failedBeans.isEmpty()) {
            throw LightDIFoundUnfulfilledBeans.create(failedBeans);
        }
    }

    public void initializeBeans() {
        List<BeanDefinition> unresolvedBeans = new ArrayList<>(this.beanDefinitions);
        int unresolved = unresolvedBeans.size();

        while (!unresolvedBeans.isEmpty()) {
            unresolvedBeans
                    .stream()
                    .map(StreamUtils.print("Beans "))
                    .filter(hasNoUnresolvedDependencies(unresolvedBeans))
                    .forEach(initializeBean(unresolvedBeans));

            unresolvedBeans.removeAll(beans.keySet());
            if (unresolved == unresolvedBeans.size()) {
                throw new RuntimeException("You got cycles in your dependency graph: " + unresolvedBeans);
            }
            unresolved = unresolvedBeans.size();
        }
    }


    private Predicate<BeanDefinition> hasNoUnresolvedDependencies(final List<BeanDefinition> unresolvedBeans) {
        return beanDefinition -> {
            List<BeanDefinition> dependencies = new ArrayList<>(beanDefinition.beanDependencies);
            dependencies.retainAll(unresolvedBeans);
            return dependencies.isEmpty();//E.g the intersection of unresolved and current dependencies is empty
        };
    }

    private Consumer<BeanDefinition> initializeBean(final List<BeanDefinition> unresolvedBeans) {
        return beanDefinition -> {
            Object obj = beanDefinition.initialize();
            beans.put(beanDefinition, obj);
        };
    }

    public <T> T getBean(Class<T> cls) {
        for (Entry<BeanDefinition, Object> entry : beans.entrySet()) {
            if (entry.getKey().canFulfill(cls)) {
                return (T) entry.getValue();
            }
        }
        BeanDefinition db = null;
        //Check for previously found beandefinitions
        for (BeanDefinition definition : beanDefinitions) {
            if (definition.canFulfill(cls)) {
                db = definition;
                break;
            }
        }
        //No definiton found, lets create one now.
        if (db == null) {
            db = new BeanDefinition.FromType(cls);
        }

        Object bean = db.initialize();
        beans.put(db, bean);
        return (T) bean;
    }

    public BeanDefinition getBeanDefinition(Class<?> type) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition.beanClass == type) {
                return beanDefinition;
            }
        }
        throw new RuntimeException("Feil: " + type);
    }
}
