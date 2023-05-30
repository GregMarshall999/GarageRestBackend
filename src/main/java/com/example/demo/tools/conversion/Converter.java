package com.example.demo.tools.conversion;

import org.javatuples.Triplet;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;

@Component
public class Converter {

    /**
     * Sets an ExampleMatcher from the given object
     * @param searchObject - object to set as example
     * @return - Setup ExampleMatcher
     * @param <O> - Type of object
     */
    public <O> ExampleMatcher buildExampleMatcher(O searchObject) {
        //Experimental function duplication
        //TODO might need rework on ID checks and default values not null
        //TODO tweek the ExampleMatcher matching method
        Function<Triplet<ExampleMatcher, List<Field>, Class<?>>, ExampleMatcher> buildMatcherFromFields = o -> {
            ExampleMatcher matcher = o.getValue0();
            List<Field> objectFields = o.getValue1();
            String fieldGetter;
            Object fieldValue;

            try {
                for (Field field : objectFields) {
                    if (field.getType().getSimpleName().equals("boolean"))
                        fieldGetter = setupBooleanMethodName("is", field);
                    else
                        fieldGetter = setupMethodName("get", field);

                    fieldValue = o.getValue2().getDeclaredMethod(fieldGetter).invoke(searchObject);
                    if (fieldValue != null)
                        matcher.withMatcher(field.getName(), new ExampleMatcher.GenericPropertyMatcher().startsWith());
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            return matcher;
        };

        ExampleMatcher matcher = ExampleMatcher.matching();
        List<Field> objectFields = List.of(searchObject.getClass().getDeclaredFields());
        Class<?> superclass;

        matcher = buildMatcherFromFields.apply(new Triplet<>(matcher, objectFields, searchObject.getClass()));

        superclass = searchObject.getClass().getSuperclass();
        while(superclass != null && !superclass.getName().equals("java.lang.Object")) {
            objectFields = List.of(superclass.getDeclaredFields());

            matcher = buildMatcherFromFields.apply(new Triplet<>(matcher, objectFields, superclass));

            superclass = superclass.getSuperclass();
        }

        return matcher;
    }

    /**
     * Appends the field name to a prefix
     * Used to build getter or setter methods
     * @param prefix - get or set normally
     * @param field - field to get or set
     * @return - built string of the method name
     */
    private String setupMethodName(String prefix, Field field) {
        return prefix + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
    }

    /**
     * Complement to setupMethodName but for boolean fields
     * @param prefix -
     * @param field -
     * @return -
     */
    private String setupBooleanMethodName(String prefix, Field field) {
        if(field.getName().startsWith("is"))
            return prefix + field.getName().substring(2);

        return prefix + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
    }
}

