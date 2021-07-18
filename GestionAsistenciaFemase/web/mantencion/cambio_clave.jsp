<html>
    <head>
        <title>Cambio de contrase&ntilde;a</title>
        <script src="js/jquery-3.4.1.min.js"></script>
    </head>
    <body>
        <div class="wrapper">
            <h1>Cambio de contrase&ntilde;a</h1>
            <form id="form1" name="form1" method="post" action="<%=request.getContextPath()%>/UsuariosController" target="_self">
                <input name="action" type="hidden" id="accion" value="changepass" />
				<input name="tipo" type="hidden" id="tipo" value="CCL" />
				<input type="password" name="password" class="password" placeholder="Contrase&ntilde;a">
                <input type="password" name="retype_password" class="retype_password" placeholder="Vuelva a escribir la contrase&ntilde;a">
                <input type="submit" class="change_password" value="Cambiar" disabled>
			</form>

            <div id="password_details">
                <h1>La contrase&ntilde;a debe cumplir los siguientes requisitos:</h1>
                <ul>
			        <li id="letter" class="invalid">Al menos<strong> una letra</strong></li>
                    <li id="capital" class="invalid">Al menos <strong>una may&uacute;scula</strong></li>
                    <li id="number" class="invalid">Al menos <strong>un n&uacute;mero</strong></li>
                    <li id="length" class="invalid">Tener al menos <strong>8 caracteres</strong></li>
                    <li id="match" class="invalid">Los campos de contrase&ntilde;a deben coincidir</li>
                </ul>
            </div>

        </div>
        <style>
            body{margin: 0;background-color: #dee8ef;font-family: arial, sans-serif;}
            .wrapper{position:relative;max-width: 260px;width: 100%; margin: 40px auto; padding: 0;background-color: #fff;border-radius: 5px;}
            .wrapper > h1{text-align: center;background-color: #195598;margin: 0;color: #fff; border-top-right-radius: 5px; border-top-left-radius: 5px;font-size: 18px;padding: 15px 0;}
            .wrapper input{padding: 10px;width: 100%; margin-bottom: 15px;font-size: 12px;border: 1px solid #dedede;}
            .change_password{background-color: #66bb6a;color: #fff;border: none !important; cursor: pointer;}
            .change_password:disabled{background-color: #dedede; color:#555;}
            .wrapper form{padding: 15px;}
            #password_details{display:none; position: absolute; left:calc(100% + 20px); border-radius: 5px; top:50px; min-width: 250px; background-color:#fff;padding: 10px;font-size: 12px;}
            #password_details > h1{margin: 0;padding: 5px;font-size: 14px;color: #555;}
            #password_details > ul{margin: 0;padding: 0 30px;}
            #password_details > ul > li{padding:5px 0;}
            #password_details:before{content: "\25C0";position: absolute;top: 20px;left: -10px;font-size: 14px; line-height: 14px;color: #fff;text-shadow: none;display: block;}
            .invalid{color:#fc2e2e}
            .valid{color:#12d600}
        </style>
        <script>
            $(document).ready(function(){
                $('input[type=password]').keyup(function() {
                    validate();
                });

                function validate(){
                    $('#password_details').show();
                    var password = $('.password').val();
                    var retype_password = $('.retype_password').val();
                    //validate the length
                    if ( password.length < 8 ) {
                        $('#length').removeClass('valid').addClass('invalid');
                        $('.change_password').removeAttr('disabled');
                    } else {
                        $('#length').removeClass('invalid').addClass('valid');
                        $('.change_password').attr('disabled', 'disabled');
                    }

                    if ( password.match(/[A-z]/) ) {
                        $('#letter').removeClass('invalid').addClass('valid');
                        $('.change_password').removeAttr('disabled');
                    } else {
                        $('#letter').removeClass('valid').addClass('invalid');
                        $('.change_password').attr('disabled', 'disabled');
                    }

                    //validate capital letter
                    if ( password.match(/[A-Z]/) ) {
                        $('#capital').removeClass('invalid').addClass('valid');
                        $('.change_password').removeAttr('disabled');
                    } else {
                        $('#capital').removeClass('valid').addClass('invalid');
                        $('.change_password').attr('disabled', 'disabled');
                    }

                    //validate number
                    if ( password.match(/\d/) ) {
                        $('#number').removeClass('invalid').addClass('valid');
                        $('.change_password').removeAttr('disabled');
                    } else {
                        $('#number').removeClass('valid').addClass('invalid');
                        $('.change_password').attr('disabled', 'disabled');
                    }

                    //validate retype password
                    if(password == retype_password && password.length > 8 &&  password.match(/[A-z]/) &&  password.match(/[A-Z]/) &&  password.match(/\d/)){
                        $('#match').removeClass('invalid').addClass('valid');
                        $('.change_password').removeAttr('disabled');
                    }else{
                        $('#match').removeClass('valid').addClass('invalid');
                        $('.change_password').attr('disabled', 'disabled');
                    }

                }
            });
        </script>
    </body>
</html>