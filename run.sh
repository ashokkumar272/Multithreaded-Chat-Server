#!/bin/bash

# Multithreaded Chat Server - Shell Script
# This script helps you easily compile and run the chat server

echo "========================================"
echo "  Multithreaded Chat Server - Java"
echo "========================================"
echo

show_menu() {
    echo "Please choose an option:"
    echo "1. Compile all Java files"
    echo "2. Start Chat Server (default port 12345)"
    echo "3. Start Chat Server (custom port)"
    echo "4. Start Test Client"
    echo "5. Show help"
    echo "6. Exit"
    echo
}

compile_files() {
    echo
    echo "Compiling Java files..."
    javac *.java
    if [ $? -eq 0 ]; then
        echo "✅ Compilation successful!"
    else
        echo "❌ Compilation failed!"
    fi
    echo
    read -p "Press Enter to continue..."
}

start_server_default() {
    echo
    echo "Starting Chat Server on port 12345..."
    echo "Press Ctrl+C to stop the server"
    echo
    java ChatServer
    read -p "Press Enter to continue..."
}

start_server_custom() {
    echo
    read -p "Enter port number (1024-65535): " port
    echo "Starting Chat Server on port $port..."
    echo "Press Ctrl+C to stop the server"
    echo
    java ChatServer $port
    read -p "Press Enter to continue..."
}

start_client() {
    echo
    echo "Starting Test Client..."
    echo "This will connect to localhost:12345 by default"
    echo
    java SimpleClient
    read -p "Press Enter to continue..."
}

show_help() {
    echo
    echo "========================================"
    echo "           HELP INFORMATION"
    echo "========================================"
    echo
    echo "This script helps you manage the Multithreaded Chat Server."
    echo
    echo "Prerequisites:"
    echo "- Java JDK 8 or higher must be installed"
    echo "- All Java files must be in the current directory"
    echo
    echo "Usage Instructions:"
    echo "1. First, compile all Java files (option 1)"
    echo "2. Start the server (option 2 or 3)"
    echo "3. In separate terminal windows, start clients (option 4)"
    echo "   or use: java SimpleClient [server] [port]"
    echo
    echo "Chat Commands (when connected):"
    echo "/help      - Show available commands"
    echo "/list      - List all connected users"
    echo "/whisper user message - Send private message"
    echo "/exit      - Disconnect from server"
    echo
    echo "Examples:"
    echo "- java ChatServer 8080          (start server on port 8080)"
    echo "- java SimpleClient localhost 8080  (connect to server on port 8080)"
    echo
    read -p "Press Enter to continue..."
}

# Main loop
while true; do
    show_menu
    read -p "Enter your choice (1-6): " choice
    
    case $choice in
        1) compile_files ;;
        2) start_server_default ;;
        3) start_server_custom ;;
        4) start_client ;;
        5) show_help ;;
        6) echo -e "\nGoodbye! 👋"; exit 0 ;;
        *) echo "Invalid choice! Please try again."; echo ;;
    esac
done