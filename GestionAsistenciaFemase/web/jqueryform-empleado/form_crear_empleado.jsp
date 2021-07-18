<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.ProveedorCorreoVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.ComunaVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>


<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<ProveedorCorreoVO> proveedoresCorreo   = (List<ProveedorCorreoVO>)session.getAttribute("proveedores_correo");
    List<ComunaVO> comunas   = (List<ComunaVO>)session.getAttribute("comunas");  
    List<CargoVO> cargos   = (List<CargoVO>)session.getAttribute("cargos");
    
%>
<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta name="generator" content="jqueryform.com">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

  <title>Crear empleado</title>
  

  <!-- Bootstrap -->
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link href="css/bootstrap-theme.min.css" rel="stylesheet">
  <link href="css/bootstrap-datepicker3.min.css" rel="stylesheet">
  

  <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

  <script type="text/javascript" src="../js/validarut.js"></script>
  
  <script>
        function replaceAll(originalString, find, replace) {
            return originalString.replace(new RegExp(find, 'g'), replace);
        };
      
        function validaForm(){
            if (validateRut('rut')) {
                var auxEmail    = document.getElementById('email').value;
                var emailDomain = document.getElementById('email_domain').value;
                
                if (auxEmail === ''){
                    alert('Debe ingresar un email');
                    document.getElementById('email').focus();
                    return false;
                }else if (emailDomain === '-1'){
                    alert('Seleccione Servicio de correo');
                    document.getElementById('email_domain').focus();
                    return false;
                }else{
                    document.getElementById('email').value = auxEmail + '@' + emailDomain;
                }
                
                /*
                var auxRut = document.getElementById('rut').value;//con puntos y guion
                var auxNumFicha = document.getElementById('cod_interno').value;//solo con el guion
                var soloRut = auxRut.replace(/./g, "");
                var newchar = '';
                soloRut = auxRut.split('.').join(newchar);
                var numFichaCorrecto = auxNumFicha.startsWith(soloRut);//true or false
                //alert('rut original: ' + auxRut + ', soloRut:' + soloRut+ ', numFicha:' + auxNumFicha+', numFichaCorrecto:' + numFichaCorrecto); 
                if (!numFichaCorrecto) {
                    alert('Num Ficha no corresponde al Rut.');
                    document.getElementById('cod_interno').focus();
                    return false;
                }
                //return numFichaCorrecto;//comentar
                */
            }else {
                document.getElementById('rut').focus();
                return false;
            }
        }
        
        function validateRut(fieldNameRut){
            var casillaRut = document.getElementById(fieldNameRut);
            return Rut(casillaRut);
        }
    
    /*
        function validateRut(casilla) {
            
            var casillaRut = document.getElementById(casilla);
            var valor = casillaRut.value.trim().replace(/[-.\s]/g, '');
            if(valor.length === 0) {
                return true;
            }

            // Aislar Cuerpo y D�gito Verificador
            var cuerpo = valor.slice(0,-1);
            var dv = valor.slice(-1).toUpperCase();
            var hold_dv = dv;

            // Si no cumple con el m�nimo ej. (n.nnn.nnn or nn.nnn.nnn)
            if(cuerpo.length < 7 || cuerpo.length > 8) 
                    return false;

            // Calcular D�gito Verificador
        var suma = 0;
        var multiplo = 2;

        // Para cada d�gito del Cuerpo
        for(i=1; i<=cuerpo.length; i++) {
            // Obtener su Producto con el M�ltiplo Correspondiente
            index = multiplo * valor.charAt(cuerpo.length - i);

            // Sumar al Contador General
            suma = suma + index;

            // Consolidar M�ltiplo dentro del rango [2,7]
            if(multiplo < 7) { 
                    multiplo = multiplo + 1; 
            } 
            else { 
                    multiplo = 2; 
            }
        }

            // Calcular D�gito Verificador en base al M�dulo 11
            dvEsperado = 11 - (suma % 11);

            // Casos Especiales (0 y K)
            dv = (dv == 'K') ? 10 : dv;
            dv = (dv == 0) ? 11 : dv;

            // Validar que el Cuerpo coincide con su D�gito Verificador
            if(dvEsperado != dv) return false; 
            //alert('cuerpo rut: '+cuerpo);
            //seteo num ficha con el mismo valor del rut
            //document.getElementById('cod_interno').value=cuerpo;    
            casillaRut.value = formatearMillones(cuerpo) + "-" + hold_dv;
            return true; 
        }
*/
        function setNumFicha(rutFormateado){
            var rutsinformato = rutFormateado.split('.').join('');
            document.getElementById('cod_interno').value=rutsinformato;
        }

        function volver(){
            var auxEmpresaId = $('#empresaId').val();
            var auxDeptoId  = $('#deptoId').val();
            var auxCencoId  = $('#cencoId').val();
            var cencoKey = auxEmpresaId+'|'+auxDeptoId+'|'+auxCencoId;
            
            $('#cco').val(cencoKey);
            document.getElementById("redirectForm").submit();
        }
        
  </script>
  
  <link href="vendor.css" rel="stylesheet">

