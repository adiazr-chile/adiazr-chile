<%@page import="cl.femase.gestionweb.vo.MaintenanceVO"%>
<%@page import="java.util.HashMap"%>
<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.common.Utilidades"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.ProcesoFiltroVO"%>

<%@page import="java.util.List"%>
<%
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    
    String empresaProcesoSelected = (String)request.getAttribute("empresaProcesoSelected");
    int intProcesoSelected = -1; //(Integer)request.getAttribute("procesoSelected");
    if (request.getAttribute("procesoSelected")!=null){
        intProcesoSelected = (Integer)request.getAttribute("procesoSelected");
    }
    
    String procesoSelected = String.valueOf(intProcesoSelected);
    String mensaje = (String)request.getAttribute("mensaje");
    String mensajeError = (String)request.getAttribute("mensajeError");
    
    boolean hayResultado = false;
    HashMap<String, MaintenanceVO> hashFilasAfectadas = (HashMap<String, MaintenanceVO>)request.getAttribute("resultado");
    if (hashFilasAfectadas == null) hashFilasAfectadas = new HashMap<>();

    if (!hashFilasAfectadas.isEmpty()) hayResultado = true;

%>
<!DOCTYPE html>
<html>
<head>
    <title>Ejecutar Traspaso a Historico</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/jquery-2.2.4.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/ui/1.12.1/jquery-ui.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/ui/1.12.1/jquery-ui.multidatespicker.js"></script>

    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

    <!-- estilos css-->
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-multiple_dates-css/result-light.css">    
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-multiple_dates-css/jquery-ui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-multiple_dates-css/jquery-ui.multidatespicker.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.css">
    <!-- estilo para botones -->
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    
    <!-- javascript y estilo para calendario datepicker  -->
    <script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.plugin.min.js"></script>
    <script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.datepick.js"></script>
    <link href="<%=request.getContextPath()%>/jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">
        
    <style type="text/css">
  	#contenidos #columna1 label {
		font-size: medium;
		vertical-align: top;
	}
	
	.labelSeccion {
		font-size: medium;
		font-weight: bold;
		font-variant: small-caps;
	}
  	.tituloForm {
		font-size: x-large;
		font-style: normal;
		color: #036;
		text-decoration: underline;
		font-variant: small-caps;
        font-size: x-large;
	}
	.texto_rojo {
		font-style: normal;
		color: #F00;
	}
	<!-- estilo para month picker-->
	.month-year-input {
	  width: 60px;
	  margin-right: 2px;
	}
  </style>
  
   <style type="text/css">
		#contenedor {
			display: table;
			border: 0px solid #ffffff;
			width: 900px;
			text-align: center;
			margin: 0 auto;
		}
		#contenidos {
			display: table-row;
		}
		#columna1, #columna2, #columna3 {
			display: table-cell;
			border: 1px solid #ffffff;
			vertical-align: top;
			padding: 10px;
			color: #036
		}
  </style>
  
  

    <script type="text/javascript">
        $(document).ready(function() {
            
			  $(document).ajaxComplete(function(){
				$("#wait").css("display", "none");
			  });
			
            $('#empresa').change(function(event) {
				 var empresaSelected = $("select#empresa").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected
                 }, function(response) {
                        var select = $('#depto');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Todos</option>";
                        for (i=0; i<response.length; i++) {
							newoption += "<option value='" + response[i].id +"'>" + response[i].nombre + "</option>";
                        }
                        $('#depto').html(newoption);
                 });
             });
             //evento para combo depto
             $('#depto').change(function(event) {
                 var empresaSelected = $("select#empresa").val();
                 var deptoSelected = $("select#depto").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected
                 }, function(response) {
                        var select = $('#cenco');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Todos</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#cenco').html(newoption);
                 });
             });
			 
			 //evento para combo centro costo
             $('#cenco').change(function(event) {
                var empresaSelected = $("select#empresa").val();
                var deptoSelected = $("select#depto").val();
                var cencoSelected = $("select#cenco").val();
                var sourceSelected = 'traspaso_historico';
                 
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                    empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                }, function(response) {
                    var select = $('#rut');
                    select.find('option').remove();
                    var newoption = "";
                    newoption += "<option value='-1'>Todos</option>";
                    for (i=0; i<response.length; i++) {
                        var auxNombre = 'RUN['+response[i].rut+'] '+response[i].nombres + 
                            ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                        newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                    }
                    $('#rut').html(newoption);
              });
            });
        	
			$( "#dialog_empresa" ).dialog({
			  autoOpen: false,
			  show: {
				effect: "blind",
				duration: 500
			  },
			  hide: {
				effect: "explode",
				duration: 500
			  }
			});
			
			$( "#dialog_departamento" ).dialog({
			  autoOpen: false,
			  show: {
				effect: "blind",
				duration: 500
			  },
			  hide: {
				effect: "explode",
				duration: 500
			  }
			});
			
			$( "#dialog_tabla" ).dialog({
			  autoOpen: false,
			  show: {
				effect: "blind",
				duration: 500
			  },
			  hide: {
				effect: "explode",
				duration: 500
			  }
			});
			
			$( "#dialog_fecha" ).dialog({
			  autoOpen: false,
			  show: {
				effect: "blind",
				duration: 500
			  },
			  hide: {
				effect: "explode",
				duration: 500
			  }
			});
                        
            $( "#dialog_resultado" ).dialog({
			  autoOpen: false,
			  show: {
				effect: "blind",
				duration: 500
			  },
			  hide: {
				effect: "explode",
				duration: 500
			  }
			});
                        
				$( "#dialog-confirm" ).dialog({
					autoOpen: false,
					resizable: false,
					height: "auto",
					width: 400,
					modal: true,
					buttons: {
					  "Ejecutar traspaso": function() {
							//alert('submit!');
							$( this ).dialog( "close" );
							$( "#form1" ).submit();
							$("#wait").css("display", "block");
					},
					  Cancel: function() {
						$( this ).dialog( "close" );
					  }
					}
				  });
                        
                        <%if (hayResultado){%>
                            $( "#dialog_resultado" ).dialog("open");
                        <%}%>
        });
        
		function showConfirmDialog(){
			$( "#dialog-confirm" ).dialog("open");	
		}
		
        function ejecutar(){
            var fecha = $("#fecha").val();
            var tablaSelected = $( "#tabla" ).val();
            var empresaSelected = $( "#empresa" ).val();
            var deptoSelected = $( "#depto" ).val();
            var cencoSelected = $( "#cenco" ).val();
            var todaLaEmpresa = $("input[name='toda_la_empresa']:checked").val();
            
            if(tablaSelected !== null){
                if (fecha !== ''){
                    if(empresaSelected === '-1'){
                        $( "#dialog_empresa" ).dialog("open");
                        $( "#empresa" ).focus();
                        return false;
                    }else if(todaLaEmpresa === 'N'){
                                if(deptoSelected === '-1'){
                                    $( "#dialog_departamento" ).dialog("open");
                                    $( "#depto" ).focus();
                                    return false;
                                }
                                //else if(cencoSelected === '-1'){
                                //    alert("Debe seleccionar un centro de costo");
                                //    $( "#cenco" ).focus();
                                //    return false;
                    }
					
                    showConfirmDialog()
                }else {
                    $( "#dialog_fecha" ).dialog("open");
                    $( "#tabla" ).focus();
                }
            }else {
                $( "#dialog_tabla" ).dialog("open");
                $( "#tabla" ).focus();
            }
			
        }
        
    </script>

