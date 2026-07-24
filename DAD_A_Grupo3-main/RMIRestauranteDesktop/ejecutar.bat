@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ============================================
echo   Overo's Restaurant - Cliente Desktop
echo ============================================
echo.
echo [1/2] Instalando dependencias y compilando...
powershell -ExecutionPolicy Bypass -File setup.ps1
if %ERRORLEVEL% NEQ 0 (
    echo Fallo la instalacion. Revisa los errores.
    pause
    exit /b 1
)
echo.
echo [2/2] Iniciando aplicacion...
powershell -ExecutionPolicy Bypass -File run.ps1
if %ERRORLEVEL% NEQ 0 (
    echo.
    pause
)
