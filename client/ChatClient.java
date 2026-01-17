import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private boolean isConnected;
    private String username;
    
    // private static final String SERVER_IP = "192.168.56.1";
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;
    
    public ChatClient() {
        this.scanner = new Scanner(System.in);
        this.isConnected = false;
    }
    
    public boolean connect() {
        try {
            System.out.println("Connecting to " + SERVER_IP + ":" + SERVER_PORT);
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            isConnected = true;
            System.out.println("Connected!\n");
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_IP);
            return false;
        } catch (ConnectException e) {
            System.err.println("Connection refused. Check server is running.");
            return false;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }
    
    public void start() {
        if (!connect()) return;
        
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
                if (serverMessage.contains("Please enter your username:")) {
                    break;
                }
            }
            
            System.out.print("Username: ");
            username = scanner.nextLine().trim();
            
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty!");
                disconnect();
                return;
            }
            
            out.println(username);
            String response = in.readLine();
            System.out.println(response);
            
            if (response.contains("rejected") || response.contains("Disconnecting")) {
                disconnect();
                return;
            }
            
            Thread listenerThread = new Thread(new ServerListener());
            listenerThread.setDaemon(true);
            listenerThread.start();
            
            System.out.println("\nAll messages are broadcast to everyone. Start chatting!\n");
            handleUserInput();
            
        } catch (IOException e) {
            System.err.println("Chat error: " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    private void handleUserInput() {
        while (isConnected) {
            try {
                if (scanner.hasNextLine()) {
                    // System.out.print("[" + username + "]: ");
                    String message = scanner.nextLine().trim();
                    
                    if (message.isEmpty()) continue;
                    
                    out.println(message);
                    
                    if (out.checkError()) {
                        System.err.println("\nConnection lost!");
                        isConnected = false;
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("\nInput error: " + e.getMessage());
                isConnected = false;
                break;
            }
        }
    }
    
    private void disconnect() {
        isConnected = false;
        try {
            System.out.println("\nDisconnecting...");
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Disconnected!");
        } catch (IOException e) {
            System.err.println("Disconnect error: " + e.getMessage());
        }
    }
    
    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while (isConnected && (serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                    if (serverMessage.contains("Server is shutting down")) {
                        isConnected = false;
                        break;
                    }
                }
                if (isConnected) {
                    System.out.println("\nServer closed unexpectedly!");
                    isConnected = false;
                }
            } catch (SocketException e) {
                if (isConnected) {
                    System.err.println("\nConnection lost!");
                    isConnected = false;
                }
            } catch (IOException e) {
                if (isConnected) {
                    System.err.println("\nReceive error: " + e.getMessage());
                    isConnected = false;
                }
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("\nCHAT CLIENT\n");
        ChatClient client = new ChatClient();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> client.disconnect()));
        client.start();
    }
}
