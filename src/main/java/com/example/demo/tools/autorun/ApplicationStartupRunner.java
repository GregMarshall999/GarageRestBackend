package com.example.demo.tools.autorun;

import com.example.demo.dto.CarDTO;
import com.example.demo.entity.Car;
import com.example.demo.repository.ICarRepository;
import com.example.demo.tools.conversion.ObjectConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ApplicationStartupRunner implements CommandLineRunner {

    @Autowired
    private ObjectConverter converter;

    @Autowired
    private ICarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {

        converter.initConverter();

        Car car = carRepository.findById(1L).orElse(null);
        CarDTO carDTO = new CarDTO();

        converter.convertSourceToTarget(car, carDTO);

        Car carConvert = new Car();
        converter.convertSourceToTarget(carDTO, carConvert);

        assert Objects.equals(car, carConvert);
        System.out.println();
    }
}
