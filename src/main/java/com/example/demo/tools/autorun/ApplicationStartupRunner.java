package com.example.demo.tools.autorun;

import com.example.demo.dto.CarDTO;
import com.example.demo.dto.WheelDTO;
import com.example.demo.entity.Car;
import com.example.demo.entity.Wheel;
import com.example.demo.repository.ICarRepository;
import com.example.demo.repository.IWheelRepository;
import com.example.demo.service.CRUDLService;
import com.example.demo.tools.paging.PageRequest;
import com.example.demo.tools.paging.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationStartupRunner implements CommandLineRunner {

    @Autowired
    private CRUDLService<Car, CarDTO> carService;

    @Autowired
    private CRUDLService<Wheel, WheelDTO> wheelService;

    @Autowired
    private IWheelRepository wheelRepository;

    @Autowired
    private ICarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {

        wheelService.initService(Wheel.class, WheelDTO.class, wheelRepository);
        carService.initService(Car.class, CarDTO.class, carRepository);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(10);

        WheelDTO idDto = new WheelDTO();
        idDto.setId(1);

        WheelDTO priceDTO = new WheelDTO();
        priceDTO.setPrice(400);

        WheelDTO brandDTO = new WheelDTO();
        brandDTO.setBrand("F1");

        WheelDTO complexWheel = new WheelDTO();
        complexWheel.setBrand("F1");
        complexWheel.setPrice(4000);

        PagedResponse<WheelDTO> search = wheelService.search(idDto, pageRequest);
        PagedResponse<WheelDTO> search1 = wheelService.search(priceDTO, pageRequest);
        PagedResponse<WheelDTO> search2 = wheelService.search(brandDTO, pageRequest);
        PagedResponse<WheelDTO> search3 = wheelService.search(complexWheel, pageRequest);

        System.out.println();

        CarDTO carIdDTO = new CarDTO();
        carIdDTO.setId(1);

        CarDTO carNameDTO = new CarDTO();
        carNameDTO.setCarName("Clio");

        CarDTO carGarageDTO = new CarDTO();
        carGarageDTO.setGarageId(1);

        CarDTO complexCar = new CarDTO();
        complexCar.setGarageId(1);
        complexCar.setCarName("Clio");

        PagedResponse<CarDTO> carSearch = carService.search(carIdDTO, pageRequest);
        PagedResponse<CarDTO> carSearch1 = carService.search(carNameDTO, pageRequest);
        PagedResponse<CarDTO> carSearch2 = carService.search(carGarageDTO, pageRequest);
        PagedResponse<CarDTO> carSearch3 = carService.search(complexCar, pageRequest);

        System.out.println();
    }
}
