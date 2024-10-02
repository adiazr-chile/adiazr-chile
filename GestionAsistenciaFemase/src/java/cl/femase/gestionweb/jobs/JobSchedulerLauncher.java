/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.jobs;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author aledi
 */
public class JobSchedulerLauncher {
    public static void main(String[] args) {
        try {
            // Crear una instancia del Scheduler
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
   
            // Definir el JobDetail
            JobDetail jobDetail = JobBuilder.newJob(SetSaldoVBAPeriodosJobQA.class)
                    .withIdentity("SetSaldoVBAPeriodosJobQA", "group1")
                    .usingJobData("empresa_id", "emp01")
                    .usingJobData("run_empleado", "12545479-8")
                    .usingJobData("proceso_id", 49)
                    .build();

            // Definir el Trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()  // Puedes cambiar esto según cuándo deseas que inicie el Job
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(60)  // Ejecuta el Job cada 60 segundos
                            .repeatForever())
                    .build();

            // Programar el Job con el Trigger
            scheduler.scheduleJob(jobDetail, trigger);

            // Iniciar el Scheduler
            scheduler.start();

            // Esperar por un tiempo (solo para propósitos de ejemplo)
            Thread.sleep(60000);

            // Detener el Scheduler
            scheduler.shutdown(true);
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
