import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * SimpleClient is a basic command-line client for testing the ChatServer.
 * It connects to the server and allows users to send messages and commands.
 * 
 * Features:
 * - Connect to chat server
 * - Send messages and receive responses
 * - Handle server messages in separate thread
 * - Graceful disconnection
 * 
 * @author Ashok Kumar
 * @version 1.0
 */
public class SimpleClient {
    
    private Socket socket;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;
    private Scanner userInput;
    private boolean isConnected;
    private String username;
    
    /**
     * Constructor for SimpleClient
     * 
     * @param serverAddress The server IP address
     * @param serverPort The server port number
     */
    public SimpleClient(String serverAddress, int serverPort) {
        try {
            // Connect to server
            socket = new Socket(serverAddress, serverPort);
            
            // Initialize streams
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput = new PrintWriter(socket.getOutputStream(), true);
            userInput = new Scanner(System.in);
            isConnected = true;
            
            System.out.println("✅ Connected to chat server at " + serverAddress + ":" + serverPort);
            System.out.println("=".repeat(60));
            
        } catch (IOException e) {
            System.err.println("❌ Failed to connect to server: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Start the client - handle user input and server messages
     */
    public void start() {
        // Start thread to listen for server messages
        Thread serverListener = new Thread(this::listenForServerMessages);
        serverListener.setDaemon(true);
        serverListener.start();
        
        // Handle user input in main thread
        handleUserInput();
    }
    
    /**
     * Listen for incoming messages from server (runs in separate thread)
     */
    private void listenForServerMessages() {
        try {
            String message;
            while (isConnected && (message = serverInput.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            if (isConnected) {
                System.err.println("❌ Lost connection to server: " + e.getMessage());
                disconnect();
            }
        }
    }
    
    /**
     * Handle user input from console
     */
    private void handleUserInput() {
        System.out.println("💬 You can start typing messages or commands:");
        System.out.println("   Type '/help' for available commands");
        System.out.println("   Type '/exit' to quit");
        System.out.println("-".repeat(60));
        
        try {
            while (isConnected && userInput.hasNextLine()) {
                String input = userInput.nextLine().trim();
                
                if (!input.isEmpty()) {
                    // Check for local commands
                    if (input.equals("/quit") || input.equals("/exit")) {
                        serverOutput.println("/exit");
                        break;
                    } else if (input.equals("/clear")) {
                        clearScreen();
                        continue;
                    }
                    
                    // Send message to server
                    serverOutput.println(input);
                    
                    // Check if server connection is still active
                    if (serverOutput.checkError()) {
                        System.err.println("❌ Connection to server lost!");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error reading user input: " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    /**
     * Clear the console screen (works on most terminals)
     */
    private void clearScreen() {
        try {
            // For Windows
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // For Unix/Linux/Mac
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // Fallback - print empty lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    /**
     * Disconnect from the server
     */
    private void disconnect() {
        isConnected = false;
        
        try {
            if (serverInput != null) serverInput.close();
            if (serverOutput != null) serverOutput.close();
            if (socket != null && !socket.isClosed()) socket.close();
            if (userInput != null) userInput.close();
        } catch (IOException e) {
            System.err.println("❌ Error during disconnect: " + e.getMessage());
        }
        
        System.out.println("\n👋 Disconnected from chat server. Goodbye!");
    }
    
    /**
     * Main method - entry point for the client application
     * 
     * @param args Command line arguments [server_address] [port]
     */
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Default server address
        int serverPort = 12345; // Default server port
        
        // Parse command line arguments
        if (args.length >= 1) {
            serverAddress = args[0];
        }
        if (args.length >= 2) {
            try {
                serverPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("❌ Invalid port number. Using default port 12345");
            }
        }
        
        System.out.println("🔌 Simple Chat Client");
        System.out.println("=".repeat(60));
        System.out.println("Connecting to: " + serverAddress + ":" + serverPort);
        
        // Create and start client
        SimpleClient client = new SimpleClient(serverAddress, serverPort);
        
        // Setup shutdown hook for graceful disconnection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Client shutting down...");
            client.disconnect();
        }));
        
        client.start();
    }
    
    /**
     * Show usage information
     */
    private static void showUsage() {
        System.out.println("Usage: java SimpleClient [server_address] [port]");
        System.out.println("Examples:");
        System.out.println("  java SimpleClient                    # Connect to localhost:12345");
        System.out.println("  java SimpleClient localhost 8080     # Connect to localhost:8080");
        System.out.println("  java SimpleClient 192.168.1.100      # Connect to 192.168.1.100:12345");
    }
}