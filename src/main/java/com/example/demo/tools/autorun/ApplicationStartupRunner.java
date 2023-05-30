package com.example.demo.tools.autorun;

import com.example.demo.dto.OwnerDTO;
import com.example.demo.entity.Owner;
import com.example.demo.repository.IOwnerRepository;
import com.example.demo.tools.conversion.ObjectConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationStartupRunner implements CommandLineRunner {

    @Autowired
    private ObjectConverter converter;

    @Autowired
    private IOwnerRepository ownerRepository;

    @Override
    public void run(String... args) throws Exception {

        Owner owner = ownerRepository.findById(1L).orElse(null);
        OwnerDTO ownerDTO = new OwnerDTO();

        converter.convertSourceToTarget(owner, ownerDTO);

        Owner ownerConvert = new Owner();
        converter.convertSourceToTarget(ownerDTO, ownerConvert);

        System.out.println();
    }
}
