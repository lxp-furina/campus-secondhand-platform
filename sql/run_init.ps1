$sqlPath = "c:\Users\Admin\Desktop\campus-secondhand-platform\sql\init.sql"
$sql = Get-Content -Path $sqlPath -Raw -Encoding UTF8
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
$tmpFile = "c:\Users\Admin\Desktop\campus-secondhand-platform\sql\tmp_init_utf8.sql"
[System.IO.File]::WriteAllText($tmpFile, $sql, $utf8NoBom)
Get-Content -Path $tmpFile -Raw -Encoding UTF8 | mysql --default-character-set=utf8mb4 -uroot -p123456 campus_secondhand
