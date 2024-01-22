package com.example.garage.entity.embeddable.car;

import com.example.garage.entity.car.Engine;
import com.example.garage.entity.car.GearBox;
import com.example.garage.entity.component.Weight;
import com.example.garage.entity.enums.car.WheelDrive;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.List;

@Embeddable
public class TechnicalSpecification {
    private Weight weight;

    @OneToOne
    private Engine engine;

    private String power;
    private String torque;

    @OneToMany
    private List<Engine> engineSwap;

    @OneToOne
    private GearBox gearBox;

    private WheelDrive wheelDrive;

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getTorque() {
        return torque;
    }

    public void setTorque(String torque) {
        this.torque = torque;
    }

    public List<Engine> getEngineSwap() {
        return engineSwap;
    }

    public void setEngineSwap(List<Engine> engineSwap) {
        this.engineSwap = engineSwap;
    }

    public GearBox getGearBox() {
        return gearBox;
    }

    public void setGearBox(GearBox gearBox) {
        this.gearBox = gearBox;
    }

    public WheelDrive getWheelDrive() {
        return wheelDrive;
    }

    public void setWheelDrive(WheelDrive wheelDrive) {
        this.wheelDrive = wheelDrive;
    }
}
