package org.example.Entity;

import org.example.Reposotory.Identifiable;

import java.util.Objects;
import java.util.UUID;

public class Customer implements Identifiable<String> {
    private String id;
    private String name;
    private String city;


    public Customer(String name, String city) {
//        this.id = UUID.randomUUID().toString(); //1175814e-2240-4c99-a08e-92e2620e8e25
//        We want to  generate a unique id simply t.ex 1234 or 1,2,3,4 so we will edit UUID toString method
        this.id = UUID.randomUUID().toString().split("-")[0]; // Generates a shorter unique ID
        this.name = name;
        this.city = city;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                '}';

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(city, customer.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, city);
    }
}
