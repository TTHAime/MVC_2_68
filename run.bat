@echo off
echo Running Emergency Shelter Assignment System...
echo.

REM ตรวจสอบว่า compile แล้วหรือยัง
if not exist "bin" (
    echo ERROR: Project not compiled yet!
    echo Please run compile.bat first.
    pause
    exit /b
)

REM ตรวจสอบว่ามี SQLite JDBC Driver หรือไม่
if not exist "lib\sqlite-jdbc-3.42.0.0.jar" (
    echo ERROR: SQLite JDBC Driver not found!
    echo.
    echo Please download sqlite-jdbc-3.42.0.0.jar from:
    echo https://github.com/xerial/sqlite-jdbc/releases
    echo.
    echo And place it in the lib/ folder
    pause
    exit /b
)

REM Run โปรแกรม
java -cp "bin;lib/*" Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================
    echo Program terminated with errors!
    echo ========================================
    pause
)
