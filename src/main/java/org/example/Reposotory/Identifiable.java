package org.example.Reposotory;

/**
 * Simple contract for entities that have an identifiable primary key.
 * <p>
 * Used to ensure every entity exposes its unique ID (e.g., String, UUID, Long).
 *
 * @param <ID> the type of the entity identifier
 */
public interface Identifiable<ID> {

    /**
     * Returns the unique identifier of the entity.
     *
     * @return the entity's ID
     */

    ID getId();
}
