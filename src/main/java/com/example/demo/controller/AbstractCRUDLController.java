package com.example.demo.controller;

import com.example.demo.dto.BaseDTO;
import com.example.demo.entity.BaseEntity;
import com.example.demo.service.ICRUDLService;
import com.example.demo.tools.paging.PageRequest;
import com.example.demo.tools.paging.PagedResponse;
import com.example.demo.tools.searching.SearchRequest;
import com.example.demo.tools.sorting.SortRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class AbstractCRUDLController<ENTITY extends BaseEntity, DTO extends BaseDTO> {
    protected final ICRUDLService<ENTITY, DTO> service;

    @Autowired
    public AbstractCRUDLController(ICRUDLService<ENTITY, DTO> service) {
        this.service = service;
    }

    @PostMapping
    public DTO save(@RequestBody DTO dto) {
        return service.createOrUpdate(dto);
    }

    @GetMapping("/{id}")
    public DTO getById(@PathVariable long id) {
        return service.getById(id);
    }

    @GetMapping("/structure")
    public DTO getStructure() {
        return service.getStructure();
    }

    @GetMapping("/list")
    public PagedResponse<DTO> list(PageRequest request) {
        return service.list(request);
    }

    @PostMapping("/search")
    public PagedResponse<DTO> search(@RequestBody DTO searchDTO, PageRequest request) {
        return service.search(searchDTO, request);
    }

    @GetMapping("/sort")
    public PagedResponse<DTO> sort(SortRequest request, PageRequest searchRequest) {
        return service.sort(request, searchRequest);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable long id) {
        return service.delete(id);
    }
}
