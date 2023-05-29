package com.example.demo.tools.autorun;

import com.example.demo.dto.WheelDTO;
import com.example.demo.entity.Wheel;
import com.example.demo.repository.IWheelRepository;
import com.example.demo.tools.conversion.ObjectConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationStartupRunner implements CommandLineRunner {

    @Autowired
    private ObjectConverter converter;

    @Autowired
    private IWheelRepository wheelRepository;

    @Override
    public void run(String... args) throws Exception {

        Wheel wheel = wheelRepository.findById(1L).orElse(null);
        WheelDTO wheelDTO = new WheelDTO();

        converter.convertSourceToTarget(wheel, wheelDTO);
    }
}
