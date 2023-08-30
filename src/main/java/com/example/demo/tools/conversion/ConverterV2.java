package com.example.demo.tools.conversion;

import com.example.demo.dto.BaseDTO;
import com.example.demo.repository.*;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@Component
public class ConverterV2 {

    @Autowired
    private ICarRepository carRepository;

    @Autowired
    private IGarageRepository garageRepository;

    @Autowired
    private IOwnerRepository ownerRepository;

    @Autowired
    private IWheelRepository wheelRepository;

    private Map<String, IBaseRepository> repositories;

    public void initRepos() {
        repositories = Map.of(
                "Car", carRepository,
                "Garage", garageRepository,
                "Owner", ownerRepository,
                "Wheel", wheelRepository);
    }

    public static Field findField(Field f, Field[] fields) {
        for (Field field : fields)
            if(f.getName().equals(field.getName()) && f.getType().equals(field.getType())
                    || f.getName().startsWith(field.getName().substring(0, field.getName().length()-1))
                    || field.getName().startsWith(f.getName().substring(0, f.getName().length()-1)))
                return field;
        return null;
    }

    public <S, T> void convertSourceToTargetV2(S source, T target) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] targetFields = target.getClass().getDeclaredFields();

        //DTO to ENTITY conversion only
        List<Field> embeddedFields = Arrays.stream(targetFields)
                .filter(field -> field.isAnnotationPresent(Embedded.class)).toList();

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
                    Field idField = Arrays.stream(sourceObject.getClass().getSuperclass().getDeclaredFields())
                            .filter(field -> field.getName().equals("id"))
                            .findAny().orElse(null);

                    assert idField != null;
                    idField.setAccessible(true);
                    targetField.setAccessible(true);
                    targetField.set(target, idField.get(sourceObject));
                    idField.setAccessible(false);
                    targetField.setAccessible(false);

                    continue;
                }
                if(targetField.getType().isAnnotationPresent(Entity.class)) {
                    sourceField.setAccessible(true);
                    Object id = sourceField.get(source);
                    sourceField.setAccessible(false);

                    String entityType = targetField.getType().getSimpleName();
                    Object entity = repositories.get(entityType).findById(id).orElse(null);

                    if(entity != null) {
                        targetField.setAccessible(true);
                        targetField.set(target, entity);
                        targetField.setAccessible(false);
                    }

                    continue;
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
                            targetList.add(idField.get(e));
                            idField.setAccessible(false);
                        }

                        targetField.setAccessible(true);
                        targetField.set(target, targetList);
                        targetField.setAccessible(false);

                        continue;
                    }

                    //id list
                    if(listType.equals(Long.class) && sourceField.getName().endsWith("Ids")) {
                        Class<?> entityType = (Class<?>) ((ParameterizedType) targetField.getGenericType()).getActualTypeArguments()[0];

                        for (Object id : sourceList) {
                            Object entity = repositories.get(entityType.getSimpleName()).findById(id).orElse(null);
                            if(entity != null)
                                targetList.add(entity);
                        }

                        targetField.setAccessible(true);
                        targetField.set(target, targetList);
                        targetField.setAccessible(false);

                        continue;
                    }
                }

                //handle regular types
                targetField.setAccessible(true);
                targetField.set(target, sourceObject);
                targetField.setAccessible(false);
            }
        }

        //handle embedded
        for (Field embeddedField : embeddedFields) {
            Object embedded = embeddedField.getType().getDeclaredConstructor().newInstance();
            convertSourceToTargetV2(source, embedded);

            embeddedField.setAccessible(true);
            embeddedField.set(target, embedded);
            embeddedField.setAccessible(false);
        }

        //handle superclass
        if(source.getClass().isAnnotationPresent(Entity.class) && target.getClass().getSuperclass().equals(BaseDTO.class)
                || source.getClass().getSuperclass().equals(BaseDTO.class) && target.getClass().isAnnotationPresent(Entity.class)) {
            Field sourceId = Arrays.stream(source.getClass().getSuperclass().getDeclaredFields())
                    .filter(field -> field.getName().equals("id"))
                    .findAny().orElse(null);

            Field targetId = Arrays.stream(target.getClass().getSuperclass().getDeclaredFields())
                    .filter(field -> field.getName().equals("id"))
                    .findAny().orElse(null);

            assert sourceId != null;
            assert targetId != null;
            sourceId.setAccessible(true);
            targetId.setAccessible(true);
            targetId.set(target, sourceId.get(source));
            sourceId.setAccessible(false);
            targetId.setAccessible(false);
        }
    }
}
