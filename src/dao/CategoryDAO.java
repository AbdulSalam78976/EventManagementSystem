package dao;

import java.util.List;
import models.Category;

/**
 * Data Access Object interface for Category entities
 */
public interface CategoryDAO {
    /**
     * Get all categories
     *
     * @return List of all categories
     */
    List<Category> findAll();

    /**
     * Get a category by its ID
     *
     * @param categoryId The ID of the category to retrieve
     * @return The category with the specified ID, or null if not found
     */
    Category findById(int categoryId);

    /**
     * Get a category by its name
     *
     * @param name The name of the category to retrieve
     * @return The category with the specified name, or null if not found
     */
    Category findByName(String name);

    /**
     * Save a new category
     *
     * @param category The category to save
     * @return The saved category
     */
    Category save(Category category);

    /**
     * Update an existing category
     *
     * @param category The category with updated information
     * @return The updated category
     */
    Category update(Category category);

    /**
     * Delete a category by its ID
     *
     * @param categoryId The ID of the category to delete
     * @return true if successful, false otherwise
     */
    boolean delete(int categoryId);

    /**
     * Check if a category with the given name exists
     *
     * @param name The name to check
     * @return true if a category with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Get categories by active status
     *
     * @param active The active status to filter by
     * @return List of categories with the specified active status
     */
    List<Category> findByActive(boolean active);
}
