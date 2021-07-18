<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>

<%
    //ArrayList<DispositivoVO> dispositivos  = 
    //    (ArrayList<DispositivoVO>)session.getAttribute("all_dispositivos");
    //para llenar lista de elementos disponibles
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<DepartamentoVO> departamentos   = (List<DepartamentoVO>)session.getAttribute("departamentos");
    List<CentroCostoVO> cencos   = (List<CentroCostoVO>)session.getAttribute("cencos");
    List<CargoVO> cargos   = (List<CargoVO>)session.getAttribute("cargos");
    
    TurnoVO turnoSelected = (TurnoVO)session.getAttribute("turnoSelected");
    List<EmpleadoVO> asignadosTurno=(List<EmpleadoVO>)session.getAttribute("asignados_turno");
    List<EmpleadoVO> noAsignadosTurno=(List<EmpleadoVO>)session.getAttribute("noasignados_turno");
    
    //para llenar listas de elementos asignados
    /*
    LinkedHashMap<String,DispositivoEmpresaVO> empresasAsignadas=new LinkedHashMap<String,DispositivoEmpresaVO>();
    LinkedHashMap<String,DispositivoDepartamentoVO> deptosAsignados=new LinkedHashMap<String,DispositivoDepartamentoVO>();
    LinkedHashMap<String,DispositivoCentroCostoVO> cencosAsignados=new LinkedHashMap<String,DispositivoCentroCostoVO>();
    String idDeviceSelected=null;    
    if (dispositivoSelected != null){
        empresasAsignadas = dispositivoSelected.getEmpresas();
        deptosAsignados = dispositivoSelected.getDepartamentos();
        cencosAsignados = dispositivoSelected.getCencos();
        idDeviceSelected = dispositivoSelected.getId();
    }
        
    if (idDeviceSelected == null) idDeviceSelected = "-1";
	System.out.println("----->Id Dispositivo seleccionado: " +idDeviceSelected);
   */	                  
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Asignacion Dispositivos</title>
  <meta name="description" content="Admin Accesos Perfil de usuario">
    <link href="<%=request.getContextPath()%>/css-varios/jquerysctipttop.css" rel="stylesheet" type="text/css">
  <script src="<%=request.getContextPath()%>/js-varios/jquery-1.12.4.min.js"></script>
  <script src="<%=request.getContextPath()%>/js-varios/bootstrap.min.js"></script>
  <script src="js/jquery.selectlistactions.js"></script>
  
  <link rel="stylesheet" href="jquery-plugins/chosen_v1.6.2/chosen.css">
  
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
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#deptoId').html(newoption);
                 });
             });
             //evento para combo depto
             $('#deptoId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected
                 }, function(response) {
                        var select = $('#cencoId');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Centro de costo</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#cencoId').html(newoption);
                 });
             });
         });
    
	function validateform(){
		
            $("#empleados_selected option").prop("selected",true);
            document.getElementById("action").value='save_asignacion';
            document.myForm.submit();
		
	}
	
	function validateBusqueda(){
            var success=true;
            if (success){
                //alert('submit mostrar asignacion existente...');
                document.getElementById("action").value='load_asignacion';
                document.myForm.submit();
            }
			
	}
      
  </script>
  
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css-varios/bootstrap.min.css">
  <link rel="stylesheet" href="css/site.css">

  <!-- estilo para botones -->
  <link rel="stylesheet"
  href="jquery-plugins/chosen_v1.6.2/docsupport/style.css">
  
  <style type="text/css" media="all">
    /* fix rtl for demo */
    .chosen-rtl .chosen-drop { left: -9000px; }
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

