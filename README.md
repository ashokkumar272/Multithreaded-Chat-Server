# 🚀 Multithreaded Chat Server in Java

A **concurrent real-time chat server** built in Java using sockets and multithreading for efficient client communication. This project demonstrates advanced Java networking, thread synchronization, and object-oriented programming principles.

## 🎯 Project Overview

This chat server can handle **multiple client connections simultaneously** using dedicated threads for each client. It features thread-safe operations and broadcast messaging where all messages are sent to all connected users.

### 🧠 Key Learning Objectives

- **Core Java Networking**: ServerSocket, Socket, I/O streams
- **Multithreading**: Thread creation, Runnable interface, thread lifecycle
- **Synchronization**: synchronized blocks, thread-safe collections (ConcurrentHashMap)
- **Socket Programming**: Client-server communication, connection management
- **Clean Architecture**: Separation of concerns, OOP design patterns

## 🏗️ Architecture

The project is organized into server and client folders:

```
📁 Multithreaded-Chat-Server/
├── 📁 server/
│   ├── 📄 ChatServer.java      → Main server class, manages client threads
│   ├── 📄 ClientHandler.java   → Handles individual client communication  
│   ├── 📄 UserManager.java     → Manages user connections and history
│   └── 📄 Message.java         → Message data structure (POJO)
├── 📁 client/
│   ├── 📄 ChatClient.java      → Client application
│   └── 📄 Message.java         → Message data structure (POJO)
└── 📄 README.md               → This documentation
```

### 🔧 Core Components

**Server Side:**
1. **ChatServer.java**: Main entry point, accepts connections, spawns client threads
2. **ClientHandler.java**: Manages individual client sessions in separate threads  
3. **UserManager.java**: Handles user registration, tracking, and re-entry logic
4. **Message.java**: Data structure for message types (broadcast and system)

**Client Side:**
1. **ChatClient.java**: Client application for connecting to the server and sending messages
2. **Message.java**: Shared message data structure

## ✨ Features

### 🌐 Server Features
- ✅ **Concurrent client handling** with dedicated threads
- ✅ **Thread-safe client management** using synchronized blocks
- ✅ **Broadcast messaging** to all connected users
- ✅ **User re-entry support** - existing users can reconnect seamlessly
- ✅ **Connection history tracking** with personalized welcome messages
- ✅ **Graceful client disconnection** handling
- ✅ **Server statistics** and logging
- ✅ **Shutdown hooks** for clean server termination

### 💬 Chat Mode
All messages are **broadcast to everyone**. Simply type your message and press Enter - no commands needed!

- Messages appear to all connected users except the sender
- System notifications for user join/leave events
- Simple and straightforward chat experience

### 🔒 Thread Safety
- **Synchronized blocks** for critical operations
- **Thread-safe user management** via UserManager
- **Atomic operations** for client add/remove
- **Safe message broadcasting** without race conditions

### 🔄 User Re-entry Support
- **Seamless Reconnection**: Users can disconnect and reconnect with the same username
- **Connection History**: Server tracks user connection statistics and timestamps
- **Personalized Welcome**: Different messages for new vs. returning users
- **Connection Tracking**: Server maintains history of all user connections

## 🚀 Quick Start

### Prerequisites
- Java JDK 8 or higher
- Command line interface (Terminal, PowerShell, CMD)

### 1. Compile the Project

```bash
# Navigate to project directory
cd Multithreaded-Chat-Server

# Compile server files
cd server
javac *.java

# Compile client files
cd ../client
javac *.java
cd ..
```

### 2. Start the Server

```bash
# Navigate to server directory
cd server

# Start server on default port 12345
java ChatServer

# Or specify a custom port
java ChatServer 8080
```

**Expected Output:**
```
==================================================
Chat Server Started Successfully!
Listening on port: 12345
Server started at: Fri Jan 17 10:30:45 EST 2026
==================================================
Waiting for client connections...
```

### 3. Connect Clients

#### Using the Chat Client
```bash
# In a new terminal window, navigate to client directory
cd client

# Run the client
java ChatClient

# Follow the prompts to enter your username and start chatting
```

**Note:** Update the `SERVER_IP` constant in ChatClient.java to match your server's IP address (default is 192.168.56.1).

#### Alternative: Using Telnet (Windows)
```bash
telnet localhost 12345
```

#### Alternative: Using Netcat (Linux/Mac)
```bash
nc localhost 12345
```

## 📋 Usage Examples

### Example 1: Basic Chat Session

