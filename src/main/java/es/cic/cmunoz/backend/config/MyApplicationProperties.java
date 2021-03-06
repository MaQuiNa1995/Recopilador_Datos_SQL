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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * Clase encargada de la gestión de los properties donde se guarda la
 * información de la base de datos a usar
 *
 * @author cmunoz
 */
public class MyApplicationProperties {

    private static PropertiesConfiguration configuration = null;

    static {
        try {
            configuration = new PropertiesConfiguration("configuracionBBDD.properties");
        } catch (org.apache.commons.configuration.ConfigurationException ex) {
            Logger.getLogger(MyApplicationProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
        configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
    }

    public static synchronized String getProperty(final String key) {
        return (String) configuration.getProperty(key);
    }
}
