<?php namespace JF;
/**

Copyright 2017 JQueryForm.com
License: http://www.jqueryform.com/license.php

FormID:  jqueryform-d81043
Date:    2017-11-30 14:39:30
Version: v2.0.3
Generated by http://www.jqueryform.com

PHP 5.3+ is required.
If mailgun is used AND the form has file upload field, PHP 5.5+ is required.

*/

class Config {
	private static $config;

    public static function getConfig( $decode = true ){
    	self::$config = self::_getConfig( $decode );
    	self::overwriteConfig();
    	return self::$config;
    }

    private static function _getConfig( $decode = true ){
        ob_start();
        // ---------------------------------------------------------------------
        // JSON format config
        // Note: please make a copy before you edit config manually
        // ---------------------------------------------------------------------

/**JSON_START**/ ?>
{
    "formId": "jqueryform-d81043",
    "email": {
        "to": "alediazrub@gmail.com",
        "cc": "",
        "bcc": "",
        "subject": "Nuevo ",
        "template": ""
    },
    "admin": {
        "users": "admin:989e5",
        "dataDelivery": "emailAndFile"
    },
    "thankyou": {
        "url": "",
        "message": "",
        "seconds": "10"
    },
    "seo": {
        "trackerId": "",
        "title": "",
        "description": "",
        "keywords": "",
        "author": ""
    },
    "mailer": "local",
    "smtp": {
        "host": "",
        "user": "",
        "password": ""
    },
    "mailgun": {
        "domain": "",
        "apiKey": "",
        "fromEmail": "",
        "fromName": ""
    },
    "styles": {
        "iCheck": {
            "enabled": true,
            "skin": "flat",
            "colorScheme": "blue"
        },
        "Select2": {
            "enabled": true
        }
    },
    "logics": [

    ],
    "fields": [
        {
            "label": "Rut",
            "field_type": "text",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "size": "small",
                "addon": [

                ],
                "validators": {
                    "alphanumeric": {
                        "enabled": true
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Este campo es requerido"
                    }
                },
                "hidden": false,
                "placeholder": "Ingrese Rut"
            },
            "id": "f4",
            "cid": "c26",
            "labelHide": false
        },
        {
            "label": "N\u00b0 Ficha",
            "field_type": "text",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "size": "small",
                "addon": [

                ],
                "validators": {
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    },
                    "alphanumeric": {
                        "enabled": true
                    }
                },
                "placeholder": "Ingrese n\u00famero de ficha"
            },
            "id": "f5",
            "cid": "c34"
        },
        {
            "label": "Nombres",
            "field_type": "name",
            "field_options": {
                "size": "small",
                "sender": "fullname",
                "images": {
                    "urls": "",
                    "slideshow": false
                },
                "validators": {
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    },
                    "minlength": {
                        "enabled": false
                    },
                    "maxlength": {
                        "enabled": true,
                        "val": "70",
                        "msg": "No debe exceder de {0} caracteres"
                    }
                },
                "placeholder": "Ingrese nombres",
                "addon": {
                    "leftIcon": "glyphicon glyphicon-user"
                }
            },
            "id": "f1",
            "cid": "c1"
        },
        {
            "label": "Apellido Paterno",
            "field_type": "text",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "size": "small",
                "addon": [

                ],
                "validators": {
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    },
                    "alphanumeric": {
                        "enabled": false
                    },
                    "maxlength": {
                        "enabled": true,
                        "val": "50"
                    }
                },
                "placeholder": "Ingrese apellido paterno"
            },
            "id": "f6",
            "cid": "c35"
        },
        {
            "label": "Apellido Materno",
            "field_type": "text",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "size": "small",
                "addon": [

                ],
                "validators": {
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    },
                    "max": {
                        "enabled": false
                    },
                    "alphanumeric": {
                        "enabled": false
                    },
                    "maxlength": {
                        "enabled": true,
                        "msg": "No debe exceder de {0} caracteres",
                        "val": "50"
                    }
                },
                "placeholder": "Ingrese apellido materno"
            },
            "id": "f7",
            "cid": "c40"
        },
        {
            "label": "Fecha de nacimiento",
            "field_type": "date",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "addon": {
                    "rightIcon": "glyphicon glyphicon-th",
                    "rightText": ""
                },
                "validators": {
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "placeholder": "Ingrese fecha de nacimiento",
                "date": {
                    "format": "dd-mm-yyyy",
                    "startView": "1"
                }
            },
            "id": "f8",
            "cid": "c55"
        },
        {
            "label": "Direcci\u00f3n",
            "field_type": "text",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "size": "small",
                "addon": [

                ],
                "validators": {
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    },
                    "maxlength": {
                        "enabled": true,
                        "msg": "No debe exceder de {0} caracteres",
                        "val": "70"
                    }
                },
                "placeholder": "Ingrese Direcci\u00f3n. Por ej.: Pasaje Los Boldos 1245, depto 487"
            },
            "id": "f9",
            "cid": "c45"
        },
        {
            "label": "Email",
            "field_type": "email",
            "field_options": {
                "size": "small",
                "sender": true,
                "images": {
                    "urls": "",
                    "slideshow": false
                },
                "validators": {
                    "email": {
                        "enabled": true
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    },
                    "maxlength": {
                        "enabled": true,
                        "val": "70",
                        "msg": "No debe exceder de {0} caracteres"
                    }
                },
                "addon": {
                    "leftIcon": "glyphicon glyphicon-envelope",
                    "leftText": ""
                },
                "placeholder": "Ingrese correo electr\u00f3nico"
            },
            "id": "f2",
            "cid": "c2"
        },
        {
            "label": "Fotograf\u00eda",
            "field_type": "file",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "file": {
                    "showPreview": true,
                    "showRemove": true,
                    "browseLabel": "Seleccione Archivo"
                },
                "validators": {
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                }
            },
            "id": "f10",
            "cid": "c58"
        },
        {
            "label": "Sexo",
            "field_type": "radio",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "Si",
                        "checked": false,
                        "value": "S"
                    },
                    {
                        "label": "No",
                        "checked": true,
                        "value": "N"
                    }
                ],
                "presetJson": "",
                "validators": {
                    "required": {
                        "enabled": false,
                        "msg": "Requerido"
                    },
                    "min": {
                        "enabled": false
                    }
                },
                "style": {
                    "columns": "col-2"
                }
            },
            "id": "f11",
            "cid": "c63"
        },
        {
            "label": "Tel\u00e9fono Fijo",
            "field_type": "phone",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "sender": false,
                "placeholder": "xxx-xxx-xxxx",
                "addon": {
                    "leftIcon": "glyphicon glyphicon-earphone"
                },
                "validators": {
                    "pattern": {
                        "enabled": true,
                        "val": "[0-9]{3,4}[ -.]*[0-9]{3,4}[ -.]*[0-9]{4}",
                        "msg": "Invalid phone number"
                    },
                    "phonenumber": {
                        "enabled": true,
                        "msg": "Verifique n\u00famero telef\u00f3nico"
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                }
            },
            "phone": {
                "validationMethod": "simple",
                "simpleFormat": "xxx-xxx-xxxx",
                "usePhoneLib": "N"
            },
            "id": "f12",
            "cid": "c69"
        },
        {
            "label": "Tel\u00e9fono m\u00f3vil",
            "field_type": "phone",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "sender": false,
                "placeholder": "+xxx-xxx-xxx-xxxx",
                "addon": {
                    "leftIcon": "glyphicon glyphicon-earphone"
                },
                "validators": {
                    "pattern": {
                        "enabled": true,
                        "val": "[+]*[0-9]{0,3}[ -.]*[0-9]{3,4}[ -.]*[0-9]{3,4}[ -.]*[0-9]{4}",
                        "msg": "Invalid phone number"
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                }
            },
            "phone": {
                "validationMethod": "simple",
                "simpleFormat": "xxx-xxx-xxxx",
                "usePhoneLib": "N"
            },
            "id": "f13",
            "cid": "c75"
        },
        {
            "label": "Comuna",
            "field_type": "dropdown",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "- Seleccione comuna -",
                        "value": "-1",
                        "checked": true
                    },
                    {
                        "label": "Comuna 1",
                        "checked": false,
                        "value": "1"
                    },
                    {
                        "label": "Comuna 2",
                        "checked": false,
                        "value": "2"
                    },
                    {
                        "label": "Comuna 3",
                        "checked": false,
                        "value": "3"
                    }
                ],
                "include_blank_option": false,
                "validators": {
                    "minlength": {
                        "msg": "Please select at least {0} option(s)",
                        "enabled": false
                    },
                    "maxlength": {
                        "msg": "Please select no more than {0} option(s)",
                        "enabled": false
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "presetJson": "",
                "multiple": false
            },
            "id": "f15",
            "cid": "c87"
        },
        {
            "label": "Estado",
            "field_type": "dropdown",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "- Seleccione estado -",
                        "value": "-1",
                        "checked": true
                    },
                    {
                        "label": "Vigente",
                        "checked": false,
                        "value": "1"
                    },
                    {
                        "label": "No Vigente",
                        "value": "2",
                        "checked": null
                    }
                ],
                "include_blank_option": false,
                "validators": {
                    "minlength": {
                        "msg": "Please select at least {0} option(s)",
                        "enabled": false
                    },
                    "maxlength": {
                        "msg": "Please select no more than {0} option(s)",
                        "enabled": false
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "presetJson": "",
                "multiple": false
            },
            "id": "f16",
            "cid": "c96"
        },
        {
            "label": "Fecha inicio contrato",
            "field_type": "date",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "addon": {
                    "rightIcon": "glyphicon glyphicon-th",
                    "rightText": ""
                },
                "validators": {
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "placeholder": "Ingrese fecha inicio de contrato",
                "date": {
                    "format": "dd-mm-yyyy",
                    "startView": "1"
                }
            },
            "cid": "c70",
            "id": "f17"
        },
        {
            "label": "Contrato Indefinido",
            "field_type": "radio",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "Si",
                        "checked": false,
                        "value": "S"
                    },
                    {
                        "label": "No",
                        "checked": true,
                        "value": "N"
                    }
                ],
                "presetJson": "",
                "validators": {
                    "required": {
                        "enabled": false,
                        "msg": "Requerido"
                    },
                    "min": {
                        "enabled": false
                    }
                },
                "style": {
                    "columns": "col-2"
                }
            },
            "cid": "c83",
            "id": "f19"
        },
        {
            "label": "Fecha t\u00e9rmino contrato",
            "field_type": "date",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "addon": {
                    "rightIcon": "glyphicon glyphicon-th",
                    "rightText": ""
                },
                "validators": {
                    "required": {
                        "enabled": false,
                        "msg": "Requerido"
                    }
                },
                "placeholder": "Ingrese fecha t\u00e9rmino de contrato",
                "date": {
                    "format": "dd-mm-yyyy",
                    "startView": "1"
                }
            },
            "cid": "c91",
            "id": "f20"
        },
        {
            "label": "Art\u00edculo N\u00b0 22",
            "field_type": "radio",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "Si",
                        "checked": false,
                        "value": "S"
                    },
                    {
                        "label": "No",
                        "checked": true,
                        "value": "N"
                    }
                ],
                "presetJson": "",
                "validators": {
                    "required": {
                        "enabled": false,
                        "msg": "Requerido"
                    },
                    "min": {
                        "enabled": false
                    }
                },
                "style": {
                    "columns": "col-2"
                }
            },
            "cid": "c98",
            "id": "f21"
        },
        {
            "label": "Empresa",
            "field_type": "dropdown",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "- Seleccione empresa -",
                        "value": "-1",
                        "checked": true
                    },
                    {
                        "label": "Empresa 1",
                        "checked": false,
                        "value": "1"
                    },
                    {
                        "label": "Empresa 2",
                        "value": "2",
                        "checked": null
                    }
                ],
                "include_blank_option": false,
                "validators": {
                    "minlength": {
                        "msg": "Please select at least {0} option(s)",
                        "enabled": false
                    },
                    "maxlength": {
                        "msg": "Please select no more than {0} option(s)",
                        "enabled": false
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "presetJson": "",
                "multiple": false,
                "dependOn": {
                    "enabled": false
                }
            },
            "cid": "c119",
            "id": "f23"
        },
        {
            "label": "Departamento",
            "field_type": "dropdown",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "- Seleccione depto -",
                        "value": "-1",
                        "checked": true
                    },
                    {
                        "label": "Depto 1",
                        "checked": false,
                        "value": "1"
                    },
                    {
                        "label": "Depto 2",
                        "value": "2",
                        "checked": null
                    }
                ],
                "include_blank_option": false,
                "validators": {
                    "minlength": {
                        "msg": "Please select at least {0} option(s)",
                        "enabled": false
                    },
                    "maxlength": {
                        "msg": "Please select no more than {0} option(s)",
                        "enabled": false
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "presetJson": "",
                "multiple": false,
                "dependOn": {
                    "enabled": true,
                    "id": "f23"
                }
            },
            "cid": "c126",
            "id": "f24"
        },
        {
            "label": "Centro de Costo",
            "field_type": "dropdown",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "- Seleccione cenco -",
                        "value": "-1",
                        "checked": true
                    },
                    {
                        "label": "Cenco 1",
                        "checked": false,
                        "value": "1"
                    },
                    {
                        "label": "Cenco 2",
                        "value": "2",
                        "checked": null
                    }
                ],
                "include_blank_option": false,
                "validators": {
                    "minlength": {
                        "msg": "Please select at least {0} option(s)",
                        "enabled": false
                    },
                    "maxlength": {
                        "msg": "Please select no more than {0} option(s)",
                        "enabled": false
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "presetJson": "",
                "multiple": false,
                "dependOn": {
                    "enabled": true,
                    "id": "f24"
                }
            },
            "cid": "c133",
            "id": "f25"
        },
        {
            "label": "Turno",
            "field_type": "dropdown",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "- Seleccione turno -",
                        "value": "-1",
                        "checked": true
                    },
                    {
                        "label": "Turno 1",
                        "checked": false,
                        "value": "1"
                    },
                    {
                        "label": "Turno 2",
                        "value": "2",
                        "checked": null
                    }
                ],
                "include_blank_option": false,
                "validators": {
                    "minlength": {
                        "msg": "Please select at least {0} option(s)",
                        "enabled": false
                    },
                    "maxlength": {
                        "msg": "Please select no more than {0} option(s)",
                        "enabled": false
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "presetJson": "",
                "multiple": false,
                "dependOn": {
                    "enabled": false
                }
            },
            "cid": "c142",
            "id": "f26"
        },
        {
            "label": "Cargo",
            "field_type": "dropdown",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "- Seleccione cargo -",
                        "value": "-1",
                        "checked": true
                    },
                    {
                        "label": "Cargo 1",
                        "checked": false,
                        "value": "1"
                    },
                    {
                        "label": "Cargo 2",
                        "value": "2",
                        "checked": null
                    }
                ],
                "include_blank_option": false,
                "validators": {
                    "minlength": {
                        "msg": "Please select at least {0} option(s)",
                        "enabled": false
                    },
                    "maxlength": {
                        "msg": "Please select no more than {0} option(s)",
                        "enabled": false
                    },
                    "required": {
                        "enabled": true,
                        "msg": "Requerido"
                    }
                },
                "presetJson": "",
                "multiple": false,
                "dependOn": {
                    "enabled": false
                }
            },
            "cid": "c149",
            "id": "f27"
        },
        {
            "label": "Autoriza ausencia",
            "field_type": "radio",
            "required": true,
            "field_options": {
                "images": {
                    "urls": "",
                    "style": [

                    ],
                    "slideshow": false
                },
                "options": [
                    {
                        "label": "Si",
                        "checked": false,
                        "value": "S"
                    },
                    {
                        "label": "No",
                        "checked": true,
                        "value": "N"
                    }
                ],
                "presetJson": "",
                "validators": {
                    "required": {
                        "enabled": false,
                        "msg": "Requerido"
                    },
                    "min": {
                        "enabled": false
                    }
                },
                "style": {
                    "columns": "col-2"
                }
            },
            "cid": "c156",
            "id": "f28"
        },
        {
            "label": "Submit Button",
            "field_type": "submit",
            "required": true,
            "field_options": {
                "page": {
                    "title": "Submit",
                    "labelPrev": "< Back",
                    "showPageNumnber": false,
                    "pageNumberText": "{page} \/ {total}"
                },
                "images": {
                    "urls": "",
                    "slideshow": false
                }
            },
            "labelHide": true,
            "submit": {
                "label": "Guardar",
                "icon": "",
                "checkRequiredFields": "Verifique todos los campos requeridos"
            },
            "id": "f0",
            "cid": "c0"
        }
    ]
}
<?php /**JSON_END**/

        $json = ob_get_clean() ;

        return $decode ? json_decode( trim($json), true ) : $json;
    } // end of getConfig()

    private static function getValue( $fieldId, $default = NULL ){
        return isset( $_POST[$fieldId] ) ? $_POST[$fieldId] : $default ;
    }

    private static function overwriteConfig(){
    	//self::get_to();
    }

    private static function get_to(){
    	$value = self::getValue( 'c25' );
    	$to = array(
    		'Option 1' => 'a@a.com',
    		'Option 2' => 'b@b.com',
    		'Option 3' => 'c@c.com',
    	);

    	if( isset( $to[$value] ) ){
    		self::$config['email']['to'] = $to[ $value ];
    	};
    }

} // end of Config class