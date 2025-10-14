package org.example;

import org.example.Entity.Category;
import org.example.Entity.Customer;
import org.example.Entity.Order;
import org.example.Entity.Product;
import org.example.Error.Safe;
import org.example.ImplRepo.InMemoryCurd;
import org.example.ImplRepo.InMemoryOrderImp;
import org.example.Reposotory.CrudRepo;
import org.example.Service.OrderService;
import org.example.Service.ServiceCrud;
import org.example.Utils.Data;
import org.example.Utils.Identify;
import org.example.Utils.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.example.Utils.Identify.updateFlow;

/**
 * CLI entry and routing for Butik System.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Start banner and optional seed data.</li>
 *   <li>Main and secondary menus for Customer/Product/Order.</li>
 *   <li>Interactive creators/editors delegating to services.</li>
 * </ul>
 *
 * <p>Invariants:
 * <ul>
 *   <li>Entities generate their IDs internally.</li>
 *   <li>Validation happens at input time and in services.</li>
 * </ul>
 */

public class App {

    // SLF4J logger (prefer SLF4J over java.util.logging)
    private static final Logger log = LoggerFactory.getLogger(App.class);

    // ===== Repositories & Services =====
    private static final CrudRepo<Customer, String> customerRepo = new InMemoryCurd<>();
    private static final CrudRepo<Product, String>  prodRepo     = new InMemoryCurd<>();
    private static final CrudRepo<Order, String>    orderRepo    = new InMemoryOrderImp();

    private static final ServiceCrud<Customer, String> customerService = new ServiceCrud<>(customerRepo);
    private static final ServiceCrud<Product, String>  prodService     = new ServiceCrud<>(prodRepo);
    private static final OrderService                   orderService    =
            new OrderService(orderRepo, prodService, customerService);

    // Shared scanner for the entire app lifetime
    private static final Scanner scanner = new Scanner(System.in);

    // Current entity context: "Customer" | "Product" | "Order"
    private static String idx;

    // ==================== Input helpers ====================

