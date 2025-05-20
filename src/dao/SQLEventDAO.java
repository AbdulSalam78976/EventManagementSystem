package dao;

import models.Event;
import models.Event.EventStatus;
import models.User;
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
    public List<Event> findByCategory(String category) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE category = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        }
        return events;
    }
    
    @Override
    public List<Event> findByVenue(String venueName) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE venue_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, venueName);
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
        String sql = "SELECT * FROM events WHERE event_date > NOW() AND status = 'APPROVED'";
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
        String sql = "SELECT * FROM events WHERE event_date < NOW()";
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
        String sql = "SELECT * FROM events WHERE status = 'APPROVED' ORDER BY event_date ASC LIMIT 5";
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
        String sql = "SELECT * FROM events WHERE DATE(event_date) = CURDATE()";
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
        String sql = "SELECT * FROM events WHERE title LIKE ? OR description LIKE ?";
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
                    "venue_name, total_slots, available_slots, " +
                    "organizer_id, category, eligibility_criteria, contact_info, status, " +
                    "main_image, main_image_type, additional_documents, additional_documents_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, event.getTitle());
            stmt.setString(paramIndex++, event.getDescription());
            stmt.setTimestamp(paramIndex++, Timestamp.valueOf(event.getEventDate()));
            stmt.setTimestamp(paramIndex++, Timestamp.valueOf(event.getRegistrationDeadline()));
            stmt.setString(paramIndex++, event.getVenueName());
            stmt.setInt(paramIndex++, event.getTotalSlots());
            stmt.setInt(paramIndex++, event.getAvailableSlots());
            stmt.setInt(paramIndex++, event.getOrganizer().getId());
            stmt.setString(paramIndex++, event.getCategory());
            stmt.setString(paramIndex++, event.getEligibilityCriteria());
            stmt.setString(paramIndex++, event.getContactInfo());
            stmt.setString(paramIndex++, event.getStatus().name());
            stmt.setBytes(paramIndex++, event.getMainImage());
            stmt.setString(paramIndex++, event.getMainImageType());
            stmt.setBytes(paramIndex++, event.getAdditionalDocuments());
            stmt.setString(paramIndex++, event.getAdditionalDocumentsType());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating event failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating event failed, no ID obtained.");
                }
            }
            
            return event;
        }
    }
    
    @Override
    public Event update(Event event) throws SQLException {
        String sql = "UPDATE events SET title = ?, description = ?, event_date = ?, registration_deadline = ?, " +
                    "venue_name = ?, total_slots = ?, available_slots = ?, " +
                    "organizer_id = ?, category = ?, eligibility_criteria = ?, contact_info = ?, status = ?, " +
                    "main_image = ?, main_image_type = ?, additional_documents = ?, additional_documents_type = ? " +
                    "WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, event.getTitle());
            stmt.setString(paramIndex++, event.getDescription());
            stmt.setTimestamp(paramIndex++, Timestamp.valueOf(event.getEventDate()));
            stmt.setTimestamp(paramIndex++, Timestamp.valueOf(event.getRegistrationDeadline()));
            stmt.setString(paramIndex++, event.getVenueName());
            stmt.setInt(paramIndex++, event.getTotalSlots());
            stmt.setInt(paramIndex++, event.getAvailableSlots());
            stmt.setInt(paramIndex++, event.getOrganizer().getId());
            stmt.setString(paramIndex++, event.getCategory());
            stmt.setString(paramIndex++, event.getEligibilityCriteria());
            stmt.setString(paramIndex++, event.getContactInfo());
            stmt.setString(paramIndex++, event.getStatus().name());
            stmt.setBytes(paramIndex++, event.getMainImage());
            stmt.setString(paramIndex++, event.getMainImageType());
            stmt.setBytes(paramIndex++, event.getAdditionalDocuments());
            stmt.setString(paramIndex++, event.getAdditionalDocumentsType());
            stmt.setInt(paramIndex++, event.getId());
            
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
    
    @Override
    public int getTotalRegistrations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setCategory(rs.getString("category"));
        event.setVenueName(rs.getString("venue_name"));
        event.setContactInfo(rs.getString("contact_info"));
        
        // Handle potentially null timestamps
        Timestamp eventDate = rs.getTimestamp("event_date");
        if (eventDate != null) {
            event.setEventDate(eventDate.toLocalDateTime());
        }
        
        Timestamp regDeadline = rs.getTimestamp("registration_deadline");
        if (regDeadline != null) {
            event.setRegistrationDeadline(regDeadline.toLocalDateTime());
        }
        
        event.setTotalSlots(rs.getInt("total_slots"));
        event.setAvailableSlots(rs.getInt("available_slots"));
        event.setEligibilityCriteria(rs.getString("eligibility_criteria"));
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            event.setStatus(Event.EventStatus.valueOf(statusStr));
        }
        
        event.setMainImage(rs.getBytes("main_image"));
        event.setMainImageType(rs.getString("main_image_type"));
        event.setAdditionalDocuments(rs.getBytes("additional_documents"));
        event.setAdditionalDocumentsType(rs.getString("additional_documents_type"));
        
        // Handle potentially null timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            event.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            event.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Load organizer
        int organizerId = rs.getInt("organizer_id");
        try {
            User organizer = userDAO.findById(organizerId);
            event.setOrganizer(organizer);
        } catch (SQLException e) {
            // Log error but don't fail the entire event load
            System.err.println("Error loading organizer for event " + event.getId() + ": " + e.getMessage());
        }
        
        return event;
    }
} 