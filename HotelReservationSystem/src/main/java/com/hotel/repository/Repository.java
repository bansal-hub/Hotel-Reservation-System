package com.hotel.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining standard CRUD operations.
 * Demonstrates the use of Interfaces and Generics.
 *
 * @param <T>  Entity type
 * @param <ID> ID type
 */
public interface Repository<T, ID> {

    /**
     * Saves or updates an entity.
     *
     * @param entity The entity to save
     */
    void save(T entity);

    /**
     * Finds an entity by its ID.
     *
     * @param id The ID to search for
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Returns all entities stored in this repository.
     *
     * @return List of all entities
     */
    List<T> findAll();

    /**
     * Removes an entity by its ID.
     *
     * @param id The ID of the entity to remove
     */
    void deleteById(ID id);

    /**
     * Persists all current entities to disk.
     */
    void persistToDisk();

    /**
     * Loads all entities from disk into memory.
     */
    void loadFromDisk();
}
