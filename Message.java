/**
 * Message class represents a chat message with its type, sender, and content.
 * This POJO helps structure the communication protocol between clients and server.
 * 
 * @author Ashok Kumar
 * @version 1.0
 */
public class Message {
    
    public enum MessageType {
        BROADCAST,    // Regular message to all users
        PRIVATE,      // Private message to specific user
        COMMAND,      // Command messages (/list, /exit)
        SYSTEM        // System notifications (user joined/left)
    }
    
    private MessageType type;
    private String sender;
    private String recipient; // For private messages
    private String content;
    private long timestamp;
    
    /**
     * Constructor for broadcast and system messages
     */
    public Message(MessageType type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Constructor for private messages
     */
    public Message(MessageType type, String sender, String recipient, String content) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        switch (type) {
            case PRIVATE:
                return String.format("[PRIVATE] %s -> %s: %s", sender, recipient, content);
            case SYSTEM:
                return String.format("[SYSTEM] %s", content);
            case BROADCAST:
            default:
                return String.format("%s: %s", sender, content);
        }
    }
}