1. **Start the server:**
   ```bash
   cd server
   java ChatServer
   ```

2. **Connect first client:**
   ```bash
   cd client
   java ChatClient
   # Enter username: Alice
   ```

3. **Connect second client (in another terminal):**
   ```bash
   cd client
   java ChatClient
   # Enter username: Bob
   ```

4. **Chat conversation:**
   ```
   [Alice]: Hello everyone!
   # Output on Bob's screen: Alice: Hello everyone!
   
   [Bob]: Hi Alice! How are you?
   # Output on Alice's screen: Bob: Hi Alice! How are you?
   
   [Alice]: Great to chat with you!
   # Output on Bob's screen: Alice: Great to chat with you!
   ```

### Example 2: Multiple Clients Broadcasting

```bash
# Terminal 1 - Server
cd server
java ChatServer

# Terminal 2 - Client 1 (Alice)
cd client
java ChatClient
# Username: Alice
[Alice]: Hello chat room!

# Terminal 3 - Client 2 (Bob)
cd client
java ChatClient
# Username: Bob
# Sees: [SYSTEM] Alice has joined the chat for the first time!
[Bob]: Hi everyone!

# Terminal 4 - Client 3 (Charlie)
cd client
java ChatClient
# Username: Charlie
# Sees: [SYSTEM] Alice has joined...
# Sees: [SYSTEM] Bob has joined...
[Charlie]: Hey folks!

# All messages are broadcast to everyone except the sender
```

## 🔍 Testing the Server

### Manual Testing Checklist

- [ ] **Multiple clients can connect simultaneously**
- [ ] **Messages are broadcasted to all users except sender**
- [ ] **System messages appear for user join/leave events**
- [ ] **Returning users get personalized welcome messages**
- [ ] **Client disconnection is handled gracefully**
- [ ] **Server handles malformed input without crashing**
- [ ] **Thread-safe operations (no race conditions)**

### Load Testing
```bash
# Test with multiple simultaneous connections
cd client
for i in {1..10}; do
    java ChatClient &
done
```

## 🛠️ Advanced Configuration

### Custom Port Configuration

**Server:**
```bash
cd server
java ChatServer 9999
```

**Client:**

Edit `ChatClient.java` and change the `SERVER_PORT` constant:
```java
private static final int SERVER_PORT = 9999;
```

Then recompile and run:
```bash
cd client
javac ChatClient.java
java ChatClient
```

### Server Monitoring
The server provides real-time statistics:
- Active connections count
- Total connections served
- Connected usernames
- Message broadcasting logs
- Connection/disconnection events

## 🐛 Troubleshooting

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| `Address already in use` | Port is busy | Use different port or kill existing process |
| `Connection refused` | Server not running | Start server first |
| `Cannot resolve hostname` | Invalid server address | Check server IP/hostname |
| Compilation errors | Missing Java files | Ensure all .java files are present |

### Debug Mode
Add debug output by modifying the server:
```java
// Add to ChatServer.java main method
System.setProperty("java.util.logging.ConsoleHandler.level", "ALL");
```

## 🔒 Security Considerations

- **Input Validation**: All user inputs are sanitized
- **Resource Management**: Proper cleanup of sockets and streams
- **Thread Safety**: No race conditions or deadlocks
- **Graceful Shutdown**: Clean disconnection handling

## 🎓 Learning Outcomes

After completing this project, you'll understand:

1. **Socket Programming**: Client-server communication patterns
2. **Multithreading**: Concurrent programming and thread management
3. **Synchronization**: Thread-safe operations and data protection
4. **Network Protocols**: TCP/IP communication fundamentals
5. **Resource Management**: Proper cleanup and memory management
6. **Error Handling**: Robust exception handling and recovery

## 🚀 Future Enhancements

Potential improvements for advanced learning:

- [ ] **GUI Client** using JavaFX or Swing
- [ ] **Private Messaging** between specific users
- [ ] **Chat Commands** (/help, /list, /whisper, etc.)
- [ ] **File Transfer** capability between clients  
- [ ] **Chat Rooms** and channel management
- [ ] **User Authentication** and registration
- [ ] **Message History** persistence with database
- [ ] **SSL/TLS Encryption** for secure communication
- [ ] **Web-based Client** using WebSockets
- [ ] **Load Balancing** for high-traffic scenarios

## 📄 License

This project is created for educational purposes. Feel free to use and modify for learning Java networking and multithreading concepts.

---

**Built with ❤️ using Java | Demonstrating Multithreading & Socket Programming**
