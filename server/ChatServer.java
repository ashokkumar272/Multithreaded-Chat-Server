import java.io.*;
import java.net.*;
import java.util.*;

/**
 * ChatServer is the main server class that manages multiple client connections
 * concurrently using threads. It provides thread-safe operations for client
 * management and message broadcasting.
 * 
 * Key features:
 * - Concurrent client handling using threads
 * - Thread-safe client management with synchronized blocks
 * - Message broadcasting to all connected clients
 * - Enhanced user management with re-entry support
 * 
 * @author Ashok Kumar
 * @version 1.0
 */
public class ChatServer {
    
    // Enhanced user manager for handling connections and history
    private final UserManager userManager = new UserManager();
    
    // Synchronization object for critical operations
    private static final Object lock = new Object();
    
    private ServerSocket serverSocket;
    private boolean isRunning;
    private int port;
    
    // Statistics
    private int totalConnections = 0;
    
    /**
     * Constructor for ChatServer
     * 
     * @param port The port number to listen on
     */
    public ChatServer(int port) {
        this.port = port;
        this.isRunning = false;
    }
    
    /**
     * Start the chat server
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            
            System.out.println("=".repeat(50));
            System.out.println("Chat Server Started Successfully!");
            System.out.println("Listening on port: " + port);
            System.out.println("=".repeat(50));
            
            // Main server loop - accept client connections
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    totalConnections++;
                    
                    String clientAddress = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("New connection #" + totalConnections + " from " + clientAddress + ":" + clientSocket.getPort());
                    
                    // Create and start new thread for each client
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.setName("Client-" + totalConnections);
                    clientThread.start();
                    
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Failed to start server on port " + port + ": " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    /**
     * Stop the chat server
     */
    public void stop() {
        isRunning = false;
        
        try {
            // Notify all clients about server shutdown
            synchronized (lock) {
                Message shutdownMsg = new Message(Message.MessageType.SYSTEM, "Server", 
                    "Server is shutting down. Goodbye!");
                Map<String, PrintWriter> activeUsers = userManager.getActiveUsers();
                for (PrintWriter writer : activeUsers.values()) {
                    writer.println(shutdownMsg.toString());
                }
            }
            
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Chat Server Stopped");
            System.out.println("Total connections served: " + totalConnections);
            System.out.println("=".repeat(50));
            
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
    
    /**
     * Add a new client to the server (thread-safe)
     * 
     * @param username The client's username
     * @param writer The client's PrintWriter for sending messages
     * @return UserManager.UserConnectionResult with connection details
     */
    public UserManager.UserConnectionResult addClient(String username, PrintWriter writer) {
        UserManager.UserConnectionResult result = userManager.addUser(username, writer);
        if (result.isSuccess()) {
            System.out.println("User '" + username + "' connected. Active users: " + userManager.getActiveUserCount());
        } else {
            System.out.println("Connection rejected for '" + username + "': " + result.getMessage());
        }
        return result;
    }
    
    /**
     * Remove a client from the server (thread-safe)
     * 
     * @param username The username to remove
     */
    public void removeClient(String username) {
        if (userManager.removeUser(username)) {
            System.out.println("User '" + username + "' disconnected. Active users: " + userManager.getActiveUserCount());
        }
    }
    
    /**
     * Check if a username is already taken (thread-safe)
     * 
     * @param username The username to check
     * @return true if taken, false otherwise
     */
    public boolean isUsernameTaken(String username) {
        return userManager.isUserActive(username);
    }
    
    /**
     * Broadcast a message to all connected clients (thread-safe)
     * 
     * @param message The message to broadcast
     */
    public void broadcastMessage(Message message) {
        synchronized (lock) {
            String formattedMessage = message.toString();
            
            // Create a list to avoid ConcurrentModificationException
            List<String> disconnectedClients = new ArrayList<>();
            
            Map<String, PrintWriter> activeUsers = userManager.getActiveUsers();
            for (Map.Entry<String, PrintWriter> entry : activeUsers.entrySet()) {
                String username = entry.getKey();
                PrintWriter writer = entry.getValue();
                
                // Skip sending message back to sender (except for system messages)
                if (message.getType() != Message.MessageType.SYSTEM && 
                    username.equals(message.getSender())) {
                    continue;
                }
                
                try {
                    writer.println(formattedMessage);
                    
                    // Check if writer encountered an error
                    if (writer.checkError()) {
                        disconnectedClients.add(username);
                    }
                } catch (Exception e) {
                    disconnectedClients.add(username);
                }
            }
            
            // Remove disconnected clients
            for (String username : disconnectedClients) {
                userManager.removeUser(username);
                System.out.println("Removed disconnected user: " + username);
            }
        }
        
        // Log broadcast messages (not private or system messages)
        if (message.getType() == Message.MessageType.BROADCAST) {
            System.out.println(message.getSender() + ": " + message.getContent());
        }
    }
    

    
    /**
     * Get a comma-separated list of connected users (thread-safe)
     * 
     * @return String containing all connected usernames
     */
    public String getConnectedUsers() {
        return userManager.getConnectedUsersList();
    }
    
    /**
     * Get the number of connected clients (thread-safe)
     * 
     * @return Number of active connections
     */
    public int getActiveConnections() {
        return userManager.getActiveUserCount();
    }
    
    /**
     * Get user information
     * 
     * @param username The username to lookup
     * @return UserInfo if found, null otherwise
     */
    public UserManager.UserInfo getUserInfo(String username) {
        return userManager.getUserInfo(username);
    }
    
    /**
     * Main method - entry point of the application
     * 
     * @param args Command line arguments (optional port number)
     */
    public static void main(String[] args) {
        int port = 12345; // Default port
        
        // Parse command line arguments
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1024 || port > 65535) {
                    System.err.println("Invalid port number. Using default port 12345");
                    port = 12345;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid port format. Using default port 12345");
            }
        }
        
        ChatServer server = new ChatServer(port);
        
        // Setup shutdown hook for graceful server shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n Shutdown signal received. Stopping server...");
            server.stop();
        }));
        
        // Start the server
        server.start();
    }
    
    /**
     * Print server statistics
     */
    public void printStats() {
        System.out.println(userManager.getServerStats());
        System.out.println("Total connections: " + totalConnections);
        System.out.println("Server port: " + port);
    }
}
