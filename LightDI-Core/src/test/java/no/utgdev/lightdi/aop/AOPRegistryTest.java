package no.utgdev.lightdi.aop;

import no.utgdev.lightdi.annotations.Bean;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AOPRegistryTest {
    @Test
    public void AOPRegistryBehavesAsSingleton() {
        AOPRegistry registry1 = AOPRegistry.getInstance();
        AOPRegistry registry2 = AOPRegistry.getInstance();

        assertThat(registry1, is(registry2));
    }

    @Test
    public void AOPFinnesExampleAnnotation() {
        AOPRegistry registry = AOPRegistry.getInstance();
        assertThat(registry.getAll(), contains(Bean.class));
    }

    private TypeSafeMatcher<? super List<AOPRegistry.AOPConfig>> contains(final Class<? extends Annotation> annotationClass) {
        return new TypeSafeMatcher<List<AOPRegistry.AOPConfig>>() {
            @Override
            protected boolean matchesSafely(List<AOPRegistry.AOPConfig> aopConfigs) {
                for (AOPRegistry.AOPConfig config : aopConfigs) {
                    if (config.annotationClass.equals(annotationClass)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Could not find in array" + annotationClass);
            }
        };
    }

}