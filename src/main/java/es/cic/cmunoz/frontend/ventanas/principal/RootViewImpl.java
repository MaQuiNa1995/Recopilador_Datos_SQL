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
package es.cic.cmunoz.frontend.ventanas.principal;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import es.cic.cmunoz.frontend.ventanas.secundarias.ConfiguracionBBDD;
import es.cic.cmunoz.frontend.ventanas.secundarias.GestionConsultas;
import es.cic.cmunoz.frontend.interfaces.View;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

/**
 * Clase usada para la generación de la ventana principal
 */
@Scope("prototype")
@Named
public class RootViewImpl implements RootView {

    private VerticalLayout capaPrincipal;

    /**
     * Constructor genérico de la clase
     */
    @Override
    public void iniciarVentana() {
        crearPanelPrincipal();
        crearTitulo();
        crearMenuPestanas();
    }
    
    /**
     * Método usado para la creación del panel principal
     */
    private void crearPanelPrincipal() {
        this.capaPrincipal = new VerticalLayout();
    }

    /**
     * Método usado para la creación del título
     */
    private void crearTitulo() {
        Label titulo = new Label("INCIDENCIAS ANALYZER");
        titulo.setHeight(10f, Unit.PERCENTAGE);
        capaPrincipal.addComponent(titulo);
    }

    /**
     * Método usado para la creación del menú de las pestañas
     */
    private void crearMenuPestanas() {

        TabSheet menuPestanas;
        GestionConsultas panelGestion;
        ConfiguracionBBDD panelConfiguracion;
        
        panelGestion = new GestionConsultas();
        panelConfiguracion = new ConfiguracionBBDD();

        menuPestanas = new TabSheet();
        menuPestanas.setWidth(100.0f, Unit.PERCENTAGE);
        menuPestanas.addTab(panelGestion, "Gestión Consultas");
        menuPestanas.addTab(panelConfiguracion,"Configuración Base De Datos");

        capaPrincipal.addComponent(menuPestanas);

    }

    @Override
    public <C> C getComponent(final Class<C> type) {
        return type.cast(this.capaPrincipal);
    }

    /**
     * Método usado para la creación del layout principal
     * @param content contenido de la ventana
     */
    @Override
    public void setContent(final View content) {
        final Component component = content.getComponent(Component.class);

        this.capaPrincipal.removeAllComponents();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth(100.0f, Unit.PERCENTAGE);
        layout.setHeight(100.0f, Unit.PERCENTAGE);

        this.capaPrincipal.addComponent(component);
    }

}
