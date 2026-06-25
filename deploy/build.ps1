$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$OutDir = Join-Path $PSScriptRoot "artifacts"

Write-Host "==> 构建前端..."
Push-Location (Join-Path $Root "frontend")
if (-not (Test-Path "node_modules")) {
    npm install
}
npm run build
Pop-Location

Write-Host "==> 构建后端..."
Push-Location (Join-Path $Root "backend")
mvn -DskipTests package
Pop-Location

Write-Host "==> 复制产物到 deploy/artifacts ..."
if (Test-Path $OutDir) {
    Remove-Item $OutDir -Recurse -Force
}
New-Item -ItemType Directory -Path $OutDir -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $OutDir "frontend") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $OutDir "uploads") -Force | Out-Null

Copy-Item -Path (Join-Path $Root "frontend\dist\*") -Destination (Join-Path $OutDir "frontend") -Recurse
Copy-Item -Path (Join-Path $Root "backend\target\secondhand-1.0.0.jar") -Destination $OutDir

Write-Host ""
Write-Host "构建完成: $OutDir"
Write-Host "  - frontend/   静态文件，给 Nginx root 使用"
Write-Host "  - secondhand-1.0.0.jar"
Write-Host ""
Write-Host "下一步 (Windows 本机):"
Write-Host "  1. 初始化数据库: mysql -uroot -p < sql\init.sql"
Write-Host "  2. 启动后端:"
Write-Host "     `$env:SPRING_PROFILES_ACTIVE='prod'"
Write-Host "     java -jar deploy\artifacts\secondhand-1.0.0.jar"
Write-Host "  3. 将 deploy\nginx\campus-secondhand-windows.conf 中的 root 改为:"
Write-Host "     $OutDir\frontend"
Write-Host "  4. 启动 Nginx 并访问 http://localhost"
