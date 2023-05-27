
<!DOCTYPE html>
<html>
  <head>
    <title>Vista Previa empleados</title>
    <link rel="stylesheet" href="css/bootstrap_3_3_0.min.css">
    <link rel="stylesheet" href="css/bootstrap-treeview_1_2_0.min.css" />
    <script type="text/javascript" charset="utf8" src="js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" charset="utf8" src="js/bootstrap-treeview_1_2_0.min.js"></script>
    
    <script type="text/javascript">
        var nodosSeleccionados = Array();
    </script>
    <style>
            @font-face {
                font-family: 'icomoon';
                src:url('https://s3.amazonaws.com/icomoon.io/4/Loading/icomoon.eot?-9haulc');
                src:url('https://s3.amazonaws.com/icomoon.io/4/Loading/icomoon.eot?#iefix-9haulc') format('embedded-opentype'),
                    url('https://s3.amazonaws.com/icomoon.io/4/Loading/icomoon.woff?-9haulc') format('woff'),
                    url('https://s3.amazonaws.com/icomoon.io/4/Loading/icomoon.ttf?-9haulc') format('truetype'),
                    url('https://s3.amazonaws.com/icomoon.io/4/Loading/icomoon.svg?-9haulc#icomoon') format('svg');
                font-weight: normal;
                font-style: normal;
            }

        [class^="icon-"], [class*=" icon-"] {
            font-family: 'icomoon';
            speak: none;
            font-style: normal;
            font-weight: normal;
            font-variant: normal;
            text-transform: none;
            line-height: 1;

            /* Better Font Rendering =========== */
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
        }

        .icon-spinner:before {
            content: "\e000";
        }
        .icon-spinner-2:before {
            content: "\e001";
        }
        .icon-spinner-3:before {
            content: "\e002";
        }
        .icon-spinner-4:before {
            content: "\e003";
        }
        .icon-spinner-5:before {
            content: "\e004";
        }
        .icon-spinner-6:before {
            content: "\e005";
        }
        .icon-spinner-7:before {
            content: "\e006";
        }

        @keyframes anim-rotate {
            0% {
                transform: rotate(0);
            }
            100% {
                transform: rotate(360deg);
            }
        }
        .spinner {
            display: inline-block;
            font-size:8em;
            height: 1em;
            line-height: 1;
            margin: .5em;
            animation: anim-rotate 2s infinite linear;
            color: #fff;
            text-shadow: 0 0 .25em rgba(255,255,255, .3);
        }
        .spinner--steps {
            animation: anim-rotate 1s infinite steps(8);
        }
        .spinner--steps2 {
            animation: anim-rotate 1s infinite steps(12);
        }
       *
        body {
            font-family: sans-serif;
            line-height: 1.5;
            font-size: 9px;
            background: #aac8e2;
        }
        .talign-center {
            text-align: center;
        }
        a, a:visited {
            text-decoration: none;
            color: #444;
            text-shadow: 0 1px 2px rgba(0,0,0, .3);
            transition: color .3s;
        }
        a:hover, a:active {
            color: #ccc;
        }
        footer {
            margin-top: 2em;
        }
 
    </style>
    
  </head>
  <body>
        <div class="container">
    	<h1>B&uacute;squeda y selecci&oacute;n de empleados</h1>
        <h5>(Busque un empleado o selecci&oacute;nelo directamente desde el &aacute;rbol)</h5>
      <div class="row"></div>
      <div class="row"></div>
      <div class="row">
        <hr>
        <div class="col-sm-3">
          <h2>Criterio de b&uacute;squeda</h2>
          <!-- <form> -->
            <div class="form-group">
              <label for="input-search" class="sr-only">xxx:</label>
              <input type="input" class="form-control" id="input-search" placeholder="Escriba para buscar..." value="">
            </div>
            <!--
            <div class="checkbox">
              <label>
                <input type="checkbox" class="checkbox" id="chk-ignore-case" value="false">
                Ignore Case
              </label>
            </div>
            -->
            <!--
            <div class="checkbox">
              <label>
                <input type="checkbox" class="checkbox" id="chk-exact-match" value="false">
                Exact Match
              </label>
            </div>
            -->
            <!--
            <div class="checkbox">
              <label>
                <input type="checkbox" class="checkbox" id="chk-reveal-results" value="false">
                Reveal Results
              </label>
            </div>
            -->
            <button type="button" class="btn btn-success" id="btn-search">Buscar</button>
            <button type="button" class="btn btn-default" id="btn-clear-search">Limpiar</button>
        <!-- </form> --></div>
        <div class="col-sm-4">
          <h2>Organizaci&oacute;n</h2>
          <div id="treeview_json" class=""></div>
        </div>
        <div class="col-sm-4">
          &nbsp;
        </div>
        <div class="col-sm-6">
          <h2>Resultado b&uacute;squeda</h2>
          <div id="search-output"></div>
        </div>
        <div class="col-sm-4">
          &nbsp;
        </div>
        <div class="col-sm-6">
          <h2>Empleados seleccionados</h2>
          <div id="selectable-output"></div>
        </div>
        
        <!--<div class="col-sm-4">
          
        </div>-->
      </div>
  	</div>

      <section class="talign-center">
        <div id="relojito" class="spinner spinner--steps icon-spinner" aria-hidden="true"></div>
        <!--
        <div class="spinner icon-spinner-2" aria-hidden="true"></div>
        <div class="spinner icon-spinner-3" aria-hidden="true"></div>
        <div class="spinner icon-spinner-4" aria-hidden="true"></div>
        <div class="spinner icon-spinner-5" aria-hidden="true"></div>
        <div class="spinner icon-spinner-6" aria-hidden="true"></div>
        -->
        <!--<div id="relojito2" class="spinner spinner--steps2 icon-spinner-7" aria-hidden="true"></div>-->
    </section>
      
    </body>
   
    <script type="text/javascript">
             
        $(document).ready(function(){
            var treeData;
            $.ajax({
              type: "GET",  
              url: "<%=request.getContextPath()%>/LoadTreeView",
              dataType: "json",       
              success: function(response)  
              {
                initTree(response);
                document.getElementById("relojito").style.display = "none";
              }   
            });
                    
            function initTree(treeData) {
                $('#treeview_json').treeview(
                {
                    data: treeData,
                    multiSelect: $('#chk-select-multi').is(':checked'),
                    expandIcon: "glyphicon glyphicon-stop",
                    collapseIcon: "glyphicon glyphicon-unchecked",
                    nodeIcon: "glyphicon glyphicon-user",
                    showTags: true,
                    //showCheckbox: true,
                    highlightSelected: true
                    ,onNodeSelected: function(event, node) {
                        var contiene = nodosSeleccionados.includes('' + node.href);
                        var label = node.text; //+ '[' + node.status + ']';
                        if (contiene === false){
                            $('#selectable-output').prepend('<p><input type="checkbox" id="check|' + node.href + '" name="check|' + node.href + '" value="' + node.href + '" checked><label for="check|' + node.href + '">'+ '('+node.href+') ' + label + ' ['+node.parent_id+']</label> Ver empleado Marcas Ausencias</p>');
                            nodosSeleccionados.push(''+node.href);
                        }
		    }
                    
                }
                );
                
                var search = function(e) {
                    var pattern = $('#input-search').val();
                            
                    var options = {
                      ignoreCase: true,
                      exactMatch: false,
                      revealResults: false
                    };
                    var results = $('#treeview_json').treeview('search', [ pattern, options ]);

                    var output = '<p>' + results.length + ' empleado(s) encontrado(s)</p>';
                    
                    $.each(results, function (index, result) {
                        output += '<p><a href="#" id="cb">'+result.text+'<input type="checkbox" id="check|' + result.href + '" name="check|' + result.href + '" value="' + result.href + '" checked"></a><label for="check|' + result.href + '">'+ '('+result.href+') ' + result.text + ' ['+result.parent_id+']</label></p>';
                        //alert('Item encontrado: (' + result.href + ') ' + result.text);
                    });
                    
                    //var contiene = nodosSeleccionados.includes(''+keyfound);
                    //alert('contiene? '+contiene)
                    //if (contiene===false){
                        $('#search-output').html(output);
                        //nodosSeleccionados.push(''+result.href);
                    //}
                }

                $('#btn-search').on('click', search);
                $('#input-search').on('keyup', search);

                $('#btn-clear-search').on('click', function (e) {
                    $('#treeview_json').treeview('clearSearch');
                    $('#input-search').val('');
                    $('#search-output').html('');
                });
            }
        });
       // document.getElementById("relojito").style.display = "none";
        
        function openEditForm(strrut){
            document.location.href='<%=request.getContextPath()%>/EmpleadosController?action=edit&rutEmpleado='+strrut; 
        }

  </script>
  	
  </body>
</html>
