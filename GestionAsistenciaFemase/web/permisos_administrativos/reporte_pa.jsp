<%@page import="cl.femase.gestionweb.common.Utilidades"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Calendar"%>
<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
    
<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    
    Calendar mycal=Calendar.getInstance();
    int anioActual = mycal.get(Calendar.YEAR);
    LinkedHashMap<Integer,String> listaAnios = new LinkedHashMap<Integer,String>();
    listaAnios.put(anioActual-5, String.valueOf(anioActual-5));
    listaAnios.put(anioActual-4, String.valueOf(anioActual-4));
    listaAnios.put(anioActual-3, String.valueOf(anioActual-3));
    listaAnios.put(anioActual-2, String.valueOf(anioActual-2));
    listaAnios.put(anioActual-1, String.valueOf(anioActual-1));
    listaAnios.put(anioActual, String.valueOf(anioActual));
    Date currentDate = mycal.getTime();
    int semestreActual = Utilidades.getSemestre(currentDate);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">
    <title>Reporte de Permisos Administrativos</title>
    <link rel="stylesheet" href="css/jquery-ui.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/prism.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/chosen.css">
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("select[name='paramSemestre']").val("<%=semestreActual%>");
            $("select[name='paramAnio']").val("<%=anioActual%>");
            // ... tu javascript original aquí ...
            $('#empresa').change(function(event) {
                var empresaSelected = $("select#empresa").val();
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaID : empresaSelected
                }, function(response) {
                    var select = $('#depto');
                    select.find('option').remove();
                    var newoption = "";
                    newoption += "<option value='-1'>Seleccione Departamento</option>";
                    for (i=0; i<response.length; i++) {
                        newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                    }
                    $('#depto').html(newoption);
                });
            });
            $('#depto').change(function(event) {
                var empresaSelected = $("select#empresa").val();
                var deptoSelected = $("select#depto").val();
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaID : empresaSelected, deptoID : deptoSelected
                }, function(response) {
                    var select = $('#cenco');
                    select.find('option').remove();
                    var newoption = "";
                    newoption += "<option value='-1'>Seleccione Centro de costo</option>";
                    for (i=0; i<response.length; i++) {
                        var labelZonaExtrema = '';
                        var labelCenco = '';
                        if (response[i].zonaExtrema === 'S') labelZonaExtrema = ' (Zona Extrema)';
                        else labelZonaExtrema = '';
                        labelCenco = response[i].nombre + " " + labelZonaExtrema;
                        newoption += "<option value='"+response[i].id+"'>"+labelCenco+"</option>";
                    }
                    $('#cenco').html(newoption);
                });
            });
            $('#cenco').change(function(event) {
                var empresaSelected = $("select#empresa").val();
                var deptoSelected = $("select#depto").val();
                var cencoSelected = $("select#cenco").val();
                var sourceSelected = 'reporte_vacaciones';
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                    empresaID : empresaSelected, deptoID : deptoSelected, cencoID : cencoSelected, source: sourceSelected
                }, function(response) {
                    var select = $('#rut');
                    select.find('option').remove();
                    var newoption = "";
                    newoption += "<option value='todos'>Todos</option>";
                    for (i=0; i<response.length; i++) {
                        var auxNombre = '['+response[i].rut+'] '+response[i].nombres +
                            ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                        newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                    }
                    $('#rut').html(newoption);
                });
                sourceSelected = 'cargar_turnos_by_cenco';
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                    empresaID : empresaSelected, deptoID : deptoSelected, cencoID : cencoSelected, source: sourceSelected
                }, function(response2) {
                    var select = $('#turno');
                    select.find('option').remove();
                    var newoption = "";
                    newoption += "<option value='-1'>Cualquiera</option>";
                    for (i2 = 0; i2 <response2.length; i2++) {
                        var idTurno = response2[i2].id;
                        var labelTurno = response2[i2].nombre;
                        newoption += "<option value='" + idTurno + "'>" + labelTurno + "</option>";
                    }
                    $('#turno').html(newoption);
                });
            });
        });

        function validaForm() {
            var empresaId = $('#empresa').val();
            var deptoId = $('#depto').val();
            var cencoId = $('#cenco').val();
            if (empresaId === '-1' 
                && deptoId === '-1' 
                && cencoId === '-1'){
                    alert('Seleccione algun criterio de busqueda');
                    return false;
            }
            return true;
        }
    </script>
    <style>
        body {
            background: #f4f8fb;
            color: #204c90;
            font-family: 'Segoe UI', 'Open Sans', Verdana, Arial, Helvetica, sans-serif;
            margin: 0;
            font-size: 15px;
        }
        .centrado {
            text-align: center;
            margin: 15px auto 15px auto;
            max-width: 600px;
        }
        h4 {
            font-size: 24px;
            font-weight: 400;
            letter-spacing: 1px;
            margin-bottom: 7px;
        }
        .leyenda {
            background: #e7f2ff;
            border-radius: 7px;
            border-left: 3px solid #2186d0;
            padding: 12px;
            margin-bottom: 16px;
            text-align: left;
            display: inline-block;
        }
        .leyenda strong {
            color: #2186d0;
        }
        .content-container {
            max-width: 670px;
            margin: 0 auto;
        }
        .padded-content-container {
            background: #fff;
            border-radius: 9px;
            box-shadow: 0 2px 9px rgba(44,66,124,0.09);
            padding: 26px 34px 18px 34px;
        }
        .filtering {
            border: 1px solid #aed4fa;
            margin-bottom: 15px;
            padding: 1px 18px 8px 18px;
            background-color: #f2f7fb;
            border-radius: 8px;
        }
        label {
            color: #204c90;
            font-weight: 500;
            margin-bottom: 6px;
            font-size: 15px;
        }
        select, input, button {
            font-size: 15px;
            padding: 8px 13px;
            border-radius: 7px;
            border: 1px solid #b4d2fb;
            margin-bottom: 15px;
        }
        select:focus, input:focus {
            outline: 2px solid #2186d0;
            background: #eef6fe;
        }
        button.button-blue {
            background: linear-gradient(100deg, #54c2fd 60%, #116ad1 100%);
            color: #fff;
            font-weight: 600;
            border: none;
            padding: 11px 24px;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.22s;
        }
        button.button-blue:hover {
            background: linear-gradient(145deg, #116ad1 50%, #54c2fd 100%);
        }
        @media (max-width: 700px) {
            .centrado, .content-container, .padded-content-container {
                max-width: 99vw;
                padding: 2vw 2vw;
            }
            h4 { font-size: 17.5px; }
        }
        .ui-datepicker-calendar { display: none !important; }
    </style>
</head>
<body>
    <div class="centrado">
        <h4>Informe de Permisos Administrativos</h4>
        <div class="leyenda">
            <strong>Semestre 1:</strong> del 1 de enero al 30 de junio<br>
            <strong>Semestre 2:</strong> del 1 de julio al 31 de diciembre
        </div>
    </div>
    <div class="content-container">
        <div class="padded-content-container">
            <div class="filtering">
                <form id="demo-form" 
                    action="<%=request.getContextPath()%>/servlet/PermisosAdministrativosReport" 
                    method="POST" 
                    onsubmit="return validaForm();">
                <!-- tu código JSP de selects y controles aquí, igual como lo tenías -->
                    <div id="content2" align="left">
                       <div id="col1"> <label for="empresa">Empresa</label></div>
                       <div id="col2">     
                            <select name="empresa" id="empresa">
                            <option value="-1" selected>----------</option>
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
                    </div>
                    <div id="content3" align="left">    
                       <div id="col1"> 
                            <label for="depto">Departamento</label>
                        </div>
                       <div id="col2"> 
                            <select id="depto" name="depto" style="width:350px;" required>
                                <option value="-1">--------</option>
                            </select>
                       </div>
                    </div>
                    <div id="content4" align="left">    
                        <div id="col1"><label>Centro Costo</label></div>
                        <div id="col2">    
                            <select name="cenco" id="cenco">
                                <option value="-1" selected>----------</option>
                            </select>
                        </div>
                    </div>
                    <div id="content5" align="left">
                        <div id="col1"><label>Empleado</label></div>
                        <div id="col2">            
                            <select name="rut" id="rut">
                                <option value="-1" selected>----------</option>
                            </select>
                        </div>
                    </div>
                    <label>A&ntilde;o:
                        <select id="paramAnio" name="paramAnio" style="width:150px;" tabindex="2" required>
                            <option value="-1">Seleccione a&ntilde;o</option>
                            <%
                                for(Integer anioKey : listaAnios.keySet()) {
                                    String anioLabel = listaAnios.get(anioKey);
                                   %>
                                   <option value="<%=anioKey%>"><%=anioLabel%></option>
                                <%}%>
                        </select>
                    </label>
                        
                    <label>Semestre:
                        <select id="paramSemestre" name="paramSemestre" style="width:150px;" tabindex="2" required>
                            <option value="1">Primer Semestre</option>
                            <option value="2">Segundo Semestre</option>
                        </select>
                    </label>
                    <br>
                    <div id="content9" align="left">             
                        <div id="col1">
                            <button type="submit" 
                                id="LoadRecordsButton"
                                name="LoadRecordsButton"
                                class="button button-blue">Generar Informe</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
