  ////////////////////////////////////////////////////////////////
// retorna los clientes  del corredor seleccionado
////////////////////////////////////////////////////////////////
    
    function clientesCorredor(corredor){

	     //creamos el objeto AJAX
             _objetus=objetus()

             //variables
             _values_send = "corredor="+escape(corredor);
             
	     // La pagina a la que vamos a llamar
            _URL_= "clientesCorredor.jsp";

	    // Abrimos el request y hacemos la consulta por background
            _objetus.open("POST",_URL_,true);
            _objetus.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            
	   // cabecera POST
           _objetus.send('&'+_values_send); 
           
	   _objetus.onreadystatechange=function() {
                   var status = document.getElementById("datos");
                   if (_objetus.readyState==4){
			   status.innerHTML="";
                           if (_objetus.status == 200){  // Pagina cargada
                                 status.innerHTML = _objetus.responseText;
                           }else{ 
                                 status.innerHTML="<font color=\"red\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">Error al realizar el requerimiento " + _objetus.responseText + " </font>";
                           }
				 
                    }  else {
			status.innerHTML="<font color=\"red\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">Realizando requerimiento </font>";
		    }
             }

           //Se envia el requerimiento
           //_objetus.send(null);
	
        return true;
    }
  
////////////////////////////////////////////////////////////////
// retorna los operadores directo  del corredor seleccionado
////////////////////////////////////////////////////////////////
    
   function operadoresCorredor(corredor){

	     //creamos el objeto AJAX
             _objetus=objetus()

             //variables
             _values_send = "corredor="+escape(corredor);
             
	     // La pagina a la que vamos a llamar
            _URL_= "operadoresCorredor.jsp";

	    // Abrimos el request y hacemos la consulta por background
            _objetus.open("POST",_URL_,true);
            _objetus.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            
	   // cabecera POST
           _objetus.send('&'+_values_send); 
           
	   _objetus.onreadystatechange=function() {
                   var status = document.getElementById("datos");
                   if (_objetus.readyState==4){
			   status.innerHTML="";
                           if (_objetus.status == 200){  // Pagina cargada
                                 status.innerHTML = _objetus.responseText;
                           }else{ 
                                 status.innerHTML="<font color=\"red\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">Error al realizar el requerimiento " + _objetus.responseText + " </font>";
                           }
				 
                    }  else {
			status.innerHTML="<font color=\"red\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">Realizando requerimiento </font>";
		    }
             }

           //Se envia el requerimiento
           //_objetus.send(null);
	
        return true;
    }
 
    
    
////////////////////////////////////////
// Devuelve el rut de la cuenta propia
////////////////////////////////////////
function rutCorredor(corredor){

	     //creamos el objeto AJAX
             _objetus=objetus()

             //variables
             _values_send = "corredor="+escape(corredor);
             
	     // La pagina a la que vamos a llamar
            _URL_= "rutCorredor.jsp";

	    // Abrimos el request y hacemos la consulta por background
            _objetus.open("POST",_URL_,true);
            _objetus.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            
	   // cabecera POST
           _objetus.send('&'+_values_send); 
           
	   _objetus.onreadystatechange=function() {
                   var status = document.getElementById("datos");
                   if (_objetus.readyState==4){
			   status.innerHTML="";
                           if (_objetus.status == 200){  // Pagina cargada
                                 status.innerHTML = _objetus.responseText;
                           }else{ 
                                 status.innerHTML="<font color=\"red\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">Error al realizar el requerimiento " + _objetus.responseText + " </font>";
                           }
				 
                    }  else {
			status.innerHTML="<font color=\"red\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">Realizando requerimiento </font>";
		    }
             }

           //Se envia el requerimiento
           //_objetus.send(null);
	
        return true;
    }
    

/////////////////////////////////////////////////////
// cambia lo que ve decuerdo al tipo de operacion
/////////////////////////////////////////////////////
function cambiaVista(tipo){

    if ((tipo != null) && (tipo.selectedIndex >= 0)){
    
        var rutcliente = document.getElementById("rutcliente");
        var rutpropio  = document.getElementById("rutpropio");
        var rutopdir   = document.getElementById("rutopdir");
        
        var currentLength = tipo.length;
        var j = 0;
        encontrado = false;
        var seleccion;
        
        for(j=0; j<=currentLength && tipo.options[j] != null  && !encontrado; j++){
            if(tipo.options[j].selected == true){
                encontrado = true;
                seleccion = tipo.options[j].value;
            }
        }
        
        
        if (encontrado){
            switch (seleccion){
                 case "1":
                         clientesCorredor(scorredor);
                         break;
                 case "2":
                         rutCorredor(scorredor);
                         break;        
                 case "3":
                         operadoresCorredor(scorredor);
                         
                         break;         
            }
        }
        
    }
}

//////////////////////////////////////////////////////
// Vista inicial
//////////////////////////////////////////////////////
function iniciaVista(){
    var mitipo = document.getElementById("tipo");
    cambiaVista(mitipo);
}

