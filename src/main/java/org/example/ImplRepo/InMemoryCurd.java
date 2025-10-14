package org.example.ImplRepo;

import org.example.Reposotory.CrudRepo;
import org.example.Reposotory.Identifiable;

import java.util.*;


public class InMemoryCurd<T extends Identifiable<ID>, ID> implements CrudRepo<T, ID> {
    //    list to store entities in memory
    private Map<ID, T> entities = new HashMap<>();

    public InMemoryCurd(Map<ID, T> entities) {
        this.entities = entities;
    }

    public InMemoryCurd() {
    }

    // ===== Method for CREATE entity =====
    @Override
    public void create(T entity) {
//        save entity to in-memory list
        ID id = entity.getId();
        entities.put(id, entity);
    }

    /**
     * ===== Method UPDATE  =====
     *
     * @param entity take entity
     */
    @Override
    public void update(T entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        ID id = entity.getId();
        if (id == null) {
            throw new IllegalArgumentException("Entity id must not be null");
        }
// save the id and obj
        entities.put(id, entity);
    }

    // ===== Method Find All  =====
    @Override
    public void delete(ID id) {
        if (id != null) {
            entities.remove(id);
        }
    }

    // ===== Method Find All  =====
    @Override
    public List<T> findAll() {
        return List.copyOf(entities.values());
    }

    /**
     * ===== Method Find BY ID  =====
     *
     * @param id ram ID, it is generic for identify the entity's id
     */
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entities.get(id));

    }
}