</head>

<body>
  
    <form name="form1" id="form1" method="post" action="<%=request.getContextPath()%>/servlet/TraspasoHistorico">
        <p></p>
        <span class="tituloForm">Ejecutar traspaso a hist&oacute;ricos</span>
      <p></p>
        
        <div id="contenidos" >
          <div id="columna1">
            <label>Tabla(s) a traspasar</label>
          </div>
            <div id="columna2" >
                <select name="tabla" size="5" multiple="MULTIPLE" id="tabla">
                    <option value="marca">Marcas</option>
                    <option value="marca_rechazo">Marcas Rechazadas</option>
                    <option value="detalle_ausencia">Ausencias (Justificaciones)</option>
                    <option value="detalle_asistencia">C&aacute;lculos de Asistencia</option>
                    <option value="mantencion_evento">Log de auditoria</option>
                </select>
          &nbsp;<span class="texto_rojo">(*) Obligatorio</span></div>
        </div>
        
        <div id="contenidos" >
          <div id="columna1">
               <label for="fecha">Fecha inicial</label>
          </div>
            <div id="columna2">
               <input type="text" name="fecha" id="fecha" readonly>
          &nbsp;<span class="texto_rojo">(*) Obligatorio</span></div>
        </div>
        
        <div id="contenidos" >
            <div id="columna1">
                <label>Empresa</label>
            </div>
            <div id="columna2" >
              <select name="empresa" id="empresa">
                <option value="-1" selected>Seleccione una empresa</option>
                <%
                    Iterator<EmpresaVO> iteraempresas = empresas.iterator();
                    while(iteraempresas.hasNext() ) {
                        EmpresaVO auxempresa = iteraempresas.next();
                        %>
                        <option value="<%=auxempresa.getId()%>"><%=auxempresa.getNombre()%></option>
                        <%
                    }
                %>
              </select>
            &nbsp;<span class="texto_rojo">(*) Obligatorio</span></div>
        </div>
        
        <div id="contenidos" >
            <div id="columna1">
                <label for="toda_la_empresa">&iquest;Toda la empresa?</label>
            </div>
            <div id="columna2">
           	  	  SI&nbsp;<input  type="radio" name="toda_la_empresa" id="toda_la_empresa" value="S" checked="CHECKED">
             	  &nbsp;&nbsp;NO&nbsp;<input type="radio" name="toda_la_empresa" id="toda_la_empresa" value="N">
            </div>
        </div>
        
        <div id="contenidos" >
            <div id="columna1">
                <label for="depto">Departamento</label>
              </div>
            <div id="columna2">
             <select id="depto" name="depto" style="width:350px;" required>
                    <option value="-1">Todos</option>
              </select>
          </div>
        </div>
        
        <div id="contenidos" >
            <div id="columna1">
                <label>Centro Costo</label>
              </div>
            <div id="columna2">
             <select name="cenco" id="cenco">
                    <option value="-1" selected>Todos</option>
              </select>
          </div>
        </div>
        
        
        
        <div id="contenidos" >
            <div id="columna1">
                <label>Empleado</label>
	        </div>
            <div id="columna2">
             	<select name="rut" id="rut">
                    <option value="-1" selected>Todos</option>
                </select>
          </div>
            
        </div>
        <div id="contenidos" >
            <div id="columna1">&nbsp;</div>
            <div id="columna2"><a class="button button-blue" href="javascript:;" onclick="ejecutar();">Ejecutar</a></div>
	  	</div>
        
        
        <input name="action" type="hidden" id="action" value="ejecutar_proceso">    
       
        <div id="dialog_fecha" title="Traspaso Hist&oacute;rico">
          <p>Debe seleccionar una fecha.</p>
        </div>
        <div id="dialog_tabla" title="Traspaso Hist&oacute;rico">
          <p>Debe seleccionar una tabla.</p>
        </div>
        <div id="dialog_empresa" title="Traspaso Hist&oacute;rico">
          <p>Debe seleccionar una empresa.</p>
        </div>
        <div id="dialog_departamento" title="Traspaso Hist&oacute;rico">
          <p>Debe seleccionar un departamento.</p>
        </div>
        <div id="dialog_resultado" title="Resultado traspaso Hist&oacute;rico">
            <ul>
                <%
                    for (String key : hashFilasAfectadas.keySet()) {
                        MaintenanceVO resultado = hashFilasAfectadas.get(key);
                        int filasAfectadas = resultado.getFilasAfectadas();
                        String label = "Tabla: "+ key + ", filas afectadas= " + filasAfectadas;
                        if (resultado.isThereError()){
                            label = "Tabla: "+ key + ", filas afectadas = 0, " 
                                + "error: " + resultado.getMsgError();
                        }
                    %>
                        <li><%=label%></li>
                    <%}%> 
            </ul> 
        </div>
        
        <div id="dialog-confirm" title="&iquest;Ejecutar traspaso a hist&oacute;rico?">
            <p>
                <span class="ui-icon ui-icon-alert" style="float:left; margin:12px 12px 20px 0;"></span>
                 Los registros existentes ser&aacute;n movidos a tablas hist&oacute;ricas. &iquest;Est&aacute; seguro?
            </p>
        </div>
            
            <div id="wait" style="display:none;width:69px;height:89px;border:1px solid black;position:absolute;top:50%;left:50%;padding:2px;">
           	 <img src='<%=request.getContextPath()%>/images/loader.gif' width="64" height="64" /><br>Procesando..
            </div>
