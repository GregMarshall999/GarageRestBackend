package com.example.demo.controller;

import com.example.demo.dto.OwnerDTO;
import com.example.demo.entity.Owner;
import com.example.demo.repository.IOwnerRepository;
import com.example.demo.service.ICRUDLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner")
public class OwnerController extends AbstractCRUDLController<Owner, OwnerDTO> {
    @Autowired
    public OwnerController(ICRUDLService<Owner, OwnerDTO> service, IOwnerRepository repository) {
        super(service, Owner.class, OwnerDTO.class, repository);
    }
}
