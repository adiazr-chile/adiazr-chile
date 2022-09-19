<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.PerfilUsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<PerfilUsuarioVO> perfiles = (List<PerfilUsuarioVO>)session.getAttribute("perfiles");
    boolean setEmpresaSelected = false;
    if (empresas != null && empresas.size() == 1){
        setEmpresaSelected = true;
    }   
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin usuarios</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript">
        function openEditForm(username){
            document.location.href='<%=request.getContextPath()%>/AdmUsuarioServlet?action=startpage&username='+username; 
        }
        
        function crearUsuario(){
            document.location.href='<%=request.getContextPath()%>/AdmUsuarioServlet?action=startpage';
	}
	</script>
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
    </style>


    <script src="../Jquery-JTable/Scripts/modernizr-2.6.2.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jquery-ui-1.10.0.min.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shCore.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushJScript.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushXml.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushCSharp.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushSql.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushPhp.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jtablesite.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jtable/jquery.jtable.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jtable/jquery.jtable.es.js" type="text/javascript"></script>
    
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Administraci&oacute;n de Usuarios<span class="light"></span></h1>
             <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label for="idPerfil">Perfil</label>
            <select id="idPerfil" name="idPerfil" style="width:150px;" required>
            <option value="-1"></option>
                <%
                    Iterator<PerfilUsuarioVO> iteraperfiles = perfiles.iterator();
                    while(iteraperfiles.hasNext() ) {
                        PerfilUsuarioVO auxperfil = iteraperfiles.next();
                        %>
                        <option value="<%=auxperfil.getId()%>"><%=auxperfil.getNombre()%></option>
                        <%
                    }
                %>
            </select>
        
        <label>Nombre usuario: <input type="text" name="username" id="username" /></label>
        <label>Nombres: <input type="text" name="nombres" id="nombres" /></label>
        <label for="empresaId">Empresa</label>
        <select id="empresaId" name="empresaId" style="width:150px;" required>
            <% if (!setEmpresaSelected){%>
                 <option value="-1"></option>
            <%}%>    
            
            <%
                Iterator<EmpresaVO> iteraempresas = empresas.iterator();
                while(iteraempresas.hasNext() ) {
                    EmpresaVO auxempresa = iteraempresas.next();
                    String selected="";
                    if (setEmpresaSelected) selected = "selected";
                    %>
                    <option value="<%=auxempresa.getId()%>" <%=selected%>><%=auxempresa.getNombre()%></option>
                    <%
                }
            %>
        </select>
        
        <label for="estado">Estado</label>
                <select id="filtroEstado" name="filtroEstado" 
                           class="chosen-select" style="width:150px;" tabindex="2">
                        <option value="-1" selected>Cualquiera</option>
                        <option value="1" >Vigente</option>
                        <option value="2">No Vigente</option>
                    </select>
        
        <button type="submit" id="LoadRecordsButton">Buscar</button>
                <input name="botoncrear" type="button" value="Crear usuario" class="button button-blue" onclick="crearUsuario();">
            
    </form>
</div>

<div id="UsuariosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#UsuariosTableContainer').jtable({       
            title: 'Usuarios del Sistema',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'usr_username ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/UsuariosController?action=list'
                //createAction:'<%=request.getContextPath()%>/UsuariosController?action=create'//,
                //updateAction: '<%=request.getContextPath()%>/UsuariosController?action=update'
                //,deleteAction: '<%=request.getContextPath()%>/AccesosController?action=delete'
            },
            fields: {
                username: {
                    title:'Username',
                    width: '10%',
                    key: true,
                    list: true,
                    create:true
                },
                password: {
                    title:'Password',
                    width: '10%',
                    key: true,
                    list: false,
                    create:true
                },
                runEmpleado:{
                    title: 'RUN empleado',
                    width: '10%',
                    edit:true
                },
                nombres:{
                    title: 'Nombres',
                    width: '10%',
                    edit:true
                },
                apPaterno:{
                    title: 'Ap Paterno',
                    width: '12%',
                    edit:true
                },
                apMaterno:{
                    title: 'Ap Materno',
                    width: '12%',
                    edit:true
                },
                email:{
                    title: 'Email',
                    width: '20%',
                    edit:true
                },
                empresaId: {
                    title:'Empresa',
                    width: '10%',
                    list: true,
                    create:true,
                    edit:true,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas';
                    }
                },  
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados';
                    },
                    width: '10%',
                    edit:true
                },
                idPerfil:{
                    title: 'Perfil',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=perfiles';
                    },
                    width: '10%',
                    edit:true
                },
                fechaCreacion:{
                    title: 'Fecha creacion',
                    width: '12%',
                    create:false,
                    edit:false
                },    
                fechaActualizacion:{
                    title: 'Fecha ult. actualizacion',
                    width: '12%',
                    create:false,
                    edit:false
                },    
                MyButton: {
                    title: 'Accion',
                    width: '6%',
                    sorting: false,
                    display: function(data) {
                         return '<button type="button" onclick="openEditForm(\'' + data.record.username + '\')">Modificar</button> ';
                    }
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#UsuariosTableContainer').jtable('load', {
                username: $('#username').val(),
                nombres: $('#nombres').val(),
                empresaId: $('#empresaId').val(),
                idPerfil: $('#idPerfil').val(),
                filtroEstado: $('#filtroEstado').val()
            });
        });

        //Load all records when page is first shown
        $('#LoadRecordsButton').click();
    });

</script>
<br />
<hr />
<h3>&nbsp;</h3><div class="tabsContainer"><div id="tabs-webforms"></div>
</div>



            </div>
            
        </div>


        <div class="main-footer" style="position: relative"></div>
    </div>
    
</body>
</html>
<script type="text/javascript">

    $(document).ready(function () {
       
        
        
        
    });

</script>
