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
            
            out.println("You can now send messages. All messages are broadcast to everyone.");
            
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
        
        // All messages are broadcast messages
        Message broadcastMsg = new Message(Message.MessageType.BROADCAST, username, message);
        server.broadcastMessage(broadcastMsg);
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
