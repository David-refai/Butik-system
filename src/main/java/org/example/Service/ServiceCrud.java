package org.example.Service;

import org.example.Error.ErrorHandling;
import org.example.Reposotory.CrudRepo;
import org.example.Reposotory.Identifiable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Generic service layer for CRUD operations with validation.
 * - No UI/printing here (pure domain/service).
 * - Throws domain exceptions (Safe.*) instead of IllegalArgumentException.
 * - Works with any T that exposes getId() (Identifiable).
 *
 * @param <T>  entity type
 * @param <ID> id type
 */
public class ServiceCrud<T extends Identifiable<ID>, ID> {

    protected final CrudRepo<T, ID> crudRepo;

    public ServiceCrud(CrudRepo<T, ID> crudRepo) {
        this.crudRepo = Objects.requireNonNull(crudRepo, "crudRepo must not be null");
    }

    // -------------------- Commands --------------------

    /**
     * Create a new entity (fails if id is null or already exists).
     */
    public void create(T entity) {
        validateOnCreate(entity);
        crudRepo.create(entity);
        afterCreate(entity);
    }

    /**
     * Update an existing entity (fails if id is null or not found).
     */
    public void update(T entity) {
        validateOnUpdate(entity);
        crudRepo.update(entity);
        afterUpdate(entity);
    }

    /**
     * Delete by id (fails if not found).
     */
    public void delete(ID id) {
        validateExist(id);
        crudRepo.delete(id);
        afterDelete(id);
    }

    // -------------------- Queries --------------------

    /**
     * Return all entities (immutable snapshot recommended by repo).
     */
    public List<T> getAll() {
        return crudRepo.findAll();  // NOTE: your CrudRepo should expose getAll(), not findAll()
    }

    /**
     * Return Optional entity (empty if not found); does not throw.
     */
    public Optional<T> findOptionalById(ID id) {
        requireIdNotNull(id);
        return crudRepo.findById(id);
    }

    /**
     * Return entity or throw Safe.NotFound if missing.
     */
    public T findByIdOrThrow(ID id) {
        requireIdNotNull(id);
        return crudRepo.findById(id)
                .orElseThrow(() -> new ErrorHandling.NotFound("Entity not found: id=" + id));
    }

    // -------------------- Validation --------------------

    /**
     * Validate before create.
     */
    protected void validateOnCreate(T e) {
        if (e == null) throw new ErrorHandling.Validation("Entity must not be null");
        if (e.getId() == null) throw new ErrorHandling.Validation("ID must not be null");
        if (crudRepo.findById(e.getId()).isPresent()) {
            throw new ErrorHandling.Duplicate("Entity already exists: id=" + e.getId());
        }
    }

    /**
     * Validate before update.
     */
    protected void validateOnUpdate(T e) {
        if (e == null) throw new ErrorHandling.Validation("Entity must not be null");
        if (e.getId() == null) throw new ErrorHandling.Validation("ID must not be null");
        if (crudRepo.findById(e.getId()).isEmpty()) {
            throw new ErrorHandling.NotFound("Entity not found: id=" + e.getId());
        }
    }

    /**
     * Ensure entity exists for operations like delete.
     */
    protected void validateExist(ID id) {
        requireIdNotNull(id);
        if (crudRepo.findById(id).isEmpty()) {
            throw new ErrorHandling.NotFound("Entity not found: id=" + id);
        }
    }

    /**
     * Guard for null IDs.
     */
    private void requireIdNotNull(ID id) {
        if (id == null) throw new ErrorHandling.Validation("ID must not be null");
    }

    // -------------------- Hooks (optional overrides) --------------------

    protected void afterCreate(T e) {
        // No-op by default; override in subclasses if you need side-effects
    }

    protected void afterUpdate(T e) {
        // No-op by default
    }

    protected void afterDelete(ID id) {
        // No-op by default
    }
}
