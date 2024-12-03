/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.common;

import cl.femase.gestionweb.vo.VacacionesSaldoPeriodoVO;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
*
* @author aledi
*/
public class CalculadoraPeriodo {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
    public static void main(String[] args) {
        // Verificar que se proporcionen dos argumentos de fecha
        if (args.length < 2) {
            System.out.println("Debe proporcionar dos fechas en el formato YYYY-MM-DD.");
            return;
        }

        // Leer las fechas de los argumentos
        String fechaInicioStr = args[0];
        String fechaActualStr = args[1];

        // Convertir las cadenas de fecha a objetos LocalDate
        LocalDate fechaInicioContrato = LocalDate.parse(fechaInicioStr);
        LocalDate fechaActual = LocalDate.parse(fechaActualStr);

        // Determinar el periodo y obtener los detalles del periodo
        VacacionesSaldoPeriodoVO periodo = determinarPeriodo(fechaInicioContrato, fechaActual);

        // Imprimir los resultados
        System.out.println("Fecha actual: " + fechaActual);
        System.out.println("Fecha Inicio Contrato: " + fechaInicioContrato);
        System.out.println("El periodo correspondiente es: " + periodo.getNumeroPeriodo());
        System.out.println("Inicio del periodo: " + periodo.getFechaInicio());
        System.out.println("Fin del periodo: " + periodo.getFechaFin());
    }

    /**
    * 
    * @param _fechaActualStr
    * @param _fechaInicioStr
    * @return 
    */
    public static VacacionesSaldoPeriodoVO getPeriodoVacaciones(String _fechaActualStr, 
            String _fechaInicioStr){
    
        // Convertir las cadenas de fecha a objetos LocalDate
        LocalDate fechaInicioContrato = LocalDate.parse(_fechaInicioStr);
        LocalDate fechaActual = LocalDate.parse(_fechaActualStr);

        // Determinar el periodo y obtener los detalles del periodo
        VacacionesSaldoPeriodoVO periodo = determinarPeriodo(fechaInicioContrato, fechaActual);

        // Imprimir los resultados
        System.out.println("Fecha actual: " + fechaActual);
        System.out.println("Fecha Inicio Contrato: " + fechaInicioContrato);
        System.out.println("El periodo correspondiente es: " + periodo.getNumeroPeriodo());
        System.out.println("Inicio del periodo: " + periodo.getFechaInicio());
        System.out.println("Fin del periodo: " + periodo.getFechaFin());
        
        return periodo;
    } 
    
    /**
    * 
    * @param _fechaInicioContrato
    * @param _fechaFinalDeseada
    * @return 
    */
    public static List<VacacionesSaldoPeriodoVO> getPeriodosTranscurridos(LocalDate _fechaInicioContrato, 
            LocalDate _fechaFinalDeseada) {
        List<VacacionesSaldoPeriodoVO> periodos = new ArrayList<>();
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate fechaInicioPeriodo = _fechaInicioContrato;
        ////LocalDate fechaFinPeriodo = _fechaInicioContrato.plusYears(1).minusDays(1); // Primer periodo es de 1 año completo
        LocalDate fechaFinPeriodo = LocalDate.of(_fechaInicioContrato.getYear() + 1
            , _fechaInicioContrato.getMonth()
            , _fechaInicioContrato.getDayOfMonth());
        
        int numeroPeriodo = 1;
        while (fechaInicioPeriodo.isBefore(_fechaFinalDeseada)) {
            // Ajustar la fecha de fin del periodo si es la última iteración
////            if (fechaFinPeriodo.isAfter(_fechaFinalDeseada) || fechaFinPeriodo.isEqual(_fechaFinalDeseada)) {
////                fechaFinPeriodo = _fechaFinalDeseada;
////            }
            VacacionesSaldoPeriodoVO periodo = 
                new VacacionesSaldoPeriodoVO(numeroPeriodo, fechaInicioPeriodo, fechaFinPeriodo);
            
            periodos.add(periodo); //fechaInicioPeriodo.format(formatter) + " al " + fechaFinPeriodo.format(formatter));

            // Avanzar al siguiente periodo
            fechaInicioPeriodo = fechaFinPeriodo.plusDays(1);
            fechaFinPeriodo = LocalDate.of(fechaInicioPeriodo.getYear() + 1, fechaInicioPeriodo.getMonth(), fechaInicioPeriodo.getDayOfMonth());
            ////fechaFinPeriodo = fechaInicioPeriodo.plusYears(1).minusDays(1);
        }

        return periodos;
    }
    
    /**
    * 
    * @param fechaInicioContrato
    * @param fechaActual
    * @return 
    */
    private static VacacionesSaldoPeriodoVO determinarPeriodo(LocalDate fechaInicioContrato, LocalDate fechaActual) {
        // Calcular la diferencia en años entre las fechas
        Period periodo = Period.between(fechaInicioContrato, fechaActual);
        int yearsBetween = periodo.getYears();

        // Determinar el periodo basado en la diferencia de años
        if (yearsBetween == 0) {
            // El primer periodo
            LocalDate inicioPeriodo = fechaInicioContrato;
            LocalDate finPeriodo = fechaInicioContrato.plusYears(1);
            return new VacacionesSaldoPeriodoVO(1, inicioPeriodo, finPeriodo);
        } else {
            // Calcular el inicio y fin del periodo correspondiente
            LocalDate inicioPeriodo = fechaInicioContrato.plusYears(yearsBetween);
            LocalDate finPeriodo = inicioPeriodo.plusYears(1);
            return new VacacionesSaldoPeriodoVO(yearsBetween + 1, inicioPeriodo, finPeriodo);
        }
    }

}
