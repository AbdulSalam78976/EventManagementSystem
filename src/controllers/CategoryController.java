package controllers;

import models.Category;
import dao.CategoryDAO;
import dao.SQLCategoryDAO;
import utils.ValidationUtils;

import java.util.List;

/**
 * Controller class for handling category-related operations
 */
public class CategoryController {
    
    private static CategoryController instance;
    private final CategoryDAO categoryDAO;
    
    // Private constructor for singleton pattern
    private CategoryController() {
        this.categoryDAO = new SQLCategoryDAO();
    }
    
    /**
     * Get the singleton instance of the CategoryController
     * 
     * @return The CategoryController instance
     */
    public static synchronized CategoryController getInstance() {
        if (instance == null) {
            instance = new CategoryController();
        }
        return instance;
    }
    
    /**
     * Create a new category
     * 
     * @param name Category name
     * @param description Category description
     * @param color Category color (hex code)
     * @param icon Category icon
     * @return The created category
     * @throws IllegalArgumentException if the input is invalid
     */
    public Category createCategory(String name, String description, String color, String icon) {
        // Validate input
        if (!ValidationUtils.isNotEmpty(name)) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        
        if (!ValidationUtils.isValidHexColor(color)) {
            throw new IllegalArgumentException("Invalid color format. Must be a valid hex color code");
        }
        
        // Check if a category with the same name already exists
        if (categoryDAO.existsByName(name)) {
            throw new IllegalArgumentException("A category with this name already exists");
        }
        
        // Create the category
        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(description != null ? description.trim() : "");
        category.setColor(color);
        category.setIcon(icon);
        category.setActive(true);
        
        // Save the category
        return categoryDAO.save(category);
    }
    
    /**
     * Update an existing category
     * 
     * @param category The category to update
     * @return The updated category
     * @throws IllegalArgumentException if the input is invalid
     */
    public Category updateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        if (category.getId() == 0) {
            throw new IllegalArgumentException("Category ID must be set");
        }
        
        if (!ValidationUtils.isNotEmpty(category.getName())) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        
        if (!ValidationUtils.isValidHexColor(category.getColor())) {
            throw new IllegalArgumentException("Invalid color format. Must be a valid hex color code");
        }
        
        // Check if category exists
        Category existingCategory = categoryDAO.findById(category.getId());
        if (existingCategory == null) {
            throw new IllegalArgumentException("Category not found");
        }
        
        // Check if the name is being changed to one that already exists
        if (!existingCategory.getName().equals(category.getName()) && 
            categoryDAO.existsByName(category.getName())) {
            throw new IllegalArgumentException("A category with this name already exists");
        }
        
        // Update the category
        return categoryDAO.update(category);
    }
    
    /**
     * Delete a category
     * 
     * @param categoryId The ID of the category to delete
     * @return true if the category was deleted, false otherwise
     * @throws IllegalArgumentException if the category ID is invalid
     */
    public boolean deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
        
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        
        return categoryDAO.delete(categoryId);
    }
    
    /**
     * Get a category by ID
     * 
     * @param categoryId The ID of the category to get
     * @return The category, or null if not found
     * @throws IllegalArgumentException if the category ID is invalid
     */
    public Category getCategoryById(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
        return categoryDAO.findById(categoryId);
    }
    
    /**
     * Get a category by name
     * 
     * @param name The name of the category to get
     * @return The category, or null if not found
     * @throws IllegalArgumentException if the name is empty
     */
    public Category getCategoryByName(String name) {
        if (!ValidationUtils.isNotEmpty(name)) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        return categoryDAO.findByName(name.trim());
    }
    
    /**
     * Get all categories
     * 
     * @return A list of all categories
     */
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }
    
    /**
     * Get active categories
     * 
     * @return A list of active categories
     */
    public List<Category> getActiveCategories() {
        return categoryDAO.findByActive(true);
    }
    
    /**
     * Activate a category
     * 
     * @param categoryId The ID of the category to activate
     * @return The activated category
     * @throws IllegalArgumentException if the category ID is invalid or category not found
     */
    public Category activateCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
        
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        
        category.setActive(true);
        return categoryDAO.update(category);
    }
    
    /**
     * Deactivate a category
     * 
     * @param categoryId The ID of the category to deactivate
     * @return The deactivated category
     * @throws IllegalArgumentException if the category ID is invalid or category not found
     */
    public Category deactivateCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
        
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        
        category.setActive(false);
        return categoryDAO.update(category);
    }
}
