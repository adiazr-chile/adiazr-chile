/* http://keith-wood.name/datepick.html
   Bosnian localisation for jQuery Datepicker.
   Written by Kenan Konjo. */
(function($) {
	'use strict';
	$.datepick.regionalOptions.bs = {
		monthNames: ['Januar','Februar','Mart','Abril','Maj','Juni',
		'Juli','Agosto','Septembar','Oktobar','Novembar','Decembar'],
		monthNamesShort: ['Ene','Feb','Mar','Abr','Maj','Jun',
		'Jul','Ago','Sep','Okt','Nov','Dic'],
		dayNames: ['Nedelja','Ponedeljak','Utorak','Srijeda','Četvrtak','Petak','Subota'],
		dayNamesShort: ['Ned','Pon','Uto','Sri','Čet','Pet','Sub'],
		dayNamesMin: ['Ne','Po','Ut','Sr','Če','Pe','Do'],
		dateFormat: 'dd.mm.yyyy',
		firstDay: 1,
		renderer: $.datepick.defaultRenderer,
		prevText: '&#x3c;',
		prevStatus: '',
		prevJumpText: '&#x3c;&#x3c;',
		prevJumpStatus: '',
		nextText: '&#x3e;',
		nextStatus: '',
		nextJumpText: '&#x3e;&#x3e;',
		nextJumpStatus: '',
		currentText: 'Danas',
		currentStatus: '',
		todayText: 'Danas',
		todayStatus: '',
		clearText: 'X',
		clearStatus: '',
		closeText: 'Zatvori',
		closeStatus: '',
		yearStatus: '',
		monthStatus: '',
		weekText: 'Wk',
		weekStatus: '',
		dayStatus: 'DD d MM',
		defaultStatus: '',
		isRTL: false
	};
	$.datepick.setDefaults($.datepick.regionalOptions.bs);
})(jQuery);
