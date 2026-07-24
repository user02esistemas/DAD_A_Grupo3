$BASE = Split-Path -Parent $MyInvocation.MyCommand.Path
$JFX_DIR = "$BASE\Lib\javafx-21.0.2"
$ZXING_DIR = "$BASE\Lib\zxing-3.5.3"

# Find JDK
$realJdk = $env:JAVA_HOME
if (-not $realJdk -or -not (Test-Path "$realJdk\bin\javac.exe")) {
    $javacCmd = Get-Command "javac.exe" -ErrorAction SilentlyContinue
    if ($javacCmd) { $realJdk = (Get-Item $javacCmd.Source).Directory.Parent.FullName }
}
if (-not $realJdk -or -not (Test-Path "$realJdk\bin\java.exe")) {
    Write-Host "JDK 21+ no encontrado" -ForegroundColor Red; exit 1
}
$JAVA = "$realJdk\bin\java.exe"

$CP = "$BASE\build\classes"
$CP += ";$BASE\..\Lib\RMIRestauranteInterface.jar"
$CP += ";$BASE\..\Lib\postgresql-42.5.4.jar"
$CP += ";$ZXING_DIR\core-3.5.3.jar"
$CP += ";$ZXING_DIR\javase-3.5.3.jar"
$MODPATH = "$JFX_DIR"

& $JAVA "--module-path=$MODPATH" "--add-modules=javafx.controls,javafx.fxml,javafx.swing" -cp "$CP" Main.Main
