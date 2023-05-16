package com.example.demo.controller;

import com.example.demo.dto.WheelDTO;
import com.example.demo.entity.Wheel;
import com.example.demo.repository.IWheelRepository;
import com.example.demo.service.ICRUDLService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wheels")
public class WheelController extends AbstractCRUDLController<Wheel, WheelDTO> {
    public WheelController(ICRUDLService<Wheel, WheelDTO> service, IWheelRepository repository) {
        super(service);
        service.initService(Wheel.class, WheelDTO.class, repository);
    }
}
