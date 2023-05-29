package com.example.demo.tools.conversion;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

abstract class ConversionUtility {

    /**
     * Fetches all fields present in the object then Maps fields to their name
     * @param object - Object to fetch fields from
     * @return - Fields mapped to their name present in object
     * @param <O> - Type of object
     */
    static <O> Map<String, Field> getObjectFields(O object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, field -> field));
    }

    /**
     * Fetches the value of the specified field on the given target
     * @param field - field value to fetch
     * @param target - target to fetch the value from
     * @return - value of the declared field
     * @param <T> - target type
     * @throws NoSuchMethodException -
     * @throws InvocationTargetException -
     * @throws IllegalAccessException -
     */
    static <T> Object getFieldValue(Field field, T target) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return getFieldValue(field, target, target);
    }

    /**
     * Fetches the value of the specified field on the given target
     * @param field - field value to fetch
     * @param invoker - method invoker object
     * @param target - target to fetch the value from
     * @return - value of the declared field
     * @param <T> - target type
     * @param <I> - method invoker type
     * @throws NoSuchMethodException -
     * @throws InvocationTargetException -
     * @throws IllegalAccessException -
     */
    static <T, I> Object getFieldValue(Field field, I invoker, T target) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String getterName = "";

        if(field.getType().getSimpleName().equals("boolean")) {
            if(field.getName().startsWith("is"))
                getterName = field.getName();
            else
                getterName = "is" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
        }
        else {
            getterName = "get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
        }

        return invoker.getClass().getDeclaredMethod(getterName).invoke(target);
    }

    /**
     * Sets the given value to the specified target
     * @param field - field to set value to
     * @param target - target to set value to
     * @param parameterType - type of the field
     * @param value - value to set
     * @param <T> - target type
     * @throws NoSuchMethodException -
     * @throws InvocationTargetException -
     * @throws IllegalAccessException -
     */
    static <T> void setValueToField(Field field, T target, Class<?> parameterType, Object value) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        setValueToField(field, target, target, parameterType, value);
    }

    /**
     * Sets the given value to the specified target
     * @param field - field to set value to
     * @param invoker - method invoker object
     * @param target - target to set value to
     * @param parameterType - type of the field
     * @param value - value to set
     * @param <T> - target type
     * @param <I> - invoker type
     * @throws NoSuchMethodException -
     * @throws InvocationTargetException -
     * @throws IllegalAccessException -
     */
    static <T, I> void setValueToField(Field field, I invoker, T target, Class<?> parameterType, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        invoker.getClass().getDeclaredMethod( "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), parameterType).invoke(target, value);
    }
}
