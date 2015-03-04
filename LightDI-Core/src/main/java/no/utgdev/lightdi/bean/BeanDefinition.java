package no.utgdev.lightdi.bean;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static no.utgdev.lightdi.utils.CollectorUtils.toUnmodifiableList;
import static no.utgdev.lightdi.utils.ReflectionStreamUtils.ClassUtils.fieldsIn;
import static no.utgdev.lightdi.utils.ReflectionStreamUtils.FieldUtils.hasAnnotation;

public abstract class BeanDefinition {
    protected final Class<?> beanClass;
    protected final List<BeanDefinition> beanDependencies;

    protected BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
        this.beanDependencies = findBeanDependencies();
    }

    private List<BeanDefinition> findBeanDependencies() {
        return fieldsIn(this.beanClass)
                        .filter(hasAnnotation(Inject.class))
                        .map(Field::getType)
                        .map(BeanDefinition.FromType::new)
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
        result = 31 * result + (beanDependencies != null ? beanDependencies.hashCode() : 0);
        return result;
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
