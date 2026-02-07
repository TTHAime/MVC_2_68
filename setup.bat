@echo off
echo ========================================
echo  Emergency Shelter Assignment System
echo  Setup Script
echo ========================================
echo.

REM ตรวจสอบว่ามี lib folder หรือยัง
if not exist "lib" (
    echo Creating lib directory...
    mkdir lib
)

REM ดาวน์โหลด SQLite JDBC Driver
echo Step 1/2: Downloading SQLite JDBC Driver...
powershell -ExecutionPolicy Bypass -File "download-jdbc.ps1"

echo.
echo Step 2/2: Compiling Java project...
call compile.bat

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo To run the program, execute:
echo   run.bat
echo.
pause
