@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set BASE=D:\Sistemas 2026-1 IX ciclo\Desarrollo de Aplicaciones Distribuidas\PAF_DAW_RMI\RMIRestauranteDesktop
set CP=%BASE%\Lib\RMIRestauranteInterface.jar;%BASE%\Lib\postgresql-42.5.4.jar;%BASE%\Lib\zxing-core-3.5.3.jar;%BASE%\Lib\zxing-javase-3.5.3.jar;%BASE%\Lib\javafx-sdk-21.0.2\lib\javafx.base.jar;%BASE%\Lib\javafx-sdk-21.0.2\lib\javafx.controls.jar;%BASE%\Lib\javafx-sdk-21.0.2\lib\javafx.fxml.jar;%BASE%\Lib\javafx-sdk-21.0.2\lib\javafx.graphics.jar;%BASE%\Lib\javafx-sdk-21.0.2\lib\javafx.swing.jar
set OUT=%BASE%\build\classes
set SRC=%BASE%\src

if not exist "%OUT%" mkdir "%OUT%"

"%JAVA_HOME%\bin\javac.exe" -cp "%CP%" -d "%OUT%" -sourcepath "%SRC%" "%SRC%\Main\Main.java" "%SRC%\Main\RMIConnection.java" "%SRC%\Login\LoginController.java" "%SRC%\Admin\DashboardController.java" "%SRC%\Admin\ProductoController.java" "%SRC%\Admin\MesaController.java" "%SRC%\Admin\SalonController.java" "%SRC%\Admin\MozoController.java" "%SRC%\Admin\ClienteAtendidosController.java" "%SRC%\Admin\ReservaBuffetController.java" "%SRC%\Admin\PresentacionController.java" "%SRC%\Admin\QRController.java" "%SRC%\Admin\ConfigController.java" "%SRC%\Caja\CajaController.java" "%SRC%\Caja\AbrirCajaController.java" "%SRC%\Caja\TicketController.java"
if %ERRORLEVEL% EQU 0 (
    echo COMPILATION SUCCESSFUL
    xcopy /s /e /y /q "%SRC%\*.fxml" "%OUT%\" >nul
    xcopy /s /e /y /q "%SRC%\*.css" "%OUT%\" >nul
    echo FXML and CSS copied
) else (
    echo COMPILATION FAILED
)
