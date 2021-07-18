/* http://keith-wood.name/datepick.html
   Norwegian localisation for jQuery Datepicker.
   Written by Naimdjon Takhirov (naimdjon@gmail.com). */
(function($) {
	'use strict';
	$.datepick.regionalOptions.no = {
		monthNames: ['Januar','Februar','Mars','Abril','Mai','Juni',
		'Juli','Agosto','Septiembre','Oktober','Noviembre','Desember'],
		monthNamesShort: ['Ene','Feb','Mar','Abr','Mai','Jun',
		'Jul','Ago','Sep','Okt','Nov','Des'],
		dayNamesShort: ['Søn','Man','Tir','Ons','Tor','Fre','Lør'],
		dayNames: ['Søndag','Mandag','Tirsdag','Onsdag','Torsdag','Fredag','Lørdag'],
		dayNamesMin: ['Sø','Ma','Ti','On','To','Fr','Lø'],
		dateFormat: 'dd.mm.yyyy',
		firstDay: 1,
		renderer: $.datepick.defaultRenderer,
		prevText: '&laquo;Forrige',
		prevStatus: '',
		prevJumpText: '&#x3c;&#x3c;',
		prevJumpStatus: '',
		nextText: 'Neste&raquo;',
		nextStatus: '',
		nextJumpText: '&#x3e;&#x3e;',
		nextJumpStatus: '',
		currentText: 'I dag',
		currentStatus: '',
		todayText: 'I dag',
		todayStatus: '',
		clearText: 'Tøm',
		clearStatus: '',
		closeText: 'Lukk',
		closeStatus: '',
		yearStatus: '',
		monthStatus: '',
		weekText: 'Uke',
		weekStatus: '',
		dayStatus: 'D, M d',
		defaultStatus: '',
		isRTL: false
	};
	$.datepick.setDefaults($.datepick.regionalOptions.no);
})(jQuery);
