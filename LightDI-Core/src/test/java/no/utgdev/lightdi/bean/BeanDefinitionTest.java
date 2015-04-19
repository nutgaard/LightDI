package no.utgdev.lightdi.bean;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BeanDefinitionTest {

    @Before
    public void setup() {
        BeanFactory.start("");
    }

    @Test
    public void scanningForDependenciesFindsAnnotatedFields() throws Exception {
        BeanDefinition.FromType fromType = new BeanDefinition.FromType(ClassWithAnnotatedFields.class);
        fromType.findBeanDependencies();

        assertThat(fromType.beanDependencies.size(), is(1));

        BeanDefinition beanDefinition = fromType.beanDependencies.get(0);
        assertThat(beanDefinition.beanClass == String.class, is(true));

    }
}

class ClassWithAnnotatedFields {

    @Inject
    private String test;


    public ClassWithAnnotatedFields() {
    }
}