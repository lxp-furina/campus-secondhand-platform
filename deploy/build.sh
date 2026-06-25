#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT/deploy/artifacts"

echo "==> 构建前端..."
cd "$ROOT/frontend"
npm install
npm run build

echo "==> 构建后端..."
cd "$ROOT/backend"
mvn -DskipTests package

echo "==> 复制产物到 deploy/artifacts ..."
rm -rf "$OUT"
mkdir -p "$OUT/frontend" "$OUT/uploads"
cp -r "$ROOT/frontend/dist/"* "$OUT/frontend/"
cp "$ROOT/backend/target/secondhand-1.0.0.jar" "$OUT/"

echo ""
echo "构建完成: $OUT"
echo "  - frontend/   静态文件 -> /var/www/campus-secondhand/frontend"
echo "  - secondhand-1.0.0.jar -> /opt/campus-secondhand/"
echo ""
echo "下一步 (Linux):"
echo "  sudo mkdir -p /var/www/campus-secondhand/frontend /var/www/campus-secondhand/uploads /opt/campus-secondhand"
echo "  sudo cp -r $OUT/frontend/* /var/www/campus-secondhand/frontend/"
echo "  sudo cp $OUT/secondhand-1.0.0.jar /opt/campus-secondhand/"
echo "  sudo cp deploy/nginx/campus-secondhand.conf /etc/nginx/sites-available/campus-secondhand"
echo "  sudo ln -sf /etc/nginx/sites-available/campus-secondhand /etc/nginx/sites-enabled/"
echo "  sudo cp deploy/campus-secondhand.service /etc/systemd/system/"
echo "  sudo systemctl daemon-reload && sudo systemctl enable --now campus-secondhand"
echo "  sudo nginx -t && sudo systemctl reload nginx"
