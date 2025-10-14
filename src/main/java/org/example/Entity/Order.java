package org.example.Entity;

import org.example.Reposotory.Identifiable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order implements Identifiable<String> {
    //    Order med egenskaper: orderId (String), products (List<Product>), customerName
//(String
//    ), totalAmount (double). Inkludera getters och setters.
    private String id;
    private String customerId;
    private double total;
    private List<Product> products; // Uncomment if you want to include products list

    public Order(String customerId, double totalAmount, List<Product> products) {
        this.id = UUID.randomUUID().toString().split("-")[0];
        this.customerId = customerId;
        this.total = totalAmount;
        this.products = products;
    }

    public Order() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getTotal() {
        total = (products == null) ? 0.0
                : products.stream().mapToDouble(Product::getPrice).sum();
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(total, order.total) == 0 && Objects.equals(id, order.id) && Objects.equals(customerId, order.customerId) && Objects.equals(products, order.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, total, products);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", totalAmount=" + total +
                ", products=" + products +
                '}';
    }


}