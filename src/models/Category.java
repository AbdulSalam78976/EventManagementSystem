package models;

/**
 * Model class representing a category for events
 */
public class Category {
    private int id;
    private String name;
    private String description;
    private String color;
    private String icon;
    private boolean active;

    // Default constructor
    public Category() {
        this.active = true;
    }

    // Constructor with essential fields
    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    // Full constructor
    public Category(int id, String name, String description, String color, String icon, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.active = active;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
