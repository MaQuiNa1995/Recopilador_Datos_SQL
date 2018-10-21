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
package es.cic.cmunoz.backend.config;

import es.cic.cmunoz.backend.job.CreadorJobs;
import es.cic.cmunoz.backend.repository.SqliteRepository;
import es.cic.cmunoz.backend.repository.SqliteRepositoryImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Clase encargada de la creación de los beans a través de javaConfig
 * @author cmunoz
 */
@Configuration
@ComponentScan("es.cic.cmunoz")
public class Configuracion {

    @Bean(name="paramJob")
    public Map<String,String> listParamJob() {
        return Collections.emptyMap();
    }
    
    @Bean
    public SqliteRepository sqliteRepository(){
        return new SqliteRepositoryImpl();
    }
    
    @Bean(name="CreadorJobs")
    public CreadorJobs creadorJobs() {
    	CreadorJobs creadorJobs = new CreadorJobs();
		return creadorJobs;
    }
    
    
}