<body>
    <form name="myForm" action="<%=request.getContextPath()%>/TurnosController" method="POST">
	<!--<div class="container">-->
	
            <h2>Turno: <%=turnoSelected.getNombre()%></h2>

             <div id="contenidos">
                <div id="columna1">
                    <label for="empresaId">Empresa</label>
                    <select id="empresaId" name="empresaId" style="width:350px;" required>
                    <option value="-1"></option>
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
                </div>
                <div id="columna2">&nbsp;</div>
                    
             </div>   
              <div id="contenidos">
                <div id="columna1">       
                    <label for="deptoId">Departamento</label>
                    <select id="deptoId" name="deptoId" style="width:350px;" required>
                        <option value="-1">--------</option>
                    </select>
                </div>
                <div id="columna2">
                    <label for="cencoId">Centro de Costo</label>
                        <select id="cencoId" name="cencoId" style="width:350px;" tabindex="2" required>
                          <option value="-1">--------</option>
                        </select>
                </div>  
                <div id="contenidos">
                    <div id="columna1">         
                        <label for="cargoId">Cargo</label>
                        <select id="cargoId" name="cargoId" style="width:350px;" required>
                        <option value="-1"></option>
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
                    <div id="columna2">
                        <input name="action" type="hidden" id="action" value="load_asignacion">
                        <input name="idTurno" type="hidden" id="idTurno" value="<%=turnoSelected.getId()%>">
                    </div>
                </div>    
                
            </div>

            <div>
                <span class="clearfix">
                    <a class="button button-blue" href="javascript:;" onclick="validateBusqueda();">Ver empleados</a>
                </span>
            </div>
	
            <p>&nbsp;</p>
            <div class="row style-select">
                
                <!-- INICIO BLOQUE ASIGNACION EMPRESAS-->
                <div class="col-md-12">
                    <div class="subject-info-box-1">
                        <label>Asignacion Empleados<br>Empleados disponibles</label>
                        <select name="empleados_base" multiple class="form-control" id="empleados_base">
                            <%  
                                if (noAsignadosTurno != null && noAsignadosTurno.size() > 0){
                                    EmpleadoVO empleadodisponible = null;
                                    Iterator<EmpleadoVO> itempleadosdisponibles = noAsignadosTurno.iterator();
                                    while(itempleadosdisponibles.hasNext()){
                                        empleadodisponible = itempleadosdisponibles.next();
                                        //if (!empresasAsignadas.containsKey(empresadisponible.getId())){
                                        %>
                                            <option value="<%=empleadodisponible.getRut()%>"><%=empleadodisponible.getNombres()%></option>
                                        <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="subject-info-arrows text-center">
                        <br /><br />
                        <input type='button' id='btnAllRightEmpleados' value='>>' class="btn btn-default" /><br />
                        <input type='button' id='btnRightEmpleados' value='>' class="btn btn-default" /><br />
                        <input type='button' id='btnLeftEmpleados' value='<' class="btn btn-default" /><br />
                        <input type='button' id='btnAllLeftEmpleados' value='<<' class="btn btn-default" />
                    </div>

                    <div class="subject-info-box-2">
                        <label>Empleados asignados</label>
                        <select name="empleados_selected" multiple class="form-control" 
                            id="empleados_selected">
                            <%  
                                if (asignadosTurno != null && asignadosTurno.size() > 0){
                                    EmpleadoVO empleadoAsignado;
                                    Iterator<EmpleadoVO> itasignados = asignadosTurno.iterator();
                                    while(itasignados.hasNext()){
                                         empleadoAsignado = itasignados.next();
                                    %>
                                    <option value="<%=empleadoAsignado.getRut()%>"><%=empleadoAsignado.getNombres()%></option>
                                    <%}
                            }%>
                        </select>
                    </div>
                </div>
                <!-- FIN BLOQUE ASIGNACION EMPRESAS-->
                
            </div>            
                <!-- BOTONES-->            
                <div class="clearfix">
                    <!-- botones -->
                    <a class="button button-blue" href="javascript:;" onclick="validateform();">Guardar</a>
                </div>
                
            
	<!--</div>-->
	
	<script>
       	
        //****botones empresas
        $('#btnRightEmpleados').click(function (e) {
            $('select').moveToListAndDelete('#empleados_base', '#empleados_selected');
            e.preventDefault();
        });

        $('#btnAllRightEmpleados').click(function (e) {
            $('select').moveAllToListAndDelete('#empleados_base', '#empleados_selected');
            e.preventDefault();
        });

        $('#btnLeftEmpleados').click(function (e) {
            $('select').moveToListAndDelete('#empleados_selected', '#empleados_base');
            e.preventDefault();
        });

        $('#btnAllLeftEmpleados').click(function (e) {
            $('select').moveAllToListAndDelete('#empleados_selected', '#empleados_base');
            e.preventDefault();
        });
        
        
    </script>
        </form>
        
        <script type="text/javascript"
  src="jquery-plugins/chosen_v1.6.2/chosen.jquery.js">
  </script>
  <script type="text/javascript"
  src="jquery-plugins/chosen_v1.6.2/docsupport/prism.js" charset="utf-8">
  </script>
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
</body>
</html>