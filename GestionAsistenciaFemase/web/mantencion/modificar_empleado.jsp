<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%@page import="java.util.List"%>
<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.ComunaVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<!-- import para mostrar imagen-->
<%@page import="java.io.File"%>
<%@page import="java.io.IOException"%>
<%@page import="java.awt.image.BufferedImage"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.math.BigInteger"%>
<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="java.awt.image.BufferedImage"%>



<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<ComunaVO> comunas   = (List<ComunaVO>)session.getAttribute("comunas"); 
    EmpleadoVO empleado = (EmpleadoVO)session.getAttribute("empleado");
    List<TurnoVO> turnos   = (List<TurnoVO>)session.getAttribute("turnos");
    List<CargoVO> cargos   = (List<CargoVO>)session.getAttribute("cargos");
    String imagesPath = (String)session.getAttribute("path_images"); 
    
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Editar/Modificar Empleado</title>
  <meta name="description" content="Modificar Empleado">
  
    <script src="<%=request.getContextPath()%>/js/jquery-1.12.4.min.js"></script>
	<script src="<%=request.getContextPath()%>/js/formularios/parsley.min.js"></script>
	<script src="<%=request.getContextPath()%>/js-varios/jquery.min.js"></script>
	<script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.plugin.min.js"></script>
	<script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.datepick.js"></script>
	<script src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>

	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.jquery.js">  </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/prism.js" charset="utf-8"> </script>
    
    <script src="https://cdn.jsdelivr.net/jquery.validation/1.15.0/jquery.validate.min.js"></script>
    <script src="https://cdn.jsdelivr.net/jquery.validation/1.15.0/additional-methods.min.js"></script>
       
        
	<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery-plugins/datepicker/css/jquery.datepick.css" >
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/style/jquery-ui.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/parsley.css" >
	<!-- Bootstrap core CSS -->
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.css" >
         <!-- estilo para botones -->
        <link rel="stylesheet" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    <!-- -->

    <link rel="stylesheet" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.css">
    <link rel="stylesheet" href="https://jqueryvalidation.org/files/demo/site-demos.css">
    
    <script type="text/javascript">
        $(document).ready(function() {
             $('#empresaId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected
                 }, function(response) {
                        var select = $('#deptoId');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Departamento</option>";
                        for (i=0; i<response.length; i++) {
                            //if (response[i].id==='<%=empleado.getDepartamento().getId()%>'){
                                //newoption += "<option value='"+response[i].id+"' selected>"+response[i].nombre+"</option>";
                            //}else {
                                newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                            //}
                        }
                        $('#deptoId').html(newoption);
                        
                 });
             });
             
            $("#modificarDivSi").hide();
            $("input[name$='modificar']").click(function() {
                var test = $(this).val();
                $("div.desc").hide();
                $("#modificarDiv" + test).show();
            });
             
             //evento para combo depto
             $('#deptoId').change(function(event) {
                
                 var empresaSelected = $("select#empresaId").val();
                 //var deptoSelected = '<%=empleado.getDepartamento().getId()%>';//$("select#deptoId").val();
                 var deptoSelected = $("select#deptoId").val();
                 //alert('empresaSelected='+empresaSelected+',deptoSelected='+deptoSelected);
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected
                 }, function(response) {
                        var select = $('#cencoId');
                        select.find('option').remove();
                        var newoption = "";
                       newoption += "<option value='-1'>Seleccione Centro de costo</option>";
                        //alert('centrosdecosto.size='+response.length);
                        for (i=0; i<response.length; i++) {
                            //if (response[i].id===<%=empleado.getCentroCosto().getId()%>){
                            //    newoption += "<option value='"+response[i].id+"' selected>"+response[i].nombre+"</option>";
                            //}else {
                                newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                            //}
                        }
                        $('#cencoId').html(newoption);
                        
                 });
             });
            
            $('#demo-form').submit(   
                function(event) {
                    var empresaId = $('#empresaId').val();
                    var deptoId = $('#deptoId').val();
                    var cencoId = $('#cencoId').val();
                    var modificar = $('input[name=modificar]:checked', '#demo-form').val();
                    if (modificar==='Si'){
                        if (empresaId === '-1') {
                            alert('Seleccione empresa');
                                                    activaTab2();
                                                    document.getElementById("empresaId").focus();
                                                    event.preventDefault();
                        }else if (deptoId === '-1') {
                            alert('Seleccione Departamento');
                                                    activaTab2();
                                                    document.getElementById("deptoId").focus();
                                                    event.preventDefault();
                        }else if (cencoId === '-1') {
                            alert('Seleccione Centro de Costo');
                                        activaTab2();
                                        document.getElementById("cencoId").focus();
                                        event.preventDefault();
                        }
                    }
                }
            );
    
           
         }
             
    );
   
    $(function() {
         $('#fechaNacimientoAsStr').datepick({dateFormat: 'dd-mm-yyyy'});
         $('#fechaInicioContratoAsStr').datepick({dateFormat: 'dd-mm-yyyy'});
         $('#fechaTerminoContratoAsStr').datepick({dateFormat: 'dd-mm-yyyy'});
     });
    	
    function volver(){
        window.history.back();
    }
    
    function loadValues(){
        //cargar items seleccionados combos...
        selectItemByValue(document.getElementById('comunaId'),<%=empleado.getComunaId()%>);
        selectItemByValue(document.getElementById('estado'),<%=empleado.getEstado()%>);
        selectItemByValue(document.getElementById('idTurno'),<%=empleado.getIdTurno()%>);
        selectItemByValue(document.getElementById('idCargo'),<%=empleado.getIdCargo()%>);
        document.getElementById("<%=empleado.getSexo()%>").checked = true;
        <%if (empleado.isAutorizaAusencia()){%>
            document.getElementById("autorizaS").checked = true;
        <%}else{%>
            document.getElementById("autorizaN").checked = true;
        <%}%>
            
        <%if (empleado.isContratoIndefinido()){%>
            document.getElementById("indefinidoS").checked = true;
        <%}else{%>
            document.getElementById("indefinidoN").checked = true;
        <%}%>    
        <%if (empleado.isArticulo22()){%>
            document.getElementById("art22S").checked = true;
        <%}else{%>
            document.getElementById("art22N").checked = true;
        <%}%>    
        var event = new Event('change');
        // Dispatch it.
        document.getElementById("empresaId").dispatchEvent(event);
        var event2 = new Event('change');
        document.getElementById("deptoId").dispatchEvent(event2);
        
        //selectItemByValue(document.getElementById("cencoId"), '<%=empleado.getCentroCosto().getId()%>');
        //Set selected
        //var objSelect = document.getElementById("cencoId");
        //setSelectedValue(objSelect, "<%=empleado.getCentroCosto().getId()%>");
    }  
    
    function selectItemByValue(elmnt, value){

        for(var i=0; i < elmnt.options.length; i++)
        { 
          if(parseInt(elmnt.options[i].value) === parseInt(value)) {
            elmnt.selectedIndex = i;
            break;
          }
        }
    }
    
    function setSelectedValue(selectObj, valueToSet) {
        //alert('valueToSet = '+valueToSet);
        for (var i = 0; i < selectObj.options.length; i++) {
             //alert('selectObj.options[i].value = '+selectObj.options[i].value);
            if (selectObj.options[i].value=== valueToSet) {
                selectObj.options[i].selected = true;
                //alert('cenco seleccionado...');
                return;
            }
        }
    }
	
	function activaTab0(){
		$( "#tabs" ).tabs({
		  active: 0
		});	
	}
	
	function activaTab1(){
		$( "#tabs" ).tabs({
		  active: 1
		});	
	}
	
	function activaTab2(){
		$( "#tabs" ).tabs({
		  active: 2
		});	
	}


  </script>
  
  
  <style type="text/css" media="all">
    /* fix rtl for demo */
    .chosen-rtl .chosen-drop { left: -9000px; }
   body {
	background-color: #fafafa;
	font-family:font-family, times, serif; 	
	font-size: 12pt;
	font-style: normal;
	color: #06C
}
   .container { margin:0px auto; max-width:1000px;}
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
	vertical-align: middle;
	padding: 10px;
	color: #036
}
  </style>
  
  
  <!--[if lt IE 9]>
  <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>

