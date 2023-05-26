package com.example.demo.service.functionnal;

import com.example.demo.dto.BaseDTO;
import com.example.demo.entity.BaseEntity;
import com.example.demo.repository.IBaseRepository;

public interface IServiceInitializer<ENTITY extends BaseEntity, DTO extends BaseDTO> {
    void initService(Class<ENTITY> entityClass, Class<DTO> dtoClass, IBaseRepository<ENTITY> repository);
}
