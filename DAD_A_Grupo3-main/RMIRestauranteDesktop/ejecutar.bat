@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ============================================
echo   Overo's Restaurant - Cliente Desktop
echo ============================================
echo.
if not exist "Lib\javafx-21.0.2\javafx.base.jar" (
    echo Instalando dependencias JavaFX y ZXing...
    powershell -ExecutionPolicy Bypass -File setup.ps1
    if %ERRORLEVEL% NEQ 0 (
        echo Fallo la instalacion. Revisa los errores.
        pause
        exit /b 1
    )
)
echo Iniciando aplicacion...
powershell -ExecutionPolicy Bypass -File run.ps1
pause