<style type="text/css">
body{
  background-color: transparent;
}

.jf-form{
  margin-top: 28px;
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
  padding: 0px 7px 0px 0px;
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

</style>


</head>

<body>


<!-- ----------------------------------------------- -->
<div class="container jf-form">
<form data-licenseKey="JF-9X6926878C359674T" 
      name="jqueryform-d81043" 
      id="jqueryform-d81043" 
      action='<%=request.getContextPath()%>/EmpleadosController' 
      method='post' 
      enctype='multipart/form-data' 
      novalidate autocomplete="on" onSubmit="return validaForm()">
        <input type="hidden" name="method" value="validateForm">
        <input type="hidden" id="serverValidationFields" name="serverValidationFields" value="">
        <input type="hidden" name="action" id="action" value="create">
        <h1><span class="h1">Crear Empleado</span></h1>

<div class="form-group rut required" data-fid="rut">
  <label class="control-label" for="rut">RUN</label>

<input type="text"  id="rut" name="rut"  class="form-control-short required rut"
       value=""   
       placeholder="99.999.999-K" 
       data-rule-minlength="9"  
       data-rule-maxlength="12"  
       data-rule-required="true" data-msg-required="Este campo es obligatorio"  
       data-mask="AA.AAA.AAA-A"
       onfocusout="setNumFicha(this.value);"
       />

</div>

<div class="form-group cod_interno required" data-fid="cod_interno">
  <label class="control-label" for="cod_interno">Num Ficha</label>

<input 
       name="cod_interno" type="text" class="text-uppercase"  
       id="cod_interno"
       placeholder="99999999-K"
       value="" size="12"
       maxlength="10"   
       readonly="yes" 
        data-rule-minlength="7"  
        data-rule-maxlength="10"  
        data-rule-required="true" data-msg-required="Este campo es obligatorio"  
        />
<input name="cod_interno_adicional" type="text" class="text-uppercase" id="cod_interno_adicional" placeholder="B" value="" size="3" maxlength="1"/>

</div>




<div class="form-group nombres required" data-fid="nombres">
  <label class="control-label" for="nombres">Nombres</label>

<div class="input-group"><span class="input-group-addon left"><i class="glyphicon glyphicon-user"></i> </span>
<input type="text" class="form-control" id="nombres" name="nombres" value="" maxlength="70"  placeholder="Ingrese nombres" 
    data-rule-required="true" data-msg-required="Requerido" 
    data-rule-maxlength="70" data-msg-maxlength="No debe exceder de {0} caracteres"   />
</div>

  
</div>




<div class="form-group apePaterno required" data-fid="apePaterno">
  <label class="control-label" for="apePaterno">Apellido Paterno</label>


<input type="text" class="form-control" id="apePaterno" name="apePaterno" value="" maxlength="50"  placeholder="Ingrese apellido paterno" 
    data-rule-required="true" data-msg-required="Requerido" 
    data-rule-maxlength="50"   />


  
</div>




<div class="form-group apeMaterno required" data-fid="apeMaterno">
  <label class="control-label" for="apeMaterno">Apellido Materno</label>


<input type="text" class="form-control" id="apeMaterno" name="apeMaterno" value="" maxlength="50"  placeholder="Ingrese apellido materno" 
    data-rule-required="true" data-msg-required="Requerido" 
    data-rule-maxlength="50" data-msg-maxlength="No debe exceder de {0} caracteres"   />


  
</div>




<div class="form-group fechaNacimientoAsStr required" data-fid="fechaNacimientoAsStr">
  <label class="control-label" for="fechaNacimientoAsStr">Fecha de nacimiento</label>

<div class="input-group date">
<input type="text" class="form-control datepicker" id="fechaNacimientoAsStr" name="fechaNacimientoAsStr" value=""  placeholder="Ingrese fecha de nacimiento" 
    data-rule-required="true" data-msg-required="Requerido"  
  data-datepicker-format="dd-mm-yyyy"
  data-datepicker-startView="1" />

</div>
  
</div>




<div class="form-group direccion required" data-fid="direccion">
  <label class="control-label" for="direccion">Direcci&oacute;n</label>


<input type="text" class="form-control" id="direccion" name="direccion" value="" maxlength="70"  placeholder="Ingrese Direccion. Por ej.: Pasaje Los Boldos 1245, depto 487" 
    data-rule-required="true" data-msg-required="Requerido" 
    data-rule-maxlength="70" data-msg-maxlength="No debe exceder de {0} caracteres"   />


  
</div>




<div class="email required" data-fid="email">
  <label class="control-label" for="email">Email</label>

<div >
		<input type="email" 
                    id="email" 
                    name="email" 
                    value="" 
                    placeholder="Ingrese correo electronico" 
                    data-rule-required="true" data-msg-required="Requerido" 
                    data-rule-maxlength="70" data-msg-maxlength="No debe exceder de {0} caracteres"   />
		@
                    <select id="email_domain" name="email_domain"   
                        data-rule-required="true" data-msg-required="Requerido" >
                      <option selected value="-1">- Seleccione servicio de correo -</option>
                        <%
                            Iterator<ProveedorCorreoVO> iteraservidores = proveedoresCorreo.iterator();
                            while(iteraservidores.hasNext() ) {
                                ProveedorCorreoVO aux1 = iteraservidores.next();
                                %>
                                <option value="<%=aux1.getDomain()%>"><%=aux1.getDomain()%></option>
                                <%
                                    
                            }
                        %>
          </select>
</div>
  
</div>




<div class="form-group foto" data-fid="foto">
  <label class="control-label" for="foto">Fotograf&iacute;a</label>

<input type="file" class="form-control" id="foto" name="foto"  value=""   
    data-rule-required="false" data-msg-required="Requerido"     data-browseLabel="Seleccione Archivo" data-showUpload="false" data-showZoom="false"/>
  
</div>




<div class="form-group sexo required" data-fid="sexo">
  <label class="control-label" for="sexo">Sexo</label>
    <select class="form-control" id="sexo" name="sexo"   
    data-rule-required="true" data-msg-required="Requerido" >
        <option  value="M" selected>Masculino</option>
        <option  value="F">Femenino</option>
    </select>
</div>


<div class="form-group fono_fijo required" data-fid="fono_fijo">
  <label class="control-label" for="fono_fijo">Tel&eacute;fono Fijo</label>

<div class="input-group"><span class="input-group-addon left"><i class="glyphicon glyphicon-earphone"></i> </span>
<input type="tel" 
    class="form-control" 
    id="fono_fijo" 
    name="fono_fijo" 
    value=""   
    maxlength="11" 
	placeholder="123 456 789" 
    data-rule-pattern="[0-9 -+.]+" 
    data-msg-pattern="Num movil invalido"  
    data-rule-phonenumber="true" 
    data-rule-maxlength="11" data-msg-maxlength="Maximo de {0} números"  
    data-rule-minlength="11" data-msg-minlength="Minimo de {0} numeros"
    data-mask="### ### ###"
     data-msg-phonenumber="Verifique numero movil" 
    data-rule-required="true" 
    data-msg-required="Requerido"   /></div>

  
</div>




<div class="form-group fono_movil required" data-fid="fono_movil">
  <label class="control-label" for="fono_movil">Tel&eacute;fono m&oacute;vil</label>

<div class="input-group"><span class="input-group-addon left"><i class="glyphicon glyphicon-earphone"></i> </span>
<input type="tel" 
    class="form-control" 
    id="fono_movil" 
    name="fono_movil" 
    value=""   
    maxlength="11" 
    placeholder="123 456 789" 
    data-rule-pattern="[0-9 -+.]+" 
    data-msg-pattern="Num movil invalido"  
    data-rule-phonenumber="true" 
    data-rule-maxlength="11" data-msg-maxlength="Maximo de {0} números"  
    data-rule-minlength="11" data-msg-minlength="Minimo de {0} numeros"
    data-mask="### ### ###"
     data-msg-phonenumber="Verifique numero movil"  
    data-rule-required="true" 
    data-msg-required="Requerido"   /></div>
</div>

<div class="form-group comunaId required" data-fid="comunaId">
  <label class="control-label" for="comunaId">Comuna</label>


<select class="form-control" id="comunaId" name="comunaId"   
    data-rule-required="true" data-msg-required="Requerido" >
  <option selected value="-1">- Seleccione comuna -</option>
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




<div class="form-group estado required" data-fid="estado">
  <label class="control-label" for="estado">Estado</label>


<select class="form-control" id="estado" name="estado"   
    data-rule-required="true" data-msg-required="Requerido" >
  <option  value="1" selected>Vigente</option>
  <option  value="2">No Vigente</option>
  </select>

  
</div>




<div class="form-group fechaInicioContratoAsStr required" data-fid="fechaInicioContratoAsStr">
  <label class="control-label" for="fechaInicioContratoAsStr">Fecha inicio contrato</label>

<div class="input-group date">
<input type="text" class="form-control datepicker" id="fechaInicioContratoAsStr" name="fechaInicioContratoAsStr" value=""  placeholder="Ingrese fecha inicio de contrato" 
    data-rule-required="true" data-msg-required="Requerido"  
  data-datepicker-format="dd-mm-yyyy"
  data-datepicker-startView="1" />
  
</div>

<div class="form-group contratoIndefinido required" data-fid="contratoIndefinido">
  <label class="control-label" for="contratoIndefinido">Contrato indefinido</label>
<select class="form-control" id="contratoIndefinido" name="contratoIndefinido"   
    data-rule-required="true" data-msg-required="Requerido" >
  <option  value="S" selected>Si</option>
  <option  value="N">No</option>
  </select>
</div>

<div class="form-group fechaTerminoContratoAsStr " data-fid="fechaTerminoContratoAsStr">
  <label class="control-label" for="fechaTerminoContratoAsStr">Fecha t&eacute;rmino contrato</label>

<div class="input-group date">
<input type="text" class="form-control datepicker" id="fechaTerminoContratoAsStr" name="fechaTerminoContratoAsStr" value=""  placeholder="Ingrese fecha termino de contrato" 
     
  data-datepicker-format="dd-mm-yyyy"
  data-datepicker-startView="1" />
</div>

<div class="form-group articulo22 required" data-fid="articulo22">
  <label class="control-label" for="articulo22">Art. 22</label>
<select class="form-control" id="articulo22" name="articulo22"   
    data-rule-required="true" data-msg-required="Requerido" >
  <option  value="S">Si</option>
  <option  value="N" selected>No</option>
  </select>
</div>

<div class="form-group empresaId required" data-fid="empresaId">
  <label class="control-label" for="empresaId">Empresa</label>


<select class="form-control" id="empresaId" name="empresaId"   
    data-rule-required="true" data-msg-required="Requerido" >
  <option selected value="-1">- Seleccione empresa -</option>
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

<div class="form-group deptoId required" data-fid="deptoId">
  <label class="control-label" for="deptoId">Departamento</label>
<select class="form-control" id="deptoId" name="deptoId"   
    data-rule-required="true" data-msg-required="Requerido" >
  <option></option>
  </select>
</div>

<div class="form-group cencoId required" data-fid="cencoId">
  <label class="control-label" for="cencoId">Centro de Costo</label>
<select class="form-control" id="cencoId" name="cencoId"   
    data-rule-required="true" data-msg-required="Requerido" >
  <option></option>
  </select>
</div>

<div class="form-group idTurno required" data-fid="idTurno">
  <label class="control-label" for="idTurno">Turno</label>


<select id="idTurno" name="idTurno"   
    data-rule-required="true" data-msg-required="Requerido" >
    <option selected value="-1">- Seleccione turno -</option>
  
  </select>

  
</div>




<div class="form-group idCargo required" data-fid="idCargo">
  <label class="control-label" for="idCargo">Cargo</label>


<select class="form-control" id="idCargo" name="idCargo"   
    data-rule-required="true" data-msg-required="Requerido" >
  <option selected value="-1">- Seleccione cargo -</option>
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

    <div class="form-group autorizaAusencia required" data-fid="autorizaAusencia">
        <label class="control-label" for="autorizaAusencia">Autoriza ausencias</label>
        <select class="form-control" id="autorizaAusencia" name="autorizaAusencia"   
            data-rule-required="true" data-msg-required="Requerido" >
            <option  value="S">Si</option>
            <option  value="N" selected="">No</option>
        </select>
    </div>
  
    <div class="form-group claveMarcacion" data-fid="claveMarcacion">
        <label class="control-label" for="claveMarcacion">Clave marcacion</label>
        <input type="text" 
               class="form-control" 
               id="claveMarcacion" 
               name="claveMarcacion" 
               value="" 
               maxlength="15"  
               placeholder="Ingrese clave marcacion" 
        data-rule-required="false" data-msg-required="Requerido" 
        data-rule-maxlength="15" 
        data-msg-maxlength="No debe exceder de {0} caracteres"   />
    </div>
</div>

<div class="form-group submit f0 " data-fid="f0" style="position: relative;">
  <label class="control-label sr-only" for="f0" style="display: block;">Submit Button</label>

  <div class="progress" style="display: none; z-index: -1; position: absolute;">
    <div class="progress-bar progress-bar-striped active" role="progressbar" style="width:100%">
    </div>
  </div>

  <button type="submit" class="btn btn-primary btn-lg" style="z-index: 1;">
  		Guardar
  </button>
  <button type="button" class="btn btn-primary btn-lg" style="z-index: 1;" 
        onClick="volver()">Volver
  </button>
  
</div>

<div class="submit" data-redirect="hola.jsp">
  <p class="error bg-warning" style="display:none;">
    </p>
</div>




</form>

</div>

<div class="container jf-thankyou" style="display:none;" data-redirect="" data-seconds="10">
  <h3>Your form has been submitted. Thank You!</h3>
</div>
                
<form name="redirectForm" id="redirectForm" method="post" action="<%=request.getContextPath()%>/mantencion/lista_empleados.jsp">  
    <input type="hidden" name="cco" id="cco" value="emp01|05|38">  
</form>
<!-- ----------------------------------------------- -->


<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/bootstrap-datepicker.min.js"></script>
<script src="js/jquery.validate.min.js"></script>
<script src="js/additional-methods.min.js"></script>
<script src="js/jquery.scrollTo.min.js"></script>
<script src="vendor.js" ></script>

<script src="js/jquery.rut.js" type="text/javascript"></script>

<script src="jqueryform.com.min.js?ver=v2.0.3&id=jqueryform-d81043" ></script>




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
  height: 34px;
  padding-top: 2px;
  box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
  border: 1px solid #ccc;
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
	 
	$(".jf-form select").css('width', '90%'); // make it responsive
	$(".jf-form select").select2({
	  templateResult: templateResult
	}).change( function(e){
	  $(e.target).valid();
	});

})();
</script>
<!-- [ End: Select2 support ] ---------------------------------- -->



