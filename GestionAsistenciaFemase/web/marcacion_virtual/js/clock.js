var updateTime = function() {
    var currentDate = new Date(),
        hours = currentDate.getHours(),
        minutes = currentDate.getMinutes(), 
        seconds = currentDate.getSeconds(),
        weekDay = currentDate.getDay(), 
        day = currentDate.getDate(), 
        month = currentDate.getMonth(), 
        year = currentDate.getFullYear();
	var f = new Date();
		//document.write(f.getDate() + "/" + (f.getMonth() +1) + "/" + f.getFullYear());	
	//alert('fecha actual: '+f.getDate() + "/" + (f.getMonth() +1) + "/" + f.getFullYear());
    const weekDays = [
        'Domingo',
        'Lunes',
        'Martes',
        'Miércoles',
        'Jueves',
        'Viernes',
        'Sabado'
    ];

    document.getElementById('weekDay').textContent = weekDays[weekDay];
    document.getElementById('day').textContent = day;
    
    const months = [
        'Enero',
        'Febrero',
        'Marzo',
        'Abril',
        'Mayo',
        'Junio',
        'Julio',
        'Agosto',
        'Septiembre',
        'Octubre',
        'Noviembre',
        'Diciembre'
    ];

    document.getElementById('month').textContent = months[month];
    document.getElementById('year').textContent = year;

    document.getElementById('hours').textContent = hours;
    
    if (minutes < 10) {
        minutes = "0" + minutes;
    }

    if (seconds < 10) {
        seconds = "0" + seconds;
    }

    document.getElementById('minutes').textContent = minutes;
    document.getElementById('seconds').textContent = seconds;
    
    document.getElementById('hh').value = hours;
    document.getElementById('mm').value = minutes;
    document.getElementById('ss').value = seconds;
    
};

updateTime();

setInterval(updateTime, 1000);