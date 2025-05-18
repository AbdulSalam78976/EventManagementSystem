package dao;

import models.Event;
import models.Event.EventStatus;
import models.User;
import models.Venue;
import models.Category;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SQLEventDAO implements EventDAO {
    private final Connection connection;
    private final UserDAO userDAO;
    
    public SQLEventDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        this.userDAO = new SQLUserDAO();
    }
    
    @Override
    public Event findById(int id) throws SQLException {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Event> findAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findByOrganizer(int organizerId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE organizer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findByStatus(EventStatus status) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findByCategory(int categoryId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findByVenue(int venueId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE venue_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, venueId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findUpcoming() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE start_date > NOW() AND status = 'APPROVED'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findPast() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE end_date < NOW()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findFeatured() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE featured = true AND status = 'APPROVED'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findToday() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE DATE(start_date) = CURDATE()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }
    
    @Override
    public List<Event> search(String query) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE name LIKE ? OR description LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        }
        return events;
    }
    
    @Override
    public Event save(Event event) throws SQLException {
        String sql = "INSERT INTO events (title, description, event_date, registration_deadline, " +
                    "venue_name, total_slots, available_slots, organizer_id, category_name, " +
                    "eligibility_criteria, schedule, status, main_image_path, additional_document_paths) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDateTime()));
            stmt.setString(5, event.getVenueName());
            stmt.setInt(6, event.getCapacity());
            stmt.setInt(7, event.getCapacity());
            stmt.setInt(8, event.getOrganizer().getId());
            stmt.setString(9, event.getCategoryName());
            stmt.setString(10, event.getRequirements());
            stmt.setString(11, ""); // schedule
            stmt.setString(12, event.getStatus().name());
            stmt.setString(13, event.getMainImagePath());
            stmt.setString(14, event.getAdditionalDocumentPaths());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating event failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                    return event;
                } else {
                    throw new SQLException("Creating event failed, no ID obtained.");
                }
            }
        }
    }
    
    @Override
    public Event update(Event event) throws SQLException {
        String sql = "UPDATE events SET title = ?, description = ?, event_date = ?, registration_deadline = ?, " +
                    "venue_name = ?, total_slots = ?, available_slots = ?, organizer_id = ?, category_name = ?, " +
                    "eligibility_criteria = ?, schedule = ?, status = ?, main_image_path = ?, additional_document_paths = ? " +
                    "WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDateTime()));
            stmt.setString(5, event.getVenueName());
            stmt.setInt(6, event.getCapacity());
            stmt.setInt(7, event.getCapacity());
            stmt.setInt(8, event.getOrganizer().getId());
            stmt.setString(9, event.getCategoryName());
            stmt.setString(10, event.getRequirements());
            stmt.setString(11, ""); // schedule
            stmt.setString(12, event.getStatus().name());
            stmt.setString(13, event.getMainImagePath());
            stmt.setString(14, event.getAdditionalDocumentPaths());
            stmt.setInt(15, event.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating event failed, no rows affected.");
            }
            return event;
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setName(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setStartDateTime(rs.getTimestamp("event_date").toLocalDateTime());
        event.setEndDateTime(rs.getTimestamp("registration_deadline").toLocalDateTime());
        event.setCapacity(rs.getInt("capacity"));
        event.setRegisteredCount(rs.getInt("total_slots") - rs.getInt("available_slots"));
        event.setStatus(EventStatus.valueOf(rs.getString("status")));
        event.setRequirements(rs.getString("eligibility_criteria"));
        event.setMainImagePath(rs.getString("main_image_path"));
        event.setAdditionalDocumentPaths(rs.getString("additional_document_paths"));
        event.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        event.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Load related entities
        String venueName = rs.getString("venue_name");
        int organizerId = rs.getInt("organizer_id");
        String categoryName = rs.getString("category_name");
        
        // Load venue
        event.setVenueName(venueName);
        
        // Load organizer
        User organizer = userDAO.getUserById(organizerId);
        if (organizer == null) {
            throw new SQLException("Organizer not found for ID: " + organizerId);
        }
        event.setOrganizer(organizer);
        
        // Load category
        event.setCategoryName(categoryName);
        
        return event;
    }
} 