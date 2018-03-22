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
package es.cic.cmunoz.frontend.ventanas.modales;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import es.cic.cmunoz.backend.config.Configuracion;
import es.cic.cmunoz.backend.exceptions.SQLInyectionException;
import es.cic.cmunoz.backend.repository.SqliteRepository;
import es.cic.cmunoz.backend.repository.SqliteRepositoryImpl;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import static org.apache.log4j.Logger.getLogger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Clase usada para la generación de un modal para guardar una nueva consulta
 *
 * @author cmunoz
 */
public class ModalNuevo extends Window {

    private TextField nombreQuery;
    private TextArea rawQuery;

    private static final Logger LOG = getLogger(ModalNuevo.class.getName());

    private static final String ERROR_VACIO = "Se Deben De Rellenar Todos Los Campos";
    private static final long serialVersionUID = 1L;
    
    private SqliteRepository repository;

    private VerticalLayout panelTodo;

    /**
     * Constructor genérico de la clase
     */
    public ModalNuevo() {

        crearPanelTodo();
        crearPanelArriba();
        crearPanelAbajo();
        setearContenido();

    }

    /**
     * Método usado para la creación del panel que engloba toda la ventana
     */
    private void crearPanelTodo() {
        
        ApplicationContext contexto = new AnnotationConfigApplicationContext(Configuracion.class);
        repository = (SqliteRepository) contexto.getBean("repository");
        
        panelTodo = new VerticalLayout();
        setModal(true);
        setClosable(false);
        setWidth(80, Unit.PERCENTAGE);
    }

    /**
     * Método usado para la generación y rellenado del panel superior
     */
    private void crearPanelArriba() {

        VerticalLayout panelIzquierda = new VerticalLayout();

        nombreQuery = new TextField("Introduce el Nombre De La Query");
        nombreQuery.setInputPrompt("Nombre De La Query");
        nombreQuery.setWidth(100f, Unit.PERCENTAGE);
        nombreQuery.setRequired(true);
        nombreQuery.setRequiredError(ERROR_VACIO);

        rawQuery = new TextArea("Introduce La Query");
        rawQuery.setRows(5);
        rawQuery.setWordwrap(true);
        rawQuery.setRequired(true);
        rawQuery.setRequiredError(ERROR_VACIO);
        rawQuery.setInputPrompt("Consulta Sql");
        rawQuery.setWidth(100f, Unit.PERCENTAGE);

        panelIzquierda.addComponents(nombreQuery, rawQuery);
        panelTodo.addComponent(panelIzquierda);
    }

    /**
     * Método usado para la generación y rellenado del panel inferior
     */
    private void crearPanelAbajo() {
        HorizontalLayout panelDerecha = new HorizontalLayout();

        Button aceptar = new Button("Aceptar");
        aceptar.setIcon(FontAwesome.PLUS_SQUARE);

        aceptar.addClickListener((Button.ClickEvent e) -> {
            
            LOG.info("nombreQuery: " + nombreQuery.getValue());
            LOG.info("rawQuery: " + rawQuery.getValue());
            
            if (("".equals(nombreQuery.getValue()))||
                    ("".equals(rawQuery.getValue()))) {
                
                crearNotificacion(ERROR_VACIO);
                
            } else {

                try {
                    comprobarQuery();

                    repository.insertarQuery(nombreQuery.getValue(), rawQuery.getValue());

                    limpiarCampos();

                    crearNotificacion("Consulta Añadida");

                    cerrar();
                } catch (SQLInyectionException ex) {
                    LOG.warn("No se permite el uso de operaciones diferentes a SELECT");
                    crearNotificacion("No se permite el uso de operaciones que no sean SELECT");
                } catch (SQLException ex) {
                    LOG.warn("Error al insertar el registro en la tabla de consultas, ".concat(ex.getMessage()));
                    crearNotificacion("Error al crear el nuevo registro: ".concat(ex.getMessage()));
                }

            }
        });

        Button cerrar = new Button("Cancelar");
        cerrar.setIcon(FontAwesome.TRASH);
        
        cerrar.addClickListener((Button.ClickEvent e) -> {
            cerrar();
        });

        panelDerecha.addComponents(aceptar, cerrar);
        panelTodo.addComponent(panelDerecha);
    }

    /**
     * Método usado para el seteo del panel que engloba todo a la ventana
     */
    private void setearContenido() {
        setContent(panelTodo);
    }

    /**
     * Método usado para la generación de ua notificación
     *
     * @param mensaje String a mostrar
     */
    private void crearNotificacion(String mensaje) {
        
        Notification notificacion = new Notification(
                null,
                mensaje,
                Notification.Type.HUMANIZED_MESSAGE);

        notificacion.setDelayMsec(3000);
        notificacion.setPosition(Position.MIDDLE_CENTER);
        notificacion.show(Page.getCurrent());
    }

    /**
     * Método usado para el limpiado de los campos editables de la ventana
     */
    private void limpiarCampos() {
        nombreQuery.setValue(null);
        rawQuery.setValue(null);
    }

    /**
     * Método para cerrar la ventana actual
     */
    private void cerrar() {
        this.close();
    }

    /**
     * Método usado para la prevención de SQLInyection
     *
     * @throws SQLInyectionException se lanzará si ve que la consulta que
     * queramos meter no es solo un select
     */
    private void comprobarQuery() throws SQLInyectionException {
        
        String[] palabrasProhibidas = {
            ";", "drop", "delete", "update"
        };

        for (String cadenaSacada : palabrasProhibidas) {
            if (rawQuery.getValue().contains(cadenaSacada)) {
                throw new SQLInyectionException("No se permiten operaciones que no sean Select's");
            }
        }

    }

}
