$BASE = Split-Path -Parent $MyInvocation.MyCommand.Path
$JFX_DIR = "$BASE\Lib\javafx-21.0.2"
$ZXING_DIR = "$BASE\Lib\zxing-3.5.3"

# Find JDK (same logic as setup.ps1)
$JAVA_HOME_CANDIDATES = @(
    $env:JAVA_HOME,
    "C:\Program Files\Java\jdk-21.0.10",
    "C:\Program Files\Java\jdk-21",
    "C:\Program Files\Eclipse Adoptium\jdk-21.*",
    "C:\Program Files\Microsoft\jdk-21.*"
)
$realJdk = $null
foreach ($cand in $JAVA_HOME_CANDIDATES) {
    if ($cand -and (Test-Path "$cand\bin\java.exe")) { $realJdk = $cand; break }
}
if (-not $realJdk) {
    $javaCmd = Get-Command "java.exe" -ErrorAction SilentlyContinue
    if ($javaCmd) { $realJdk = (Get-Item $javaCmd.Source).Directory.Parent.FullName }
}
if (-not $realJdk -or -not (Test-Path "$realJdk\bin\java.exe")) {
    Write-Host "JDK 21+ no encontrado" -ForegroundColor Red
    Write-Host "Instala JDK 21 desde: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}
$JAVA = "$realJdk\bin\java.exe"
Write-Host "JDK: $realJdk" -ForegroundColor Cyan

$CP = "$BASE\build\classes"
$CP += ";$BASE\..\Lib\RMIRestauranteInterface.jar"
$CP += ";$BASE\..\Lib\postgresql-42.5.4.jar"
$CP += ";$ZXING_DIR\core-3.5.3.jar"
$CP += ";$ZXING_DIR\javase-3.5.3.jar"
$MODPATH = "$JFX_DIR"

& $JAVA "--module-path=$MODPATH" "--add-modules=javafx.controls,javafx.fxml,javafx.swing" -cp "$CP" Main.Main
