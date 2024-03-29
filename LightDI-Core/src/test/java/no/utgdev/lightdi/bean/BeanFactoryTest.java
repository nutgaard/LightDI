package no.utgdev.lightdi.bean;

import no.utgdev.lightdi.example.beanfactorytest.ClassWithBeanAnnotation;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class BeanFactoryTest {

    private BeanFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new BeanFactory("no.utgdev.lightdi.example.beanfactorytest");
    }

    @Test
    public void scanningClassPathShouldFindBeanAnnotatedTypesAndMethods() throws Exception {
        List<BeanDefinition> beanDefinitions = factory.getAllBeanDefinitions();

        assertThat(beanDefinitions, contains(ClassWithBeanAnnotation.class));   //Defined in ClassWithBeanAnnotation
        assertThat(beanDefinitions, contains(String.class));                    //Defined in ClassWithMethodBeanAnnotation
        assertThat(beanDefinitions, contains(Integer.class));                   //Defined in ClassWithMethodBeanAnnotation
    }

    private TypeSafeMatcher<? super List<BeanDefinition>> contains(Class<?> cls) {
        return new TypeSafeMatcher<List<BeanDefinition>>() {


            @Override
            public void describeTo(Description description) {
                description.appendText(cls.toString() + " to be in list. But it was not");
            }

            @Override
            protected boolean matchesSafely(List<BeanDefinition> beanDefinitions) {
                for (BeanDefinition definition : beanDefinitions) {
                    if (definition.beanClass.equals(cls)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}

