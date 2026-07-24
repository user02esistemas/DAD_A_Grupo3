@echo off
set JAVA="C:\Program Files\Java\jdk-21\bin\javac.exe"
set BASE=D:\Sistemas 2026-1 IX ciclo\Desarrollo de Aplicaciones Distribuidas\PAF_DAW_RMI
set CP=%BASE%\Lib\RMIRestauranteInterface.jar;%BASE%\RMIRestauranteWeb\build\web\WEB-INF\lib\postgresql-42.5.4.jar
set SRC=%BASE%\RMIRestauranteServer\src
set OUT=%BASE%\RMIRestauranteServer\build\classes
if not exist "%OUT%" mkdir "%OUT%"
del /S /Q "%OUT%\*.class" 2>nul
echo Compiling RMIRestauranteServer...
"%JAVA%" -cp "%CP%" -d "%OUT%" -sourcepath "%SRC%" "%SRC%\Servicio\ServidorSistema.java" "%SRC%\DAO\AdministradorDAO.java" "%SRC%\DAO\ClienteDAO.java" "%SRC%\DAO\CodigoPagoDAO.java" "%SRC%\DAO\Conexion.java" "%SRC%\DAO\DatosEmpresaDAO.java" "%SRC%\DAO\DatosEnvioDAO.java" "%SRC%\DAO\EstadoComandaDAO.java" "%SRC%\DAO\EstadoMesaDAO.java" "%SRC%\DAO\EstadoMovimientoDAO.java" "%SRC%\DAO\EstadoPedidoDAO.java" "%SRC%\DAO\EstadoPresentacionDAO.java" "%SRC%\DAO\EstadoProductoDAO.java" "%SRC%\DAO\FormaPagoDAO.java" "%SRC%\DAO\MesaDAO.java" "%SRC%\DAO\MesaGrupoDAO.java" "%SRC%\DAO\MovimientoDAO.java" "%SRC%\DAO\MovimientoMesaDAO.java" "%SRC%\DAO\MovimientoPagoDAO.java" "%SRC%\DAO\MovimientoPedidoDAO.java" "%SRC%\DAO\MozoDAO.java" "%SRC%\DAO\NotificacionDAO.java" "%SRC%\DAO\PresentacionDAO.java" "%SRC%\DAO\ProductoDAO.java" "%SRC%\DAO\RegistroCajaDAO.java" "%SRC%\DAO\ReservaBuffetDAO.java" "%SRC%\DAO\SalonDAO.java" "%SRC%\DAO\TiempoPreparacionDAO.java" "%SRC%\DAO\TipoClienteDAO.java" "%SRC%\DAO\TipoDocumentoDAO.java" "%SRC%\DAO\TipoMovimientoDAO.java" "%SRC%\DAO\TipoProductoDAO.java"
if %errorlevel%==0 (echo [OK] Compilacion exitosa) else (echo [ERROR] Compilacion fallida)
