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

import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.server.SpringVaadinServlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.RegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Clase que inicia la aplicación con <b>SpringBoot</b>
 */
@Configuration
@ComponentScan
@EnableVaadin
@EnableAutoConfiguration(exclude = {
    BatchAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    DataSourceAutoConfiguration.class
})
public class Main extends SpringBootServletInitializer {

    /**
     * Método que ejecuta la aplicación
     *
     * @param args Argumentos que podrían pasarse al ejecutar la aplicación
     */
    public static void main(final String... args) {
        SpringApplication app = new SpringApplication(Main.class);
        app.run(args);
    }

    /**
     * Método usado para el mapeado del <b>EndPoint de Vaadin</b>
     *
     * @return objeto que contiene el servlet
     */
    @Bean
    public RegistrationBean vaadinSpringBootServlet() {
        final SpringVaadinServlet servlet = new SpringVaadinServlet();
        return new ServletRegistrationBean(servlet, "/*");
    }
}