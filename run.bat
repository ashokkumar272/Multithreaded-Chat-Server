@echo off
REM Multithreaded Chat Server - Windows Batch Script
REM This script helps you easily compile and run the chat server

echo ========================================
echo   Multithreaded Chat Server - Java
echo ========================================
echo.

:menu
echo Please choose an option:
echo 1. Compile all Java files
echo 2. Start Chat Server (default port 12345)
echo 3. Start Chat Server (custom port)
echo 4. Start Test Client
echo 5. Show help
echo 6. Exit
echo.
set /p choice="Enter your choice (1-6): "

if "%choice%"=="1" goto compile
if "%choice%"=="2" goto server_default
if "%choice%"=="3" goto server_custom
if "%choice%"=="4" goto client
if "%choice%"=="5" goto help
if "%choice%"=="6" goto exit
echo Invalid choice! Please try again.
echo.
goto menu

:compile
echo.
echo Compiling Java files...
javac *.java
if %errorlevel%==0 (
    echo ✅ Compilation successful!
) else (
    echo ❌ Compilation failed!
)
echo.
pause
goto menu

:server_default
echo.
echo Starting Chat Server on port 12345...
echo Press Ctrl+C to stop the server
echo.
java ChatServer
pause
goto menu

:server_custom
echo.
set /p port="Enter port number (1024-65535): "
echo Starting Chat Server on port %port%...
echo Press Ctrl+C to stop the server
echo.
java ChatServer %port%
pause
goto menu

:client
echo.
echo Starting Test Client...
echo This will connect to localhost:12345 by default
echo.
java SimpleClient
pause
goto menu

:help
echo.
echo ========================================
echo             HELP INFORMATION
echo ========================================
echo.
echo This script helps you manage the Multithreaded Chat Server.
echo.
echo Prerequisites:
echo - Java JDK 8 or higher must be installed
echo - All Java files must be in the current directory
echo.
echo Usage Instructions:
echo 1. First, compile all Java files (option 1)
echo 2. Start the server (option 2 or 3)
echo 3. In separate terminal windows, start clients (option 4)
echo    or use: java SimpleClient [server] [port]
echo.
echo Chat Commands (when connected):
echo /help      - Show available commands
echo /list      - List all connected users
echo /whisper user message - Send private message
echo /exit      - Disconnect from server
echo.
echo Examples:
echo - java ChatServer 8080          (start server on port 8080)
echo - java SimpleClient localhost 8080  (connect to server on port 8080)
echo.
pause
goto menu

:exit
echo.
echo Goodbye! 👋
exit /b 0