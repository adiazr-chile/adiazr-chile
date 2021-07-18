/* http://keith-wood.name/datepick.html
   Montenegrin localisation for jQuery Datepicker.
   By Miloš Milošević - fleka d.o.o. */
(function($) {
	'use strict';
	$.datepick.regionalOptions['me-ME'] = {
		monthNames: ['Januar','Februar','Mart','Abril','Maj','Jun',
		'Jul','Avgust','Septembar','Oktobar','Novembar','Decembar'],
		monthNamesShort: ['Ene','Feb','Mar','Abr','Maj','Jun',
		'Jul','Avg','Sep','Okt','Nov','Dic'],
		dayNames: ['Neđelja','Poneđeljak','Utorak','Srijeda','Četvrtak','Petak','Subota'],
		dayNamesShort: ['Neđ','Pon','Uto','Sri','Čet','Pet','Sub'],
		dayNamesMin: ['Ne','Po','Ut','Sr','Če','Pe','Do'],
		dateFormat: 'dd/mm/yyyy',
		firstDay: 1,
		renderer: $.datepick.defaultRenderer,
		prevText: '&#x3c;',
		prevStatus: 'Prikaži prethodni mjesec',
		prevJumpText: '&#x3c;&#x3c;',
		prevJumpStatus: 'Prikaži prethodnu godinu',
		nextText: '&#x3e;',
		nextStatus: 'Prikaži sljedeći mjesec',
		nextJumpText: '&#x3e;&#x3e;',
		nextJumpStatus: 'Prikaži sljedeću godinu',
		currentText: 'Danas',
		currentStatus: 'Tekući mjesec',
		todayText: 'Danas',
		todayStatus: 'Tekući mjesec',
		clearText: 'Obriši',
		clearStatus: 'Obriši trenutni datum',
		closeText: 'Zatvori',
		closeStatus: 'Zatvori kalendar',
		yearStatus: 'Prikaži godine',
		monthStatus: 'Prikaži mjesece',
		weekText: 'Sed',
		weekStatus: 'Sedmica',
		dayStatus: '\'Datum\' DD, M d',
		defaultStatus: 'Odaberi datum',
		isRTL: false
	};
	$.datepick.setDefaults($.datepick.regionalOptions['me-ME']);
})(jQuery);
