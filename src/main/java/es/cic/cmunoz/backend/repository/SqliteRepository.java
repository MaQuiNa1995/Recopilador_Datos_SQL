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
package es.cic.cmunoz.backend.repository;

import java.sql.SQLException;
import java.util.List;

/**
 * @autor cmunoz
 * @version 1.0
 */
public interface SqliteRepository {

    /**
     * Método usado para la eliminación de una consulta
     *
     * @param nombreQuery String que contiene el nombre de la query
     */
    void borrarQuery(String nombreQuery);

    /**
     * Método encargado de la creación de la base de datos
     */
    void crearBaseDatos();

    /**
     * Método encargado de la creación de la carpeta donde se guardará el CSV
     * generado
     */
    void crearCarpeta();

    /**
     * Método encargado de la recuperación de los nombres de las cnsultas que
     * están guardadas en base de datos
     *
     * @return Lista de los nombres de las consultas de base de datos
     */
    List<String> getNombresConsultas();

    /**
     * Método encargado de la recuperación de la consulta de base de datos
     *
     * @param nombreQuery String que contieneel nombre de la query
     * @return String que contiene la query recuperada
     */
    String getRawQuery(String nombreQuery);

    /**
     * Método encargado de la inserción de una nueva consulta en base de datos
     *
     * @param nombreQuery String que contiene el nombre de la query
     * @param rawQuery String que contiene la consulta
     * @throws SQLException se lanzará si hubo algún problema en la inserción
     */
    void insertarQuery(String nombreQuery, String rawQuery) throws SQLException;

    /**
     * Método usado para la modificación de la fecha de ejecución de una
     * consulta ejecutada
     *
     * @param nombreQuery String que contiene el nombre de la query a modificar
     */
    void updateConsulta(String nombreQuery);
    
}
