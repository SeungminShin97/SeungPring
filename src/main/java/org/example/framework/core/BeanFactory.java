package org.example.framework.core;

public interface BeanFactory {

    /**
     * Does this bean factory contain a bean definition or externally registered singleton instance with the given name?
     */
    boolean containsBean(String name);

    /**
     * Return the aliases for the given bean name, if any.
     */
    String[] getAliases(String name);

    <T> T getBean(Class<T> requiredType);

    Object getBean(String beanName);

    /**
     * Determine the type of the bean with the given name.
     */
    Class<?> getType(String name);

    boolean isPrototype(String name);

    boolean isSingleton(String name);

    /**
     * Check whether the bean with the given name matches the specified type.
     */
    boolean isTypeMath(String name, Class<?> typeToMatch);
}
