$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$Jar = Join-Path $PSScriptRoot "artifacts\secondhand-1.0.0.jar"

if (-not (Test-Path $Jar)) {
    Write-Host "未找到 $Jar，请先运行: .\deploy\build.ps1"
    exit 1
}

$UploadDir = Join-Path $PSScriptRoot "artifacts\uploads"
New-Item -ItemType Directory -Path $UploadDir -Force | Out-Null

$env:SPRING_PROFILES_ACTIVE = "prod"
$env:UPLOAD_PATH = $UploadDir
$env:DB_HOST = if ($env:DB_HOST) { $env:DB_HOST } else { "127.0.0.1" }
$env:DB_USERNAME = if ($env:DB_USERNAME) { $env:DB_USERNAME } else { "root" }
$env:DB_PASSWORD = if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "123456" }

Write-Host "启动后端 (prod) ..."
Write-Host "  UPLOAD_PATH=$env:UPLOAD_PATH"
java -jar $Jar