</div>
    </form>
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/prism.js" charset="utf-8"></script>
    <script type="text/javascript">
        var config = {
          '.chosen-select'           : {},
          '.chosen-select-deselect'  : {allow_single_deselect:true},
          '.chosen-select-no-single' : {disable_search_threshold:10},
          '.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
          '.chosen-select-width'     : {width:"95%"}
        }
        for (var selector in config) {
          $(selector).chosen(config[selector]);
        }
		
		
    </script>
  
    <script type="text/javascript">
        
        $(document).ready(function() {
            
            <%if ( (mensaje != null && mensaje.compareTo("") != 0) 
                || (mensajeError != null && mensajeError.compareTo("") != 0)){%>
                    $('#dialog-message').dialog({
                        modal: true,
                        buttons: {
                            Ok: function() {
                              $( this ).dialog( "close" );
                            }
                        }
                    });
            <%}%>
         });

         $.datepicker.regional['es'] = {
            closeText: 'Cerrar',
            prevText: '< Ant',
            nextText: 'Sig >',
            currentText: 'Hoy',
            monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
            monthNamesShort: ['Ene','Feb','Mar','Abr', 'May','Jun','Jul','Ago','Sep', 'Oct','Nov','Dic'],
            dayNames: ['Domingo', 'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado'],
            dayNamesShort: ['Dom','Lun','Mar','Mie','Juv','Vie','Sab'],
            dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sa'],
            weekHeader: 'Sm',
            dateFormat: 'dd/mm/yy',
            firstDay: 1,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''
        };
        $.datepicker.setDefaults($.datepicker.regional['es']);
    
        $(function() {
            $('#fecha').datepick({dateFormat: 'yyyy-mm-dd',maxDate: 0});
        });
        
        
    </script>
</body>

</html>

