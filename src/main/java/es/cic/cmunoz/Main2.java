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
package es.cic.cmunoz;

import es.cic.cmunoz.backend.job.CreadorJobs;
import java.awt.AWTException;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Clase que inicia los Jobs
 */
@Component
public class Main2 {

    private AbstractApplicationContext context;

    static Logger LOG = Logger.getLogger(Main2.class);

    /**
     * Método main que ejecuta la aplicación y levanta el contexto de Spring
     *
     * @param args posibles parámetros que se le pueden pasar a la aplicación
     * @throws AWTException excepción que se lanza con el manejo de las ventanas
     * @throws IOException excepción que se lanza cuando no se puede leer un
     * recurso
     * @throws InterruptedException excepción que se lanza cuando la ejecución
     * del hilo se interrumpe
     */
    public static void main(final String... args) throws AWTException, IOException, InterruptedException {

        LOG.info("Instanciando/Levantando Contexto De Spring");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-batch-context.xml");

        CreadorJobs crearJobs = new CreadorJobs();
        crearJobs.crearJob();

        while (true) {
            Thread.sleep(100);
            if (crearJobs.getActivo() != null) {
                LOG.info("Estado Del Job: ".concat(BatchStatus.COMPLETED.toString()));
                break;
            }
        }
        LOG.info("Saliendo Del Main 2");
        System.exit(0);
    }

}