function cambiaCorredor(fcorredor){
   if (fcorredor && fcorredor.selectedIndex >= 0){
        var currentLength = fcorredor.length;
        var j = 0;
        var encontrado = false;
        var seleccion;
        
        for(j=0; j<=currentLength && fcorredor.options[j] != null  && !encontrado; j++){
            if(fcorredor.options[j].selected == true){
                encontrado = true;
                seleccion = fcorredor.options[j].value;
            }
        }
        
        if (encontrado){
           scorredor = seleccion;
           var mytipo = document.getElementById("tipo");
           cambiaVista(mytipo);
        }
    }
}


///////////////////////////////////////////////////////////////////////////////////////
// Recupera de base de datos los montos tranzados
///////////////////////////////////////////////////////////////////////////////////////
function recuperaMontos(){
   var currentLength = 0;
   var j = 0;
   encontrado = false;
   var seleccion;
   var srut;
   var stipo;
   
   var fechaoper = document.getElementById("fechaoperacion");
   //alert('fecha operacion: '+fechaoper.value);
   if (!validaFecha(fechaoper.value)){
        alert("Fecha de operacion incorrecta");
        fechaoper.select();
        fechaoper.focus();
        return false;
   }
    
   var mitipo = document.getElementById("tipo");
  
   currentLength = mitipo.length;
   j = 0;
   encontrado = false;
        
   for(j=0; j<=currentLength && mitipo.options[j] != null  && !encontrado; j++){
       if(mitipo.options[j].selected == true){
           encontrado = true;
           seleccion = mitipo.options[j].value;
       }
   }
        
   if ( !encontrado){
      alert("No hay tipo de operacion seleccionado");
      mitipo.focus();
      return false;
   }
   
   switch (seleccion){
           case "1":
                   stipo = "001";
                   var frut = document.getElementById("rutcliente");
                   
                   srut = frut.value;

                   if ( (srut == "") || (!validaRut(srut))){
                        alert("Rut invalido");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   break;
           case "2":
                   stipo = "221";
                   var frut = document.getElementById("rutpropio");
                   
                   if ( (frut == null) || (frut.value == "") || (!validaRut(frut.value))){
                        alert("Rut invalido");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   srut = frut.value;
                   
                   break;        
           case "3":
                   stipo = "101";
                   var frut = document.getElementById("rutopdir");
                   currentLength = frut.length;
                   j = 0;
                   encontrado = false;
        
                   for(j=0; j<=currentLength && frut.options[j] != null  && !encontrado; j++){
                        if(frut.options[j].selected == true){
                            encontrado = true;
                            seleccion = frut.options[j].value;
                        }
                   }
                    
                   if (!encontrado){
                        alert("Rut no seleccionado");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   srut = seleccion;
                   
                   if ( (srut == "") || (!validaRut(srut))){
                        alert("Rut invalido");
                        rut.focus();
                        return false;
                   }
                   
                   break;         
    }
      
   var sfecha = fechaoper.value;
   
   //creamos el objeto AJAX
   _objetus=objetus()

   //variables
   _values_send = "corredor="+escape(scorredor);
   _values_send = _values_send + "&"+ "rut="+srut;
   _values_send = _values_send + "&"+ "fecha="+sfecha;
   _values_send = _values_send + "&"+ "tipo="+stipo;

   //alert('llamando a recuperaMontos.jsp');
   // La pagina a la que vamos a llamar
   _URL_= "recuperaMontos.jsp";

	    // Abrimos el request y hacemos la consulta por background
            _objetus.open("POST",_URL_,true);
            _objetus.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            
	   // cabecera POST
           _objetus.send('&'+_values_send); 
           
	   _objetus.onreadystatechange=function() {
                   if (_objetus.readyState==4){
                           if (_objetus.status == 200){  // Pagina cargada
                                 var xmldoc = _objetus.responseXML;
                                 
                                 var error = xmldoc.getElementsByTagName('error').item(0);
                                 
                                 if ( error.firstChild.data == "NO"){
                                    var monto_pesos = xmldoc.getElementsByTagName('monto_pesos').item(0);
                                    var monto_uf = xmldoc.getElementsByTagName('monto_uf').item(0);
                                    var tasa = xmldoc.getElementsByTagName('tasa').item(0);
                                    var derechos_pesos = xmldoc.getElementsByTagName('derechos_pesos').item(0);
                                    var derechos_uf = xmldoc.getElementsByTagName('derechos_uf').item(0);
                                    var horaLastX = xmldoc.getElementsByTagName('horaLast').item(0);
                                    var fechaLastX = xmldoc.getElementsByTagName('fechaLast').item(0);
                                    
                                 
                                    var monto = document.getElementById("montopeso");
                                    var tas = document.getElementById("tasa");
                                    var derpesos = document.getElementById("derechospesos");
                                    var deruf = document.getElementById("derechosuf");
                                    var horalast = document.getElementById("horalast");
                                    var fechalast = document.getElementById("fechalast");
                                    var dminguf = document.getElementById("dminguf");

                                 
                                    monto.value = monto_pesos.firstChild.data;
                                    tas.value = tasa.firstChild.data;
                                    derpesos.value = derechos_pesos.firstChild.data;
                                    deruf.value = derechos_uf.firstChild.data;
                                    horalast.value = horaLastX.firstChild.data;
                                    fechalast.value = fechaLastX.firstChild.data;
                                    dminguf.value = monto_uf.firstChild.data;
                                    var eliminar = document.getElementById("eliminar");
                                    eliminar.disabled = false;

                                    var ingresar = document.getElementById("ingresar");
                                    ingresar.disabled = true;

                                 } else {

                                    var monto = document.getElementById("montopeso");
                                    var tas = document.getElementById("tasa");
                                    var derpesos = document.getElementById("derechospesos");
                                    var deruf = document.getElementById("derechosuf");
                                    var horalast = document.getElementById("horalast");
                                    var fechalast = document.getElementById("fechalast");

                                    monto.value = '';
                                    tas.value = '';
                                    derpesos.value = '';
                                    deruf.value = '';
                                    horalast.value = '';
                                    fechalast.value = '';

                                    var eliminar = document.getElementById("eliminar");
                                    eliminar.disabled = true;

                                    var ingresar = document.getElementById("ingresar");
                                    ingresar.disabled = true;

                                    alert("No hay datos para la consulta");
    
                                 }
                           }	 
                    }
             }
	
        return true;
}
    
    
//////////////////////////////////////////////////////
// Calcula los montos a mostrar en pantalla
//////////////////////////////////////////////////////
function calculaMontos(){
   var currentLength = 0;
   var j = 0;
   encontrado = false;
   var seleccion;
   var srut;
   var stipo;
   
   var fechaoper = document.getElementById("fechaoperacion");
   
   if ( !validaFecha(fechaoper.value)){
        alert("Fecha de operacion incorrecta:"+fechaoper.value);
        fechaoper.select();
        fechaoper.focus();
        return false;
   }
    
   var mitipo = document.getElementById("tipo");
   
   currentLength = mitipo.length;
   j = 0;
   encontrado = false;
        
   for(j=0; j<=currentLength && mitipo.options[j] != null  && !encontrado; j++){
       if(mitipo.options[j].selected == true){
           encontrado = true;
           seleccion = mitipo.options[j].value;
       }
   }
        
   if ( !encontrado){
      alert("No hay tipo de operacion seleccionado");
      mitipo.focus();
      return false;
   }
   
   switch (seleccion){
           case "1":
                   stipo = "001";
                   var frut = document.getElementById("rutcliente");
                   
                   srut = frut.value;

                   if ( (srut == "") || (!validaRut(srut))){
                        alert("Rut invalido");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   break;
           case "2":
                   stipo = "221";
                   var frut = document.getElementById("rutpropio");
                   
                   if ( (frut == null) || (frut.value == "") || (!validaRut(frut.value))){
                        alert("Rut invalido");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   srut = frut.value;
                   
                   break;        
           case "3":
                   stipo = "101";
                   var frut = document.getElementById("rutopdir");
                   currentLength = frut.length;
                   j = 0;
                   encontrado = false;
        
                   for(j=0; j<=currentLength && frut.options[j] != null  && !encontrado; j++){
                        if(frut.options[j].selected == true){
                            encontrado = true;
                            seleccion = frut.options[j].value;
                        }
                   }
                    
                   if (!encontrado){
                        alert("Rut no seleccionado");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   srut = seleccion;
                   
                   if ( (srut == "") || (!validaRut(srut))){
                        alert("Rut invalido");
                        rut.focus();
                        return false;
                   }
                   
                   break;         
    }

   var monto = document.getElementById("montopeso");
   var valorMonto = monto.value.replace(",","");
   valorMonto = valorMonto.replace(".",",");
   
   if ( isNaN(valorMonto) || (parseFloat(valorMonto) < 0) ){
           alert("Monto incorrecto");
           monto.select();
           monto.focus();
           return false
   }
   
   var sfecha = fechaoper.value;
   
   //creamos el objeto AJAX
   _objetus=objetus()

   //variables
   _values_send = "corredor="+escape(scorredor);
   _values_send = _values_send + "&"+ "rut="+srut;
   _values_send = _values_send + "&"+ "fecha="+sfecha;
   _values_send = _values_send + "&"+ "tipo="+stipo;
   _values_send = _values_send + "&"+ "monto="+valorMonto;

   // La pagina a la que vamos a llamar
   _URL_= "calculaMontos.jsp";

	    // Abrimos el request y hacemos la consulta por background
            _objetus.open("POST",_URL_,true);
            _objetus.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            
	   // cabecera POST
           _objetus.send('&'+_values_send); 
           
	   _objetus.onreadystatechange=function() {
                   if (_objetus.readyState==4){
                           if (_objetus.status == 200){  // Pagina cargada
                                 var xmldoc = _objetus.responseXML;
                                 var error = xmldoc.getElementsByTagName('error').item(0);
                                 
                                 if ( error.firstChild.data == "NO"){
                                    var tasa = xmldoc.getElementsByTagName('tasa').item(0);
                                    var derechos_pesos = xmldoc.getElementsByTagName('derechos_pesos').item(0);
                                    var derechos_uf = xmldoc.getElementsByTagName('derechos_uf').item(0);
                                 
                                    var tas = document.getElementById("tasa");
                                    var derpesos = document.getElementById("derechospesos");
                                    var  deruf = document.getElementById("derechosuf");

                                    var dtotderuf = document.getElementById("dtotderuf");
                                    var uf = document.getElementById("uf");
                                    var dnewmacum = document.getElementById("dnewmacum");
                                    var dmacumuffin = document.getElementById("dmacumuffin");
                                    var dtotderpesos = document.getElementById("dtotderpesos");
                                    var ingsidep = document.getElementById("ingsidep");
                                    var transitrel = document.getElementById("transitrel");
                                    
                                    tas.value = tasa.firstChild.data;
                                    derpesos.value = derechos_pesos.firstChild.data;
                                    deruf.value = derechos_uf.firstChild.data;
                                 
                                    dtotderuf.value = xmldoc.getElementsByTagName('dtotderuf').item(0).firstChild.data;
                                    uf.value = xmldoc.getElementsByTagName('uf').item(0).firstChild.data;
                                    dnewmacum.value = xmldoc.getElementsByTagName('dnewmacum').item(0).firstChild.data;
                                    dmacumuffin.value = xmldoc.getElementsByTagName('dmacumuffin').item(0).firstChild.data;
                                    dtotderpesos.value = xmldoc.getElementsByTagName('dtotderpesos').item(0).firstChild.data;
                                    ingsidep.value = xmldoc.getElementsByTagName('ingsidep').item(0).firstChild.data;
                                    transitrel.value = xmldoc.getElementsByTagName('transitrel').item(0).firstChild.data;
                                    
                                    var ingresar = document.getElementById("ingresar");
                                    ingresar.disabled = false;

                                    var calcular = document.getElementById("calcular");
                                    calcular.disabled = true;
                                 } else {
                                    var glosa = xmldoc.getElementsByTagName('glosa').item(0);

                                    var tas = document.getElementById("tasa");
                                    var derpesos = document.getElementById("derechospesos");
                                    var deruf = document.getElementById("derechosuf");

                                    var dtotderuf = document.getElementById("dtotderuf");
                                    var uf = document.getElementById("uf");
                                    var dnewmacum = document.getElementById("dnewmacum");
                                    var dmacumuffin = document.getElementById("dmacumuffin");
                                    var dtotderpesos = document.getElementById("dtotderpesos");
                                    var ingsidep = document.getElementById("ingsidep");
                                    var transitrel = document.getElementById("transitrel");

                                    var monto = document.getElementById("montopeso");
                                    
                                    monto.value = '';
                                    tas.value = '';
                                    derpesos.value = '';
                                    deruf.value = '';
                                    dtotderuf.value = '';
                                    uf.value = '';
                                    dnewmacum.value = '';
                                    dmacumuffin.value = '';
                                    dtotderpesos.value = '';
                                    ingsidep.value = '';
                                    transitrel.value = '';

                                    var ingresar = document.getElementById("ingresar");
                                    ingresar.disabled = true;

                                    alert("Error:"+glosa.firstChild.data);
                                 }
                           }	 
                    }
             }
        return true;
}






//////////////////////////////////////////////////////
// Ingresa los montos en pantalla
//////////////////////////////////////////////////////
function ingresaMontos(){
   var currentLength = 0;
   var j = 0;
   encontrado = false;
   var seleccion;
   var srut;
   var stipo;
   
   var fechaoper = document.getElementById("fechaoperacion");
   
   if ( !validaFecha(fechaoper.value)){
        alert("Fecha de operacion incorrecta:"+fechaoper.value);
        fechaoper.select();
        fechaoper.focus();
        return false;
   }
    
   var  mitipo = document.getElementById("tipo");
   
   currentLength = mitipo.length;
   j = 0;
   encontrado = false;
        
   for(j=0; j<=currentLength && mitipo.options[j] != null  && !encontrado; j++){
       if(mitipo.options[j].selected == true){
           encontrado = true;
           seleccion = mitipo.options[j].value;
       }
   }
        
   if ( !encontrado){
      alert("No hay tipo de operacion seleccionado");
      mitipo.focus();
      return false;
   }
   
   switch (seleccion){
           case "1":
                   stipo = "001";
                   var frut = document.getElementById("rutcliente"); 
                   
                   srut = frut.value;

                   if ( (srut == "") || (!validaRut(srut))){
                        alert("Rut invalido");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   break;
           case "2":
                   stipo = "221";
                   var frut = document.getElementById("rutpropio");
                   
                   if ( (frut == null) || (frut.value == "") || (!validaRut(frut.value))){
                        alert("Rut invalido");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   srut = frut.value;
                   
                   break;        
           case "3":
                   stipo = "101";
                   var frut = document.getElementById("rutopdir");
                   currentLength = frut.length;
                   j = 0;
                   encontrado = false;
        
                   for(j=0; j<=currentLength && frut.options[j] != null  && !encontrado; j++){
                        if(frut.options[j].selected == true){
                            encontrado = true;
                            seleccion = frut.options[j].value;
                        }
                   }
                    
                   if (!encontrado){
                        alert("Rut no seleccionado");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   srut = seleccion;
                   
                   if ( (srut == "") || (!validaRut(srut))){
                        alert("Rut invalido");
                        rut.focus();
                        return false;
                   }
                   
                   break;         
    }

   var monto = document.getElementById("montopeso");
   var valorMonto = monto.value.replace(",","");
   //valorMonto = valorMonto.replace(".",",");
   
   if ( isNaN(valorMonto) || (parseFloat(valorMonto) < 0) ){
           alert("Monto incorrecto");
           monto.select();
           monto.focus();
           return false
   }
   
   
   var tasa = document.getElementById("tasa");
   var valortasaSin = tasa.value.replace(",","");
   //var valortasa = valortasaSin.replace(".",",");
   var valortasa = valortasaSin;

   if ( isNaN(valortasaSin) ){
           alert("Valor tasa incorrecta valor errado");
           monto.select();
           monto.focus();
           return false
   }


   var derechosuf = document.getElementById("derechosuf");
   var valorderechosufSin = derechosuf.value.replace(",","");
   //var valorderechosuf = valorderechosufSin.replace(".",",");
   var valorderechosuf = valorderechosufSin;

   if ( isNaN(valorderechosufSin) || (parseFloat(valorderechosufSin) < 0) ){
           alert("Valor derechos en UF incorrecto");
           monto.select();
           monto.focus();
           return false
   }


   var derechospesos = document.getElementById("derechospesos");
   var valorderechospesosSin = derechospesos.value.replace(",","");
   //var valorderechospesos = valorderechospesosSin.replace(".",",");
   var valorderechospesos = valorderechospesosSin;

   if ( isNaN(valorderechospesosSin) || (parseFloat(valorderechospesosSin) < 0) ){
           alert("Valor derechos en pesos incorrecto");
           monto.select();
           monto.focus();
           return false
   }

   var sfecha = fechaoper.value;

   // rescatamos los demas valores

   var dtotderuf = document.getElementById("dtotderuf");
   var valordtotderuf = dtotderuf.value;
   
   if ( isNaN(valordtotderuf)  ){
           alert("Error en valores calculados (dtotderuf)");
           monto.select();
           monto.focus();
           return false
   }


   var uf = document.getElementById("uf");
   var valoruf = uf.value;
   
   if ( isNaN(valoruf)  ){
           alert("Error en valores calculados (uf)");
           monto.select();
           monto.focus();
           return false
   }


   var dnewmacum = document.getElementById("dnewmacum");
   var valordnewmacum = dnewmacum.value;
   
   if ( isNaN(valordnewmacum)  ){
           alert("Error en valores calculados (dnewmacum)");
           monto.select();
           monto.focus();
           return false
   }

   var dmacumuffin = document.getElementById("dmacumuffin");
   var valordmacumuffin = dmacumuffin.value;
   
   if ( isNaN(valordmacumuffin)  ){
           alert("Error en valores calculados (dmacumuffin)");
           monto.select();
           monto.focus();
           return false
   }

   var dtotderpesos = document.getElementById("dtotderpesos");
   var valordtotderpesos = dtotderpesos.value;
   
   if ( isNaN(valordtotderpesos)  ){
           alert("Error en valores calculados (dtotderpesos)");
           monto.select();
           monto.focus();
           return false
   }

   var ingsidep = document.getElementById("ingsidep");
   var valoringsidep = ingsidep.value;
   
   if ( isNaN(valoringsidep)  ){
           alert("Error en valores calculados (ingsidep)");
           monto.select();
           monto.focus();
           return false
   }

   var transitrel = document.getElementById("transitrel");
   var valortransitrel = transitrel.value;
   
   if ( isNaN(valortransitrel)  ){
           alert("Error en valores calculados (transitrel)");
           monto.select();
           monto.focus();
           return false
   }

   //creamos el objeto AJAX
   _objetus=objetus()

   //variables
   _values_send = "corredor="+escape(scorredor);
   _values_send = _values_send + "&"+ "rut="+srut;
   _values_send = _values_send + "&"+ "fecha="+sfecha;
   _values_send = _values_send + "&"+ "tipo="+stipo;
   _values_send = _values_send + "&"+ "monto="+valorMonto;
   _values_send = _values_send + "&"+ "tasa="+valortasa;
   _values_send = _values_send + "&"+ "derechosuf="+valorderechosuf;
   _values_send = _values_send + "&"+ "derechospesos="+valorderechospesos;
   _values_send = _values_send + "&"+ "dtotderuf="+dtotderuf.value;
   _values_send = _values_send + "&"+ "uf="+uf.value;
   _values_send = _values_send + "&"+ "dnewmacum="+dnewmacum.value;
   _values_send = _values_send + "&"+ "dmacumuffin="+dmacumuffin.value;
   _values_send = _values_send + "&"+ "dtotderpesos="+dtotderpesos.value;
   _values_send = _values_send + "&"+ "ingsidep="+ingsidep.value;
   _values_send = _values_send + "&"+ "transitrel="+transitrel.value;


   // La pagina a la que vamos a llamar
   _URL_= "ingresaMontos.jsp";

	    // Abrimos el request y hacemos la consulta por background
            _objetus.open("POST",_URL_,true);
            _objetus.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            
	   // cabecera POST
           _objetus.send('&'+_values_send); 
           
	   _objetus.onreadystatechange=function() {
                   if (_objetus.readyState==4){
                           if (_objetus.status == 200){  // Pagina cargada
                                 var xmldoc = _objetus.responseXML;
                                 var error = xmldoc.getElementsByTagName('error').item(0);
                                 
                                 if ( error.firstChild.data != "NO"){
                                    var glosa = xmldoc.getElementsByTagName('glosa').item(0);
                                    alert("Error:"+glosa.firstChild.data);
                                 }

                                 var calcular = document.getElementById("calcular");
                                 calcular.disabled = true;
                                 var eliminar = document.getElementById("eliminar");
                                 eliminar.disabled = true;

                                 var ingresar = document.getElementById("ingresar");
                                 ingresar.disabled = true;

                                 var tas = document.getElementById("tasa");
                                 var derpesos = document.getElementById("derechospesos");
                                 var deruf = document.getElementById("derechosuf");

                                 var dtotderuf = document.getElementById("dtotderuf");
                                 var uf = document.getElementById("uf");
                                 var dnewmacum = document.getElementById("dnewmacum");
                                 var dmacumuffin = document.getElementById("dmacumuffin");
                                 var dtotderpesos = document.getElementById("dtotderpesos");
                                 var ingsidep = document.getElementById("ingsidep");
                                 var transitrel = document.getElementById("transitrel");
                                 var monto = document.getElementById("montopeso");
                                    
                                 monto.value = '';

                                 tas.value = '';
                                 derpesos.value = '';
                                 deruf.value = '';
                                 dtotderuf.value = '';
                                 uf.value = '';
                                 dnewmacum.value = '';
                                 dmacumuffin.value = '';
                                 dtotderpesos.value = '';
                                 ingsidep.value = '';
                                 transitrel.value = '';
                           }	 
                    }
             }
        return true;
}

//////////////////////////////////////////////////////
// Elimina los montos en pantalla
//////////////////////////////////////////////////////
function eliminaMontos(){
   var currentLength = 0;
   var j = 0;
   encontrado = false;
   var seleccion;
   var srut;
   var stipo;
   
   var fechaoper = document.getElementById("fechaoperacion");
   
   if ( !validaFecha(fechaoper.value)){
        alert("Fecha de operacion incorrecta:"+fechaoper.value);
        fechaoper.select();
        fechaoper.focus();
        return false;
   }
    
   var mitipo = document.getElementById("tipo");
   
   currentLength = mitipo.length;
   j = 0;
   encontrado = false;
        
   for(j=0; j<=currentLength && mitipo.options[j] != null  && !encontrado; j++){
       if(mitipo.options[j].selected == true){
           encontrado = true;
           seleccion = mitipo.options[j].value;
       }
   }
        
   if ( !encontrado){
      alert("No hay tipo de operacion seleccionado");
      mitipo.focus();
      return false;
   }
   
   switch (seleccion){
           case "1":
                   stipo = "001";
                   var frut = document.getElementById("rutcliente");
                   
                   srut = frut.value;

                   if ( (srut == "") || (!validaRut(srut))){
                        alert("Rut invalido");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   break;
           case "2":
                   stipo = "221";
                   var frut = document.getElementById("rutpropio");
                   
                   if ( (frut == null) || (frut.value == "") || (!validaRut(frut.value))){
                        alert("Rut invalido");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   srut = frut.value;
                   
                   break;        
           case "3":
                   stipo = "101";
                   var frut = document.getElementById("rutopdir");
                   currentLength = frut.length;
                   j = 0;
                   encontrado = false;
        
                   for(j=0; j<=currentLength && frut.options[j] != null  && !encontrado; j++){
                        if(frut.options[j].selected == true){
                            encontrado = true;
                            seleccion = frut.options[j].value;
                        }
                   }
                    
                   if (!encontrado){
                        alert("Rut no seleccionado");
                        frut.select();
                        frut.focus();
                        return false;
                   }
                   
                   srut = seleccion;
                   
                   if ( (srut == "") || (!validaRut(srut))){
                        alert("Rut invalido");
                        rut.focus();
                        return false;
                   }
                   
                   break;         
    }

   var monto = document.getElementById("montopeso");
   var valorMonto = monto.value.replace(",","");
   //valorMonto = valorMonto.replace(".",",");
   
   if ( isNaN(valorMonto) || (parseFloat(valorMonto) < 0) ){
           alert("Monto incorrecto");
           monto.select();
           monto.focus();
           return false
   }
   
   
   var tasa = document.getElementById("tasa");
   var valortasa = tasa.value.replace(",","");
   //valortasa = valortasa.replace(".",",");
   
   if ( isNaN(valortasa) ){
           alert("Elimina Valor tasa incorrecta");
           monto.select();
           monto.focus();
           return false
   }


   var derechosuf = document.getElementById("derechosuf");
   var valorderechosuf = derechosuf.value.replace(",","");
   //valorderechosuf = valorderechosuf.replace(".",",");
   
   if ( isNaN(valorderechosuf) || (parseFloat(valorderechosuf) < 0) ){
           alert("Valor derechos en UF incorrecto");
           monto.select();
           monto.focus();
           return false
   }


   var derechospesos = document.getElementById("derechospesos");
   var valorderechospesos = derechospesos.value.replace(",","");
   //valorderechospesos = valorderechospesos.replace(".",",");
   
   if ( isNaN(valorderechospesos) || (parseFloat(valorderechospesos) < 0) ){
           alert("Valor derechos en pesos incorrecto " );
           monto.select();
           monto.focus();
           return false
   }


   var dminguf = document.getElementById("dminguf");
   var valordminguf = dminguf.value;
   
   if ( isNaN(valordminguf) || (parseFloat(valordminguf) < 0) ){
           alert("Valor monto en uf incorrecto");
           monto.select();
           monto.focus();
           return false
   }

   var sfecha = fechaoper.value;

   // rescatamos los demas valores
   var fechalast = document.getElementById("fechalast");
   var horalast = document.getElementById("horalast");

   if ( (fechalast == "") || (horalast == "")){
        alert("No hay nigun registro consultado");
        return false;
   }

   //creamos el objeto AJAX
   _objetus=objetus();

   //variables
   _values_send = "corredor="+escape(scorredor);
   _values_send = _values_send + "&"+ "rut="+srut;
   _values_send = _values_send + "&"+ "fecha="+sfecha;
   _values_send = _values_send + "&"+ "tipo="+stipo;
   _values_send = _values_send + "&"+ "monto="+valorMonto;
   _values_send = _values_send + "&"+ "tasa="+valortasa;
   _values_send = _values_send + "&"+ "derechosuf="+valorderechosuf;
   _values_send = _values_send + "&"+ "derechospesos="+valorderechospesos;
   _values_send = _values_send + "&"+ "fechalast="+fechalast.value;
   _values_send = _values_send + "&"+ "horalast="+horalast.value;
   _values_send = _values_send + "&"+ "dminguf="+dminguf.value;
 

   // La pagina a la que vamos a llamar
             _URL_= "eliminaMontos.jsp";

	    // Abrimos el request y hacemos la consulta por background
            _objetus.open("POST",_URL_,true);
            _objetus.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            
	   // cabecera POST
           _objetus.send('&'+_values_send); 
           
	   _objetus.onreadystatechange=function() {
                   if (_objetus.readyState==4){
                           if (_objetus.status == 200){  // Pagina cargada
                                 var xmldoc = _objetus.responseXML;
                                 var error = xmldoc.getElementsByTagName('error').item(0);
                                 
                                 if ( error.firstChild.data != "NO"){
                                    var glosa = xmldoc.getElementsByTagName('glosa').item(0);
                                    alert("Error:"+glosa.firstChild.data);
                                 }

                                  var calcular = document.getElementById("calcular");
                                    calcular.disabled = true;
                                    var eliminar = document.getElementById("eliminar");
                                    eliminar.disabled = true;
                                    var ingresar = document.getElementById("ingresar");
                                    ingresar.disabled = true;

                                 var tas = document.getElementById("tasa");
                                 var derpesos = document.getElementById("derechospesos");
                                 var deruf = document.getElementById("derechosuf");

                                 var dtotderuf = document.getElementById("dtotderuf");
                                 var uf = document.getElementById("uf");
                                 var dnewmacum = document.getElementById("dnewmacum");
                                 var dmacumuffin = document.getElementById("dmacumuffin");
                                 var dtotderpesos = document.getElementById("dtotderpesos");
                                 var ingsidep = document.getElementById("ingsidep");
                                 var transitrel = document.getElementById("transitrel");
                                 var monto = document.getElementById("montopeso");
                                    
                                 monto.value = '';

                                 tas.value = '';
                                 derpesos.value = '';
                                 deruf.value = '';
                                 dtotderuf.value = '';
                                 uf.value = '';
                                 dnewmacum.value = '';
                                 dmacumuffin.value = '';
                                 dtotderpesos.value = '';
                                 ingsidep.value = '';
                                 transitrel.value = '';

                           }	 
                    }
             }
        return true;
}

//////////////////////////
// Pone el rut en el campo correspndiente
////////////////////////////
function escribeRutCliente(campo){

   var myrutcliente = document.getElementById("rutcliente");
   
   var currentLength = campo.length;
   var j = 0;
   var encontrado = false;
   var seleccion = "";
     
   for(j=0; j<=currentLength && campo.options[j] != null  && !encontrado; j++){
       if(campo.options[j].selected == true){

           if ( campo.options[j].value != "0-0"){
                encontrado = true;
                seleccion = campo.options[j].value;
           } else {
                encontrado = true;
                seleccion = "";
           }
       }
   }
    
    if ( encontrado ){
       myrutcliente.value = seleccion;
    } else {
       myrutcliente.value = "";
    }

}



/////////////////////////////
// cambia elcomo bo si el rut es valido
// y si existe el rut en el combo
//////////////////////////////////////////
 function cambiarCombo(field, event) {
         var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
         if (keyCode == 13) {

             if (!validaRut(field.value) ){
                alert("Rut incorrecto ");
                field.select();
                field.focus();
                return false;
             }

             var myusuarios = document.getElementById("usuario");
             var currentLength = myusuarios.length;
             var j = 0;
     
             for(j=0; j<=currentLength && myusuarios.options[j] != null; j++){
               if(myusuarios.options[j].value == field.value){
                      myusuarios.options[j].selected = true;
                      return false;
                }
             }
             myusuarios.options[0].selected = true;
             return false;

          } else
         return true;
} 



/////////////////////////////
// cambia elcomo bo si el rut es valido
// y si existe el rut en el combo
//////////////////////////////////////////
 function cambiarComboFocusLost(field) {

     if (field.value == "") return false;

     if (!validaRut(field.value) ){
         alert("Rut incorrecto ");
         field.select();
         field.focus();
         return false;
     }

     var myusuarios = document.getElementById("usuario");
     var currentLength = myusuarios.length;
     var j = 0;
     
     for(j=0; j<=currentLength && myusuarios.options[j] != null; j++){
           if(myusuarios.options[j].value == field.value){
             myusuarios.options[j].selected = true;
             return false;
         }
     }
     myusuarios.options[0].selected = true;
     return false;
} 



///////////////////////////////////////////////////////
// Activa el boton de calcular
///////////////////////////////////////////////////////
function activaBotonReturn(campo, event){
         var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
         if (keyCode == 13) {
            activaBoton(campo);
          } else
         return true;
   
}


///////////////////////////////////////////////////////
// Activa el boton de calcular
///////////////////////////////////////////////////////
function activaBoton(campo){
   var monto = document.getElementById("montopeso");


   if ( monto.value == "") return false;

   var valorMonto = monto.value.replace(",","");
   //valorMonto = valorMonto.replace(".",",");
   
   if ( isNaN(valorMonto) || (parseFloat(valorMonto) < 0) ){
           alert("Monto incorrecto ");
           monto.select();
           monto.focus();
           return false
   }
   
   var  calcular = document.getElementById("calcular");
   calcular.disabled = false;
   
}


//////////////////////////////////////////////////////
// Actuliza los montos
//////////////////////////////////////////////////////
function actualizaMontos(){
   var currentLength = 0;
   var j = 0;
   encontrado = false;
   var seleccion;
   var srut;
   var stipo;
   
   var fechaoper = document.getElementById("fechaoperacion");
   
   if ( !validaFecha(fechaoper.value)){
        alert("Fecha de operacion incorrecta:"+fechaoper.value);
        fechaoper.select();
        fechaoper.focus();
        return false;
   }
   

   //creamos el objeto AJAX
   _objetus=objetus();

    var sfecha = fechaoper.value;

   //variables
   _values_send = "corredor="+escape(scorredor);
   _values_send = _values_send + "&"+ "fecha="+sfecha;
   
 

   // La pagina a la que vamos a llamar
             _URL_= "actualizaMontos.jsp";

	    // Abrimos el request y hacemos la consulta por background
            _objetus.open("POST",_URL_,true);
            _objetus.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            
	   // cabecera POST
           _objetus.send('&'+_values_send); 
           
            var actualizar = document.getElementById("actualizar");
            actualizar.disabled = true;

	   _objetus.onreadystatechange=function() {
                   if (_objetus.readyState==4){
                           if (_objetus.status == 200){  // Pagina cargada
                                 var xmldoc = _objetus.responseXML;
                                 var error = xmldoc.getElementsByTagName('error').item(0);
                                 
                                 
                                 if ( error.firstChild.data == "NO"){
                                    alert("Actualizacion realizada");
   
                                 } else {
                                    var glosa = xmldoc.getElementsByTagName('glosa').item(0);
                                    alert("Error:"+glosa.firstChild.data);
                                 }
                                var actualizar = document.getElementById("actualizar");
                                actualizar.disabled = false;
                           }	 
                    }
             }
        return true;
}


