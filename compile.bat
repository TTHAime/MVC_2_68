@echo off
echo Compiling Java project...
echo.

REM สร้าง directory สำหรับ compiled classes
if not exist "bin" mkdir bin

REM Compile โปรเจกต์ (รองรับ Java 8)
javac -source 8 -target 8 -encoding UTF-8 -d bin -cp "lib/*;." src/model/*.java src/database/*.java src/controller/*.java src/view/*.java src/Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilation successful!
    echo ========================================
    echo.
    echo To run the program, execute: run.bat
) else (
    echo.
    echo ========================================
    echo Compilation failed! Please check errors above.
    echo ========================================
)

pause
