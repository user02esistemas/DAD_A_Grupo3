@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set BASE=D:\Sistemas 2026-1 IX ciclo\Desarrollo de Aplicaciones Distribuidas\PAF_DAW_RMI\RMIRestauranteDesktop
set CP=%BASE%\build;%BASE%\Lib\RMIRestauranteInterface.jar;%BASE%\Lib\postgresql-42.5.4.jar;%BASE%\Lib\zxing-core-3.5.3.jar;%BASE%\Lib\zxing-javase-3.5.3.jar
set MODPATH=%BASE%\Lib\javafx-sdk-21.0.2\lib

set PATH=%BASE%\Lib\javafx-sdk-21.0.2\bin;%PATH%
"%JAVA_HOME%\bin\java.exe" --module-path "%MODPATH%" --add-modules javafx.controls,javafx.fxml -cp "%CP%" Main.Main
pause
