// Uso: Simple... se debe pasar la cadena de la fecha y devuelve false si no es válida...
// El Formato es dd-mm-aaaa
// Ejemplo: if (Validar('14-08-1981')==false) { alert('Entrada Incorrecta') }
// Uso en formularios: onSubmit="return Validar(this.fecha.value)"
//
// Este script y otros muchos pueden
// descarse on-line de forma gratuita
// en El Código: www.elcodigo.net

function validaFecha(Cadena){
    
	var Fecha= new String(Cadena)	// Crea un string
	var RealFecha= new Date()	// Para sacar la fecha de hoy
	// Cadena Año
	var Ano= new String(Fecha.substring(Fecha.lastIndexOf("-")+1,Fecha.length))
	// Cadena Mes
	var Mes= new String(Fecha.substring(Fecha.indexOf("-")+1,Fecha.lastIndexOf("-")))
	// Cadena Día
	var Dia= new String(Fecha.substring(0,Fecha.indexOf("-")))

	// Valido el año
	if ((isNaN(Ano)) || (Ano.length<4) || (parseFloat(Ano)<1989)){
		return false
	}
	// Valido el Mes
	if ((isNaN(Mes)) || (parseFloat(Mes)<1) || (parseFloat(Mes)>12)){
		return false
	}
	// Valido el Dia
	if ((isNaN(Dia)) || (parseFloat(Dia)<1) || (parseFloat(Dia)>31)){
		return false
	}
        
        iMes = parseFloat(Mes) 
        iAno = parseFloat(Ano)
        iDia = parseFloat(Dia)
        
        if (iMes == 2){
            if ((iAno%4 == 0) && ((iAno%100 != 0) || (iAno%400==0))){
		 if (iDia > 29 ) 
		 	return false;
            } else {
                if ( iDia > 28 ) 
		 	return false;
            }
        }
        
	if ((iMes==4) || (iMes==6) || (iMes==9) || (iMes==11)) {
                 if ( iDia > 30) {
		 	return false;
		 }
	}
	
        return true;
}


function Comparar_Fechas(fecha1,fecha2)
{
String1 = fecha1;
String2 = fecha2;

// Si los dias y los meses llegan con un valor menor que 10
// Se concatena un 0 a cada valor dentro del string
if (String1.substring(1,2)=="-") {
String1="0"+String1
}
if (String1.substring(4,5)=="-"){
String1=String1.substring(0,3)+"0"+String1.substring(3,9)
}


if (String2.substring(1,2)=="-") {
String2="0"+String2
}
if (String2.substring(4,5)=="-"){
String2=String2.substring(0,3)+"0"+String2.substring(3,9)
}


dia1=String1.substring(0,2);
mes1=String1.substring(3,5);
anyo1=String1.substring(6,10);

dia2=String2.substring(0,2);
mes2=String2.substring(3,5);
anyo2=String2.substring(6,10);


if (dia1 == "08") // parseInt("08") == 10 base octogonal
dia1 = "8";
if (dia1 == '09') // parseInt("09") == 11 base octogonal
dia1 = "9";
if (mes1 == "08") // parseInt("08") == 10 base octogonal
mes1 = "8";
if (mes1 == "09") // parseInt("09") == 11 base octogonal
mes1 = "9";
if (dia2 == "08") // parseInt("08") == 10 base octogonal
dia2 = "8";
if (dia2 == '09') // parseInt("09") == 11 base octogonal
dia2 = "9";
if (mes2 == "08") // parseInt("08") == 10 base octogonal
mes2 = "8";
if (mes2 == "09") // parseInt("09") == 11 base octogonal
mes2 = "9";

dia1=parseFloat(dia1);
dia2=parseFloat(dia2);

mes1=parseFloat(mes1);
mes2=parseFloat(mes2);

anyo1=parseFloat(anyo1);
anyo2=parseFloat(anyo2);

if (anyo1>anyo2)
{
return false;
}

if ((anyo1==anyo2) && (mes1>mes2))
{
return false;
}
if ((anyo1==anyo2) && (mes1==mes2) && (dia1>dia2))
{
return false;
}

return true;
}


