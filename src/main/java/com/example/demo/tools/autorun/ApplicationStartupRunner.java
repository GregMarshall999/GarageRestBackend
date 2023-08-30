package com.example.demo.tools.autorun;

import com.example.demo.dto.GarageDTO;
import com.example.demo.entity.Car;
import com.example.demo.entity.Garage;
import com.example.demo.entity.Owner;
import com.example.demo.entity.Wheel;
import com.example.demo.entity.embeddables.CarFields;
import com.example.demo.entity.embeddables.CarInfo;
import com.example.demo.entity.embeddables.GarageAccounting;
import com.example.demo.entity.embeddables.GarageFields;
import com.example.demo.tools.conversion.ConverterV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class ApplicationStartupRunner implements CommandLineRunner {
    @Autowired
    ConverterV2 converterV2;

    @Override
    public void run(String... args) throws Exception {
        converterV2.initRepos();

        Wheel wheel = new Wheel();
        wheel.setId(1);
        wheel.setBrand("Brand");
        wheel.setPrice(50);

        CarFields carFields = new CarFields();
        carFields.setCarName("Tester");
        carFields.setNew(true);

        CarInfo carInfo = new CarInfo();
        carInfo.setCost(100);
        carInfo.setMotInfo("Test");
        carInfo.setRange(500);

        Car car1 = new Car();
        car1.setId(1);
        car1.setCarFields(carFields);
        car1.setCarInfo(carInfo);
        car1.setWheel(wheel);

        Car car2 = new Car();
        car2.setId(2);
        car2.setCarFields(carFields);
        car2.setCarInfo(carInfo);
        car2.setWheel(wheel);

        Owner owner = new Owner();
        owner.setId(1);
        owner.setName("Branni");
        owner.setCars(List.of(car1, car2));
        owner.setAppointments(List.of(LocalDateTime.now()));

        GarageAccounting garageAccounting = new GarageAccounting();
        garageAccounting.setCost(100);
        garageAccounting.setRef("26451K");
        garageAccounting.setIncome(500);

        GarageFields garageFields = new GarageFields();
        garageFields.setGarageAccounting(garageAccounting);

        Garage garage = new Garage();
        garage.setId(1);
        garage.setGarageFields(garageFields);
        garage.setOwner(owner);
        garage.setCars(List.of(car1, car2));

        car1.setGarage(garage);
        car2.setGarage(garage);

        GarageDTO garageDTO = new GarageDTO();

        converterV2.convertSourceToTargetV2(garage, garageDTO);

        Garage g = new Garage();
        converterV2.convertSourceToTargetV2(garageDTO, g);

        System.out.println(garageDTO);
    }
}
