package org.example.Reposotory;

import java.util.List;
import java.util.Optional;

public interface CrudRepo<T, ID> {
    void create(T entity);

    void update(T entity);

    Optional<T> findById(ID id);

    void delete(ID id);

    List<T> findAll();


}
