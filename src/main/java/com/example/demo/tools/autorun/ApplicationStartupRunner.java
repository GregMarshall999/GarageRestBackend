package com.example.demo.tools.autorun;

import com.example.demo.entity.Car;
import com.example.demo.entity.embeddables.CarFields;
import com.example.demo.repository.ICarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.List;

@Configuration
public class ApplicationStartupRunner implements CommandLineRunner {
    @Autowired
    private ICarRepository repository;

    @Override
    public void run(String... args) throws Exception {
        Car exampleCar = new Car();
        CarFields carFields = new CarFields();
        exampleCar.setCarFields(carFields);
        exampleCar.getCarFields().setCarName("Clio");

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnorePaths("id");

        exampleMatcher = exampleMatcher.withIgnorePaths("carFields.isNew");
               // .withMatcher("carFields.carName", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase());

        Example<Car> example = Example.of(exampleCar, exampleMatcher);

        List<Car> all = repository.findAll(example);

        System.out.println(all);
    }
}
