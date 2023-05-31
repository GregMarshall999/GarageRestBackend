package com.example.demo.service;

import com.example.demo.dto.BaseDTO;
import com.example.demo.entity.BaseEntity;
import com.example.demo.repository.IBaseRepository;
import com.example.demo.tools.conversion.Converter;
import com.example.demo.tools.conversion.ObjectConverter;
import com.example.demo.tools.paging.PageRequest;
import com.example.demo.tools.paging.PageRequestUtil;
import com.example.demo.tools.paging.PagedResponse;
import com.example.demo.tools.search.Searcher;
import com.example.demo.tools.sorting.SortRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Scope("prototype")
public class CRUDLService<ENTITY extends BaseEntity, DTO extends BaseDTO> implements ICRUDLService<ENTITY, DTO> {
    @Autowired
    private ObjectConverter objectConverter;

    @Autowired
    private Converter converter;

    private IBaseRepository<ENTITY> repository;
    private Class<ENTITY> entityClass;
    private Class<DTO> dtoClass;

    @Override
    public void initService(Class<ENTITY> entityClass, Class<DTO> dtoClass, IBaseRepository<ENTITY> repository) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
        this.repository = repository;
    }

    @Override
    public DTO createOrUpdate(DTO dto) {
        final ENTITY entity;

        if(dto.getId() == 0)
            entity = initObject(entityClass);
        else
            entity = repository.findById(dto.getId()).orElse(null);

        if(entity == null)
            return null;

        updateEntity(entity, dto);

        try {
            final ENTITY savedEntity = repository.save(entity);
            objectConverter.convertSourceToTarget(savedEntity, dto);
            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public DTO getById(long id) {
        try {
            final ENTITY entity = repository.findById(id).orElse(null);

            if(entity == null)
                return null;

            DTO dto = initObject(dtoClass);
            objectConverter.convertSourceToTarget(entity, dto);
            return dto;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PagedResponse<DTO> list(PageRequest pageRequest) {
        return buildPagedResponse(repository.findAll(PageRequestUtil.toPageRequest(pageRequest)));
    }

    @Override
    public boolean delete(long id) {
        final ENTITY entity = repository.findById(id).orElse(null);

        if(entity == null)
            return false;

        try {
            repository.delete(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public PagedResponse<DTO> search(DTO searchDTO, PageRequest pageRequest) {
        try {
            ENTITY entity = initObject(entityClass);
            objectConverter.convertSourceToTarget(searchDTO, entity);
            ExampleMatcher matcher = Searcher.buildExampleMatcher(searchDTO);
            Example<ENTITY> example = Example.of(entity, matcher);
            return buildPagedResponse(repository.findAll(example, PageRequestUtil.toPageRequest(pageRequest)));
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PagedResponse<DTO> sortSearch(DTO searchDTO, SortRequest sortRequest, PageRequest pageRequest) {
        try {
            ENTITY entity = initObject(entityClass);
            objectConverter.convertSourceToTarget(searchDTO, entity);

            Example<ENTITY> example = Example.of(entity, converter.buildExampleMatcher(searchDTO));

            if(sortRequest.getDirection().equals("asc") || sortRequest.getDirection().equals("desc"))
                return buildPagedResponse(repository.findAll(
                        example,
                        org.springframework.data.domain.PageRequest.of(
                                pageRequest.getPage(),
                                pageRequest.getSize(),
                                Sort.by(Sort.Direction.fromString(sortRequest.getDirection()), sortRequest.getActive()))));

            return search(searchDTO, pageRequest);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PagedResponse<DTO> sort(SortRequest sortRequest, PageRequest pageRequest) {
        return switch (sortRequest.getDirection()) {
            case "asc" -> buildPagedResponse(repository.findAll(org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage(), pageRequest.getSize(), Sort.by(Sort.Direction.ASC, sortRequest.getActive()))));
            case "desc" -> buildPagedResponse(repository.findAll(org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage(), pageRequest.getSize(), Sort.by(Sort.Direction.DESC, sortRequest.getActive()))));
            default -> list(pageRequest);
        };
    }

    @Override
    public DTO getStructure() {
        return initObject(dtoClass);
    }

    protected void updateEntity(ENTITY entity, DTO dto) {
        try {
            objectConverter.convertSourceToTarget(dto, entity);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <O> O initObject(Class<O> objectClass) {
        try {
            return objectClass.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private PagedResponse<DTO> buildPagedResponse(Page<ENTITY> response) {
        try {
            if(response.isEmpty())
                return new PagedResponse<>(Collections.emptyList(), 0, response.getTotalElements());

            final List<DTO> dtos = new ArrayList<>();
            objectConverter.convertSourceListToTargetList(response.getContent(), dtos, () -> initObject(dtoClass));
            return new PagedResponse<>(dtos, dtos.size(), response.getTotalElements());
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
