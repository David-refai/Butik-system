package org.example.Entity;

import org.example.Reposotory.Identifiable;

import java.util.Objects;
import java.util.UUID;

public class Product implements Identifiable<String> {
    private String id;
    private String name;
    private Enum<Category> category;
    private Double price;

    public Product(String name, Enum<Category> category, Double price) {
        this.id = UUID.randomUUID().toString().split("-")[0];
        this.name = name;
        this.category = category;
        this.price = price;
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

    public Enum getCategory() {
        return category;
    }

    public void setCategory(Enum<Category> category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name) && Objects.equals(category, product.category) && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price);
    }
}