Estado HTTP 500 – Internal Server Error
Tipo Informe de Excepción

mensaje No se puede compilar la clase para JSP:

Descripción El servidor encontró un error interno que hizo que no pudiera rellenar este requerimiento.

excepción

org.apache.jasper.JasperException: No se puede compilar la clase para JSP: 

Ha tenido lugar un error en la línea: [619] en el archivo jsp: [/mozo.jsp]
Duplicate local variable todosEntregados
616:                 <% if (idMovimientoActivo > 0) {
617:                     boolean yaEnCocina = movFull != null && movFull.getIdEstadoComanda() == 3;
618:                     boolean hayParaEntregar = false;
619:                     boolean todosEntregados = !pedidos.isEmpty();
620:                     for (MovimientoPedido mp : pedidos) {
621:                         if (mp.getIdEstadoPedido() == 3) hayParaEntregar = true;
622:                         if (mp.getIdEstadoPedido() != 4) todosEntregados = false;


Stacktrace:
	org.apache.jasper.compiler.DefaultErrorHandler.javacError(DefaultErrorHandler.java:72)
	org.apache.jasper.compiler.ErrorDispatcher.javacError(ErrorDispatcher.java:192)
	org.apache.jasper.compiler.JDTCompiler.generateClass(JDTCompiler.java:542)
	org.apache.jasper.compiler.Compiler.compile(Compiler.java:371)
	org.apache.jasper.compiler.Compiler.compile(Compiler.java:343)
	org.apache.jasper.compiler.Compiler.compile(Compiler.java:329)
	org.apache.jasper.JspCompilationContext.compile(JspCompilationContext.java:603)
	org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:399)
	org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:376)
	org.apache.jasper.servlet.JspServlet.service(JspServlet.java:324)
	javax.servlet.http.HttpServlet.service(HttpServlet.java:623)
	org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)
nota La traza completa de la causa de este error se encuentra en los archivos de registro del servidor.

Apache Tomcat/9.0.98