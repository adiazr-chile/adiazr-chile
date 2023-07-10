<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.google.gson.Gson"%>
<%@ page import="com.google.gson.JsonObject"%>
 
<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();
 
map = new HashMap<Object,Object>(); map.put("label", "Ene"); map.put("y", 12); list.add(map);
map = new HashMap<Object,Object>(); map.put("label", "Feb"); map.put("y", 20); list.add(map);
map = new HashMap<Object,Object>(); map.put("label", "Mar"); map.put("y", 5); list.add(map);
map = new HashMap<Object,Object>(); map.put("label", "Abr"); map.put("y", 6); list.add(map);
map = new HashMap<Object,Object>(); map.put("label", "May"); map.put("y", 6); list.add(map);

String dataPoints = gsonObj.toJson(list);
%>
 
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
window.onload = function() { 
 
var chart = new CanvasJS.Chart("chartContainer", {
	theme: "light2",
	title: {
		text: "Inasistencias - [Fundacion Mi Casa],[V- Valparaíso],[CENIM SAN ANTONIO]"
	},
	axisX: {
		title: "Mes"
	},
	axisY: {
		title: "N° de Inasistencias",
		includeZero: true
	},
	data: [{
		type: "line",
		yValueFormatString: "#,##0 inasistencias",
		dataPoints : <%out.print(dataPoints);%>
	}]
});
chart.render();
 
};
</script>
</head>
<body>
<div id="chartContainer" style="height: 370px; width: 100%;"></div>
<script src="https://cdn.canvasjs.com/canvasjs.min.js"></script>
</body>
</html>                              