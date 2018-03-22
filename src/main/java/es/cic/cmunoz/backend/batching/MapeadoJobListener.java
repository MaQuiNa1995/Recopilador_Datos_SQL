/*
 * Copyright 2017 cmunoz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.cic.cmunoz.backend.batching;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * Clase usada para ser el listener de los job
 */
public class MapeadoJobListener implements JobExecutionListener {

    private static final Logger LOG = Logger.getLogger(MapeadoJobListener.class.getName());
    private DateTime horaInicio;

    /**
     * Método usado cuando se inicia el Job
     *
     * @param jobExecution objeto que contiene la ejecución del job
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        horaInicio = new DateTime();

        Calendar calendario = new GregorianCalendar();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minutos = calendario.get(Calendar.MINUTE);
        int segundos = calendario.get(Calendar.SECOND);
        
        String horaActual= String.valueOf(hora)
                .concat(String.valueOf(minutos))
                .concat(String.valueOf(segundos));

        LOG.log(Level.INFO, "El Job Empieza a Las: ".concat(horaActual));
    }

    /**
     * Método usado cuando se acaba el Job
     *
     * @param jobExecution objeto que contiene la ejecución del job
     */
    @Override
    public void afterJob(JobExecution jobExecution) {

        DateTime horaTerminado;

        horaTerminado = new DateTime();
        LOG.log(Level.INFO, "El Job Termino a Las: ".concat(String.valueOf(horaTerminado)));
        LOG.log(Level.INFO, "Tiempo Empleado: {0} Segundos", calcularTiempo(horaInicio, horaTerminado) / 1000f);

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            LOG.log(Level.INFO, "Estado Del Job: {0}", BatchStatus.COMPLETED);

        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {

            LOG.warning("El Job Fallo Con Las Siguientes Excepciones: ");

            List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();

            exceptionList.stream().forEach((Throwable th) -> {
                LOG.warning(th.getLocalizedMessage());
            });
        }
    }

    /**
     * Método usado para calcular el tiempo que ha estado ejecutando el job
     *
     * @param inicio Hora inicio
     * @param fin Hora fin
     * @return fechaAhora tiempo en milisegundos que han pasado
     */
    private long calcularTiempo(DateTime inicio, DateTime fin) {

        long fechaAhora = fin.getMillis() - inicio.getMillis();

        return fechaAhora;
    }

}
