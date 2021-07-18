<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloSistemaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="java.util.LinkedHashMap"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%
	UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
	SimpleDateFormat sdfhora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	java.util.Date ahora = new java.util.Date();
	LinkedHashMap<String, ModuloSistemaVO> modulosSistema = 
    	(LinkedHashMap<String, ModuloSistemaVO>)session.getAttribute("modulosSistema");
            
%>

<html>
    <head>
        <title>Accesos rapidos - with jQuery and CSS3</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="description" content="Slide Down Box Menu with jQuery and CSS3" />
        <meta name="keywords" content="jquery, css3, sliding, box, menu, cube, navigation, portfolio, thumbnails"/>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/quick_menu/css/style.css" type="text/css" media="screen"/>
        <style>
			body{
				background:#c8c8f2;
				font-family:Arial;
			}
			span.reference{
				position:fixed;
				left:10px;
				bottom:10px;
				font-size:12px;
			}
			span.reference a{
				color:#aaa;
				text-transform:uppercase;
				text-decoration:none;
				text-shadow:1px 1px 1px #000;
				margin-right:30px;
			}
			span.reference a:hover{
				color:#ddd;
			}
			ul.sdt_menu{
	margin-top: 50px;
			}
			h1.title{
				text-indent:-9000px;
				<!-- background:transparent url(title.png) no-repeat top left;-->
				width:633px;
				height:69px;
			}
		</style>
    </head>

    <body>
		<div class="content">
			<h1 class="title">Acceso rapido</h1>
			<ul id="sdt_menu" class="sdt_menu">
				
    <%
		LinkedHashMap<String, LinkedHashMap<String, ModuloAccesoPerfilVO>> accesosModulo = theUser.getAccesosModulo();
		String keyModuleId;
		String moduleTitulo;
		String moduleSubTitulo;
		String moduleImagen;
		
		Iterator<ModuloSistemaVO> itModulos = modulosSistema.values().iterator();
		while (itModulos.hasNext())
		{
			ModuloSistemaVO currentModulo = itModulos.next();
			LinkedHashMap<String, ModuloAccesoPerfilVO> listaAccesos 
				= accesosModulo.get(""+currentModulo.getModulo_id());
			moduleTitulo = currentModulo.getTitulo();
			moduleSubTitulo = currentModulo.getSubTitulo();
			moduleImagen = currentModulo.getImagen();
                        if (currentModulo.getAccesoRapido().compareTo("S")==0)
                        {%>
				<li>
					<a href="#">
						<img src="<%=request.getContextPath()%>/quick_menu/images/<%=moduleImagen%>" alt=""/>
						<span class="sdt_active"></span>
						<span class="sdt_wrap">
							<span class="sdt_link"><%=moduleTitulo%></span>
							<span class="sdt_descr"><%=moduleSubTitulo%></span>
						</span>
					</a>
					<div class="sdt_box">
					   <%
                                                String thetarget="_self";
						Iterator<ModuloAccesoPerfilVO> actionsIterator = listaAccesos.values().iterator();
						while (actionsIterator.hasNext()) {
							ModuloAccesoPerfilVO theaction = actionsIterator.next();
							if (theaction.getAccesoUrl() != null) {
								if (theaction.getAccesoNombre().compareTo("endsession") == 0){
									thetarget="_parent";
								}%>
								
								<a href="<%=request.getContextPath()%>/
									<%=theaction.getAccesoUrl()%>" target="<%=thetarget%>" 
										title="<%=theaction.getAccesoLabel()%>">
									<%=theaction.getAccesoLabel()%>
							   
							<%}
						}//fin iteracion de accesos
                                              %>      
					</div>
				</li>
			<%}//fin if accesoRapido = 'S'
		}//fin iteracion de modulos    
                %>
                
			</ul>
		</div>
        
        <!-- The JavaScript -->
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/quick_menu/jquery.easing.1.3.js"></script>
        <script type="text/javascript">
            $(function() {
				/**
				* for each menu element, on mouseenter, 
				* we enlarge the image, and show both sdt_active span and 
				* sdt_wrap span. If the element has a sub menu (sdt_box),
				* then we slide it - if the element is the last one in the menu
				* we slide it to the left, otherwise to the right
				*/
                $('#sdt_menu > li').bind('mouseenter',function(){
					var $elem = $(this);
					$elem.find('img')
						 .stop(true)
						 .animate({
							'width':'170px',
							'height':'170px',
							'left':'0px'
						 },400,'easeOutBack')
						 .andSelf()
						 .find('.sdt_wrap')
					     .stop(true)
						 .animate({'top':'140px'},500,'easeOutBack')
						 .andSelf()
						 .find('.sdt_active')
					     .stop(true)
						 .animate({'height':'170px'},300,function(){
						var $sub_menu = $elem.find('.sdt_box');
						if($sub_menu.length){
							var left = '170px';
							if($elem.parent().children().length == $elem.index()+1)
								left = '-170px';
							$sub_menu.show().animate({'left':left},200);
						}	
					});
				}).bind('mouseleave',function(){
					var $elem = $(this);
					var $sub_menu = $elem.find('.sdt_box');
					if($sub_menu.length)
						$sub_menu.hide().css('left','0px');
					
					$elem.find('.sdt_active')
						 .stop(true)
						 .animate({'height':'0px'},300)
						 .andSelf().find('img')
						 .stop(true)
						 .animate({
							'width':'0px',
							'height':'0px',
							'left':'85px'},400)
						 .andSelf()
						 .find('.sdt_wrap')
						 .stop(true)
						 .animate({'top':'25px'},500);
				});
            });
        </script>
    </body>
</html>