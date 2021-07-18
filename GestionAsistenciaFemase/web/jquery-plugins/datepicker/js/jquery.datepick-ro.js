/* http://keith-wood.name/datepick.html
   Romanian localisation for jQuery Datepicker.
   Written by Edmond L. (ll_edmond@walla.com) and Ionut G. Stan (ionut.g.stan@gmail.com). */
(function($) {
	'use strict';
	$.datepick.regionalOptions.ro = {
		monthNames: ['Ianuarie','Februarie','Martie','Abrilie','Mai','Iunie',
		'Iulie','Agosto','Septembrie','Octombrie','Noiembrie','Decembrie'],
		monthNamesShort: ['Ian','Feb','Mar','Abr','Mai','Iun',
		'Iul','Ago','Sep','Oct','Noi','Dic'],
		dayNames: ['Duminică','Luni','Marti','Miercuri','Joi','Vineri','Sâmbătă'],
		dayNamesShort: ['Dum','Lun','Mar','Mie','Joi','Vin','Sâm'],
		dayNamesMin: ['Du','Lu','Ma','Mi','Jo','Vi','Sâ'],
		dateFormat: 'dd.mm.yyyy',
		firstDay: 1,
		renderer: $.datepick.defaultRenderer,
		prevText: '&laquo;Precedentă',
		prevStatus: 'Arată luna precedenta',
		prevJumpText: '&laquo;&laquo;',
		prevJumpStatus: '',
		nextText: 'Urmatoare&raquo;',
		nextStatus: 'Arată luna urmatoare',
		nextJumpText: '&raquo;&raquo;',
		nextJumpStatus: '',
		currentText: 'Azi',
		currentStatus: 'Arată luna curenta',
		todayText: 'Azi',
		todayStatus: 'Arată luna curenta',
		clearText: 'Curat',
		clearStatus: 'Sterge data curenta',
		closeText: 'Închide',
		closeStatus: 'Închide fara schimbare',
		yearStatus: 'Arată un an diferit',
		monthStatus: 'Arată o luna diferita',
		weekText: 'Săpt',
		weekStatus: 'Săptamana anului',
		dayStatus: 'Selectează D, M d',
		defaultStatus: 'Selectează o data',
		isRTL: false
	};
	$.datepick.setDefaults($.datepick.regionalOptions.ro);
})(jQuery);
