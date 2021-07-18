/* http://keith-wood.name/datepick.html
   Afrikaans localisation for jQuery Datepicker.
   Written by Renier Pretorius and Ruediger Thiede. */
(function($) {
	'use strict';
	$.datepick.regionalOptions.af = {
		monthNames: ['Januarie','Februarie','Maart','Abril','Mei','Junie',
		'Julie','Augustus','Septiembre','Oktober','Noviembre','Desember'],
		monthNamesShort: ['Ene','Feb','Mrt','Abr','Mei','Jun',
		'Jul','Ago','Sep','Okt','Nov','Des'],
		dayNames: ['Sondag','Maandag','Dinsdag','Woensdag','Donderdag','Vrydag','Saterdag'],
		dayNamesShort: ['Son','Maan','Dins','Woens','Don','Vry','Sab'],
		dayNamesMin: ['So','Ma','Di','Wo','Do','Vr','Sa'],
		dateFormat: 'dd/mm/yyyy',
		firstDay: 1,
		renderer: $.datepick.defaultRenderer,
		prevText: 'Vorige',
		prevStatus: 'Vertoon vorige maand',
		prevJumpText: '&#x3c;&#x3c;',
		prevJumpStatus: 'Vertoon vorige jaar',
		nextText: 'Volgende',
		nextStatus: 'Vertoon volgende maand',
		nextJumpText: '&#x3e;&#x3e;',
		nextJumpStatus: 'Vertoon volgende jaar',
		currentText: 'Vandag',
		currentStatus: 'Vertoon huidige maand',
		todayText: 'Vandag',
		todayStatus: 'Vertoon huidige maand',
		clearText: 'Vee uit',
		clearStatus: 'Verwyder die huidige datum',
		closeText: 'Klaar',
		closeStatus: 'Sluit sonder verandering',
		yearStatus: 'Vertoon \'n ander jaar',
		monthStatus: 'Vertoon \'n ander maand',
		weekText: 'Wk',
		weekStatus: 'Week van die jaar',
		dayStatus: 'Kies DD, M d',
		defaultStatus: 'Kies \'n datum',
		isRTL: false
	};
	$.datepick.setDefaults($.datepick.regionalOptions.af);
})(jQuery);
