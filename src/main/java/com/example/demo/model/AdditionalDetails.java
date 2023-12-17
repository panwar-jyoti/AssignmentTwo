package com.example.demo.model;

public class AdditionalDetails {
    String merchant;
    String location;

    public AdditionalDetails(String merchant, String location) {
        this.merchant = merchant;
        this.location = location;
    }

    @Override
    public String toString() {
        return "AdditionalDetails{" +
                "merchant='" + merchant + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}