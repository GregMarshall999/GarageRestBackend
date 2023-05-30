package com.example.demo.tools.conversion;

import com.example.demo.entity.BaseEntity;
import com.example.demo.repository.IBaseRepository;
import com.example.demo.repository.ICarRepository;
import com.example.demo.repository.IGarageRepository;
import com.example.demo.repository.IOwnerRepository;
import com.example.demo.repository.IWheelRepository;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.tools.conversion.ConversionUtility.getObjectFields;
import static com.example.demo.tools.conversion.ConversionUtility.getFieldValue;
import static com.example.demo.tools.conversion.ConversionUtility.setValueToField;

@Component
public class ObjectConverter implements ITypeAcceptor {

    @Autowired
    private ICarRepository carRepository;

    @Autowired
    private IGarageRepository garageRepository;

    @Autowired
    private IOwnerRepository ownerRepository;

    @Autowired
    private IWheelRepository wheelRepository;

    private Map<String, IBaseRepository> repositories = new HashMap<>();

    public <S, T> void convertSourceToTarget(S source, T target) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if(repositories.isEmpty())
            initRepositories();

        Class<?> sourceSuperclass;

        Field currentSourceField;
        Field currentTargetField;

        List<Object> embeddables = new ArrayList<>();

        //get source and target fields
        Map<String, Field> declaredSourceFields = getObjectFields(source);
        Map<String, Field> declaredTargetFields = getObjectFields(target);

        Object sourceEntity;
        Object sourceEntityId;

        String entityName;

        declaredTargetFields.forEach((s, f) -> {
            if(f.isAnnotationPresent(Embedded.class)) {
                try {
                    Object o = f.getType().getDeclaredConstructor().newInstance();
                    embeddables.add(o);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //go through all source fields and handle to target conversion
        for(String sourceFieldName : declaredSourceFields.keySet()) {
            currentSourceField = declaredSourceFields.get(sourceFieldName);
            currentTargetField = declaredTargetFields.get(sourceFieldName);

            //dto foreign key handling
            if(currentSourceField.getType().equals(long.class) && currentSourceField.getName().endsWith("Id")) {
                currentTargetField = declaredTargetFields.get(sourceFieldName.substring(0, sourceFieldName.length() - 2));
                if(currentTargetField != null) {
                    entityName = currentTargetField.getType().getSimpleName();
                    sourceEntityId = getFieldValue(currentSourceField, source);
                    sourceEntity = repositories.get(entityName).findById(sourceEntityId).orElse(null);
                    setValueToField(currentTargetField, target, currentTargetField.getType(), sourceEntity);
                }
                continue;
            }

            //identical field handling
            if(currentTargetField != null && currentTargetField.getType().equals(currentSourceField.getType()) && isAcceptedCommonType(currentTargetField.getType())) {
                setValueToField(currentTargetField, target, currentTargetField.getType(), getFieldValue(currentSourceField, source));
                continue;
            }

            //entity handling
            if(currentSourceField.getType().isAnnotationPresent(Entity.class)) {
                currentTargetField = declaredTargetFields.get(sourceFieldName + "Id");

                if(currentTargetField != null) {
                    sourceEntity = getFieldValue(currentSourceField, source);
                    sourceSuperclass = sourceEntity.getClass().getSuperclass();

                    while(sourceSuperclass != null && !sourceSuperclass.equals(Object.class) && !sourceSuperclass.equals(BaseEntity.class))
                        sourceSuperclass = sourceSuperclass.getSuperclass();

                    if(sourceSuperclass != null && sourceSuperclass.equals(BaseEntity.class)) {
                        sourceEntityId = sourceSuperclass.getDeclaredMethod("getId").invoke(sourceEntity);
                        setValueToField(currentTargetField, target, currentTargetField.getType(), sourceEntityId);
                    }
                }
            }

            //embedded handling
            if(currentSourceField.isAnnotationPresent(Embedded.class))
                convertSourceToTarget(getFieldValue(currentSourceField, source), target);
        }

        //target embedded handling
        for(Object o : embeddables) {
            convertSourceToTarget(source, o);
            target.getClass().getDeclaredMethod("set" + o.getClass().getSimpleName(), o.getClass()).invoke(target, o);
        }

        //superclass handling
        String getterName;
        String setterName;
        Map<String, Field> sourceSuperClassFields;
        Map<String, Field> targetSuperClassFields;
        sourceSuperclass = source.getClass().getSuperclass();
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
    }

    private void initRepositories() {
        repositories.put("Car", carRepository);
        repositories.put("Garage", garageRepository);
        repositories.put("Owner", ownerRepository);
        repositories.put("Wheel", wheelRepository);
    }

    @Override
    public boolean isAcceptedCommonType(Class<?> type) {
        if(type.isPrimitive())
            return true;

        return switch (type.getTypeName()) {
            case "java.lang.String", "java.time.LocalDateTime" -> true;
            default -> false;
        };
    }
}