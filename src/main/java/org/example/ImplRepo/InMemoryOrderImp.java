package org.example.ImplRepo;

import org.example.Entity.Order;
import org.example.Reposotory.CrudRepo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory Order repository with:
 * <ul>
 *   <li>Primary store: {@code Map<orderId, Order>} — O(1) CRUD by ID.</li>
 *   <li>Secondary index: {@code Map<customerId, List<orderId>>} for efficient lookups.</li>
 * </ul>
 *
 * <p>Consistency:
 * <ul>
 *   <li>Indexes are updated on create, update (reindex if customer changes), and delete.</li>
 *   <li>Uses {@code computeIfAbsent} to atomically create per-customer lists.</li>
 * </ul>
 *
 * <p>Thread-safety: backed by {@code ConcurrentHashMap}.
 */

public class InMemoryOrderImp implements CrudRepo<Order, String> {

    /**
     * Primary store: orders by ID.
     */
    private final Map<String, Order> byId = new ConcurrentHashMap<>();

    /**
     * Secondary index: customerId -> list of orderIds.
     */
    private final Map<String, List<String>> byCustomer = new ConcurrentHashMap<>();

    /**
     * Creates a new order and indexes it by customerId.
     *
     * @param order the {@link Order} to store
     * @throws NullPointerException     if order is null
     * @throws IllegalArgumentException if order ID is null, blank, or duplicate
     */
    @Override
    public void create(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        String id = order.getId();
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("order id must not be null/blank");
        }
        if (byId.containsKey(id)) {
            throw new IllegalArgumentException("duplicate order id: " + id);
        }
        byId.put(id, order);
        indexCustomer(order.getCustomerId(), id); // maintain secondary index
    }

    /**
     * Updates an existing order. If the customerId changes, the index is rebalanced.
     *
     * @param order the updated {@link Order}
     *
     *
     * @throws IllegalArgumentException if the entity does not exist or ID is missing
     *
     */
    @Override
    public void update(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        String id = order.getId();
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("order id must not be null/blank");
        }
        Order old = byId.get(id);
        if (old == null) {
            throw new IllegalArgumentException("order not found: " + id);
        }
        // Re-index if the customer has changed
        if (!Objects.equals(old.getCustomerId(), order.getCustomerId())) {
            deindexCustomer(old.getCustomerId(), id);
            indexCustomer(order.getCustomerId(), id);
        }
        byId.put(id, order);
    }

    /**
     * Deletes an order and removes its customer index entry.
     *
     * @param id the order ID to remove
     * @throws IllegalArgumentException if order not found
     */
    @Override
    public void delete(String id) {
        Objects.requireNonNull(id, "id must not be null");
        Order removed = byId.remove(id);
        if (removed == null) {
            throw new IllegalArgumentException("order not found: " + id);
        }
        deindexCustomer(removed.getCustomerId(), id);
    }

    /**
     * Finds an order by ID.
     *
     * @param id the order ID
     * @return optional {@link Order}, empty if not found
     */
    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    /**
     * Returns all orders (unmodifiable).
     *
     * @return list of all {@link Order} objects
     */
    @Override
    public List<Order> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(byId.values()));
    }

    // ------------------------------------------------------------
    // Internal index maintenance helpers
    // ------------------------------------------------------------

    /**
     * Adds an orderId under the given customerId.
     * Uses computeIfAbsent for atomic list creation.
     *
     * @param customerId the customer ID
     * @param orderId    the order ID
     */
    private void indexCustomer(String customerId, String orderId) {
        if (customerId == null) return;
        byCustomer.computeIfAbsent(customerId, k -> new ArrayList<>()).add(orderId);
    }

    /**
     * Removes an orderId from the customer's index list.
     * Cleans up empty lists to prevent memory leaks.
     *
     * @param customerId the customer ID
     * @param orderId    the order ID
     */
    private void deindexCustomer(String customerId, String orderId) {
        if (customerId == null) return;
        List<String> list = byCustomer.get(customerId);
        if (list == null) return;
        list.remove(orderId);
        if (list.isEmpty()) byCustomer.remove(customerId);
    }
}
