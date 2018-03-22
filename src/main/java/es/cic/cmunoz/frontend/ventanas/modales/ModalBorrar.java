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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import es.cic.cmunoz.backend.repository.SqliteRepositoryImpl;

/**
 * Clase usada para la generación de un modal para borrar una consulta
 *
 * @author cmunoz
 */
public class ModalBorrar extends Window {

    private static final long serialVersionUID = 1L;
    private SqliteRepositoryImpl repository;

    private VerticalLayout panelTodo;

    private String consultaBorrar;

    /**
     * Constructor genérico de la clase
     *
     * @param consultaBorrar String del nombre de la consulta que hayamos
     * seleccionado para borrarla
     */
    public ModalBorrar(String consultaBorrar) {

        this.consultaBorrar = consultaBorrar;

        repository = new SqliteRepositoryImpl();
        crearPanelTodo();
        crearPanelArriba();
        crearPanelAbajo();
        setearContenido();

    }

    /**
     * Método usado para la generación y rellenado del panel que engloba toda la
     * ventana
     */
    private void crearPanelTodo() {
        panelTodo = new VerticalLayout();
        setModal(true);
        setClosable(false);
    }

    /**
     * Método encargado de la creación y rellenado del panel superior
     */
    private void crearPanelArriba() {
        VerticalLayout panelArriba = new VerticalLayout();
        Label labelConfirmar = new Label(
                "<h2>Desea Confirmar La Eliminación De: <u>"
                .concat(consultaBorrar)
                .concat("</u></h2>"),
                ContentMode.HTML
        );

        labelConfirmar.setWidth(100f, Unit.PERCENTAGE);
        panelArriba.addComponent(labelConfirmar);
        panelTodo.addComponent(panelArriba);
    }

    /**
     * Método encargado de la creación y rellenado del panel inferior
     */
    private void crearPanelAbajo() {
        HorizontalLayout panelDerecha = new HorizontalLayout();

        Button aceptar = new Button("Aceptar");
        aceptar.setIcon(FontAwesome.PLUS_SQUARE);
        aceptar.addClickListener((Button.ClickEvent e) -> {

            repository.borrarQuery(consultaBorrar);

            crearNotificacion("Consulta Borrada: ".concat(consultaBorrar));

            cerrar();
        });

        Button cerrar = new Button("Cancelar");
        cerrar.setIcon(FontAwesome.REMOVE);

        cerrar.addClickListener((Button.ClickEvent e) -> {
            cerrar();
        });

        panelDerecha.addComponents(aceptar, cerrar);
        panelTodo.addComponent(panelDerecha);
    }

    /**
     * Método encargado del cierre de la ventana actual
     */
    private void cerrar() {
        this.close();
    }

    /**
     * Método encargado del seletado del panel que engloba a toda la ventana
     */
    private void setearContenido() {
        setContent(panelTodo);
    }

    /**
     * Método encargado de la creación de una notificación paa mostrar en
     * pantalla
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

}
