package no.utgdev.lightdi.bean;

import java.lang.reflect.Method;

public abstract class BeanDefinition {
    protected final Class beanClass;

    protected BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
    }

    public static class FromType extends BeanDefinition {

        public FromType(Class beanClass) {
            super(beanClass);
        }
    }

    public static class FromMethod extends BeanDefinition {
        private final Method method;

        public FromMethod(Method method) {
            super(method.getDeclaringClass());
            this.method = method;
        }
    }
}
