package no.utgdev.lightdi.aop;

import no.utgdev.lightdi.annotations.AOPAnnotation;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class AOPRegistry {
    final static Logger logger = getLogger(AOPRegistry.class);

    private static final AOPRegistry instance = new AOPRegistry();
    private final List<AOPConfig> registry;

    public static AOPRegistry getInstance() {
        return instance;
    }

    public AOPRegistry() {
        Reflections reflections = new Reflections("");

        registry = reflections.getSubTypesOf(AOPAnnotation.class)
                .stream()
                .filter((cls) -> cls != AOPAnnotation.class)
                .map(AOPConfig::new)
                .collect(Collectors.toList());
    }

    public List<AOPConfig> getAll() {
        return Collections.unmodifiableList(this.registry);
    }

    class AOPConfig {
        public final Class<?> annotationClass;

        public AOPConfig(Class<?> annotationClass) {
            this.annotationClass = annotationClass;
        }
    }
}
