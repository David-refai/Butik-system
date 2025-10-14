package org.example.Utils;

import org.example.Entity.Category;
import org.example.Entity.Customer;
import org.example.Entity.Order;
import org.example.Entity.Product;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class that provides initial seed data for the application.
 * <p>
 * Contains static factory methods that generate lists of:
 * <ul>
 *     <li>Customers</li>
 *     <li>Products</li>
 *     <li>Orders</li>
 * </ul>
 * Used mainly for testing and demonstration purposes (no database connection required).
 */
public class Data {
//    this class is to create some initial data for the application to use

    /**
     * Creates a predefined list of {@link Customer} objects.
     *
     * @return a list of demo customers
     */
    public static List<Customer> getCustomers() {
        Customer customer1 = new Customer("Alice", "Stockholm");
        Customer customer2 = new Customer("Bob", "Gothenburg");
        Customer customer3 = new Customer("Charlie", "Malmö");
        Customer customer4 = new Customer("David", "Uppsala");
        Customer customer5 = new Customer("Eva", "Västerås");
        Customer customer6 = new Customer("John", "Lund");
        Customer customer7 = new Customer("Grace", "Linköping");
        Customer customer8 = new Customer("Harry", "Potter");
        Customer customer9 = new Customer("Ivy", "Gothenburg");
        Customer customer10 = new Customer("Jack", "Stockholm");

        return Arrays.asList(customer1, customer2, customer3, customer4, customer5, customer6, customer7,
                customer8, customer9, customer10);

    }

    /**
     * Creates a predefined list of {@link Product} objects with realistic categories and prices.
     *
     * @return a list of demo products
     */
    public static List<Product> getProducts() {
        return Arrays.asList(
                // --- Computers / Laptops ---
                new Product("Laptop 15\"", Category.COMPUTERS, 1199.00),
                new Product("Gaming Laptop", Category.COMPUTERS, 1599.00),
                new Product("Ultrabook 14\"", Category.COMPUTERS, 999.00),
                new Product("Desktop Tower", Category.COMPUTERS, 899.00),
                new Product("Chromebook 13\"", Category.COMPUTERS, 349.00),

                // --- Smartphones / Tablets ---
                new Product("Android Smartphone", Category.SMARTPHONES, 699.00),
                new Product("iOS Smartphone", Category.SMARTPHONES, 999.00),
                new Product("Budget Smartphone", Category.SMARTPHONES, 249.00),
                new Product("Tablet 10\"", Category.SMARTPHONES, 329.00),
                new Product("Pro Tablet 12.9\"", Category.SMARTPHONES, 1099.00),

                // --- Monitors / Displays ---
                new Product("27\" Monitor", Category.ELECTRONICS, 279.00),
                new Product("34\" Ultrawide Monitor", Category.ELECTRONICS, 599.00),

                // --- Accessories / Storage ---
                new Product("Mechanical Keyboard", Category.ACCESSORIES, 129.00),
                new Product("Wireless Mouse", Category.ACCESSORIES, 49.00),
                new Product("USB-C Hub 7-in-1", Category.ACCESSORIES, 59.00),
                new Product("External SSD 1TB", Category.STORAGE, 119.00),
                new Product("External HDD 4TB", Category.STORAGE, 99.00),
                new Product("NVMe SSD 2TB", Category.STORAGE, 189.00),

                // --- Audio ---
                new Product("Over-Ear Headphones", Category.AUTOMOTIVE, 149.00),
                new Product("Wireless Earbuds", Category.AUTOMOTIVE, 89.00),
                new Product("Bluetooth Speaker", Category.AUTOMOTIVE, 79.00),
                new Product("Soundbar", Category.AUTOMOTIVE, 199.00),

                // --- Photography ---
                new Product("Mirrorless Camera", Category.PHOTOGRAPHY, 899.00),
                new Product("Action Camera", Category.PHOTOGRAPHY, 299.00),
                new Product("Tripod", Category.PHOTOGRAPHY, 69.00),
                new Product("Ring Light", Category.PHOTOGRAPHY, 49.00),

                // --- Wearables ---
                new Product("Smartwatch", Category.WEARABLES, 249.00),
                new Product("Fitness Band", Category.WEARABLES, 79.00),

                // --- Office / Furniture / Stationery ---
                new Product("Printer", Category.OFFICE_SUPPLIES, 199.00),
                new Product("A4 Paper (500)", Category.OFFICE_SUPPLIES, 9.99),
                new Product("Desk Chair", Category.FURNITURE, 149.00),
                new Product("Standing Desk", Category.FURNITURE, 399.00),
                new Product("LED Desk Lamp", Category.LIGHTING, 29.00),
                new Product("Notebook Set", Category.STATIONERY, 12.00),
                new Product("Ballpoint Pens (10)", Category.STATIONERY, 6.50),

                // --- Networking ---
                new Product("Wi-Fi 6 Router", Category.NETWORK, 129.00),
                new Product("Ethernet Switch 8-port", Category.NETWORK, 49.00),
                new Product("Cat6 Cable 10m", Category.NETWORK, 12.00),

                // --- Kitchen / Home Appliances ---
                new Product("Air Fryer", Category.KITCHEN, 129.00),
                new Product("Espresso Machine", Category.KITCHEN, 299.00),
                new Product("TV 55\" 4K", Category.HOME_APPLIANCES, 499.00),
                new Product("Vacuum Cleaner", Category.HOME_APPLIANCES, 149.00)
        );
    }


    /**
     * Creates a list of {@link Order} objects using subsets of predefined products.
     *
     * @return a list of demo orders
     */

    public static List<Order> getOrders() {
        Order order1 = new Order("1", 1200.00, getProducts().subList(0, 1));
        Order order2 = new Order("2", 800.00, getProducts().subList(1, 2));
        Order order3 = new Order("3", 400.00, getProducts().subList(2, 3));
        Order order4 = new Order("4", 150.00, getProducts().subList(3, 4));
        Order order5 = new Order("5", 250.00, getProducts().subList(4, 5));
        Order order6 = new Order("6", 600.00, getProducts().subList(5, 6));
        Order order7 = new Order("7", 200.00, getProducts().subList(6, 7));
        Order order8 = new Order("8", 300.00, getProducts().subList(7, 8));
        Order order9 = new Order("9", 100.00, getProducts().subList(8, 9));
        Order order10 = new Order("10", 50.00, getProducts().subList(9, 10));
        return Arrays.asList(order1, order2, order3, order4, order5, order6, order7, order8, order9, order10);
    }

}
