package com.example.demo.tools.conversion;

import jakarta.persistence.Embedded;
import org.javatuples.Triplet;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class Converter {
    /**
     * Converts the source object to the target object
     * All fields from source must match the fields in the target
     * @param source - object to convert
     * @param target - object to convert to
     * @param <S> - source type
     * @param <T> - target type
     */
    public <S, T> void convertSourceToTarget(S source, T target) { //TODO skip object mapping if object has entity annotation (id only is required set in superclass handling and needs to be fetched from the database)
        List<Field> sourceFields = List.of(source.getClass().getDeclaredFields());
        List<Field> targetFields = List.of(target.getClass().getDeclaredFields());
        Object sourceObject, targetObject, targetInnerObject, embedded;
        Method targetSetter, sourceGetter;
        Class<?> actualTypeArgument; //used for lists

        try {
            for(Field sourceField : sourceFields) {
                if(fieldExistsInTarget(sourceField, targetFields)) {
                    //primitive handling
                    if(isPrimitive(sourceField)) {
                        //if field is boolean
                        if(sourceField.getType().getSimpleName().equals("boolean")) {
                            sourceGetter = source.getClass()
                                    .getDeclaredMethod(setupBooleanMethodName("is", sourceField));
                            targetSetter = target.getClass()
                                    .getDeclaredMethod( setupBooleanMethodName("set", sourceField),
                                            sourceField.getType());
                        }
                        else {
                            sourceGetter = source.getClass().getDeclaredMethod(setupMethodName("get", sourceField));
                            targetSetter = target.getClass().getDeclaredMethod(setupMethodName("set", sourceField),
                                    sourceField.getType());
                        }
                        targetSetter.invoke(target, sourceGetter.invoke(source));
                        continue;
                    }

                    //list handling
                    if(sourceField.getType().getSimpleName().equals("List")) {
                        Field targetField = findFieldInTarget(sourceField, targetFields);
                        if(targetField != null) {
                            List<Object> targetInnerList = new ArrayList<>();
                            actualTypeArgument = (Class<?>) ((ParameterizedType)targetField.getGenericType())
                                    .getActualTypeArguments()[0];

                            //TODO return type check if instance List<Object>
                            List<Object> sourceInnerList = (List<Object>) source.getClass()
                                    .getDeclaredMethod(setupMethodName("get", sourceField)).invoke(source);

                            if(sourceInnerList != null) {
                                for(Object o : sourceInnerList) {
                                    targetInnerObject = actualTypeArgument.getDeclaredConstructor().newInstance();
                                    convertSourceToTarget(o, targetInnerObject);
                                    targetInnerList.add(targetInnerObject);
                                }

                                target.getClass().getDeclaredMethod(setupMethodName("set", targetField),
                                                targetField.getType())
                                        .invoke(target, targetInnerList);
                            }
                        }
                        continue;
                    }

                    //object handling
                    Field targetField = findFieldInTarget(sourceField, targetFields);
                    if(targetField != null) {
                        sourceObject = source.getClass().getDeclaredMethod(setupMethodName("get", sourceField))
                                .invoke(source);
                        if(sourceObject == null)
                            continue;
                        targetObject = targetField.getType().getDeclaredConstructor().newInstance();
                        convertSourceToTarget(sourceObject, targetObject);
                        target.getClass().getDeclaredMethod(setupMethodName("set", targetField), targetField.getType())
                                .invoke(target, targetObject);
                        continue;
                    }
                }

                //embedded handling
                if(sourceField.isAnnotationPresent(Embedded.class)) {
                    embedded = source.getClass().getDeclaredMethod(setupMethodName("get", sourceField))
                            .invoke(source);
                    if(embedded != null)
                        convertSourceToTarget(embedded, target);
                }
            }

            //target embedded handling
            Map<Object, Field> targetEmbeddedMap = MapAllTargetEmbedded(targetFields);
            for(Object key : targetEmbeddedMap.keySet()) {
                convertSourceToTarget(source, key);
                target.getClass().getDeclaredMethod(setupMethodName("set", targetEmbeddedMap.get(key)),
                                targetEmbeddedMap.get(key).getType())
                        .invoke(target, key);
            }

            //superclass handling
            Class<?> sourceSuperclass = source.getClass().getSuperclass();
            Class<?> targetSuperclass = target.getClass().getSuperclass();
            while(sourceSuperclass != null && targetSuperclass != null &&       //TODO check same type
                    !sourceSuperclass.getName().equals("java.lang.Object") &&
                    !targetSuperclass.getName().equals("java.lang.Object")) {

                sourceFields = List.of(sourceSuperclass.getDeclaredFields());
                targetFields = List.of(targetSuperclass.getDeclaredFields());

                for(Field sourceField : sourceFields) {
                    if(fieldExistsInTarget(sourceField, targetFields)) {
                        Field targetSuperClassField = findFieldInTarget(sourceField, targetFields);
                        if(targetSuperClassField != null) {
                            sourceObject = sourceSuperclass.getDeclaredMethod(setupMethodName("get", sourceField))
                                    .invoke(source);

                            targetSuperclass.getDeclaredMethod( setupMethodName("set", targetSuperClassField),
                                            targetSuperClassField.getType())
                                    .invoke(target, sourceObject);
                        }
                    }
                }

                sourceSuperclass = sourceSuperclass.getSuperclass();
                targetSuperclass = targetSuperclass.getSuperclass();
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the source list to the target list using a target builder
     * The builder is used to create a new instance to modify with the converter then add to the list
     * @param sources - source list
     * @param targets - target list
     * @param builder - target building container
     * @param <S> - source type
     * @param <T> - target type
     */
    public <S, T> void convertSourceListToTargetList(List<S> sources, List<T> targets, TargetBuilder<T> builder) {
        if(targets.size() == 0) {
            T target;
            for (S source : sources) {
                target = builder.resetTarget();
                convertSourceToTarget(source, target);
                targets.add(target);
            }
        }
    }

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
     * Builds a Map of embedded Object field linked with their fields from the targetFields
     * @param targetFields - fields to analyse
     * @return - Map of embedded object field with their fields
     */
    private Map<Object, Field> MapAllTargetEmbedded(List<Field> targetFields)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Map<Object, Field> targetEmbeddedMap = new HashMap<>();

        for(Field field : targetFields)
            if(field.isAnnotationPresent(Embedded.class))
                targetEmbeddedMap.put(field.getType().getDeclaredConstructor().newInstance(), field);

        return targetEmbeddedMap;
    }

    /**
     * Checks if the field has primitive type
     * Basic objects like string can be added as long as the DTO/ENTITIES share them
     * @param field - field to filter
     * @return - true if primitive, false if more complex object
     */
    private boolean isPrimitive(Field field) {
        if(field.getType().isPrimitive() || field.getType().getName().equals("fr.dawan.erp.entities.jwt.Role")) { //todo check if this is simplified
            return true;
        }

        return switch (field.getType().getSimpleName()) {
            case    "BigDecimal",
                    "LocalDate",
                    "String" -> true;
            default -> false;
        };
    }

    /**
     * Checks if the given field exists among the target fields
     * Compares the field name, so they both need to be identical
     * @param sourceField - field to find
     * @param targetFields - provided target fields
     * @return - true if found
     */
    private boolean fieldExistsInTarget(Field sourceField, List<Field> targetFields) {
        for(Field field : targetFields)
            if(field.getName().equals(sourceField.getName()))
                return true;

        return false;
    }

    /**
     * Complement to fieldExistsInTarget
     * Here we return the found field from target fields
     * @param sourceField - field to find
     * @param targetFields - provided fields
     * @return - the found field
     */
    private Field findFieldInTarget(Field sourceField, List<Field> targetFields) {
        for(Field field : targetFields)
            if(field.getName().equals(sourceField.getName()))
                return field;

        return null;
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

