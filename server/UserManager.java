import java.util.*;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UserManager handles user registration, tracking, and re-entry logic.
 * It maintains user history and provides enhanced user management features.
 * 
 * @author Ashok Kumar
 * @version 1.0
 */
public class UserManager {
    
    // Active users currently connected
    private final Map<String, PrintWriter> activeUsers = new HashMap<>();
    
    // User history and statistics
    private final Map<String, UserInfo> userHistory = new HashMap<>();
    
    // Synchronization lock
    private final Object lock = new Object();
    
    /**
     * Inner class to store user information
     */
    public static class UserInfo {
        private String username;
        private LocalDateTime firstConnection;
        private LocalDateTime lastConnection;
        private LocalDateTime lastDisconnection;
        private int totalConnections;
        private boolean isCurrentlyActive;
        
        public UserInfo(String username) {
            this.username = username;
            this.firstConnection = LocalDateTime.now();
            this.lastConnection = LocalDateTime.now();
            this.totalConnections = 1;
            this.isCurrentlyActive = true;
        }
        
        // Getters and setters
        public String getUsername() { return username; }
        public LocalDateTime getFirstConnection() { return firstConnection; }
        public LocalDateTime getLastConnection() { return lastConnection; }
        public LocalDateTime getLastDisconnection() { return lastDisconnection; }
        public int getTotalConnections() { return totalConnections; }
        public boolean isCurrentlyActive() { return isCurrentlyActive; }
        
        public void setLastConnection(LocalDateTime lastConnection) {
            this.lastConnection = lastConnection;
        }
        
        public void setLastDisconnection(LocalDateTime lastDisconnection) {
            this.lastDisconnection = lastDisconnection;
        }
        
        public void incrementConnections() {
            this.totalConnections++;
        }
        
        public void setCurrentlyActive(boolean active) {
            this.isCurrentlyActive = active;
        }
        
        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return String.format("User: %s, Connections: %d, First: %s, Last: %s", 
                username, totalConnections, 
                firstConnection.format(formatter),
                lastConnection.format(formatter));
        }
    }
    
    /**
     * Add a user to the server (handles both new and returning users)
     * 
     * @param username The username to add
     * @param writer The PrintWriter for the user
     * @return UserConnectionResult indicating the result
     */
    public UserConnectionResult addUser(String username, PrintWriter writer) {
        synchronized (lock) {
            // Check if user is currently active
            if (activeUsers.containsKey(username)) {
                return new UserConnectionResult(false, "Username '" + username + "' is already active", null);
            }
            
            // Add to active users
            activeUsers.put(username, writer);
            
            UserInfo userInfo;
            boolean isReturningUser = userHistory.containsKey(username);
            
            if (isReturningUser) {
                // Returning user
                userInfo = userHistory.get(username);
                userInfo.setLastConnection(LocalDateTime.now());
                userInfo.incrementConnections();
                userInfo.setCurrentlyActive(true);
                
                return new UserConnectionResult(true, "Welcome back, " + username + "! " +
                    "This is your connection #" + userInfo.getTotalConnections(), userInfo);
            } else {
                // New user
                userInfo = new UserInfo(username);
                userHistory.put(username, userInfo);
                
                return new UserConnectionResult(true, "Welcome to the chat, " + username + "! " +
                    "This is your first time here.", userInfo);
            }
        }
    }
    
    /**
     * Remove a user from active connections
     * 
     * @param username The username to remove
     * @return true if user was removed, false if not found
     */
    public boolean removeUser(String username) {
        synchronized (lock) {
            PrintWriter removed = activeUsers.remove(username);
            
            if (removed != null && userHistory.containsKey(username)) {
                UserInfo userInfo = userHistory.get(username);
                userInfo.setLastDisconnection(LocalDateTime.now());
                userInfo.setCurrentlyActive(false);
                return true;
            }
            
            return removed != null;
        }
    }
    
    /**
     * Get all active users
     * 
     * @return Map of active users
     */
    public Map<String, PrintWriter> getActiveUsers() {
        synchronized (lock) {
            return new HashMap<>(activeUsers);
        }
    }
    
    /**
     * Get user information
     * 
     * @param username The username to lookup
     * @return UserInfo if found, null otherwise
     */
    public UserInfo getUserInfo(String username) {
        synchronized (lock) {
            return userHistory.get(username);
        }
    }
    
    /**
     * Get all user history
     * 
     * @return Map of all user history
     */
    public Map<String, UserInfo> getAllUserHistory() {
        synchronized (lock) {
            return new HashMap<>(userHistory);
        }
    }
    
    /**
     * Get formatted list of connected users
     * 
     * @return Formatted string of active users
     */
    public String getConnectedUsersList() {
        synchronized (lock) {
            if (activeUsers.isEmpty()) {
                return "No users connected";
            }
            return String.join(", ", activeUsers.keySet());
        }
    }
    
    /**
     * Get server statistics
     * 
     * @return Formatted statistics string
     */
    public String getServerStats() {
        synchronized (lock) {
            StringBuilder stats = new StringBuilder();
            stats.append("=".repeat(40)).append("\n");
            stats.append("📊 SERVER STATISTICS\n");
            stats.append("=".repeat(40)).append("\n");
            stats.append("Active Users: ").append(activeUsers.size()).append("\n");
            stats.append("Total Users Ever: ").append(userHistory.size()).append("\n");
            
            if (!activeUsers.isEmpty()) {
                stats.append("Currently Online: ").append(String.join(", ", activeUsers.keySet())).append("\n");
            }
            
            // Most active user
            UserInfo mostActive = userHistory.values().stream()
                .max((u1, u2) -> Integer.compare(u1.getTotalConnections(), u2.getTotalConnections()))
                .orElse(null);
            
            if (mostActive != null) {
                stats.append("Most Active User: ").append(mostActive.getUsername())
                     .append(" (").append(mostActive.getTotalConnections()).append(" connections)\n");
            }
            
            stats.append("=".repeat(40));
            return stats.toString();
        }
    }
    
    /**
     * Check if username is currently active
     * 
     * @param username Username to check
     * @return true if active, false otherwise
     */
    public boolean isUserActive(String username) {
        synchronized (lock) {
            return activeUsers.containsKey(username);
        }
    }
    
    /**
     * Get count of active users
     * 
     * @return Number of active users
     */
    public int getActiveUserCount() {
        synchronized (lock) {
            return activeUsers.size();
        }
    }
    
    /**
     * Result class for user connection attempts
     */
    public static class UserConnectionResult {
        private final boolean success;
        private final String message;
        private final UserInfo userInfo;
        
        public UserConnectionResult(boolean success, String message, UserInfo userInfo) {
            this.success = success;
            this.message = message;
            this.userInfo = userInfo;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public UserInfo getUserInfo() { return userInfo; }
    }
}
