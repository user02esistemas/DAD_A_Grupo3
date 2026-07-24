$BASE = Split-Path -Parent $MyInvocation.MyCommand.Path
$LIB = "$BASE\Lib"
$JFX_VER = "21.0.2"
$ZXING_VER = "3.5.3"
$MAVEN = "https://repo1.maven.org/maven2"
$JFX = "org/openjfx"
$ZXING = "com/google/zxing"
$MODULES = @("javafx-base","javafx-controls","javafx-fxml","javafx-graphics","javafx-swing")

Write-Host "=== Overo's Desktop - Setup ===" -ForegroundColor Green

# Check Java
# Find real JDK installation
$JAVA_HOME_CANDIDATES = @(
    $env:JAVA_HOME,
    "C:\Program Files\Java\jdk-21.0.10",
    "C:\Program Files\Java\jdk-21",
    "C:\Program Files\Eclipse Adoptium\jdk-21.*",
    "C:\Program Files\Microsoft\jdk-21.*"
)
$realJdk = $null
foreach ($cand in $JAVA_HOME_CANDIDATES) {
    if ($cand -and (Test-Path "$cand\bin\javac.exe")) { $realJdk = $cand; break }
}
if (-not $realJdk) {
    # Try to find javac.exe via Get-Command
    $javacCmd = Get-Command "javac.exe" -ErrorAction SilentlyContinue
    if ($javacCmd) { $realJdk = (Get-Item $javacCmd.Source).Directory.Parent.FullName }
}
if (-not $realJdk -or -not (Test-Path "$realJdk\bin\javac.exe")) {
    $realJdk = Read-Host "Ruta de JDK 21+ (ej: C:\Program Files\Java\jdk-21.0.10)"
}
$env:JAVA_HOME = $realJdk
$JAVA_BIN = "$realJdk\bin\java.exe"
$JAVAC = "$realJdk\bin\javac.exe"
Write-Host "JDK: $realJdk" -ForegroundColor Cyan

# Download JavaFX JARs
$JFX_DIR = "$LIB\javafx-$JFX_VER"
if (-not (Test-Path "$JFX_DIR\javafx.base.jar")) {
    Write-Host "Descargando JavaFX $JFX_VER..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $JFX_DIR -Force | Out-Null
    foreach ($mod in $MODULES) {
        $url1 = "$MAVEN/$JFX/$mod/$JFX_VER/$mod-$JFX_VER.jar"
        $url2 = "$MAVEN/$JFX/$mod/$JFX_VER/$mod-$JFX_VER-win.jar"
        $out1 = "$JFX_DIR\$mod.jar"
        $out2 = "$JFX_DIR\$mod-win.jar"
        Write-Host "  $mod..." -NoNewline
        try { Invoke-WebRequest -Uri $url1 -OutFile $out1 -UseBasicParsing -ErrorAction Stop; Write-Host " OK" -ForegroundColor Green } catch { Write-Host " FAIL" -ForegroundColor Red }
        try { Invoke-WebRequest -Uri $url2 -OutFile $out2 -UseBasicParsing -ErrorAction Stop; Write-Host "  $mod-win OK" -ForegroundColor Green } catch { Write-Host "  $mod-win FAIL" -ForegroundColor Red }
    }
} else {
    Write-Host "JavaFX ya descargado" -ForegroundColor Cyan
}

# Download ZXing
$ZXING_DIR = "$LIB\zxing-$ZXING_VER"
if (-not (Test-Path "$ZXING_DIR\core-$ZXING_VER.jar")) {
    Write-Host "Descargando ZXing $ZXING_VER..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $ZXING_DIR -Force | Out-Null
    $zxingJars = @("core","javase")
    foreach ($j in $zxingJars) {
        $url = "$MAVEN/$ZXING/$j/$ZXING_VER/$j-$ZXING_VER.jar"
        $out = "$ZXING_DIR\$j-$ZXING_VER.jar"
        Write-Host "  $j..." -NoNewline
        try { Invoke-WebRequest -Uri $url -OutFile $out -UseBasicParsing -ErrorAction Stop; Write-Host " OK" -ForegroundColor Green } catch { Write-Host " FAIL" -ForegroundColor Red }
    }
} else {
    Write-Host "ZXing ya descargado" -ForegroundColor Cyan
}

# Build classpath
$CP = "$BASE\build\classes"
$CP += ";$BASE\..\Lib\RMIRestauranteInterface.jar"
$CP += ";$BASE\..\Lib\postgresql-42.5.4.jar"
$CP += ";$ZXING_DIR\core-$ZXING_VER.jar"
$CP += ";$ZXING_DIR\javase-$ZXING_VER.jar"
$MODPATH = "$JFX_DIR"

# Compile
Write-Host "Compilando..." -ForegroundColor Yellow
New-Item -ItemType Directory -Path "$BASE\build\classes" -Force | Out-Null
Get-ChildItem "$BASE\src" -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Out-File "$env:TEMP\desktop_src.txt" -Encoding ASCII
& $JAVAC "--module-path=$MODPATH" "--add-modules=javafx.controls,javafx.fxml,javafx.swing" -cp "$CP" -d "$BASE\build\classes" "@$env:TEMP\desktop_src.txt" 2>&1
if ($LASTEXITCODE -ne 0) { Write-Host "Compilacion fallida" -ForegroundColor Red; Read-Host "Presiona Enter para salir"; exit 1 }
Get-ChildItem "$BASE\src" -Recurse -Filter *.fxml | ForEach-Object {
    $rel = $_.FullName.Substring("$BASE\src\".Length)
    $dest = "$BASE\build\classes\$rel"
    New-Item -ItemType Directory -Path (Split-Path $dest -Parent) -Force | Out-Null
    Copy-Item $_.FullName $dest -Force
}
Get-ChildItem "$BASE\src" -Recurse -Filter *.css | ForEach-Object {
    $rel = $_.FullName.Substring("$BASE\src\".Length)
    $dest = "$BASE\build\classes\$rel"
    New-Item -ItemType Directory -Path (Split-Path $dest -Parent) -Force | Out-Null
    Copy-Item $_.FullName $dest -Force
}
Write-Host "Compilacion OK" -ForegroundColor Green
Write-Host ""
Write-Host "Ejecuta con: .\run.ps1" -ForegroundColor Cyan
