package org.example.Utils;

import org.example.Reposotory.Identifiable;
import org.example.Service.ServiceCrud;

import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Generic console flows for create and update operations
 * over a {@code ServiceCrud<T, String>} service.
 * entity type implementing {@code Identifiable<String>}
 */

public class Identify {
    /**
     * Runs a generic create flow:
     * <ol>
     *   <li>Build an entity using the provided creator.</li>
     *   <li>Persist via the service.</li>
     *   <li>Print a success line and an optional summary.</li>
     * </ol>
     *
     * @param service     target service layer
     * @param scanner     console scanner
     * @param entityLabel display label (e.g., "Customer")
     * @param creator     function that reads from Scanner and returns an entity (or null to abort)
     * @param summaryFn   optional pretty-printer for the created entity
     */

    // ===== Generic CREATE flow =====
    public static <T extends Identifiable<String>> void createFlow(
            ServiceCrud<T, String> service,
            Scanner scanner,
            String entityLabel,
            Function<Scanner, T> creator,
            Function<T, String> summaryFn
    ) {
        try {
            T entity = creator.apply(scanner);
            if (entity == null) {
                System.out.println("Creation aborted.");
                return;
            }
            service.create(entity);
            System.out.println("✓ " + entityLabel + " created successfully. ID: " + entity.getId());
            if (summaryFn != null) System.out.println(summaryFn.apply(entity));
        } catch (IllegalArgumentException ex) {
            System.out.println("Create failed: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
        }
    }

    /**
     * Runs a generic update flow:
     * <ol>
     *   <li>Read an ID and load the entity (or fail).</li>
     *   <li>Show a summary and run a type-specific editor.</li>
     *   <li>Save updates via the service.</li>
     * </ol>
     *
     * @param service     target service layer
     * @param scanner     console scanner
     * @param entityLabel display label
     * @param summaryFn   summary printer used before editing
     * @param editFn      type-specific in-place editor (Enter keeps current)
     */

    // ===== Generic UPDATE flow =====
    public static <T extends Identifiable<String>> void updateFlow(
            ServiceCrud<T, String> service,
            Scanner scanner,
            String entityLabel,
            Function<T, String> summaryFn,
            BiConsumer<T, Scanner> editFn) {

        try {
            System.out.print("Enter " + entityLabel + " ID to update: ");
            String id = scanner.nextLine().trim();
            if (id.isEmpty()) {
                System.out.println("ID cannot be empty.");
                return;
            }

            T entity = service.findOptionalById(id)
                    .orElseThrow(() -> new IllegalArgumentException(entityLabel + " not found: " + id));

            System.out.println("Current values:");
            System.out.println(" " + summaryFn.apply(entity));

            // Let the specific editor adjust fields; Enter keeps current values
            editFn.accept(entity, scanner);

            service.update(entity);
            System.out.println("✓ " + entityLabel + " updated successfully.");

        } catch (IllegalArgumentException ex) {
            System.out.println("Update failed: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
        }
    }



}
