package com.example.demo.tools.conversion;

import com.example.demo.entity.BaseEntity;
import com.example.demo.repository.IBaseRepository;
import com.example.demo.repository.ICarRepository;
import com.example.demo.repository.IGarageRepository;
import com.example.demo.repository.IOwnerRepository;
import com.example.demo.repository.IWheelRepository;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.tools.conversion.ConversionUtility.getObjectFields;
import static com.example.demo.tools.conversion.ConversionUtility.getFieldValue;
import static com.example.demo.tools.conversion.ConversionUtility.isAcceptedCommonType;
import static com.example.demo.tools.conversion.ConversionUtility.setValueToField;

@Component
public class Converter {

    @Autowired
    private ICarRepository carRepository;

    @Autowired
    private IGarageRepository garageRepository;

    @Autowired
    private IOwnerRepository ownerRepository;

    @Autowired
    private IWheelRepository wheelRepository;

    private Map<String, IBaseRepository> repositories = new HashMap<>();

    /**
     * Converts the source object to the target object by porting identical fields
     * @param source - source object to convert
     * @param target - target object with source data
     * @param <S> - type of source
     * @param <T> - type of target
     * @throws InvocationTargetException -
     * @throws NoSuchMethodException -
     * @throws IllegalAccessException -
     */
    public <S, T> void convertSourceToTarget(S source, T target) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if(repositories.isEmpty())
            initRepositories();

        Class<?> sourceSuperclass;
        Class<?> listTypeArgument;

        Field currentSourceField;
        Field currentTargetField;

        List<Object> embeddables = new ArrayList<>();
        List<Object> sourceList;
        List<Object> targetList;

        //get source and target fields
        Map<String, Field> declaredSourceFields = getObjectFields(source);
        Map<String, Field> declaredTargetFields = getObjectFields(target);

        Object sourceEntity;
        Object sourceEntityId;

        String entityName = "";

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

            //list handling
            if(currentSourceField.getType().equals(List.class)) {
                listTypeArgument = (Class<?>) ((ParameterizedType) currentSourceField.getGenericType()).getActualTypeArguments()[0];

                //entity list
                if(listTypeArgument.isAnnotationPresent(Entity.class)) {
                    currentTargetField = declaredTargetFields.get(sourceFieldName.substring(0, sourceFieldName.length()-1) + "Ids");
                    sourceList = (List<Object>) getFieldValue(currentSourceField, source);
                    targetList = new ArrayList<>();

                    try {
                        for(Object o : sourceList) {
                            sourceSuperclass = o.getClass().getSuperclass();

                            while(sourceSuperclass != null && !sourceSuperclass.equals(Object.class) && !sourceSuperclass.equals(BaseEntity.class))
                                sourceSuperclass = sourceSuperclass.getSuperclass();

                            if(sourceSuperclass != null && sourceSuperclass.equals(BaseEntity.class)) {
                                sourceEntityId = sourceSuperclass.getDeclaredMethod("getId").invoke(o);
                                targetList.add(sourceEntityId);
                            }
                        }
                    } catch (LazyInitializationException ignored) {}

                    setValueToField(currentTargetField, target, currentTargetField.getType(), targetList);
                    continue;
                }

                //foreign key list
                if(listTypeArgument.equals(Long.class) && currentSourceField.getName().endsWith("Ids")) {
                    currentTargetField = declaredTargetFields.get(currentSourceField.getName().substring(0, currentSourceField.getName().length()-3) + "s");
                    sourceList = (List<Object>) getFieldValue(currentSourceField, source);
                    targetList = new ArrayList<>();

                    if(currentTargetField != null) {
                        entityName = ((ParameterizedType) currentTargetField.getGenericType()).getActualTypeArguments()[0].getTypeName();
                        entityName = entityName.split("\\.")[entityName.split("\\.").length - 1];
                        for (Object o : sourceList) {
                            sourceEntity = repositories.get(entityName).findById(o).orElse(null);
                            targetList.add(sourceEntity);
                            System.out.println();
                        }
                        setValueToField(currentTargetField, target, currentTargetField.getType(), targetList);
                    }
                    continue;
                }
            }

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

            //entity handling
            if(currentSourceField.getType().isAnnotationPresent(Entity.class)) {
                currentTargetField = declaredTargetFields.get(sourceFieldName + "Id");

                if(currentTargetField != null) {
                    sourceEntity = getFieldValue(currentSourceField, source);

                    if(sourceEntity != null) {
                        sourceSuperclass = sourceEntity.getClass().getSuperclass();

                        while(sourceSuperclass != null && !sourceSuperclass.equals(Object.class) && !sourceSuperclass.equals(BaseEntity.class))
                            sourceSuperclass = sourceSuperclass.getSuperclass();

                        if(sourceSuperclass != null && sourceSuperclass.equals(BaseEntity.class)) {
                            sourceEntityId = sourceSuperclass.getDeclaredMethod("getId").invoke(sourceEntity);
                            setValueToField(currentTargetField, target, currentTargetField.getType(), sourceEntityId);
                        }
                    }
                }
                continue;
            }

            //embedded handling
            if(currentSourceField.isAnnotationPresent(Embedded.class)) {
                convertSourceToTarget(getFieldValue(currentSourceField, source), target);
                continue;
            }

            //identical field handling
            if(currentTargetField != null && currentTargetField.getType().equals(currentSourceField.getType()) && isAcceptedCommonType(currentTargetField.getType()))
                setValueToField(currentTargetField, target, currentTargetField.getType(), getFieldValue(currentSourceField, source));
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

    /**
     * Converts the source list to the target list using a target builder
     * The builder is used to create a new instance to modify with the converter then add to the list
     * @param sources - source list
     * @param targets - target list
     * @param builder - target building container
     * @param <S> - source type
     * @param <T> - target type
     */
    public <S, T> void convertSourceListToTargetList(List<S> sources, List<T> targets, TargetBuilder<T> builder) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if(targets.size() == 0) {
            T target;
            for (S source : sources) {
                target = builder.resetTarget();
                convertSourceToTarget(source, target);
                targets.add(target);
            }
        }
    }

    private void initRepositories() {
        repositories.put("Car", carRepository);
        repositories.put("Garage", garageRepository);
        repositories.put("Owner", ownerRepository);
        repositories.put("Wheel", wheelRepository);
    }
}