<script type="text/javascript">
// dropdown fields
/*;(function(){
  var dataSource = {"f24":{"dependOn":"f23","remote":null,"options":[{"label":"- Seleccione depto -","value":"-1","checked":true},{"label":"Depto 1","checked":false,"value":"1"},{"label":"Depto 2","value":"2","checked":null}]},"f25":{"dependOn":"f24","remote":null,"options":[{"label":"- Seleccione cenco -","value":"-1","checked":true},{"label":"Cenco 1","checked":false,"value":"1"},{"label":"Cenco 2","value":"2","checked":null}]}};
  $(document).trigger( 'dependent:setup', dataSource );
})();*/
</script>

<script type="text/javascript">

    	// start jqueryform initialization
	// --------------------------------
	JF.init('#jqueryform-d81043');

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
             
            //evento para combo centro costo
            $('#cencoId').change(function(event) {
                var empresaSelected = $("select#empresaId").val();
                var deptoSelected = $("select#deptoId").val();
                var cencoSelected = $("select#cencoId").val();
                var sourceSelected = 'cargar_turnos_by_cenco';
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                   empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                }, function(response2) {
                        var select = $('#idTurno');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Turno</option>";
                        for (i2 = 0; i2 <response2.length; i2++) {
                            var idTurno = response2[i2].id;
                            var labelTurno = response2[i2].nombre;
                            newoption += "<option value='" + idTurno + "'>" + labelTurno + "</option>";
                        }
                        $('#idTurno').html(newoption);
                        //setTurnoSelected();
                });
            }); 


            $.validator.addMethod("rut", function(value, element) {
                    return this.optional(element) || $.Rut.validar(value);
            }, "Este campo debe ser un rut valido.");

            $("#jqueryform-d81043").validate();

            $('#rut').Rut({
              validation: false
            });
         });
</script>

  </body>
</html>