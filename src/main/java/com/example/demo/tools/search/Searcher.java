package com.example.demo.tools.search;

import org.springframework.data.domain.ExampleMatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class Searcher {
    public static <DTO> ExampleMatcher buildExampleMatcher(DTO searchDTO) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName;
        ExampleMatcher matcher;
        boolean fieldSet = false;
        boolean isSingleField = true;

        List<Field> declaredFields = List.of(searchDTO.getClass().getDeclaredFields());

        for(Field field : declaredFields) {
            if(!isSingleField)
                break;

            if(field.getType().equals(boolean.class)) {
                if(field.getName().startsWith("is"))
                    methodName = field.getName();
                else
                    methodName = "is" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);

                if(fieldSet) {
                    isSingleField &= !((boolean) searchDTO.getClass().getDeclaredMethod(methodName).invoke(searchDTO));
                }
                else
                    fieldSet |= ((boolean) searchDTO.getClass().getDeclaredMethod(methodName).invoke(searchDTO));

                continue;
            }

            methodName = "get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);

            if(field.getType().equals(long.class)) {
                if(fieldSet) {
                    isSingleField &= (long) searchDTO.getClass().getDeclaredMethod(methodName).invoke(searchDTO) != 0L;
                }
                else
                    fieldSet |= (long)searchDTO.getClass().getDeclaredMethod(methodName).invoke(searchDTO) != 0L;

                continue;
            }

            if(fieldSet) {
                isSingleField &= searchDTO.getClass().getDeclaredMethod(methodName).invoke(searchDTO) != null;
            }
            else
                fieldSet |= searchDTO.getClass().getDeclaredMethod(methodName).invoke(searchDTO) != null;
        }

        if(isSingleField) {

        }

        matcher = ExampleMatcher.matchingAny().withIgnoreNullValues();
        return matcher;
    }
}
