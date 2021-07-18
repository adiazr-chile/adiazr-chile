<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.DispositivoCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DispositivoDepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="java.util.List"%>
<%@page import="cl.femase.gestionweb.vo.DispositivoEmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.DispositivoVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>

<%
    ArrayList<DispositivoVO> dispositivos  = 
        (ArrayList<DispositivoVO>)session.getAttribute("all_dispositivos");
    //para llenar lista de elementos disponibles
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<DepartamentoVO> departamentos   = (List<DepartamentoVO>)session.getAttribute("departamentos");
    List<CentroCostoVO> cencos   = (List<CentroCostoVO>)session.getAttribute("cencos");
    
    DispositivoVO dispositivoSelected = (DispositivoVO)request.getAttribute("dispositivo_selected");
    
    //para llenar listas de elementos asignados
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
    
	function validateform(){
		//var items = document.getElementById("deptos_selected").options.length;
		//if (items > 0){
                var selectidx = document.getElementById("device_id").selectedIndex;
                if (document.getElementById("device_id").options[selectidx].value==="-1"){
                    alert ('Seleccione un dispositivo...');
                    document.getElementById("device_id").focus();
                }else{ 
                    $("#deptos_selected option").prop("selected",true);
                    $("#cencos_selected option").prop("selected",true);
                    $("#empresas_selected option").prop("selected",true);
                    
                    document.getElementById("action").value='save_asignacion';
                    //alert('submit guardar asignacion de empresas, deptos y cencos!!');
                    document.myForm.submit();
                }
		/*}else {
			alert('No hay departamentos seleccionados...');
			document.getElementById("deptos_selected").focus();
		}*/
	}
	  
	function validateDispositivo(){
            var selectidx = document.getElementById("device_id").selectedIndex;
            //alert('index selected= '+selectidx+', valor: '+document.getElementById("device_id").options[selectidx].value);
            var success=true;
            if (document.getElementById("device_id").options[selectidx].value==="-1"){ 
                    alert ('Seleccione un dispositivo...');
                    document.getElementById("device_id").focus();
                    success=false;
            } 

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
   body { background-color:#fafafa; font-family: times, serif; font-size:12pt; font-style:normal;}
   .container { margin:0px auto; max-width:660px;}
  </style>
  
  
  <!--[if lt IE 9]>
  <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>

<body>
    <form name="myForm" action="<%=request.getContextPath()%>/DispositivosController" method="POST">
	<div class="container">
	
            <h2>Asignacion de dispositivos</h2>

            <div>
                <em>Dispositivo</em> 

                <select id="device_id" name="device_id" class="chosen-select" style="width:350px;" tabindex="2">
                  <option value="-1"></option>

                  <%  DispositivoVO obj1=null;
                      String straux="";
                      Iterator<DispositivoVO> iteradispositivos = dispositivos.iterator();
                      while(iteradispositivos.hasNext()){
                          obj1 = iteradispositivos.next();
                          straux="";
                          if (idDeviceSelected.compareTo(obj1.getId()) == 0){    
                                  straux="selected";
                          }%>
                          <option value="<%=obj1.getId()%>" <%=straux%>><%=obj1.getId()%></option>
                      <%}%>

                </select>
                <input name="action" type="hidden" id="action" value="load_asignacion">
            </div>

            <div>
                <span class="clearfix">
                    <a class="button button-blue" href="javascript:;" onclick="validateDispositivo();">Ver asignacion departamentos</a>
                </span>
            </div>
	
            <p>&nbsp;</p>
            <div class="row style-select">
                
                <!-- INICIO BLOQUE ASIGNACION EMPRESAS-->
                <div class="col-md-12">
                    <div class="subject-info-box-1">
                        <label>Asignacion Empresas<br>Empresas disponibles</label>
                        <select name="empresas_base" multiple class="form-control" id="empresas_base">
                            <%  
                                EmpresaVO empresadisponible = null;
                                Iterator<EmpresaVO> itempresasdisponibles = empresas.iterator();
                                while(itempresasdisponibles.hasNext()){
                                    empresadisponible = itempresasdisponibles.next();
                                    if (!empresasAsignadas.containsKey(empresadisponible.getId())){
                                    %>
                                        <option value="<%=empresadisponible.getId()%>"><%=empresadisponible.getNombre()%></option>
                                    <%}
                                }%>
                        </select>
                    </div>

                    <div class="subject-info-arrows text-center">
                        <br /><br />
                        <input type='button' id='btnAllRightEmpresas' value='>>' class="btn btn-default" /><br />
                        <input type='button' id='btnRightEmpresas' value='>' class="btn btn-default" /><br />
                        <input type='button' id='btnLeftEmpresas' value='<' class="btn btn-default" /><br />
                        <input type='button' id='btnAllLeftEmpresas' value='<<' class="btn btn-default" />
                    </div>

                    <div class="subject-info-box-2">
                        <label>Empresas asignadas</label>
                        <select name="empresas_selected" multiple class="form-control" 
                            id="empresas_selected">
                            <%   Collection c44 = empresasAsignadas.values();
                            DispositivoEmpresaVO empresaAsignada=null;
                            Iterator<DispositivoEmpresaVO> iteraempresasAsignadas = c44.iterator();
                            while(iteraempresasAsignadas.hasNext()){
                                    empresaAsignada = iteraempresasAsignadas.next();

                            %>
                            <option value="<%=empresaAsignada.getEmpresaId()%>"><%=empresaAsignada.getEmpresaNombre()%></option>
                            <%}%>
                        </select>
                    </div>
                </div>
                <!-- FIN BLOQUE ASIGNACION EMPRESAS-->
                
                
                <!-- INICIO BLOQUE ASIGNACION DEPARTAMENTOS-->
                <div class="col-md-12">
                    <div class="subject-info-box-1">
                        <label>Asignacion Departamentos<br>Departamentos disponibles</label>
                        <select name="deptos_base" multiple class="form-control" id="deptos_base">
                            <%  
                                DepartamentoVO deptodisponible = null;
                                Iterator<DepartamentoVO> itdeptosdisponibles = departamentos.iterator();
                                while(itdeptosdisponibles.hasNext()){
                                    deptodisponible = itdeptosdisponibles.next();
                                    if (!deptosAsignados.containsKey(deptodisponible.getId())){
                                    %>
                                        <option value="<%=deptodisponible.getId()%>"><%=deptodisponible.getNombre()%></option>
                                    <%}
                                }%>
                        </select>
                    </div>

                    <div class="subject-info-arrows text-center">
                        <br /><br />
                        <input type='button' id='btnAllRight' value='>>' class="btn btn-default" /><br />
                        <input type='button' id='btnRight' value='>' class="btn btn-default" /><br />
                        <input type='button' id='btnLeft' value='<' class="btn btn-default" /><br />
                        <input type='button' id='btnAllLeft' value='<<' class="btn btn-default" />
                    </div>

                    <div class="subject-info-box-2">
                        <label>Departamentos asignados</label>
                        <select name="deptos_selected" multiple class="form-control" 
                            id="deptos_selected">
                            <%   Collection c4 = deptosAsignados.values();
                            DispositivoDepartamentoVO deptoAsignado=null;
                            Iterator<DispositivoDepartamentoVO> iteradeptosAsignados = c4.iterator();
                            while(iteradeptosAsignados.hasNext()){
                                    deptoAsignado = iteradeptosAsignados.next();

                            %>
                            <option value="<%=deptoAsignado.getDeptoId()%>"><%=deptoAsignado.getDeptoNombre()%></option>
                            <%}%>
                        </select>
                    </div>
                </div>
                <!-- FIN BLOQUE ASIGNACION DEPARTAMENTOS-->
                
                <!-- INICIO BLOQUE ASIGNACION CENTROS COSTO-->
                <div class="col-md-12">
                    <div class="subject-info-box-1">
                        <label>Asignacion Centros de Costo<br>Centros de Costo disponibles</label>
                        <select name="cencos_base" multiple class="form-control" id="cencos_base">
                            <%  
                                CentroCostoVO cencodisponible = null;
                                Iterator<CentroCostoVO> itcencosdisponibles = cencos.iterator();
                                while(itcencosdisponibles.hasNext()){
                                    cencodisponible = itcencosdisponibles.next();
                                    if (!cencosAsignados.containsKey(""+cencodisponible.getId())){
                                    %>
                                        <option value="<%=cencodisponible.getId()%>"><%=cencodisponible.getNombre()%></option>
                                    <%}
                                }%>
                        </select>
                    </div>

                    <div class="subject-info-arrows text-center">
                        <br /><br />
                        <input type='button' id='btnAllRightCencos' value='>>' class="btn btn-default" /><br />
                        <input type='button' id='btnRightCencos' value='>' class="btn btn-default" /><br />
                        <input type='button' id='btnLeftCencos' value='<' class="btn btn-default" /><br />
                        <input type='button' id='btnAllLeftCencos' value='<<' class="btn btn-default" />
                    </div>

                    <div class="subject-info-box-2">
                        <label>Centros de Costo asignados</label>
                        <select name="cencos_selected" multiple class="form-control" 
                            id="cencos_selected">
                            <%   Collection collcencos = cencosAsignados.values();
                            DispositivoCentroCostoVO cencoAsignado=null;
                            Iterator<DispositivoCentroCostoVO> iteracencosAsignados = collcencos.iterator();
                            while(iteracencosAsignados.hasNext()){
                                cencoAsignado = iteracencosAsignados.next();
                            %>
                            <option value="<%=cencoAsignado.getCencoId()%>"><%=cencoAsignado.getCencoNombre()%></option>
                            <%}%>
                        </select>
                    </div>
                </div>
                <!-- FIN BLOQUE ASIGNACION CENTROS COSTO-->
                
                
            </div>            
                <!-- BOTONES-->            
                <div class="clearfix">
                    <!-- botones -->
                    <a class="button button-blue" href="javascript:;" onclick="validateform();">Guardar</a>
                </div>
                
            
	</div>
	
	<script>
       	
        $('#btnRight').click(function (e) {
            $('select').moveToListAndDelete('#deptos_base', '#deptos_selected');
            e.preventDefault();
        });

        $('#btnAllRight').click(function (e) {
            $('select').moveAllToListAndDelete('#deptos_base', '#deptos_selected');
            e.preventDefault();
        });

        $('#btnLeft').click(function (e) {
            $('select').moveToListAndDelete('#deptos_selected', '#deptos_base');
            e.preventDefault();
        });

        $('#btnAllLeft').click(function (e) {
            $('select').moveAllToListAndDelete('#deptos_selected', '#deptos_base');
            e.preventDefault();
        });
        
        //****botones empresas
        $('#btnRightEmpresas').click(function (e) {
            $('select').moveToListAndDelete('#empresas_base', '#empresas_selected');
            e.preventDefault();
        });

        $('#btnAllRightEmpresas').click(function (e) {
            $('select').moveAllToListAndDelete('#empresas_base', '#empresas_selected');
            e.preventDefault();
        });

        $('#btnLeftEmpresas').click(function (e) {
            $('select').moveToListAndDelete('#empresas_selected', '#empresas_base');
            e.preventDefault();
        });

        $('#btnAllLeftEmpresas').click(function (e) {
            $('select').moveAllToListAndDelete('#empresas_selected', '#empresas_base');
            e.preventDefault();
        });
        
        //botones centros costo
        $('#btnRightCencos').click(function (e) {
            $('select').moveToListAndDelete('#cencos_base', '#cencos_selected');
            e.preventDefault();
        });

        $('#btnAllRightCencos').click(function (e) {
            $('select').moveAllToListAndDelete('#cencos_base', '#cencos_selected');
            e.preventDefault();
        });

        $('#btnLeftCencos').click(function (e) {
            $('select').moveToListAndDelete('#cencos_selected', '#cencos_base');
            e.preventDefault();
        });

        $('#btnAllLeftCencos').click(function (e) {
            $('select').moveAllToListAndDelete('#cencos_selected', '#cencos_base');
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