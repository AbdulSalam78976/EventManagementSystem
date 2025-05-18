package models;

/**
 * Model class representing a venue where events can be held
 */
public class Venue {
    private int id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private int capacity;
    private String description;
    private String contactInfo;
    private boolean active;
    private String type;

    // Default constructor
    public Venue() {
        this.active = true;
    }

    // Constructor with essential fields
    public Venue(String name, String address, int capacity, String type) {
        this();
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.type = type;
    }

    // Full constructor
    public Venue(int id, String name, String address, String city, String state, 
                String zipCode, int capacity, String description, String contactInfo, 
                boolean active, String type) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.capacity = capacity;
        this.description = description;
        this.contactInfo = contactInfo;
        this.active = active;
        this.type = type;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the full address as a formatted string
     * 
     * @return The full address
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(address);
        
        if (city != null && !city.isEmpty()) {
            sb.append(", ").append(city);
        }
        
        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }
        
        if (zipCode != null && !zipCode.isEmpty()) {
            sb.append(" ").append(zipCode);
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + getFullAddress() + '\'' +
                ", capacity=" + capacity +
                ", type=" + type +
                '}';
    }
}
