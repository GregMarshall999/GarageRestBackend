package com.example.demo.tools.conversion;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConverterV2 {

    public static Field findField(Field f, Field[] fields) {
        for (Field field : fields)
            if(f.getName().equals(field.getName()) && f.getType().equals(field.getType())
                    || f.getName().startsWith(field.getName().substring(0, field.getName().length()-1))
                    || field.getName().startsWith(f.getName().substring(0, f.getName().length()-1)))
                return field;
        return null;
    }

    public <S, T> void convertSourceToTargetV2(S source, T target) throws IllegalAccessException {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] targetFields = target.getClass().getDeclaredFields();

        for (Field sourceField : sourceFields) {
            sourceField.setAccessible(true);
            Object sourceObject = sourceField.get(source);
            sourceField.setAccessible(false);

            //handle embedded fields
            if(sourceField.isAnnotationPresent(Embedded.class)) {
                convertSourceToTargetV2(sourceObject, target);
                continue;
            }

            Field targetField = findField(sourceField, targetFields);
            if(targetField != null) {
                //handle entity
                if(sourceField.getType().isAnnotationPresent(Entity.class)) {



                    /*Field[] sourceObjectFields = sourceObject.getClass().getDeclaredFields();
                    for (Field sourceObjectField : sourceObjectFields) {
                        if(sourceObjectField.getName().equals("id")) {
                            sourceObjectField.setAccessible(true);
                            Object id = sourceObjectField.get(sourceObject);
                            sourceObjectField.setAccessible(false);

                            targetField.setAccessible(true);
                            targetField.set(target, id);
                            targetField.setAccessible(false);
                            break;
                        }
                    }*/
                    continue;
                }
                if(targetField.getType().isAnnotationPresent(Entity.class)) {
                    //TODO handle
                }

                //handle lists
                if(sourceField.getType().equals(List.class)) {
                    Class<?> listType = (Class<?>) ((ParameterizedType) sourceField.getGenericType()).getActualTypeArguments()[0];
                    List<Object> sourceList = (List<Object>) sourceObject;
                    List<Object> targetList = new ArrayList<>();

                    //entity list
                    if(listType.isAnnotationPresent(Entity.class)) {
                        Field idField = Arrays.stream(listType.getSuperclass().getDeclaredFields())
                                .filter(field -> field.getName().equals("id"))
                                .findAny().orElse(null);

                        for(Object e : sourceList) {
                            assert idField != null;
                            idField.setAccessible(true);
                            Object sourceObjectId = idField.get(e);
                            idField.setAccessible(false);
                            targetList.add(sourceObjectId);
                        }

                        targetField.setAccessible(true);
                        targetField.set(target, targetList);
                        targetField.setAccessible(false);

                        continue;
                    }

                    //id list
//                    if(false) {
//                        continue;
//                    }
                }

                //handle regular types
                targetField.setAccessible(true);
                targetField.set(target, sourceObject);
                targetField.setAccessible(false);
            }
        }

        //handle superclass
    }
}
