<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Dashboard</title>

<script language="javascript">

</script>

<style>
	.iframe-container {
		position: relative;
		overflow: hidden;
		padding-top: 2%;
	}
	
	html, body, .dashboard {
		width:100%;
		height:100%;
		margin:0;
	}
	.col1, .col2, .box7, .box11 {
		float:left;
	}
	.col1 {
	width: 50%;
	height: 100%;
	}
	.col1 > div {
		width:100%;
		height:25%;
	}
	.col2 {
	width: 50%;
	height: 100%;
	}
	.box6 {
		float:right;
		width: 47.5%;
		height:65%;
	}
	.box5, .box8 {
		width:52.5%;
	}
	.box5 {
		height:30%;
	}
	.box8 {
		height:35%;
	}
	.box9 {
		height:15%;
	}
	.box10 {
		height:20%;
	}
	.box7, .box11 {
		width:15%;
	}
	.box7 {
		height:90%;
	}
	.box11 {
		height:10%;
	}
	/* following just to see what we are doing :) */
	 div {
		box-sizing:border-box;
	}
	
</style>

</head>

<body>
    <!--
    <div class="iframe-container">
      <iframe width="100%" height="500" src="<%=request.getContextPath()%>/graficos/marcas_combinadas.jsp"></iframe>
      <iframe width="100%" height="500" src="<%=request.getContextPath()%>/graficos/horas_atraso.jsp"></iframe>
    </div>
    -->
    <div class="dashboard page-container">
    <div class="col1">
        <div class="box1"><iframe width="100%" height="500" src="<%=request.getContextPath()%>/graficos/marcas_combinadas.jsp"></iframe></div>
    </div>
    <div class="col2">
      <div class="box2"><iframe width="100%" height="500" src="<%=request.getContextPath()%>/graficos/horas_atraso.jsp"></iframe></div>
    </div>
    
   
</div><!--/dashboard-->

</body>
</html>
