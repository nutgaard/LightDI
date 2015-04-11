package no.utgdev.lightdi.bean;

import no.utgdev.lightdi.exceptions.LightDIFoundUnfulfilledBeans;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import static no.utgdev.lightdi.utils.CollectorUtils.toUnmodifiableList;
import static no.utgdev.lightdi.utils.ReflectionStreamUtils.ClassUtils.fieldsIn;
import static no.utgdev.lightdi.utils.ReflectionStreamUtils.FieldUtils.hasAnnotation;

public abstract class BeanDefinition {
    protected final Class<?> beanClass;
    protected List<BeanDefinition> beanDependencies;

    protected BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
    }

    public void findBeanDependencies() {
        this.beanDependencies = fieldsIn(this.beanClass)
                .filter(hasAnnotation(Inject.class))
                .map(Field::getType)
                .map(type -> BeanFactory.getInstance().getBeanDefinition(type))
                .collect(toUnmodifiableList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeanDefinition)) return false;

        BeanDefinition that = (BeanDefinition) o;

        if (beanClass != null ? !beanClass.equals(that.beanClass) : that.beanClass != null) return false;
        if (beanDependencies != null ? !beanDependencies.equals(that.beanDependencies) : that.beanDependencies != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = beanClass != null ? beanClass.hashCode() : 0;
        return result;
    }

    public abstract boolean canFulfill(Class<?> type);

    public abstract Object initialize();

    public static class FromType extends BeanDefinition {

        public FromType(Class beanClass) {
            super(beanClass);
        }

        @Override
        public boolean canFulfill(Class<?> type) {
            return type.isAssignableFrom(this.beanClass);
        }

        @Override
        public Object initialize() {
            try {
                BeanFactory beanFactory = BeanFactory.getInstance();
                Object obj = this.beanClass.newInstance();

                fieldsIn(this.beanClass)
                        .filter(hasAnnotation(Inject.class))
                        .forEach(new Consumer<Field>() {
                            @Override
                            public void accept(Field field) {
                                Class<?> type = field.getType();
                                try {
                                    Object typeInstance = beanFactory.getBean(type);

                                    field.setAccessible(true);
                                    field.set(obj, typeInstance);
                                } catch (IllegalAccessException e) {
                                    throw new LightDIFoundUnfulfilledBeans("Could not find an initialized bean fulfulling the class: " + type + " when initializing " + obj, e);
                                }
                            }
                        });

                return obj;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new LightDIFoundUnfulfilledBeans("Could not initialize object: " + this.beanClass, e);
            }
        }

        @Override
        public String toString() {
            return "BeanDefinition.FromType{" +
                    "beanClass=" + beanClass +
                    '}';
        }
    }

    public static class FromMethod extends BeanDefinition {
        private final Method method;
        private final Class declaringClass;

        public FromMethod(Method method) {
            super(method.getReturnType());
            this.method = method;
            this.declaringClass = method.getDeclaringClass();
        }

        @Override
        public String toString() {
            return "BeanDefinition.FromMethod{" +
                    "beanClass=" + beanClass +
                    ", method=" + method +
                    ", declaringClass=" + declaringClass +
                    '}';
        }

        @Override
        public boolean canFulfill(Class<?> type) {
            return type.isAssignableFrom(this.method.getReturnType());
        }

        @Override
        public Object initialize() {
            try {
                BeanFactory beanFactory = BeanFactory.getInstance();
                Object bean = beanFactory.getBean(this.declaringClass);
                return method.invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new LightDIFoundUnfulfilledBeans("Could not invoke method: " + method + " found in " + declaringClass + " as part of creating bean: " + beanClass, e);
            }
        }
    }
}