    /** Prompt+read a single trimmed line. */
    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** Read a non-empty string (loops until valid). */
    private static String readNonEmpty(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (!s.isEmpty()) return s;
            System.out.println("Value cannot be empty. Try again.");
        }
    }

    /** Read an int in [1..max]. */
    private static int readInt(String prompt, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v < 1 || v > max) {
                    System.out.printf("Out of range (%d-%d).%n", 1, max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                log.warn(e.getMessage());
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    /** Shortcut: read a choice in 1..6. */
    private static int readMenu1to6() {
        return readInt("Your choice [1-6]: ", 6);
    }

    /** Read a positive integer (>0). */
    private static int readPositiveInt(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v > 0) return v;
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter a positive number.");
        }
    }

    /** Read a non-negative price (double >= 0). */
    private static double readDouble() {
        while (true) {
            String s = readLine("Enter a price: ");
            try {
                double v = Double.parseDouble(s);
                if (v >= 0.0) return v;
                System.out.println("Value must be >= 0.0");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    /** Pick a Category from the enum by index. */
    private static Category readCategory() {
        Category[] cats = Category.values();
        System.out.println("Choose a category:");
        for (int i = 0; i < cats.length; i++) {
            System.out.printf("%d) %s%n", i + 1, cats[i].name());
        }
        int choice = readInt("Category [1-" + cats.length + "]: ", cats.length);
        return cats[choice - 1];
    }

    // ==================== App start ====================

    /**
     * Entry point for the main loop.
     * Seeds initial demo data then shows the main menu.
     */
    public static void start() {
        Menu.printBanner();
        seedData();

        while (true) {
            System.out.println("Choose an entity:\n1) Customer\n2) Product\n3) Order\n4) Exit");
            String choice = readLine("Your choice: ");

            switch (choice) {
                case "1":
                    idx = "Customer";
                    secondMenu("Customer");
                    break;
                case "2":
                    idx = "Product";
                    secondMenu("Product");
                    break;
                case "3":
                    idx = "Order";
                    secondMenu("Order");
                    break;
                case "4":
                    System.out.println("Bye!");
                    log.info("Application exit by user.");
                    return;
                default:
                    System.out.println("Invalid selection. Please try again.");
                    log.warn("Invalid main menu choice: {}", choice);
            }
        }
    }

    // ==================== Entity menu (per-type) ====================

    /**
     * Shows the entity menu (Add / View all / Update / Delete / Find / Back).
     * Uses the global 'idx' to dispatch entity-specific behavior.
     */
    private static void secondMenu(String entity) {
        String[] menuOptions = {
                "1. Add " + entity,
                "2. View all " + entity + "s",
                "3. Update " + entity,
                "4. Delete " + entity,
                "5. Find " + entity + " by id",
                "6. Back"
        };

        while (true) {
            Menu.printMenu(menuOptions);
            int choice = readMenu1to6();

            switch (choice) {
                case 1: // Add
                    switch (idx) {
                        case "Customer":
                            Identify.createFlow(customerService, scanner, "Customer",
                                    App::createCustomer, App::customerSummary);
                            break;
                        case "Product":
                            Identify.createFlow(prodService, scanner, "Product",
                                    App::createProduct, App::productSummary);
                            break;
                        case "Order":
                            // Wrap in Safe.run to keep UI responsive on exceptions
                            Safe.run(() -> {
                                Order o = createOrderInteractive();
                                if (o == null) return; // user aborted
                                orderService.create(o);
                                System.out.printf("✓ Order created. ID: %s | Total: %.2f%n",
                                        o.getId(), o.getTotal());
                                log.info("Order created id={} customerId={} items={}",
                                        o.getId(), o.getCustomerId(),
                                        (o.getProducts() == null ? 0 : o.getProducts().size()));
                            }, "CreateOrder");
                            break;
                        default:
                            log.warn("Unknown entity: {}", idx);
                    }
                    break;

                case 2: // View all
                    if ("Product".equals(idx)) {
                        List<Product> products = prodService.getAll();
                        if (products.isEmpty()) {
                            System.out.println("No products found.");
                            log.info("Products list requested but empty.");
                            break;
                        }
                        printAllProducts(products);

                    } else if ("Order".equals(idx)) {
                        List<Order> orders = orderService.getAll();

                        if (orders.isEmpty()) {
                            System.out.println("No orders found.");
                            log.info("Orders list requested but empty.");
                            break;
                        }
                        System.out.println("-----------------".repeat(10));
                        System.out.printf("%-12s %-12s %-65s %-10s%n",
                                "ID", "CustomerId", "Products (qty)", "Total");
                        System.out.println("-----------------".repeat(10));
                        orders.forEach(o -> {
                            String items = formatOrderItemsNamesWithQty(o);
                            System.out.printf("%-12s %-12s %-60s %10.2f%n",
                                    o.getId(), o.getCustomerId(), items, o.getTotal());
                        });
                        System.out.println("=".repeat(120));
                    } else { // Customer
                        List<Customer> customers = customerService.getAll();
                        if (customers.isEmpty()) {
                            System.out.println("No customers found.");
                            log.info("Customers list requested but empty.");
                            break;
                        }
                        System.out.println("------------------".repeat(5));
                        System.out.printf("%-12s %-20s %-20s%n", "ID", "Name", "City");
                        System.out.println("------------------".repeat(5));                        customers.forEach(c ->
                                System.out.printf("%-12s %-20s %-20s%n",
                                        c.getId(), c.getName(), c.getCity()));
                    }
                    System.out.println("=".repeat(120));
                    break;

                case 3: // Update (generic)
                    switch (idx) {
                        case "Customer":
                            updateFlow(customerService, scanner, "Customer",
                                    App::customerSummary, App::editCustomer);
                            break;
                        case "Product":
                            updateFlow(prodService, scanner, "Product",
                                    App::productSummary, App::editProduct);
                            break;
                        case "Order":
                            Safe.run(() -> updateFlow(orderService, scanner, "Order",
                                    App::orderSummary, App::editOrder), "UpdateOrder");
                            break;
                        default:
                            System.out.println("Unknown entity: " + idx);
                            log.warn("Update requested for unknown entity: {}", idx);
                    }
                    break;

                case 4: // Delete
                    String delId = readLine("Enter " + entity + " ID to delete: ");
                    Safe.run(() -> {
                        if ("Customer".equals(idx))      customerService.delete(delId);
                        else if ("Product".equals(idx))  prodService.delete(delId);
                        else                             orderService.delete(delId);
                        System.out.println("✓ Deleted successfully.");
                        log.info("Deleted {} id={}", idx, delId);
                    }, "Delete" + entity);
                    break;

                case 5: // Find by id
                    String findId = readLine("Enter " + entity + " ID to find: ");
                    if ("Customer".equals(idx)) {
                        customerService.findOptionalById(findId)
                                .ifPresentOrElse(
                                        c -> System.out.println(customerSummary(c)),
                                        () -> System.out.println("Not found.")
                                );
                    } else if ("Product".equals(idx)) {
                        prodService.findOptionalById(findId)
                                .ifPresentOrElse(
                                        p -> System.out.println(productSummary(p)),
                                        () -> System.out.println("Not found.")
                                );
                    } else {
                        orderService.findOptionalById(findId)
                                .ifPresentOrElse(
                                        o -> System.out.println(orderSummary(o)),
                                        () -> System.out.println("Not found.")
                                );
                    }
                    break;

                case 6: // Back
                    return;

                default:
                    System.out.println("Invalid selection. Please try again.");
                    log.warn("Invalid selection in secondMenu for {}.", entity);
            }
        }
    }

    /** Simple table print for products. */
    private static void printAllProducts(List<Product> products) {
        System.out.println("----------------".repeat(5));
        System.out.printf("%-12s %-20s %-20s %-10s%n", "ID", "Name", "Category", "Price");
        System.out.println("----------------".repeat(5));
        products.forEach(p ->
                System.out.printf("%-12s %-20s %-20s %10.2f%n",
                        p.getId(), p.getName(),
                        (p.getCategory() == null ? "-" : p.getCategory().name()),
                        p.getPrice()));
        System.out.println("=".repeat(120));
    }

    // ==================== Summaries & inline editors ====================

    private static String customerSummary(Customer c) {
        return String.format("- Name: %s%n- City: %s", c.getName(), c.getCity());
    }

    private static void editCustomer(Customer c, Scanner scanner) {
        System.out.print("New name (press Enter to keep current): ");
        String s = scanner.nextLine().trim();
        if (!s.isEmpty()) c.setName(s);

        System.out.print("New city (press Enter to keep current): ");
        s = scanner.nextLine().trim();
        if (!s.isEmpty()) c.setCity(s);
    }

    private static String productSummary(Product p) {
        return String.format("- Name: %s%n- Category: %s%n- Price: %.2f",
                p.getName(),
                (p.getCategory() == null ? "-" : p.getCategory().name()),
                p.getPrice());
    }

    private static void editProduct(Product p, Scanner scanner) {
        // Name
        System.out.print("New name (press Enter to keep current): ");
        String in = scanner.nextLine().trim();
        if (!in.isEmpty()) p.setName(in);

        // Category (supports number or enum name)
        System.out.println("New category (press Enter to keep current):");
        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) System.out.printf("%d) %s%n", i + 1, cats[i].name());
        String catIn = scanner.nextLine().trim();
        if (!catIn.isEmpty()) {
            Category newCat = null;
            try {
                int n = Integer.parseInt(catIn);
                if (n >= 1 && n <= cats.length) newCat = cats[n - 1];
                else System.out.println("Out of range. Keeping current category.");
            } catch (NumberFormatException e) {
                try {
                    newCat = Category.valueOf(catIn.toUpperCase().replace(' ', '_'));
                } catch (IllegalArgumentException ex) {
                    System.out.println("Invalid category name. Keeping current category.");
                }
            }
            if (newCat != null) p.setCategory(newCat);
        }

        // Price
        System.out.print("New price (press Enter to keep current): ");
        String s = scanner.nextLine().trim();
        if (!s.isEmpty()) {
            try {
                double price = Double.parseDouble(s);
                if (price < 0) System.out.println("Price must be >= 0. Keeping current.");
                else p.setPrice(price);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current price.");
            }
        }
    }

    /** Build a one-line summary of an order: "Name xQty, ..." */
    private static String orderSummary(Order o) {
        String products = (o.getProducts() == null || o.getProducts().isEmpty())
                ? "-"
                : o.getProducts().stream()
                .collect(Collectors.groupingBy(Product::getName, Collectors.counting()))
                .entrySet().stream()
                .map(e -> e.getKey() + " x" + e.getValue())
                .reduce((a, b) -> a + ", " + b)
                .orElse("-");

        return String.format("- CustomerId: %s%n- Products: %s%n- Total: %.2f",
                o.getCustomerId(), products, o.getTotal());


    }

    /**
     * Interactive order editor (change customer, add/remove items, set quantity).
     * Protected by try/catch so a bad action doesn't crash the flow.
     */
    private static void editOrder(Order o, Scanner scanner) {
        try {
            // Optionally change customer
            System.out.print("Change customer? (y/N): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y")) {
                String newCid = pickCustomerIdOrBack();
                if (newCid != null) {
                    o.setCustomerId(newCid);
                    System.out.println("Customer changed to: " + newCid);
                } else {
                    System.out.println("Keeping current customer.");
                }
            }

            // Manage items loop
            while (true) {
                // Show current state
                System.out.println("\nCurrent items:");
                if (o.getProducts() == null || o.getProducts().isEmpty()) {
                    System.out.println("  (empty)");
                } else {
                    Map<String, Long> qty = o.getProducts().stream()
                            .collect(Collectors.groupingBy(Product::getId, Collectors.counting()));
                    System.out.printf("%-12s %-20s %-6s %-8s%n", "ProdID", "Name", "Qty", "Price");
                    System.out.println("------------------------------------------------");
                    qty.forEach((pid, count) -> prodService.findOptionalById(pid).ifPresent(p ->
                            System.out.printf("%-12s %-20s %-6d %-8.2f%n",
                                    p.getId(), p.getName(), count, p.getPrice())));
                }

                System.out.println("\nWhat do you want to do?");
                System.out.println("1) Add product(s)");
                System.out.println("2) Remove product(s)");
                System.out.println("3) Set quantity for a product");
                System.out.println("4) Clear all items");
                System.out.println("5) Done");
                String choiceStr = readLine("Choice [1-5]: ");

                int choice;
                try {
                    choice = Integer.parseInt(choiceStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number.");
                    log.warn("editOrder invalid menu choice: {}", choiceStr);
                    continue;
                }

                if (choice == 5) break;

                switch (choice) {
                    case 1: { // add
                        String pid = readNonEmpty("Enter Product ID to add: ");
                        var pOpt = prodService.findOptionalById(pid);
                        if (pOpt.isEmpty()) {
                            System.out.println("Product not found: " + pid);
                            break;
                        }
                        int qtyToAdd = readPositiveInt("Quantity to add: ");
                        for (int i = 0; i < qtyToAdd; i++) o.getProducts().add(pOpt.get());
                        System.out.println("Added " + qtyToAdd + " x " + pOpt.get().getName());
                        break;
                    }
                    case 2: { // remove
                        String pid = readNonEmpty("Enter Product ID to remove: ");
                        long currentQty = o.getProducts().stream().filter(p -> p.getId().equals(pid)).count();
                        if (currentQty == 0) {
                            System.out.println("This product is not in the order.");
                            break;
                        }
                        System.out.println("Current qty = " + currentQty);
                        int qtyToRemove = readPositiveInt("Quantity to remove: ");
                        if (qtyToRemove > currentQty) qtyToRemove = (int) currentQty;

                        int removed = 0;
                        Iterator<Product> it = o.getProducts().iterator();
                        while (it.hasNext() && removed < qtyToRemove) {
                            if (it.next().getId().equals(pid)) {
                                it.remove();
                                removed++;
                            }
                        }
                        System.out.println("Removed " + removed + " item(s).");
                        break;
                    }
                    case 3: { // set exact qty
                        String pid = readNonEmpty("Enter Product ID to set qty: ");
                        var pOpt = prodService.findOptionalById(pid);
                        if (pOpt.isEmpty()) {
                            System.out.println("Product not found: " + pid);
                            break;
                        }
                        int newQty = readPositiveInt("New quantity: ");
                        o.getProducts().removeIf(p -> p.getId().equals(pid));
                        for (int i = 0; i < newQty; i++) o.getProducts().add(pOpt.get());
                        System.out.println("Quantity set to " + newQty + " for " + pOpt.get().getName());
                        break;
                    }
                    case 4: { // clear all
                        o.getProducts().clear();
                        System.out.println("All items cleared.");
                        break;
                    }
                    default:
                        log.warn("Invalid choice.");
                }
            }

            System.out.println("\n✓ Order updated.");
            System.out.println(orderSummary(o));
            log.info("Order updated id={} customerId={} items={}",
                    o.getId(), o.getCustomerId(),
                    (o.getProducts() == null ? 0 : o.getProducts().size()));

        } catch (Exception ex) {
            log.error("Unexpected error while editing order", ex);
            System.out.println("Unexpected error while editing order: " + ex.getMessage());
        }
    }

    // ==================== Creators (focus section) ====================

    /**
     * Creator for Customer.
     * Input validation:
     *  - Both fields required (non-empty).
     * ID policy:
     *  - The Customer class should generate its own ID (e.g., UUID) in the constructor.
     */
    private static Customer createCustomer(Scanner sc) {
        String name = readNonEmpty("Enter name: ");
        String city = readNonEmpty("Enter city: ");
        return new Customer(name, city);
    }

    /**
     * Creator for Product.
     * Input validation:
     *  - Name: non-empty
     *  - Category: chosen from enum menu
     *  - Price: >= 0.0
     * ID policy:
     *  - Product should generate its own ID (e.g., UUID) in the constructor.
     */
    private static Product createProduct(Scanner sc) {
        String name = readNonEmpty("Enter product name: ");
        Category category = readCategory();
        double price = readDouble();
        return new Product(name, category, price);
    }

    // ==================== Helpers ====================

    /** Build "Name xQty" list, truncated for table width. */
    private static String formatOrderItemsNamesWithQty(Order o) {
        if (o.getProducts() == null || o.getProducts().isEmpty()) return "-";
        String s = o.getProducts().stream()
                .collect(Collectors.groupingBy(Product::getName, Collectors.counting()))
                .entrySet().stream()
                .map(e -> e.getKey() + " x" + e.getValue())
                .reduce((a, b) -> a + ", " + b)
                .orElse("-");
        if (s.length() > 60) s = s.substring(0, Math.max(0, 60 - 3)) + "...";
        return s;
    }

    /** Show customers and return a valid ID or null if user typed 'back'. */
    private static String pickCustomerIdOrBack() {
        List<Customer> allCustomers = customerService.getAll();
        if (allCustomers.isEmpty()) {
            System.out.println("No customers available. Add customers first.");
            return null;
        }
        System.out.printf("%-12s %-20s %-20s%n", "ID", "Name", "City");
        System.out.println("--------------------------------------------------------");
        allCustomers.forEach(c ->
                System.out.printf("%-12s %-20s %-20s%n", c.getId(), c.getName(), c.getCity())
        );
        System.out.println("=".repeat(Math.max(0, allCustomers.size() - 1)));

        while (true) {
            String customerId = readNonEmpty("Enter Customer ID (or 'back' to cancel): ");
            if ("back".equalsIgnoreCase(customerId)) return null;
            if (customerService.findOptionalById(customerId).isPresent()) return customerId;
            System.out.println("Customer not found: " + customerId);
        }
    }

    /** Print all products (helper for order builder). */
    private static void showAllProducts() {
        List<Product> allProducts = prodService.getAll();
        if (allProducts.isEmpty()) {
            System.out.println("No products available. Add products first.");
            return;
        }
        printAllProducts(allProducts);
    }

    /**
     * Interactively builds an {@link org.example.Entity.Order}:
     * <ol>
     *   <li>Pick a valid customer (or abort).</li>
     *   <li>Display products and collect productId → quantity.</li>
     *   <li>Expand quantities to a flat list of products and compute total.</li>
     * </ol>
     *
     * @return a new Order or {@code null} if aborted by user
     */

    private static Order createOrderInteractive() {
        try {
            // (1) customer
            String customerId = pickCustomerIdOrBack();
            if (customerId == null) {
                System.out.println("Order creation aborted.");
                return null;
            }

            // (2) products
            showAllProducts();

            // (3) collect items
            Map<String, Integer> items = new LinkedHashMap<>();
            while (true) {
                String pid = readNonEmpty("Enter Product ID (or 'done' to finish): ");
                if (pid.equalsIgnoreCase("done")) break;

                var pOpt = prodService.findOptionalById(pid);
                if (pOpt.isEmpty()) {
                    System.out.println("Product not found: " + pid);
                    continue;
                }
                int qty = readPositiveInt("Qty: ");
                items.merge(pid, qty, Integer::sum);
            }
            if (items.isEmpty()) {
                System.out.println("No items selected. Order creation aborted.");
                return null;
            }

            // (4) expand product list
            List<Product> productList = new ArrayList<>();
            for (Map.Entry<String, Integer> e : items.entrySet()) {
                Product p = prodService.findOptionalById(e.getKey()).orElseThrow();
                for (int i = 0; i < e.getValue(); i++) productList.add(p);
            }

            // (5) compute total
            double total = productList.stream().mapToDouble(Product::getPrice).sum();

            // (6) construct order
            return new Order(customerId, total, productList);

        } catch (Exception ex) {
            log.error("Failed to create order", ex);
            System.out.println("Failed to create order: " + ex.getMessage());
            return null;
        }
    }

    // ==================== Seed (optional demo data) ====================

    /**
     * Demo data seeding:
     *  - Creates customers & products
     *  - Creates orders and assigns customers in a round-robin fashion
     * Assumes entities generate their own IDs (e.g., UUID).
     */
    @SuppressWarnings("unused")
    private static void seedData() {
        // Customers
        List<String> cid = new ArrayList<>();
        for (Customer customer : Data.getCustomers()) {
            customerService.create(customer);
            cid.add(customer.getId());
        }

        // Products
        for (Product product : Data.getProducts()) {
            prodService.create(product);
        }

        // Orders (round-robin assignment of customerId)
        int i = 0;
        List<Order> orders = Data.getOrders();
        for (Order order : orders) {
            if (cid.isEmpty()) break;
            String customerId = cid.get(i % cid.size());
            order.setCustomerId(customerId);
            orderService.create(order);
            i++;
        }
        log.info("Seeded {} customers, {} products, {} orders.",
                cid.size(), Data.getProducts().size(), orders.size());
    }
}
