<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DetalleAusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.AusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>


<%
    List<UsuarioCentroCostoVO> cencos 
        = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
    String msgError = (String)request.getAttribute("msgerror");	
	String cencoId = (String)request.getAttribute("cencoId");
	System.out.println("[GestionFemaseWeb]crear_detalle_ausencia]cencoId attribute: " + cencoId);	
%>
<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta name="generator" content="jqueryform.com">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

  <title>Informe Horas Extras</title>
  
  
  <script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/bootstrap-datepicker.min.js"></script>
<script src="js/jquery.validate.min.js"></script>
<script src="js/additional-methods.min.js"></script>
<script src="js/jquery.scrollTo.min.js"></script>
<script src="vendor.js" ></script>

<script src="js/jquery.rut.js" type="text/javascript"></script>

<script src="jqueryform.com.min.js?ver=v2.0.3&id=jqueryform-d81043" ></script>
  
  <!-- Bootstrap -->
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link href="css/bootstrap-theme.min.css" rel="stylesheet">
  <link href="css/bootstrap-datepicker3.min.css" rel="stylesheet">
  

  <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

  <script>
        function replaceAll(originalString, find, replace) {
            return originalString.replace(new RegExp(find, 'g'), replace);
        };
      
        function limpiar(){
            document.location.reload(true);
        }
       
  </script>
  
  	<style type="text/css">
        body{
          background-color: transparent;
        }
        
        .jf-form{
          margin-top: 8px;
        }
        
        .jf-form > form{
          margin-bottom: 32px;
        }
        
        .jf-option-box{
          display: none;
          margin-left: 8px;
        }
        
        .jf-hide{
          display: none;
        }
        
        .jf-disabled {
            background-color: #eeeeee;
            opacity: 0.6;
            pointer-events: none;
        }
        
        /* 
        overwrite bootstrap default margin-left, because the <label> doesn't contain the <input> element.
        */
        .checkbox input[type=checkbox], .checkbox-inline input[type=checkbox], .radio input[type=radio], .radio-inline input[type=radio] {
          position: absolute;
          margin-top: 4px \9;
          margin-left: 0px;
        }
        
        div.form-group{
          /*padding: 8px 8px 4px 8px;*/
        }
        
        .mainDescription{
          margin-bottom: 10px;
        }
        
        p.description{
          margin:0px;
        }
        
        .responsive img{
          width: 100%;
        }
        
        p.error, p.validation-error{
          padding: 5px;
        }
        
        p.error{
          margin-top: 10px;
          color:#a94442;
        }
        
        p.server-error{
          font-weight: bold;
        }
        
        div.thumbnail{
          position: relative;
          text-align: center;
        }
        
        div.thumbnail.selected p{
          color: #ffffff;
        }
        
        div.thumbnail .glyphicon-ok-circle{
          position: absolute;
          top: 16px;
          left: 16px;
          color: #ffffff;
          font-size: 32px;
        }
        
        .jf-copyright{color: #f5f1f1; display: inline-block; margin: 16px;display:none;}
        
        .form-group.required .control-label:after {
            color: #dd0000;
            content: "*";
            margin-left: 6px;
        }
        
        .submit .btn.disabled, .submit .btn[disabled]{
          background: transparent;
          opacity: 0.75;
        }
        
        /* for image option with span text */
        .checkbox label > span, .radio label > span{
          display: block;
        }
        
        .form-group.inline .control-label,
        .form-group.col-1 .control-label,
        .form-group.col-2 .control-label,
        .form-group.col-3 .control-label
        {
          display: block;
        }
        
        .form-group.inline div.radio,
        .form-group.inline div.checkbox{
          display: inline-block;
        }
        
        .form-group.col-1 div.radio,
        .form-group.col-1 div.checkbox{
          display: block;
        }
        
        .form-group.col-2 div.radio,
        .form-group.col-2 div.checkbox{
          display: inline-flex;
          width: 48%;
        }
        
        .form-group.col-3 div.radio,
        .form-group.col-3 div.checkbox{
          display: inline-flex;
          width: 30%;
        }
        
        .clearfix:after {
           content: ".";
           visibility: hidden;
           display: block;
           height: 0;
           clear: both;
        }
        
        #errmsg1
        {
        color: red;
        }

		#errmsg2
        {
        color: red;
        }
	</style>


</head>

<body>

<!-- ----------------------------------------------- -->
<div class="container jf-form">
<form data-licenseKey="JF-9X6926878C359674T" 
      name="jqueryform-d81043" 
      id="jqueryform-d81043" 
      action='<%=request.getContextPath()%>/ReporteHorasExtras' 
      method='post' 
      novalidate autocomplete="on">
        <input type="hidden" name="method" value="validateForm">
        <input type="hidden" id="serverValidationFields" name="serverValidationFields" value="">
        <input type="hidden" name="action" id="action" value="create">
        <h1>Informe de horas extras</h1>
    <div class="form-group fechaTerminoContratoAsStr " data-fid="fechaTerminoContratoAsStr">
      <div class="form-group cencoId required" data-fid="cencoId">
        <label class="control-label" for="cencoId">Centro de Costo</label>
            <select 
                class="form-control" 
                id="cencoId" 
                name="cencoId"   
                data-rule-required="true" 
                data-msg-required="Requerido" >
                        <option value="-1" selected>----------</option>
                        <%
                        String valueCenco="";
                        String labelCenco="";    
                        Iterator<UsuarioCentroCostoVO> iteracencos = cencos.iterator();
                        while(iteracencos.hasNext() ) {
                            UsuarioCentroCostoVO auxCenco = iteracencos.next();
                            valueCenco = auxCenco.getEmpresaId() + "|" + auxCenco.getDeptoId() + "|" + auxCenco.getCcostoId();
                            labelCenco = "[" + auxCenco.getEmpresaNombre()+ "]," 
                                + "[" + auxCenco.getDeptoNombre()+ "],"
                                + "[" + auxCenco.getCcostoNombre()+ "]";
                        %>
                        <option value="<%=valueCenco%>"><%=labelCenco%></option>
                        <%
                    }
                %>
                </select>
          </div>
    </div>
    <div class="form-group rutEmpleado required" data-fid="rutEmpleado">
          <label class="control-label" for="rutEmpleado">Empleado(s)</label>
          <select name="rutEmpleado" size="10" multiple id="rutEmpleado"   
        data-rule-required="true" data-msg-required="Requerido" >
            <option value='-1'>Seleccione Empleado</option>
          </select>
    </div>
    <div class="form-group idAusencia required" data-fid="idAusencia">
    <div class="form-group fechaInicioAsStr required" data-fid="fechaInicioAsStr">
      <label class="control-label" for="fechaInicioAsStr">Fecha de inicio</label>
  <div>
    <input type="text" class="datepicker" id="fechaInicioAsStr" name="fechaInicioAsStr" value="" 
           placeholder="Ingrese fecha de inicio" 
    data-rule-required="true" data-msg-required="Requerido"  
  data-datepicker-format="dd-mm-yyyy"
  data-datepicker-startView="1" />
  </div>
</div>
<div class="form-group soloHoraInicio required" data-fid="soloHoraInicio"></div>
<div class="form-group fechaFinAsStr required" data-fid="fechaFinAsStr">
  <label class="control-label" for="fechaFinAsStr">Fecha fin</label>
  <div class="input-group date">
    <input type="text" class="datepicker" id="fechaFinAsStr" name="fechaFinAsStr" value=""  
        placeholder="Ingrese fecha fin" 
    data-rule-required="true" data-msg-required="Requerido"  
  data-datepicker-format="dd-mm-yyyy"
  data-datepicker-startView="1" />
  </div>
</div>
<div class="form-group submit f0 " data-fid="f0" style="position: relative;">
  <label class="control-label sr-only" for="f0" style="display: block;">Submit Button</label>

  <div class="progress" style="display: none; z-index: -1; position: absolute;">
    <div class="progress-bar progress-bar-striped active" role="progressbar" style="width:100%">
    </div>
  </div>

  <button type="button" id='btnGuardar' class="btn btn-primary btn-lg" style="z-index: 1;">
  		Generar Informe
  </button>
  <button type="button" class="btn btn-primary btn-lg" style="z-index: 1;" onclick="limpiar()">Limpiar </button>
  
</div>

<div class="submit" data-redirect="hola.jsp">
  <p class="error bg-warning" style="display:none;">
    </p>
</div>

</form>

</div>

<div class="container jf-thankyou" style="display:none;" data-redirect="" data-seconds="10">
  <h3>Datos ingresados correctamente</h3>
</div>
                
<form name="redirectForm" id="redirectForm" method="post" 
action="<%=request.getContextPath()%>/mantencion/detalle_ausencias.jsp">  
    <input type="hidden" name="cco" id="cco" value="<%=cencoId%>">  
</form>
<!-- ----------------------------------------------- -->




<!-- [ Start: iCheck support ] ---------------------------------- -->
<link href="css/_all.min.css" rel="stylesheet">
<style type="text/css">
/* overwrite bootstrap styles */
.checkbox input[type=checkbox], .checkbox-inline input[type=checkbox], .radio input[type=radio], .radio-inline input[type=radio] {
    position: relative;
    margin-top: 0px;
    margin-left: 2px;
}

.checkbox label, .radio label {
   padding-left: 4px;
}

</style>

<script src="js/icheck.min.js"></script>
<script type="text/javascript">


	;(function ($, undefined)
	{
	
	var checkers = '.icheckbox_flat-blue, .iradio_flat-blue';
	
	function initICheck( $input ){
	  $input.iCheck({
			checkboxClass: 'icheckbox_flat-blue',
			radioClass: 'iradio_flat-blue'
	  }).on('ifClicked', function(e){
		setTimeout( function(){
		  $(e.target).valid();
		  $(e.target).trigger('change').trigger('handleOptionBox');
		}, 250);
	  });
	}; // func

	//$('.jf-form .checkbox, .jf-form .radio')
	$('.jf-form input[type="checkbox"], .jf-form input[type="radio"]').each( function( i, e ){
		var $input = $(e), $div = $input.closest('.checkbox,.radio'), hasImg = $div.find('label > img').length;
		if( hasImg ){
	
			$input.css({ 'opacity': '0', 'position': 'absolute', 'left': '-1000px', 'right': '-1000px'} );
	
		}else{
	
			initICheck( $input );
	
			// IE11 and under, the table-cell makes the checkboxes/radio buttons not clickable
			var isWin = navigator.platform.indexOf('Win') > -1,
				isEdge = navigator.userAgent.indexOf('Edge/') > -1,
				noTableCell = isWin && !isEdge;
			if( !noTableCell ){
			  $div.find('label').css( { display: 'table-cell' } );
			  $(checkers).css( { display: 'table-cell' } );
			};
	
		};
	});

})(jQuery);

</script>
<!-- [ End: iCheck support ] ---------------------------------- -->


<!-- [ Start: Select2 support ] ---------------------------------- -->
<link rel="stylesheet" type="text/css" href="css/select2.min.css">
<script type="text/javascript" src="js/select2.full.min.js"></script>
<style type="text/css">
.select2-hidden-accessible{
	opacity: 0;
    width:1% !important;
}
.select2-container .select2-selection--single{
  /*height: 34px;
  padding-top: 2px;
  box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
  border: 1px solid #ccc;*/
}
.select2-container--default .select2-selection--single .select2-selection__arrow{
  top: 4px;
}
</style>
<script type="text/javascript">
;(function(){
	
	function templateResult (obj) {
	  if (!obj.id) { return obj.text; }

	  var img = $(obj.element).data('imgSrc');
	  if( img ){
	    return $( '<span><img src="' + img + '" class="img-flag" /> ' + obj.text + '</span>' );
	  };

	  return obj.text;
	};
	 
	/*$(".jf-form select").css('width', '50%'); // make it responsive
	$(".jf-form select").select2({
	  templateResult: templateResult
	}).change( function(e){
	  $(e.target).valid();
	});
	*/
})();
</script>
<!-- [ End: Select2 support ] ---------------------------------- -->

<script type="text/javascript">

    	// start jqueryform initialization
	// --------------------------------
	//JF.init('#jqueryform-d81043');

        $(document).ready(function() {
             //rutEmpleado
             
            //evento para combo centro costo
            $('#cencoId').change(function(event) {
                 //var tokenCenco = $('select#cencoId').val().split("|");
                 var empresaSelected  = null;
                 var deptoSelected    = null;
                 var cencoSelected    = $('select#cencoId').val();
                 
                 var sourceSelected = 'ausencias_detalle_crear';
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response) {
                        var select = $('#rutEmpleado');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Empleado</option>";
                        for (i=0; i<response.length; i++) {
                           var auxNombre = '['+response[i].rut+'] '+response[i].nombres + 
                                ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                            newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                        }
                        $('#rutEmpleado').html(newoption);
                 });
		
             });

            $("#jqueryform-d81043").validate();
			<%if (msgError != null){%>
				$('.modal').toggleClass('is-visible');
			<%}%>
			$('.modal-toggle').on('click', function(e) {
                //alert('aquii');
                e.preventDefault();
                $('.modal').toggleClass('is-visible');
            });
            function closeDialog(){
                $('.modal').toggleClass('is-visible');
            }
			
            //validacion HORA INICIO
            /*$("#soloHoraInicio").change(function (e) {
                var valor = parseInt($(this).val());
                if (valor > 23){
                    $("#errmsg1").html("Hora no valida").show().fadeOut(3000);
                    return false;
                }
            });*/
            	
            //btnGuardar	
            $("#btnGuardar").click(function(event) {
				
				$("#jqueryform-d81043").submit();
                /*var horaInicio = $('#soloHoraInicio').val();
                
                if (ausenciaPorHora === 'S') {//ausencia por hora				
                    if (horaInicio === '' || minsInicio === '' || horaFin === '' || minsFin === '') {
                      alert('Debe completar horas y minutos.');
                      event.preventDefault();
                    } else {
                      $("#jqueryform-d81043").submit();
                    }
                }else $("#jqueryform-d81043").submit();*/
            });
					
        });
</script>
<div class="modal">
    <div class="modal-overlay modal-toggle"></div>
    <div class="modal-wrapper modal-transition">
      <div class="modal-header">
        <button class="modal-close modal-toggle"><svg class="icon-close icon" viewBox="0 0 32 32"><use xlink:href="#icon-close"></use></svg></button>
        <h2 class="modal-heading">Error</h2>
      </div>
      
      <div class="modal-body">
        <div class="modal-content">
          <p>No se pudo ingresar el registro. Ya existe una ausencia para las fechas especificadas</p>
          <button onclick="closeDialog()">Cerrar</button>
        </div>
      </div>
    </div>
  </div>
  </body>
</html>