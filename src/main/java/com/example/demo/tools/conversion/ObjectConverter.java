package com.example.demo.tools.conversion;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.tools.conversion.ConversionUtility.getObjectFields;
import static com.example.demo.tools.conversion.ConversionUtility.getFieldValue;
import static com.example.demo.tools.conversion.ConversionUtility.setValueToField;

@Component
public class ObjectConverter implements ITypeAcceptor {

    //needs to handle common fields in superclass
    //needs to handle common primitive fields
    //needs to handle common object fields
    //needs to handle entity to id's (converting to DTO)
    //try to handle dto entity id's to entity
    //needs to handle embedded fields
    //needs to recognise embedded fields pathing in dtos

    //first step is to collect all the fields and all their values from the source
    //these values are stored in a map

    public <S, T> void convertSourceToTarget(S source, T target) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Field currentSourceField;
        Field currentTargetField;

        //get source and target fields
        Map<String, Field> declaredSourceFields = getObjectFields(source);
        Map<String, Field> declaredTargetFields = getObjectFields(target);

        //go through all source fields and handle to target conversion
        for(String sourceFieldName : declaredSourceFields.keySet()) {
            //check if the source field exists in target and is same type
            currentSourceField = declaredSourceFields.get(sourceFieldName);
            currentTargetField = declaredTargetFields.get(sourceFieldName);
            if(currentTargetField != null && currentTargetField.getType().equals(currentSourceField.getType())) {
                //accepted common type handling
                if(isAcceptedCommonType(currentTargetField.getType()))
                    setValueToField(currentTargetField, target, currentTargetField.getType(), getFieldValue(currentSourceField, source));


            }
        }

        //superclass handling
        String getterName;
        String setterName;
        Map<String, Field> sourceSuperClassFields;
        Map<String, Field> targetSuperClassFields;
        Class<?> sourceSuperclass = source.getClass().getSuperclass();
        Class<?> targetSuperclass = target.getClass().getSuperclass();
        while(sourceSuperclass != null && targetSuperclass != null &&
                !sourceSuperclass.equals(Object.class) && !targetSuperclass.equals(Object.class)) {

            sourceSuperClassFields = Arrays.stream(sourceSuperclass.getDeclaredFields())
                    .collect(Collectors.toMap(Field::getName, field -> field));
            targetSuperClassFields = Arrays.stream(targetSuperclass.getDeclaredFields())
                    .collect(Collectors.toMap(Field::getName, field -> field));

            for(String superClassFieldName : sourceSuperClassFields.keySet()) {
                currentSourceField = sourceSuperClassFields.get(superClassFieldName);
                currentTargetField = targetSuperClassFields.get(superClassFieldName);
                if(currentTargetField != null && currentTargetField.getType().equals(currentSourceField.getType())) {
                    if(isAcceptedCommonType(currentTargetField.getType())) {
                        if(currentSourceField.getType().getSimpleName().equals("boolean")) {
                            if(currentSourceField.getName().startsWith("is"))
                                getterName = currentSourceField.getName();
                            else
                                getterName = "is" + currentSourceField.getName().toUpperCase().charAt(0) + currentSourceField.getName().substring(1);
                        }
                        else {
                            getterName = "get" + currentSourceField.getName().toUpperCase().charAt(0) + currentSourceField.getName().substring(1);
                        }
                        setterName = "set" + currentTargetField.getName().toUpperCase().charAt(0) + currentTargetField.getName().substring(1);

                        targetSuperclass.getDeclaredMethod(setterName, currentTargetField.getType())
                                .invoke(target, sourceSuperclass.getDeclaredMethod(getterName).invoke(source));
                    }
                }
            }

            sourceSuperclass = sourceSuperclass.getSuperclass();
            targetSuperclass = targetSuperclass.getSuperclass();
        }

        System.out.println();
    }

    @Override
    public boolean isAcceptedCommonType(Class<?> type) {
        if(type.isPrimitive())
            return true;

        return switch (type.getTypeName()) {
            case "java.lang.String" -> true;
            default -> false;
        };
    }
}