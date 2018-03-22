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
package es.cic.cmunoz.frontend.ventanas.secundarias;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * Clase Encargada De La Configuración de la base de datos a usar
 */
@Component
public class ConfiguracionBBDD extends VerticalLayout {

    private static final String FICHERO_RUTA = "configuracionBBDD.properties";

    private static final Logger LOG = Logger.getLogger(ConfiguracionBBDD.class.getName());
    private static final long serialVersionUID = -2399979473483212731L;

    private HorizontalLayout panelBotones;

    private TextField urlText;
    private TextField driverText;
    private TextField userText;
    private TextField passText;

    /**
     * Constructorgenérico de la clase
     */
    public ConfiguracionBBDD() {
        configurarVentana();
        crearPanelCampos();
        crearPanelBotones();
    }

    /**
     * Método usado para la configuración de la ventana en el que ponemos el
     * tamaño a Full en ambas dimensiones
     */
    private void configurarVentana() {
        this.setSizeFull();
    }

    /**
     * Método usado para la generación y rellenado del panel que contiene los
     * campos configurables de la base de datos a usar
     */
    private void crearPanelCampos() {
        VerticalLayout panelCampos = new VerticalLayout();

        driverText = new TextField("Driver Base De Datos");
        driverText.setWidth(100, Unit.PERCENTAGE);

        urlText = new TextField("Url Base De Datos");
        urlText.setWidth(100, Unit.PERCENTAGE);

        userText = new TextField("Nombre De Usuario De Base De Datos");
        userText.setWidth(100, Unit.PERCENTAGE);

        passText = new TextField("Contraseña De La Base De Datos");
        passText.setWidth(100, Unit.PERCENTAGE);

        panelCampos.addComponents(driverText, urlText, userText, passText);
        this.addComponent(panelCampos);
    }

    /**
     * Método encargado de la generación y rellenado del panel de botones
     */
    private void crearPanelBotones() {
        panelBotones = new HorizontalLayout();
        crearBotonLimpiar();
        crearBotonGuardar();
        crearBotonChristian();
        this.addComponent(panelBotones);

        leerFichero();
    }

    /**
     * Método encargado para el seteo de la configuración de la base de datos de
     * LeagueOfLegends de Christian No funcionará si no tienes la ruta siguiente
     * la base de datos
     *
     * @see Ruta Base Datos:
     * 'C:/Users/cmunoz/InfoLol/BaseDatos/LeagueOfLegends.sqlite'
     */
    private void crearBotonChristian() {
        NativeButton botonChristian = new NativeButton("Config Christian");
        botonChristian.setIcon(FontAwesome.TROPHY);

        botonChristian.addClickListener((Button.ClickEvent e) -> {
            urlText.setValue("jdbc:sqlite:C:/Users/cmunoz/InfoLol/BaseDatos/LeagueOfLegends.sqlite");
            driverText.setValue("org.sqlite.JDBC");
            userText.setValue("Ninguna");
            passText.setValue("Ninguna");
        });

        panelBotones.addComponent(botonChristian);
    }

    /**
     * Método usado para la creación del boton Limpiar
     */
    private void crearBotonLimpiar() {

        NativeButton botonLimpiar = new NativeButton("Limpiar");
        botonLimpiar.setIcon(FontAwesome.TRASH_O);

        botonLimpiar.addClickListener((Button.ClickEvent e) -> {
            limpiar();
            crearNotificacion(
                    "",
                    "Campos Limpiados",
                    Notification.Type.TRAY_NOTIFICATION
            );
        });

        panelBotones.addComponent(botonLimpiar);
    }

    /**
     * Método usado para la creación del boton Guardar
     */
    private void crearBotonGuardar() {
        NativeButton botonGuardar = new NativeButton("Guardar Configuración");
        botonGuardar.setIcon(FontAwesome.SAVE);

        botonGuardar.addClickListener((Button.ClickEvent e) -> {

            guardarConfiguracion();
//            limpiar();
            crearNotificacion("",
                    "Configuración Guardada",
                    Notification.Type.TRAY_NOTIFICATION
            );

        });
        panelBotones.addComponent(botonGuardar);
    }

    /**
     * Método usado para la creación de notificaciones
     *
     * @param titulo título de la notificacion
     * @param mensaje mensaje de la notificación
     * @param estilo estilo de la notificación
     */
    private void crearNotificacion(String titulo,
            String mensaje, Notification.Type estilo) {

        Notification.show(titulo,
                mensaje,
                estilo);
    }

    /**
     * Método usado para la limpieza de todos los campos editables
     */
    private void limpiar() {
        urlText.setValue("");
        driverText.setValue("");
        userText.setValue("");
        passText.setValue("");
    }

    /**
     * Método usado para la lectura del fichero de configuración
     */
    private void leerFichero() {

        final int DRIVER = 1;
        final int URL = 2;
        final int USER = 3;
        final int PASS = 4;

        try (BufferedReader br
                = new BufferedReader(
                        new FileReader(FICHERO_RUTA))) {
            String linea;

            int contador = 1;
            while ((linea = br.readLine()) != null) {
                switch (contador) {
                    case DRIVER:
                        driverText.setValue(linea.substring(19,
                                linea.length())
                        );
                        break;
                    case URL:
                        urlText.setValue(linea.substring(16,
                                linea.length())
                        );
                        break;
                    case USER:
                        userText.setValue(linea.substring(17,
                                linea.length())
                        );
                        break;
                    case PASS:
                        passText.setValue(linea.substring(17,
                                linea.length())
                        );
                        break;
                    default:
                        break;
                }
                contador++;
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Liada parda raz\u00f3n:{0}", ex.getMessage());
        }
    }

    /**
     * Método usado para guardar la información de la base de datos a usar enla
     * aplicación
     */
    private void guardarConfiguracion() {

        String[] configuracionArray = {
            "basedatos.driver = ".concat(driverText.getValue()),
            "basedatos.url = ".concat(urlText.getValue()),
            "basedatos.user = ".concat(userText.getValue()),
            "basedatos.pass = ".concat(passText.getValue())
        };

        File fichero = new File(FICHERO_RUTA);

        if (fichero.exists()) {
            fichero.delete();
        }

        try (BufferedWriter br = new BufferedWriter(
                new FileWriter(FICHERO_RUTA, true))) {

            for (String cadenaSacada : configuracionArray) {
                br.write(cadenaSacada.concat("\n"));
            }

        } catch (IOException ex) {
            Logger.getLogger(ConfiguracionBBDD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
