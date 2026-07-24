#requires -Version 5.1
<#
.SYNOPSIS
    Compila y ejecuta RMIRestauranteDesktop desde PowerShell.
.DESCRIPTION
    Verifica librerias, compila las fuentes Java, copia recursos FXML/CSS
    y lanza la aplicacion JavaFX apuntando a Main.Main.
#>

$ErrorActionPreference = "Stop"

# -----------------------------------------------------------------------------
# 1. Rutas base
# -----------------------------------------------------------------------------
$BASE = Split-Path -Parent $MyInvocation.MyCommand.Definition
$LIB = Join-Path $BASE "Lib"
$SRC = Join-Path $BASE "src"
$OUT = Join-Path $BASE "build\classes"

# Preferir JDK 21 (el target del proyecto) si esta disponible
$possibleJdks = @(
    "C:\Program Files\Java\jdk-21",
    "C:\Program Files\Eclipse Adoptium\jdk-21",
    "C:\Program Files\Amazon Corretto\jdk-21",
    $env:JAVA_HOME
)

$JAVA_HOME = $null
foreach ($jdk in $possibleJdks) {
    if ($jdk -and (Test-Path (Join-Path $jdk "bin\javac.exe"))) {
        $JAVA_HOME = $jdk
        break
    }
}

$JAVAC = Join-Path $JAVA_HOME "bin\javac.exe"
$JAVA = Join-Path $JAVA_HOME "bin\java.exe"

# -----------------------------------------------------------------------------
# 2. Verificar Java
# -----------------------------------------------------------------------------
if (-not (Test-Path $JAVAC) -or -not (Test-Path $JAVA)) {
    Write-Host "[ERROR] No se encontro javac.exe o java.exe en: $JAVA_HOME" -ForegroundColor Red
    Write-Host "        Configura la variable JAVA_HOME o instala JDK 21 en esa ruta." -ForegroundColor Red
    exit 1
}

Write-Host "[OK] Java detectado en $JAVA_HOME" -ForegroundColor Green

# -----------------------------------------------------------------------------
# 3. Verificar librerias
# -----------------------------------------------------------------------------
$requiredJars = @(
    "RMIRestauranteInterface.jar",
    "postgresql-42.5.4.jar",
    "zxing-core-3.5.3.jar",
    "zxing-javase-3.5.3.jar"
)

$missing = @()
foreach ($jar in $requiredJars) {
    $path = Join-Path $LIB $jar
    if (-not (Test-Path $path)) {
        $missing += $jar
    }
}

$JAVAFX_LIB = Join-Path $LIB "javafx-sdk-21.0.2\lib"
$JAVAFX_CONTROLS = Join-Path $JAVAFX_LIB "javafx.controls.jar"
if (-not (Test-Path $JAVAFX_CONTROLS)) {
    $missing += "javafx-sdk-21.0.2\lib\*.jar"
}

if ($missing.Count -gt 0) {
    Write-Host "[ERROR] Faltan las siguientes librerias en $LIB`:" -ForegroundColor Red
    foreach ($m in $missing) {
        Write-Host "        - $m" -ForegroundColor Red
    }
    Write-Host "        Descarga JavaFX SDK 21.0.2 y ZXing 3.5.3 y colocalos en $LIB." -ForegroundColor Red
    exit 1
}

Write-Host "[OK] Todas las librerias estan presentes" -ForegroundColor Green

# -----------------------------------------------------------------------------
# 4. Construir classpath
# -----------------------------------------------------------------------------
$cpParts = @(
    (Join-Path $LIB "RMIRestauranteInterface.jar"),
    (Join-Path $LIB "postgresql-42.5.4.jar"),
    (Join-Path $LIB "zxing-core-3.5.3.jar"),
    (Join-Path $LIB "zxing-javase-3.5.3.jar"),
    (Join-Path $JAVAFX_LIB "javafx.base.jar"),
    (Join-Path $JAVAFX_LIB "javafx.controls.jar"),
    (Join-Path $JAVAFX_LIB "javafx.fxml.jar"),
    (Join-Path $JAVAFX_LIB "javafx.graphics.jar"),
    (Join-Path $JAVAFX_LIB "javafx.swing.jar")
)
$CP = $cpParts -join ";"

# -----------------------------------------------------------------------------
# 5. Compilar
# -----------------------------------------------------------------------------
New-Item -ItemType Directory -Force -Path $OUT | Out-Null

$javaFiles = Get-ChildItem -Path $SRC -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
if (-not $javaFiles) {
    Write-Host "[ERROR] No se encontraron archivos .java en $SRC" -ForegroundColor Red
    exit 1
}

# Guardar lista de fuentes en archivo temporal para evitar linea muy larga
$tempList = Join-Path $BASE ".sources.txt"
[System.IO.File]::WriteAllLines($tempList, [string[]]$javaFiles, [System.Text.UTF8Encoding]::new($false))

Write-Host "[INFO] Compilando $(($javaFiles).Count) archivos Java..." -ForegroundColor Cyan
& $JAVAC -cp $CP -d $OUT -sourcepath $SRC "@$tempList"

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilacion fallida" -ForegroundColor Red
    Remove-Item -Path $tempList -Force -ErrorAction SilentlyContinue
    exit 1
}

Remove-Item -Path $tempList -Force -ErrorAction SilentlyContinue
Write-Host "[OK] Compilacion exitosa" -ForegroundColor Green

# -----------------------------------------------------------------------------
# 6. Copiar recursos (FXML, CSS, imagenes)
# -----------------------------------------------------------------------------
$resources = Get-ChildItem -Path $SRC -Recurse | Where-Object {
    $_.Extension -in @(".fxml", ".css", ".png", ".jpg", ".jpeg", ".gif")
}

foreach ($res in $resources) {
    $relativePath = $res.FullName.Substring($SRC.Length + 1)
    $destPath = Join-Path $OUT $relativePath
    $destDir = Split-Path -Parent $destPath
    New-Item -ItemType Directory -Force -Path $destDir | Out-Null
    Copy-Item -Path $res.FullName -Destination $destPath -Force
}

Write-Host "[OK] Recursos copiados a $OUT" -ForegroundColor Green

# -----------------------------------------------------------------------------
# 7. Ejecutar
# -----------------------------------------------------------------------------
$RUN_CP = "$OUT;" + $CP

Write-Host "[INFO] Ejecutando aplicacion..." -ForegroundColor Cyan
Write-Host "       java --module-path $JAVAFX_LIB --add-modules javafx.controls,javafx.fxml -cp ... Main.Main" -ForegroundColor DarkGray

& $JAVA --module-path $JAVAFX_LIB --add-modules javafx.controls,javafx.fxml -cp $RUN_CP Main.Main

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] La aplicacion finalizo con codigo $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}
