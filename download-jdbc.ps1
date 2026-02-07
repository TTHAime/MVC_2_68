# สคริปต์สำหรับดาวน์โหลด SQLite JDBC Driver อัตโนมัติ
# Download SQLite JDBC Driver

Write-Host "Downloading SQLite JDBC Driver..." -ForegroundColor Green

# สร้างโฟลเดอร์ lib ถ้ายังไม่มี
if (-not (Test-Path "lib")) {
    New-Item -ItemType Directory -Path "lib" | Out-Null
}

# URL สำหรับดาวน์โหลด
$url = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar"
$output = "lib\sqlite-jdbc-3.42.0.0.jar"

# ตรวจสอบว่ามีไฟล์อยู่แล้วหรือไม่
if (Test-Path $output) {
    Write-Host "SQLite JDBC Driver already exists!" -ForegroundColor Yellow
    exit
}

# ดาวน์โหลด
try {
    Invoke-WebRequest -Uri $url -OutFile $output
    Write-Host "Download completed successfully!" -ForegroundColor Green
    Write-Host "File saved to: $output" -ForegroundColor Cyan
} catch {
    Write-Host "Download failed: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please manually download from:" -ForegroundColor Yellow
    Write-Host $url -ForegroundColor Cyan
    Write-Host "And place it in the lib/ folder" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
