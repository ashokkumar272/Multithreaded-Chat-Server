# 🚀 Multithreaded Chat Server in Java

A **concurrent real-time chat server** built in Java using sockets and multithreading for efficient client communication. This project demonstrates advanced Java networking, thread synchronization, and object-oriented programming principles.

## 🎯 Project Overview

This chat server can handle **multiple client connections simultaneously** using dedicated threads for each client. It features thread-safe operations, message broadcasting, private messaging, and a clean command-based interface.

### 🧠 Key Learning Objectives

- **Core Java Networking**: ServerSocket, Socket, I/O streams
- **Multithreading**: Thread creation, Runnable interface, thread lifecycle
- **Synchronization**: synchronized blocks, thread-safe collections (ConcurrentHashMap)
- **Socket Programming**: Client-server communication, connection management
- **Clean Architecture**: Separation of concerns, OOP design patterns

## 🏗️ Architecture

The project consists of four main components:

```
📁 Multithreaded-Chat-Server/
├── 📄 ChatServer.java      → Main server class, manages client threads
├── 📄 ClientHandler.java   → Handles individual client communication  
├── 📄 Message.java         → Message data structure (POJO)
├── 📄 SimpleClient.java    → Test client for server validation
└── 📄 README.md           → This documentation
```

### 🔧 Core Components

1. **ChatServer.java**: Main entry point, accepts connections, spawns client threads
2. **ClientHandler.java**: Manages individual client sessions in separate threads  
3. **Message.java**: Data structure for different message types (broadcast, private, system)
4. **SimpleClient.java**: Command-line client for testing the server

## ✨ Features

### 🌐 Server Features
- ✅ **Concurrent client handling** with dedicated threads
- ✅ **Thread-safe client management** using synchronized blocks
- ✅ **Message broadcasting** to all connected users
- ✅ **Private messaging** between users
- ✅ **User re-entry support** - existing users can reconnect seamlessly
- ✅ **Connection history tracking** with personalized welcome messages
- ✅ **Enhanced command processing** (/help, /list, /whisper, /whoami, /stats, /exit)
- ✅ **Graceful client disconnection** handling
- ✅ **Server statistics** and logging
- ✅ **Shutdown hooks** for clean server termination

### 💬 Chat Commands
| Command | Description | Example |
|---------|-------------|---------|
| `/help` | Show available commands | `/help` |
| `/list` | List all connected users | `/list` |
| `/whisper <user> <message>` | Send private message | `/whisper john Hello there!` |
| `/whoami` | Show your connection info | `/whoami` |
| `/stats` | Show server statistics | `/stats` |
| `/exit` | Disconnect from server | `/exit` |

### 🔒 Thread Safety
- **ConcurrentHashMap** for client storage
- **Synchronized blocks** for critical operations
- **Atomic operations** for client add/remove
- **Safe message broadcasting** without race conditions

### 🔄 User Re-entry Support
- **Seamless Reconnection**: Users can disconnect and reconnect with the same username
- **Connection History**: Server tracks user connection statistics and timestamps
- **Personalized Welcome**: Different messages for new vs. returning users
- **User Information**: `/whoami` command shows personal connection details
- **Server Statistics**: `/stats` command displays server-wide user analytics

## 🚀 Quick Start

### Prerequisites
- Java JDK 8 or higher
- Command line interface (Terminal, PowerShell, CMD)

### 1. Compile the Project

```bash
# Navigate to project directory
cd Multithreaded-Chat-Server

# Compile all Java files
javac *.java
```

### 2. Start the Server

```bash
# Start server on default port 12345
java ChatServer

# Or specify a custom port
java ChatServer 8080
```

**Expected Output:**
```
==================================================
🚀 Chat Server Started Successfully!
📡 Listening on port: 12345
⏰ Server started at: Wed Oct 23 10:30:45 EST 2025
==================================================
Waiting for client connections...
```

### 3. Connect Clients

#### Option A: Using the Provided Test Client
```bash
# In a new terminal window
java SimpleClient

# Or connect to specific server
java SimpleClient localhost 8080
```

#### Option B: Using Telnet (Windows)
```bash
telnet localhost 12345
```

#### Option C: Using Netcat (Linux/Mac)
```bash
nc localhost 12345
```

## 📋 Usage Examples

### Example 1: Basic Chat Session

1. **Start the server:**
   ```bash
   java ChatServer
   ```

2. **Connect first client:**
   ```bash
   java SimpleClient
   # Enter username: Alice
   ```

3. **Connect second client:**
   ```bash
   java SimpleClient  
   # Enter username: Bob
   ```

4. **Chat conversation:**
   ```
   Alice: Hello everyone!
   Bob: Hi Alice! How are you?
   Alice: /whisper Bob I'm doing great, thanks!
   Bob: /list
   Server: Connected users: Alice, Bob
   ```

### Example 2: Multiple Clients with Commands

```bash
# Terminal 1 - Server
java ChatServer 12345

# Terminal 2 - Client 1
java SimpleClient localhost 12345
# Username: Alice
Alice: Hello chat room!
Alice: /list
Alice: /whisper Bob Hey Bob, private message!

# Terminal 3 - Client 2  
java SimpleClient localhost 12345
# Username: Bob
Bob: Hi Alice!
Bob: /help
Bob: /exit

# Terminal 4 - Client 3
java SimpleClient localhost 12345
# Username: Charlie
Charlie: Anyone there?
Charlie: /list
```

## 🔍 Testing the Server

### Manual Testing Checklist

- [ ] **Multiple clients can connect simultaneously**
- [ ] **Messages are broadcasted to all users except sender**
- [ ] **Private messages work correctly**
- [ ] **User list shows all connected clients**
- [ ] **Client disconnection is handled gracefully**
- [ ] **Server handles malformed input without crashing**
- [ ] **Thread-safe operations (no race conditions)**

### Load Testing
```bash
# Test with multiple simultaneous connections
for i in {1..10}; do
    java SimpleClient &
done
```

## 🛠️ Advanced Configuration

### Custom Port Configuration
```bash
# Server
java ChatServer 9999

# Client  
java SimpleClient localhost 9999
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
