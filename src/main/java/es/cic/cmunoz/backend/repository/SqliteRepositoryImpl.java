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

import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class SqliteRepositoryImpl implements SqliteRepository {

    private static final Logger LOG = getLogger(SqliteRepositoryImpl.class.getName());
    private static final String RUTA_HOME = System.getProperty("user.home");
    private static final String RUTA_CARPETA_BBDD = RUTA_HOME.concat("\\ConsultasBBDD");
    private static final String RUTA_BBDD = RUTA_CARPETA_BBDD.concat("\\BaseDatosConsultas.sqlite");

    /**
     * Método encargado de la creación de la carpeta donde se guardará el CSV
     * generado
     */
    @Override
    public void crearCarpeta() {
        if (!existeCarpeta()) {
            LOG.info("Creando Carpeta De La Base De Datos: ".concat(RUTA_CARPETA_BBDD));
            File carpetaAplicacion = new File(RUTA_CARPETA_BBDD);
            carpetaAplicacion.mkdir();
        }
    }

    /**
     * Método encargado del verificado de la existencia de la carpeta donde se
     * guardan los CSV generados
     *
     * @return booleano con el estado de la existencia de la carpeta
     */
    private boolean existeCarpeta() {
        LOG.info("Verificando Carpeta De La Base De Datos: ".concat(RUTA_CARPETA_BBDD));
        File carpeta = new File(RUTA_CARPETA_BBDD);
        return carpeta.exists();
    }

    /**
     * Método encargado de la creación de la base de datos
     */
    @Override
    public void crearBaseDatos() {
        if (existeBaseDatos()==false) {
            crearTablaConsultas();
            insertPrueba();
        }
    }

    /**
     * Método encargado de la verificación de la existencia de la base de datos
     *
     * @return
     */
    private boolean existeBaseDatos() {
        File bbdd = new File(RUTA_BBDD);
        return bbdd.exists();
    }

    /**
     * Método encargado de la creación de la conexión a la base de datos
     */
    private Connection conectarBaseDatos() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection("jdbc:sqlite:".concat(RUTA_BBDD));
            LOG.info("Creando Conexión con base de datos en: " + RUTA_BBDD);
        } catch (SQLException ex) {
            LOG.warn("Error al conectar a la base de datos:".concat(ex.getMessage()));
        }

        return conexion;
    }

    /**
     * Método encargado de la creación de latabla de la base de datos
     */
    private void crearTablaConsultas() {

        LOG.info("Creando tabla");

        try (Connection conexion = conectarBaseDatos(); Statement stmt = conexion.createStatement()) {

            String sentenciaSql = "create table CONSULTAS"
                    .concat(" (p_consultas     integer primary key autoincrement  NOT NULL,")
                    .concat(" nombreQuery     text                               NOT NULL,")
                    .concat(" query           text                               NOT NULL,")
                    .concat(" fechaCreacion   text                               NOT NULL,")
                    .concat(" fechaEjecucion  text                               NULL")
                    .concat(")");

            stmt.executeUpdate(sentenciaSql);

        } catch (SQLException ex) {
            LOG.warn("Error al crear la tabla de consultas, ".concat(ex.getMessage()));
        }
    }

    /**
     * Método encargado de la recuperación de los nombres de las cnsultas que
     * están guardadas en base de datos
     *
     * @return Lista de los nombres de las consultas de base de datos
     */
    @Override
    public List<String> getNombresConsultas() {

        String sentenciaSql = "SELECT nombreQuery FROM CONSULTAS";

        List<String> listaNombresQuerys = new ArrayList<>();

        try (Connection conexion = conectarBaseDatos();
                PreparedStatement query = conexion.prepareStatement(sentenciaSql)) {

            ResultSet resultado = query.executeQuery();

            while (resultado.next()) {

                String nombreQuery = resultado.getString("nombreQuery");

                listaNombresQuerys.add(nombreQuery);
            }

        } catch (SQLException ex) {
            LOG.warn("Error al consultar la tabla de consultas, ".concat(ex.getMessage()));
        }

        return listaNombresQuerys;
    }

    /**
     * Método encargado de la recuperación de la consulta de base de datos
     *
     * @param nombreQuery String que contieneel nombre de la query
     * @return String que contiene la query recuperada
     */
    @Override
    public String getRawQuery(String nombreQuery) {
        String sentenciaSql = "SELECT query FROM CONSULTAS WHERE nombreQuery = ? ";

        String queryAPelo = "";

        try (Connection conexion = conectarBaseDatos();
                PreparedStatement query = conexion.prepareStatement(sentenciaSql)) {

            query.setString(1, nombreQuery);

            ResultSet result = query.executeQuery();

            queryAPelo = result.getString("query");

        } catch (SQLException ex) {
            LOG.warn("Error al consultar la tabla de consultas, ".concat(ex.getMessage()));
        }
        return queryAPelo;
    }

    /**
     * Método encargado de la inserción de una nueva consulta en base de datos
     *
     * @param nombreQuery String que contiene el nombre de la query
     * @param rawQuery String que contiene la consulta
     * @throws SQLException se lanzará si hubo algún problema en la inserción
     */
    @Override
    public void insertarQuery(String nombreQuery, String rawQuery) throws SQLException {

        String sentenciaSql = "INSERT into CONSULTAS (nombreQuery,query,fechaCreacion,fechaEjecucion) values (?,?,?,?)";

        try (Connection conexion = conectarBaseDatos();
                PreparedStatement query = conexion.prepareStatement(sentenciaSql)) {

            query.setString(1, nombreQuery);
            query.setString(2, rawQuery);
            query.setString(3, getFechaAhoraMismo());
            query.setString(4, null);

            query.execute();

        }

    }

    /**
     * Método usado para la eliminación de una consulta
     *
     * @param nombreQuery String que contiene el nombre de la query
     */
    @Override
    public void borrarQuery(String nombreQuery) {

        String sentenciaSql = "DELETE FROM CONSULTAS WHERE nombreQuery = ?";

        try (Connection conexion = conectarBaseDatos();
                PreparedStatement query = conexion.prepareStatement(sentenciaSql)) {

            query.setString(1, nombreQuery);

            query.execute();

        } catch (SQLException ex) {
            LOG.warn("Error al deletear consulta en la tabla de consultas, ".concat(ex.getMessage()));
        }

    }

    /**
     * Método usado para la modificación de la fecha de ejecución de una
     * consulta ejecutada
     *
     * @param nombreQuery String que contiene el nombre de la query a modificar
     */
    @Override
    public void updateConsulta(String nombreQuery) {
        String sentenciaSql = "UPDATE CONSULTAS SET fechaEjecucion = ? WHERE nombreQuery = ?";

        try (Connection conexion = conectarBaseDatos();
                PreparedStatement query = conexion.prepareStatement(sentenciaSql)) {

            LOG.info("Updateo la consulta para cambiar la fecha de ejecución");

            query.setString(1, getFechaAhoraMismo());
            query.setString(2, nombreQuery);

            query.executeUpdate();

        } catch (SQLException ex) {
            LOG.warn("Error al Updatear consulta en la tabla de consultas, ".concat(ex.getMessage()));
        }
    }

    /**
     * Método usado para la recuperación de la fecha actual
     * 
     * @return fechaActual String que contiene una representación de la hora y fecha actuales
     */
    private String getFechaAhoraMismo() {
        String fechaActual = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

        return fechaActual;
    }

    /**
     * Método usado parala inserción de una consulta de prueba 
     */
    private void insertPrueba() {
        String sentenciaSql = "INSERT into CONSULTAS (nombreQuery,query,fechaCreacion,fechaEjecucion) values (?,?,?,?)";

        try (Connection conexion = conectarBaseDatos();
                PreparedStatement query = conexion.prepareStatement(sentenciaSql)) {

            query.setString(1, "Nombre De La Query");
            query.setString(2, "Select * from CONSULTAS");

            String fechaAhora = getFechaAhoraMismo();

            query.setString(3, fechaAhora);
            query.setString(4, fechaAhora);

            query.execute();

        } catch (SQLException ex) {
            LOG.warn("Error al insertar la tabla de consultas, ".concat(ex.getMessage()));
        }
    }

}
