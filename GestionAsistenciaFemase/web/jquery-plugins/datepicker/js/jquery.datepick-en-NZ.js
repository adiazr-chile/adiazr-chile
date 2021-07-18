/* http://keith-wood.name/datepick.html
   English/New Zealand localisation for jQuery Datepicker.
   Based on en-GB. */
(function($) {
	'use strict';
	$.datepick.regionalOptions['en-NZ'] = {
		monthNames: ['Enero','Febrero','Marzo','Abril','Mayo','Junio',
		'Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'],
		monthNamesShort: ['Ene','Feb','Mar','Abr','Mayo','Jun',
		'Jul','Ago','Sep','Oct','Nov','Dic'],
		dayNames: ['Domingo','Lunes','Martes','Miercoles','Jueves','Viernes','Sabado'],
		dayNamesShort: ['Dom','Lun','Mar','Mier','Jue','Vier','Sab'],
		dayNamesMin: ['Do','Lu','Ma','We','Th','Fr','Sa'],
		dateFormat: 'dd/mm/yyyy',
		firstDay: 1,
		renderer: $.datepick.defaultRenderer,
		prevText: 'Prev',
		prevStatus: 'Show the previous month',
		prevJumpText: '&#x3c;&#x3c;',
		prevJumpStatus: 'Show the previous year',
		nextText: 'Next',
		nextStatus: 'Show the next month',
		nextJumpText: '&#x3e;&#x3e;',
		nextJumpStatus: 'Show the next year',
		currentText: 'Current',
		currentStatus: 'Show the current month',
		todayText: 'Today',
		todayStatus: 'Show today\'s month',
		clearText: 'Clear',
		clearStatus: 'Erase the current date',
		closeText: 'Done',
		closeStatus: 'Close without change',
		yearStatus: 'Show a different year',
		monthStatus: 'Show a different month',
		weekText: 'Wk',
		weekStatus: 'Week of the year',
		dayStatus: 'Select DD, M d',
		defaultStatus: 'Select a date',
		isRTL: false
	};
	$.datepick.setDefaults($.datepick.regionalOptions['en-NZ']);
})(jQuery);
