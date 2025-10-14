package org.example.Service;

import org.example.Entity.Customer;
import org.example.Entity.Order;
import org.example.Entity.Product;
import org.example.Error.ErrorHandling;
import org.example.Reposotory.CrudRepo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 * Order-specific orchestration:
 * <ul>
 *   <li>Validates customer existence before create/update.</li>
 *   <li>Expands product quantities, computes totals if needed.</li>
 *   <li>Provides convenience queries (e.g., by customer).</li>
 * </ul>
 */

public class OrderService extends ServiceCrud<Order, String> {

    private final ServiceCrud<Product, String> productService;
    private final ServiceCrud<Customer, String> customerService;

    public OrderService(CrudRepo<Order, String> orderRepo,
                        ServiceCrud<Product, String> productService,
                        ServiceCrud<Customer, String> customerService) {
        super(orderRepo);
        this.productService = Objects.requireNonNull(productService);
        this.customerService = Objects.requireNonNull(customerService);
    }

    /**
     * Place new order from customerId + productId->qty
     */
    public Order place(String customerId, Map<String, Integer> items) {
        if (customerId == null || customerId.isBlank())
            throw new ErrorHandling.Validation("customerId is empty");
        // ensure customer exists
        customerService.findOptionalById(customerId)
                .orElseThrow(() -> new ErrorHandling.NotFound("Customer not found: " + customerId));

        if (items == null || items.isEmpty())
            throw new ErrorHandling.Validation("Order has no items");

        // expand to product list
        java.util.ArrayList<Product> productList = new java.util.ArrayList<>();
        for (var e : items.entrySet()) {
            Product p = productService.findOptionalById(e.getKey())
                    .orElseThrow(() -> new ErrorHandling.NotFound("Product not found: " + e.getKey()));
            int qty = e.getValue() == null ? 0 : e.getValue();
            if (qty <= 0) throw new ErrorHandling.Validation("Invalid qty for " + p.getId() + ": " + qty);
            for (int i = 0; i < qty; i++) productList.add(p);
        }
        if (productList.isEmpty()) throw new ErrorHandling.Validation("Order has no items.");

        double total = productList.stream().mapToDouble(Product::getPrice).sum();
        Order o = new Order(customerId, total, productList);
        create(o); // uses validateOnCreate below
        return o;
    }

    @Override
    protected void validateOnCreate(Order o) {
        super.validateOnCreate(o); // checks id duplicate/null
        // domain checks:
        ensureCustomerExists(o.getCustomerId());
        ensureProductsNotEmpty(o.getProducts());
    }

    @Override
    protected void validateOnUpdate(Order o) {
        super.validateOnUpdate(o); // checks id exists
        // domain checks:
        ensureCustomerExists(o.getCustomerId());
        ensureProductsNotEmpty(o.getProducts());
    }

    private void ensureCustomerExists(String customerId) {
        if (customerId == null || customerId.isBlank())
            throw new ErrorHandling.Validation("customerId must not be empty");
        customerService.findOptionalById(customerId)
                .orElseThrow(() -> new ErrorHandling.NotFound("Customer not found: " + customerId));
    }

    private void ensureProductsNotEmpty(List<Product> products) {
        if (products == null || products.isEmpty())
            throw new ErrorHandling.Validation("Order must contain at least one product");
    }
}
