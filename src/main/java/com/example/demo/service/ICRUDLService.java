package com.example.demo.service;

import com.example.demo.dto.BaseDTO;
import com.example.demo.entity.BaseEntity;
import com.example.demo.service.functionnal.ICreatingUpdatingService;
import com.example.demo.service.functionnal.IGettingService;
import com.example.demo.service.functionnal.IListingService;
import com.example.demo.service.functionnal.IRemovingService;
import com.example.demo.service.functionnal.IServiceInitializer;
import com.example.demo.service.functionnal.ISortSearchingService;
import com.example.demo.service.functionnal.IStructureService;

public interface ICRUDLService<ENTITY extends BaseEntity, DTO extends BaseDTO> extends IServiceInitializer<ENTITY, DTO>,
        ICreatingUpdatingService<DTO>, IGettingService<DTO>, IListingService<DTO>, IRemovingService,
        ISortSearchingService<DTO>, IStructureService<DTO> {}
