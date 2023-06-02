package com.example.demo.tools.search;

import com.example.demo.entity.BaseEntity;
import jakarta.persistence.Embedded;
import org.springframework.data.domain.ExampleMatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class Searcher {
    public static <O> ExampleMatcher buildExampleMatcher(O searchObject) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        List<String> ignoredPaths = setAllIgnoredPaths(searchObject, "");

        if(ignoredPaths.size() != 0)
            matcher = matcher.withIgnorePaths(ignoredPaths.toArray(new String[0]));

        return matcher;
    }

    private static <O> List<String> setAllIgnoredPaths(O searchObject, String pathToField) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<String> ignoredPaths = new ArrayList<>();
        List<Field> declaredFields = List.of(searchObject.getClass().getDeclaredFields());

        String getterName;
        Object fieldValue;
        String path;

        for(Field field : declaredFields) {
            if(field.getType().equals(boolean.class)) {
                if(field.getName().startsWith("is"))
                    getterName = field.getName();
                else
                    getterName = "is" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
            }
            else
                getterName = "get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);

            fieldValue = searchObject.getClass().getDeclaredMethod(getterName).invoke(searchObject);
            path = pathToField + field.getName();

            if(field.getType().isPrimitive()) {
                if(fieldValue instanceof Boolean)
                    if(fieldValue.equals(false))
                        ignoredPaths.add(path);

                if( fieldValue instanceof Byte ||
                    fieldValue instanceof Short ||
                    fieldValue instanceof Integer)
                    if(fieldValue.equals(0))
                        ignoredPaths.add(path);

                if(fieldValue instanceof Long)
                    if(fieldValue.equals(0L))
                        ignoredPaths.add(path);

                if(fieldValue instanceof Float)
                    if(fieldValue.equals(0.0f))
                        ignoredPaths.add(path);

                if(fieldValue instanceof Double)
                    if(fieldValue.equals(0.0d))
                        ignoredPaths.add(path);

                if(fieldValue instanceof Character)
                    if(fieldValue.equals('\u0000'))
                        ignoredPaths.add(path);

                continue;
            }

            if(field.isAnnotationPresent(Embedded.class) && fieldValue != null) {
                ignoredPaths.addAll(setAllIgnoredPaths(fieldValue, field.getName()+"."));
                continue;
            }

            if(fieldValue == null)
                ignoredPaths.add(path);
        }

        Class<?> superclass = searchObject.getClass().getSuperclass();
        while (superclass != null && !superclass.equals(Object.class) && !superclass.equals(BaseEntity.class))
            superclass = superclass.getSuperclass();

        if(superclass != null && superclass.equals(BaseEntity.class)) {
            fieldValue = superclass.getDeclaredMethod("getId").invoke(searchObject);

            if(fieldValue != null && fieldValue.equals(0L))
                ignoredPaths.add("id");
        }

        return ignoredPaths;
    }
}