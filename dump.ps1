# 프로젝트 덤프 파일 생성 스크립트
# 루트 폴더에서 실행
# 윈도우 Git Bash :  powershell.exe -ExecutionPolicy Bypass -File dump.ps1

$targetDir = Join-Path $PSScriptRoot "src"
$outputFile = Join-Path $PSScriptRoot "project_dump.txt"

if (Test-Path $outputFile) {
    Remove-Item $outputFile
}

# 🔥 UTF-8로 빈 파일 먼저 생성
"" | Out-File -Encoding UTF8 $outputFile

Get-ChildItem -Path $targetDir -Recurse -Filter "*.java" | ForEach-Object {

    Add-Content -Encoding UTF8 $outputFile "=================================================="
    Add-Content -Encoding UTF8 $outputFile "FILE: $($_.FullName)"
    Add-Content -Encoding UTF8 $outputFile "=================================================="
    Add-Content -Encoding UTF8 $outputFile ""

    Get-Content -Encoding UTF8 $_.FullName | Add-Content -Encoding UTF8 $outputFile
    Add-Content -Encoding UTF8 $outputFile "`n"
}

Write-Host "완료: $outputFile"
