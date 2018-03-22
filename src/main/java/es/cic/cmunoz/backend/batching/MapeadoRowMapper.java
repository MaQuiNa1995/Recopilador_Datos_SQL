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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.cic.cmunoz.backend.domain.Mapeado;
import es.cic.cmunoz.backend.config.ApplicationContextProvider;
import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Clase usada para el procesado de cada resultado
 */
@Component("rowMapper")
@StepScope
public class MapeadoRowMapper implements RowMapper<Mapeado> {

    private String select;
    private ApplicationContext context;

    /**
     * Constructor genérico de la clase
     */
    public MapeadoRowMapper() {
        context = ApplicationContextProvider.getApplicationContext();
    }

    /**
     * Método usado para el procesado de cada resultado
     *
     * @param rs resultado de la consulta
     * @param rowNum numero de columna
     * @return Mapeado objeto que contiene los resultados de la consulta
     * @throws SQLException Excepción que se lanza si hubo algun error al hacer
     * la consulta
     */
    @Override
    public Mapeado mapRow(ResultSet rs, int rowNum) throws SQLException {

        String[] parametrosSpliteados = select.split(",");

        Map<String, String> mapaCampos = new HashMap<>();

        for (int i = 0; i < parametrosSpliteados.length; i++) {
            mapaCampos.put(
                    parametrosSpliteados[i],
                    rs.getString(parametrosSpliteados[i])
            );
        }

        Mapeado mapeadoObjeto = new Mapeado();

        mapeadoObjeto.setMapaCadenas(mapaCampos);

        return mapeadoObjeto;
    }

    /**
     * Método usado para el seteado de la variable select
     *
     * @param select Valor que setearemos a la variable select de la clase
     */
    @Value("#{jobParameters['select']}")
    public void setSelect(String select) {
        this.select = select;
    }

}
