package dao;

import models.Venue;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SQLVenueDAO implements VenueDAO {
    
    @Override
    public Venue findById(int id) {
        String sql = "SELECT * FROM venues WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVenue(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Venue> findAll() {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    @Override
    public List<Venue> findByCapacity(int minCapacity) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE capacity >= ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, minCapacity);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    @Override
    public Venue findByName(String name) {
        String sql = "SELECT * FROM venues WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVenue(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Venue save(Venue venue) {
        String sql = "INSERT INTO venues (name, description, city, capacity, type) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, venue.getName());
            stmt.setString(2, venue.getDescription());
            stmt.setString(3, venue.getCity());
            stmt.setInt(4, venue.getCapacity());
            stmt.setString(5, venue.getType());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        venue.setId(rs.getInt(1));
                        return venue;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Venue update(Venue venue) {
        String sql = "UPDATE venues SET name = ?, description = ?, city = ?, " +
                    "capacity = ?, type = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, venue.getName());
            stmt.setString(2, venue.getDescription());
            stmt.setString(3, venue.getCity());
            stmt.setInt(4, venue.getCapacity());
            stmt.setString(5, venue.getType());
            stmt.setInt(6, venue.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return venue;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM venues WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Venue> findByCapacityRange(int minCapacity, int maxCapacity) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE capacity >= ? AND capacity <= ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, minCapacity);
            stmt.setInt(2, maxCapacity);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    @Override
    public List<Venue> getAvailableVenuesForDate(LocalDate date) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT v.* FROM venues v " +
                    "WHERE v.id NOT IN (SELECT venue_id FROM events " +
                    "WHERE DATE(start_date) = ? AND status != 'CANCELLED')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    @Override
    public List<Venue> findByActive(boolean active) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE active = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, active);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    @Override
    public List<Venue> findByCity(String city) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE city = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, city);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM venues WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Venue> getActiveVenues() {
        return findByActive(true);
    }
    
    @Override
    public List<Venue> findByType(String type) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE type = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    @Override
    public List<Venue> search(String query) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE name LIKE ? OR description LIKE ? OR city LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                venues.add(mapResultSetToVenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    private Venue mapResultSetToVenue(ResultSet rs) throws SQLException {
        Venue venue = new Venue();
        venue.setId(rs.getInt("id"));
        venue.setName(rs.getString("name"));
        venue.setDescription(rs.getString("description"));
        venue.setCity(rs.getString("city"));
        venue.setCapacity(rs.getInt("capacity"));
        venue.setType(rs.getString("type"));
        venue.setActive(rs.getBoolean("active"));
        return venue;
    }
} 