<body onLoad="loadValues()">
   
   
   
   
   
   <!--<div id="container" class="container">-->
	<!--<div class="bs-callout bs-callout-warning hidden">
	  <h4>Oh snap!</h4>
	  <p>This form seems to be invalid :(</p>
	</div>

	<div class="bs-callout bs-callout-info hidden">
	  <h4>Yay!</h4>
	  <p>Everything seems to be ok :)</p>
	</div>-->

       <form id="demo-form" action="<%=request.getContextPath()%>/EmpleadosController" 
              data-parsley-validate="" method="post" enctype="multipart/form-data">
       
          <h1><span class="h1">Modificar Empleado</span>
            <input type="hidden" name="action" id="action" value="update">
            <!-- MAX_FILE_SIZE must precede the file input field -->
            <input type="hidden" name="MAX_FILE_SIZE" value="30000" />          
          </h1>
      
	  
           <!-- Inicio Tabs con formulario -->
        <div id="tabs">
            <ul>
              <li><a href="#fragment-1"><span>Informaci&oacute;n Personal-1</span></a></li>
              <li><a href="#fragment-2"><span>Informaci&oacute;n Personal-1</span></a></li>
              <li><a href="#fragment-3"><span>Informaci&oacute;n Laboral</span></a></li>
            </ul>
            <div id="fragment-1">
                <!-- TAB 1-->
                <div id="contenidos">
			<div id="columna1"><label for="rut">Rut * :</label>
                        <input name="rut" type="text" 
                               required class="form-control" 
                               value="<%=empleado.getRut()%>" 
                               maxlength="12"></div>
			<div id="columna2"><label for="rut">Num Ficha * :</label>
                        <input type="text" 
                               class="form-control" 
                               name="cod_interno" 
                               required maxlength="20"
                               value="<%=empleado.getCodInterno()%>"></div>
		</div>
                <div id="contenidos">
                    <div id="columna1"><label for="nombres">Nombres * :</label>
                        <input name="nombres" type="text" required class="form-control" value="<%=empleado.getNombres()%>" maxlength="70">
                    </div>
                    <div id="columna2"><label for="apePaterno">Ap. Paterno * :</label>
                        <input name="apePaterno" type="text" required class="form-control" id="apePaterno" value="<%=empleado.getApePaterno()%>" maxlength="50">
                    </div>
		</div>
                <div id="contenidos">
			<div id="columna1"><label for="apeMaterno">Ap. Materno * :</label>
                            <input name="apeMaterno" type="text" required class="form-control" id="apeMaterno" value="<%=empleado.getApeMaterno()%>" maxlength="50">
                        </div>
                        <div id="columna2" ><label for="fec_nac">Fecha de nacimiento *:</label>
                            <input name="fechaNacimientoAsStr" type="text" required id="fechaNacimientoAsStr" value="<%=empleado.getFechaNacimientoAsStr()%>">
                        </div>
			
                </div>
                <div id="contenidos">
			<div id="columna1"> <label for="direccion">Direcci&oacute;n * :</label>
                            <input name="direccion" type="text" required class="form-control" value="<%=empleado.getDireccion()%>" maxlength="70">
                        </div>
			<div id="columna2" ><label for="email">Email * :</label>
                            <input name="email" type="email" required class="form-control" value="<%=empleado.getEmail()%>" maxlength="70" data-parsley-trigger="change">
                        </div>
		</div>    
            </div><!-- FIN TAB1 -->
            
            <div id="fragment-2">
                <!-- TAB 2-->
                <div id="contenidos">
                    <div id="columna1">
                        
                        <!-- INICIO Mostrar foto empleado-->
                        <%//write image
                            try{
                                System.out.println("====>Mostrar foto: "+empleado.getPathFoto());
                                BufferedImage bImage = ImageIO.read(new File(imagesPath+File.separator+empleado.getPathFoto()));//give the path of an image
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ImageIO.write( bImage, "jpg", baos );
                                baos.flush();
                                byte[] imageInByteArray = baos.toByteArray();
                                baos.close();                                   
                                String b64 = DatatypeConverter.printBase64Binary(imageInByteArray);
                                %>
                                <img  class="img-responsive" src="data:image/jpg;base64, <%=b64%>" width="120" height="120"/>                            
                                <% 
                            }catch(IOException e){
                              System.out.println("Error al mostrar imagen: "+e.getMessage());
                            } 
                        %>
                        Cambiar Foto:
                        <input type="radio" name="cambiarFoto" value="S" >Si<br>
                        <input type="radio" name="cambiarFoto" value="N" checked>No<br>
                        
                        <!-- FIN Mostrar foto empleado-->
                        <label for="foto">Foto :(S&oacute;lo se permite formato .png y .jpg): </label> 
                        <input type="file" name="foto">
                    </div>
                    <div id="columna2">
                        &nbsp;
                    </div>
		</div>
                        
                <div id="contenidos">
                     <div id="columna1">     
                         <label for="sexo">Sexo *:</label> 
                         <p>		
                             M: <input type="radio" name="sexo" id="M" value="M" required>
                             F: <input type="radio" name="sexo" id="F" value="F" >
                         </p>         
                     </div>
                     <div id="columna2">&nbsp;</div>
                </div>
                <div id="contenidos">
                    <div id="columna1"><label for="fono_fijo">Tel&eacute;fono fijo * :</label>
                        <input type="text" class="form-control" name="fono_fijo" required maxlength="20" value="<%=empleado.getFonoFijo()%>">
                    </div>
                    <div id="columna2"><label for="fono_movil">Tel&eacute;fono m&oacute;vil * :</label>
                        <input type="text" class="form-control" name="fono_movil" required maxlength="20" value="<%=empleado.getFonoMovil()%>">
                    </div>
		</div>        
                <div id="contenidos">
                    <div id="columna1"><label for="comunaId">Comuna</label>
                        <!--<select id="comunaId" name="comunaId" class="chosen-select" style="width:350px;" tabindex="2" required>-->
                        <select id="comunaId" name="comunaId" style="width:350px;" tabindex="2" required>
                          <option value="-1"></option>
                          <%
                              Iterator<ComunaVO> iteracomunas = comunas.iterator();
                              while(iteracomunas.hasNext() ) {
                                  ComunaVO auxcomuna = iteracomunas.next();
                                  %>
                                  <option value="<%=auxcomuna.getId()%>"><%=auxcomuna.getNombre()%></option>
                                  <%
                              }
                          %>
                        </select>
                    </div>
                        <div id="columna2">&nbsp;</div>
                    
                </div>        
                        
            </div><!-- FIN TAB2-->
            
            <div id="fragment-3">
              <!-- TAB 3-->
              <div id="contenidos">
                <div id="columna1"><label for="estado">Estado</label>
                  <select id="estado" name="estado" style="width:350px;" tabindex="2" required>
                    <option value="-1"></option>
                    <option value="1">Vigente</option>
                    <option value="2">No Vigente</option>
                  </select>
                </div>
                <div id="columna2">&nbsp;</div>
            </div>
            <div id="contenidos">   
                <div id="columna1"><label for="fec_contrato">Fecha inicio contrato *:</label>
                  <input name="fechaInicioContratoAsStr" 
                         type="text" 
                         id="fechaInicioContratoAsStr" 
                         value="<%=empleado.getFechaInicioContratoAsStr()%>"
                         required>
                </div>
                <div id="columna2">&nbsp;</div>
            </div>   
            
            <div id="contenidos">   
                    <div id="columna1">&nbsp;
                        <label for="sexo">Contrato indefinido *:</label>
                        <p>
                        Si: <input type="radio" name="contratoIndefinido" id="indefinidoS" value="S" checked required>
                        No: <input type="radio" name="contratoIndefinido" id="indefinidoN" value="N" >
                        </p>
                    </div>
                    <div id="columna2"><label for="fec_termino_contrato">Fecha termino contrato *:</label>
                      <input name="fechaTerminoContratoAsStr" 
                             type="text" 
                             required 
                             id="fechaTerminoContratoAsStr"
                             value="<%=empleado.getFechaTerminoContratoAsStr()%>"
                             required>
                    </div>
                </div>
                <div id="contenidos">   
                    <div id="columna1"><label for="art22">Art. 22 *:</label>
                      <p>
                        Si: <input type="radio" name="articulo22" id="art22S" value="S" required>
                        No: <input type="radio" name="articulo22" id="art22N" value="N" checked>
                        </p>
                    </div>
                    <div id="columna2">&nbsp;</div>
                </div>             
                         
                <div id="contenidos">         
                    <div id="columna1">
                        <%=empleado.getEmpresa().getNombre()%>
                        -&nbsp;<%=empleado.getDepartamento().getNombre()%>
                        -&nbsp;<%=empleado.getCentroCosto().getNombre()%>
                    </div>
                    <div id="columna2">&nbsp;</div>
                 </div>
                 <div id="contenidos">   
                    <div id="columna1">
                        Modificar Empresa - Depto - Centro Costo: 
                        NO<input type="radio" name="modificar" checked="checked" value="No"/>
                        SI<input type="radio" name="modificar" value="Si" />
                    </div>
                    <div id="columna2">
                        <div id="modificarDivNo" class="desc"></div>
                    </div>
                 </div>   
                 <div id="contenidos">
                     <div id="columna1">
                        <div id="modificarDivSi" class="desc">
                           <label for="empresaId">Empresa</label>
                           <select id="empresaId" name="empresaId" style="width:350px;" required>
                           <option value="-1"></option>
                               <%
                                   String selected="";
                                   Iterator<EmpresaVO> iteraempresas = empresas.iterator();
                                   while(iteraempresas.hasNext() ) {
                                       EmpresaVO auxempresa = iteraempresas.next();
                                       System.out.println("[GestionFemaseWeb]modificar_"
                                           + "empleado.jsp]empleado."
                                           + "empresa= "+empleado.getEmpresa().getId());
                                       selected = "";
                                       //if (empleado.getEmpresa().getId().compareTo(auxempresa.getId())==0) selected="selected";
                                       %>
                                       <option value="<%=auxempresa.getId()%>" <%=selected%>><%=auxempresa.getNombre()%></option>
                                       <%
                                   }
                               %>
                           </select>
                           <select id="deptoId" name="deptoId" style="width:350px;" required>
                               <option value="-1">--------</option>
                               <label for="cencoId">Centro de Costo</label>
                           </select>    
                           <select id="cencoId" name="cencoId" style="width:350px;" tabindex="2" required>
                             <option value="-1">--------</option>
                           </select>
                       </div>
                    </div>
                    <div id="columna2">&nbsp;</div>       
                 </div>       
                    
                
             <div id="contenidos">
                <div id="columna1">
                    <label for="idTurno">Turno</label>
                    <select id="idTurno" name="idTurno" style="width:350px;" required>
                    <!--<option value="-1"></option>-->
                        <%
                            Iterator<TurnoVO> iteraturnos = turnos.iterator();
                            while(iteraturnos.hasNext() ) {
                                TurnoVO auxturno = iteraturnos.next();
                                %>
                                <option value="<%=auxturno.getId()%>"><%=auxturno.getNombre()%></option>
                                <%
                            }
                        %>
                    </select>
                </div>
                <div id="columna2">
                <label for="autorizaausencias">Autoriza Ausencias *:</label>
                    <p>
                    Si: <input type="radio" name="autorizaAusencia" id="autorizaS" value="S" required>
                    No: <input type="radio" name="autorizaAusencia" id="autorizaN" value="N">
                    </p> 
                </div>
            </div>
            <div id="contenidos">
                <div id="columna1">
                    <label for="idCargo">Cargo</label>
                    <select id="idCargo" name="idCargo" style="width:350px;" required>
                    <!--<option value="-1"></option>-->
                        <%
                            Iterator<CargoVO> iteracargos = cargos.iterator();
                            while(iteracargos.hasNext() ) {
                                CargoVO auxcargo = iteracargos.next();
                                %>
                                <option value="<%=auxcargo.getId()%>"><%=auxcargo.getNombre()%></option>
                                <%
                            }
                        %>
                    </select>
                </div>
                <div id="columna2">&nbsp;</div>
            </div>              
        </div><!-- FIN TAB 3-->
        </div><!-- Fin Tabs con formulario -->
        
        <!-- Contenedor de botones-->
          <div id="contenedor">
	       <div id="contenidos">
			<div id="columna1"><small>* requerido</small></div>
			<div id="columna2"><input type="submit" class="button button-blue" value="Guardar"></div>
			<div id="columna3"><input name="botonvolver" type="button" value="Volver" class="button button-blue" onclick="volver();"></div>
		</div>
            </div><!-- Fin contenedor de divs con los campos de formulario -->
	<!-- Contenedor de botones-->
        </form>
  <!--</div>-->
  
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
  
  <script>
// just for the demos, avoids form submit
/*jQuery.validator.setDefaults({
  debug: true,
  success: "valid"
});*/
$( "#demo-form" ).validate({
  rules: {
    foto: {
      required: false,
      extension: "jpg|png"
    }
  }
});

$(function () {
  $('#demo-form').parsley().on('field:validated', function() {
    var ok = $('.parsley-error').length === 0;
    $('.bs-callout-info').toggleClass('hidden', !ok);
    $('.bs-callout-warning').toggleClass('hidden', ok);
  })
  
});

$( "#tabs" ).tabs(
{
  }
);

</script>
</body>
</html>