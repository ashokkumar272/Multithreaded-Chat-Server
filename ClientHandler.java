import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * ClientHandler manages individual client connections in separate threads.
 * Each connected client gets its own ClientHandler instance that processes
 * incoming messages, handles commands, and communicates with the main server.
 * 
 * @author Ashok Kumar
 * @version 1.0
 */
public class ClientHandler implements Runnable {
    
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private ChatServer server;
    private boolean isConnected;
    
    /**
     * Constructor for ClientHandler
     * 
     * @param socket The client socket connection
     * @param server Reference to the main ChatServer
     */
    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.isConnected = true;
        
        try {
            // Initialize input and output streams
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error initializing client streams: " + e.getMessage());
            cleanup();
        }
    }
    
    /**
     * Main thread execution method
     */
    @Override
    public void run() {
        try {
            // Request username from client
            out.println("Welcome to the Chat Server!");
            out.println("Please enter your username:");
            
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                out.println("Invalid username. Disconnecting...");
                return;
            }
            
            username = username.trim();
            
            // Try to add user to server
            UserManager.UserConnectionResult result = server.addClient(username, out);
            if (!result.isSuccess()) {
                out.println(result.getMessage());
                return;
            }
            
            // Send personalized welcome message
            out.println(result.getMessage());
            
            // Notify all clients about new/returning user
            String joinMessage = result.getUserInfo().getTotalConnections() == 1 
                ? username + " has joined the chat for the first time!"
                : username + " has returned to the chat (connection #" + result.getUserInfo().getTotalConnections() + ")";
                
            server.broadcastMessage(new Message(Message.MessageType.SYSTEM, "Server", joinMessage));
            
            out.println("Type /help for available commands.");
            
            // Main message processing loop
            String inputLine;
            while (isConnected && (inputLine = in.readLine()) != null) {
                processMessage(inputLine.trim());
            }
            
        } catch (SocketException e) {
            // Client disconnected normally
            System.out.println("Client " + username + " disconnected normally");
        } catch (IOException e) {
            System.err.println("Error handling client " + username + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    /**
     * Process incoming message from client
     * 
     * @param message The raw message from client
     */
    private void processMessage(String message) {
        if (message.isEmpty()) {
            return;
        }
        
        // Handle commands
        if (message.startsWith("/")) {
            handleCommand(message);
        } else {
            // Regular broadcast message
            Message broadcastMsg = new Message(Message.MessageType.BROADCAST, username, message);
            server.broadcastMessage(broadcastMsg);
        }
    }
    
    /**
     * Handle client commands
     * 
     * @param command The command string
     */
    private void handleCommand(String command) {
        String[] parts = command.split("\\s+", 3);
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "/help":
                showHelp();
                break;
                
            case "/list":
                showUserList();
                break;
                
            case "/whisper":
                if (parts.length < 3) {
                    out.println("Usage: /whisper <username> <message>");
                } else {
                    handleWhisper(parts[1], parts[2]);
                }
                break;
                
            case "/whoami":
                showMyInfo();
                break;
                
            case "/stats":
                showServerStats();
                break;
                
            case "/exit":
            case "/quit":
                handleExit();
                break;
                
            default:
                out.println("Unknown command: " + cmd + ". Type /help for available commands.");
                break;
        }
    }
    
    /**
     * Show available commands to the client
     */
    private void showHelp() {
        out.println("Available commands:");
        out.println("/help - Show this help message");
        out.println("/list - Show list of connected users");
        out.println("/whisper <username> <message> - Send private message");
        out.println("/whoami - Show your connection info");
        out.println("/stats - Show server statistics");
        out.println("/exit - Disconnect from server");
    }
    
    /**
     * Show list of connected users
     */
    private void showUserList() {
        String userList = server.getConnectedUsers();
        out.println("Connected users: " + userList);
    }
    
    /**
     * Handle private message (whisper)
     * 
     * @param targetUser The recipient username
     * @param message The private message content
     */
    private void handleWhisper(String targetUser, String message) {
        if (targetUser.equals(username)) {
            out.println("You cannot send a private message to yourself!");
            return;
        }
        
        Message privateMsg = new Message(Message.MessageType.PRIVATE, username, targetUser, message);
        boolean sent = server.sendPrivateMessage(privateMsg);
        
        if (sent) {
            out.println("[WHISPER to " + targetUser + "]: " + message);
        } else {
            out.println("User '" + targetUser + "' not found or not connected.");
        }
    }
    
    /**
     * Show user's own connection information
     */
    private void showMyInfo() {
        UserManager.UserInfo userInfo = server.getUserInfo(username);
        if (userInfo != null) {
            out.println("=".repeat(40));
            out.println("Your Connection Information:");
            out.println("=".repeat(40));
            out.println("Username: " + userInfo.getUsername());
            out.println("Total connections: " + userInfo.getTotalConnections());
            out.println("First connected: " + userInfo.getFirstConnection());
            out.println("Current session started: " + userInfo.getLastConnection());
            if (userInfo.getLastDisconnection() != null) {
                out.println("Last disconnected: " + userInfo.getLastDisconnection());
            }
            out.println("Status: Currently active");
            out.println("=".repeat(40));
        } else {
            out.println("User information not available.");
        }
    }
    
    /**
     * Show server statistics
     */
    private void showServerStats() {
        server.printStats();
        // Send stats to this client
        out.println("📊 Server Statistics:");
        out.println("Active users: " + server.getActiveConnections());
        out.println("Connected users: " + server.getConnectedUsers());
    }
    
    /**
     * Handle client exit command
     */
    private void handleExit() {
        out.println("Goodbye " + username + "! You can reconnect anytime using the same username.");
        isConnected = false;
    }
    
    /**
     * Clean up resources and notify server of disconnection
     */
    private void cleanup() {
        try {
            if (username != null) {
                server.removeClient(username);
                server.broadcastMessage(new Message(Message.MessageType.SYSTEM, "Server", 
                    username + " has left the chat"));
            }
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            
        } catch (IOException e) {
            System.err.println("Error during cleanup for " + username + ": " + e.getMessage());
        }
    }
    
    /**
     * Get the username of this client
     * 
     * @return The client's username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Check if client is still connected
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }
}