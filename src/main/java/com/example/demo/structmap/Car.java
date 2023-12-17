package com.example.demo.structmap;

public class Car {

    private String make;
    private int numberOfSeats;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public CarType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Car{" +
                "make='" + make + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", type=" + type +
                '}';
    }

    public void setType(CarType type) {
        this.type = type;
    }

    public Car(String make, int numberOfSeats, CarType type) {
        this.make = make;
        this.numberOfSeats = numberOfSeats;
        this.type = type;
    }

    private CarType type;

    //constructor, getters, setters etc.
}