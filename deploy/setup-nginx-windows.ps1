$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$FrontendRoot = (Join-Path $Root "deploy\artifacts\frontend") -replace '\\', '/'
$OutConf = Join-Path $PSScriptRoot "nginx\generated-windows.conf"

$ips = Get-NetIPAddress -AddressFamily IPv4 -ErrorAction SilentlyContinue |
    Where-Object { $_.IPAddress -notlike '127.*' -and $_.PrefixOrigin -ne 'WellKnown' } |
    Select-Object -ExpandProperty IPAddress

$conf = @"
# 其他电脑访问: http://<本机局域网IP>  （需放行防火墙 80 端口）
server {
    listen 80 default_server;
    server_name _;

    root $FrontendRoot;
    index index.html;

    client_max_body_size 50m;

    location / {
        try_files `$uri `$uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8090;
        proxy_http_version 1.1;
        proxy_set_header Host `$host;
        proxy_set_header X-Real-IP `$remote_addr;
        proxy_set_header X-Forwarded-For `$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto `$scheme;
        proxy_read_timeout 120s;
    }

    location /uploads/ {
        proxy_pass http://127.0.0.1:8090;
        proxy_set_header Host `$host;
    }
}
"@

Set-Content -Path $OutConf -Value $conf -Encoding UTF8

Write-Host "已生成: $OutConf"
Write-Host ""
Write-Host "在 nginx.conf 的 http {} 内加入:"
Write-Host "  include $OutConf;"
Write-Host ""
Write-Host "然后: nginx -t && nginx -s reload"
Write-Host ""
Write-Host "本机访问:   http://localhost"
if ($ips) {
    foreach ($ip in $ips) {
        Write-Host "局域网访问: http://$ip"
    }
    Write-Host ""
    Write-Host "若其他电脑无法访问，以管理员运行放行 80 端口:"
    Write-Host "  New-NetFirewallRule -DisplayName 'Nginx HTTP' -Direction Inbound -LocalPort 80 -Protocol TCP -Action Allow"
} else {
    Write-Host "局域网访问: http://<本机IP>  （ipconfig 查看 IPv4 地址）"
}
