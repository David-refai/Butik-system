package org.example.Utils;

import org.example.Reposotory.Identifiable;
import org.example.Service.ServiceCrud;

import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Identify {

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
