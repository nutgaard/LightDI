package no.utgdev.lightdi.aop;

import no.utgdev.lightdi.annotations.AOPAnnotation;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class AOPRegistry {
    final static Logger logger = getLogger(AOPRegistry.class);

    private static final AOPRegistry instance = new AOPRegistry();
    private final List<AOPConfig> registry;

    public static AOPRegistry getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    private AOPRegistry() {
        Reflections reflections = new Reflections("");
        logger.info("Scanning for annotations annotated with AOPAnnotation.class");
        registry = reflections.getTypesAnnotatedWith(AOPAnnotation.class, true)
                .stream()
                .filter((Class<?> cls) -> cls != AOPAnnotation.class)
                .map(cls -> (Class<? extends Annotation>) cls)
                .map(AOPConfig::new)
                .collect(Collectors.toList());

        logger.info("AOPRegistry started. Found: " + registry);
    }

    public List<AOPConfig> getAll() {
        return Collections.unmodifiableList(this.registry);
    }

    public Stream<AOPConfig> getAllStream() {
        return getAll().stream();
    }

    public static class AOPConfig {
        public final Class<? extends Annotation> annotationClass;

        public AOPConfig(Class<? extends Annotation> annotationClass) {
            this.annotationClass = annotationClass;
        }

        @Override
        public String toString() {
            return "AOPConfig{" +
                    "annotationClass=" + annotationClass +
                    '}';
        }

        public static class Property {
            public static Function<? super AOPConfig, Class<? extends Annotation>> annotationClass = (AOPConfig aopConfig) -> aopConfig.annotationClass;
        }
    }
}
