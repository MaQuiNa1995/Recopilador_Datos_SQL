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
package es.cic.cmunoz.backend.job;

import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import es.cic.cmunoz.backend.config.ApplicationContextProvider;

/**
 * Clase encargada de la creación de jobs
 * @author cmunoz
 */
@Component
public class CreadorJobs {

    private static final Logger LOG = getLogger(CreadorJobs.class.getName());
    private HashMap<String, String> mapaParams;

    private AbstractApplicationContext context;

    private JobExecution execution;

    /**
     * Método usado para la creación de un job
     * @param listaMapasParams 
     */
    public void crearJob(Map<String, String> listaMapasParams) {

        context = ApplicationContextProvider.getApplicationContext();
        Job job = (Job) context.getBean("consultaResultJob");

        mapaParams = deserializarLista();

        boolean exito = true;

        execution = null;

        LOG.info("Creando Job: " + job.getName());

        JobParameters parametros = crearParametrosJob(listaMapasParams);

        LOG.info("Pasando Parámetros Al Job");

        if (listaMapasParams == null) {
            LOG.info("No Se pudo Ejecutar El Job Debido A Que No Hay Definida Ninguna Consulta");
        } else {

            try {
                LOG.info("Ejecutando Job Con Los Siguientes Parámetros: \n" + parametros.toString());
                execution = ((JobLauncher) context.getBean("jobLauncher")).run(job, parametros);
                LOG.info("Estado Del Job: ".concat(execution.getStatus().toString()));
            } catch (JobExecutionException jee) {
                exito = false;
                LOG.info("Job: " + job.getName() + " Fall\u00f3 , Raz\u00f3n:{0}" + jee.getMessage());
            }
        }

        if ((exito) && (execution != null)) {
            LOG.info("Éxito Job Ejecutado Con Éxito");
        } else {
            LOG.info("Falló La Ejecución Del Job");
        }

        LOG.info("Estado Del Job: ".concat(execution.getStatus().toString()));

    }

    /**
     * Método usado para la creación de los parámetros del job
     *
     * @param mapa Hashmap que contiene la consutla entera y la parte de las
     * select
     * @return Hashmap que contiene los parámetros del job
     */
    private JobParameters crearParametrosJob(Map<String, String> mapa) {

        String carpetaHoy = crearCarpeta();

        Date fecha = new Date();

        String cadenaFecha = fecha.toString();
        String select = mapa.get("sql.select");
        String consulta = mapa.get("sql.consulta");
        String destino = carpetaHoy
                .concat("/Consulta")
                .concat(".csv");

        JobParameter paramFecha = new JobParameter(cadenaFecha);
        JobParameter paramSelect = new JobParameter(select);
        JobParameter paramConsulta = new JobParameter(consulta);
        JobParameter paramDestino = new JobParameter(destino);

        Map<String, JobParameter> mapaParametros = new HashMap<>();

        mapaParametros.put("fecha", paramFecha);
        mapaParametros.put("select", paramSelect);
        mapaParametros.put("consulta", paramConsulta);
        mapaParametros.put("destino", paramDestino);

        JobParameters jobParametros = new JobParameters(mapaParametros);

        return jobParametros;
    }

    /**
     * Método usado para la creación de las carpetas
     *
     * @return fechaActual String de la fecha actual con formato dd-MM-yyyy
     */
    private String crearCarpeta() {

        String fechaActual = new SimpleDateFormat("dd-MM-yyyy")
                .format(Calendar.getInstance()
                        .getTime());

        File rutaCarpeta = new File(fechaActual);

        if (!existeCarpeta(fechaActual)) {

            rutaCarpeta.mkdir();
        }

        return fechaActual;
    }

    /**
     * Método usado para la verificación de la existencia de una carpeta
     *
     * @param rutaCarpeta String que contiene la ruta de la carpeta
     * @return boolean que contiene la existencia de la carpeta
     */
    private boolean existeCarpeta(String rutaCarpeta) {

        File carpeta = new File(rutaCarpeta);

        return carpeta.exists();
    }

    /**
     * Método para la deserialización del hashmap que guardamos previamente en
     * un fichero
     *
     * @return Hashmap que contiene el mapa que guardamos previamente
     */
    @SuppressWarnings("unchecked")
	private HashMap<String, String> deserializarLista() {

        final String FICHERO_NOMBRE = "hashmap.ser";

        context = ApplicationContextProvider.getApplicationContext();
        mapaParams = (HashMap<String, String>) context.getBean("parametrosJob");

        try (FileInputStream fos = new FileInputStream(FICHERO_NOMBRE);
                ObjectInputStream ois = new ObjectInputStream(fos)) {
            LOG.info("Objeto Deserializado Con Éxito");
            mapaParams = (HashMap<String, String>) ois.readObject();
        } catch (FileNotFoundException ex) {
            LOG.info("Archivo No Encontrado, Razón ".concat(ex.getMessage()));
        } catch (IOException ex) {
            LOG.info("No se puede escribir en el fichero, Razón ".concat(ex.getMessage()));
        } catch (ClassNotFoundException ex) {
            LOG.info("Clase No Encontrada, Mas info: ".concat(ex.getMessage()));
        }

        File archivoEliminar = new File(FICHERO_NOMBRE);
        if (archivoEliminar.exists()) {
            archivoEliminar.delete();
        }

        LOG.info("Retornando Lista");

        return mapaParams;
    }

    /**
     * Método usado para la verificación de la ejecución del job
     *
     * @return retorna la los milisegundos que tardó el job en ejecutar devuelve
     * nulo si el job no temrinó
     */
    public Date getActivo() {
        return execution.getEndTime();
    }

}
