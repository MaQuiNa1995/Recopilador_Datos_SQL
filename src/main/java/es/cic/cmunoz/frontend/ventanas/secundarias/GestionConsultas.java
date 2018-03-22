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

import com.opencsv.CSVReader;
import com.vaadin.data.Container;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import es.cic.cmunoz.Procesador;
import es.cic.cmunoz.backend.config.Configuracion;
import es.cic.cmunoz.backend.repository.SqliteRepository;
import es.cic.cmunoz.frontend.ventanas.modales.ModalBorrar;
import es.cic.cmunoz.frontend.ventanas.modales.ModalNuevo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.apache.log4j.Logger.getLogger;

/**
 * Esta clase gestiona la parte gráfica de la generación de las consultas
 *
 * @author cmunoz
 */
@Component
public class GestionConsultas extends VerticalLayout {

    private static final Logger LOG = getLogger(GestionConsultas.class.getName());

    private static final long serialVersionUID = -1233447283545037053L;

    private final SqliteRepository repository;

    private HashMap<String, String> listaMapasParams;

    private String nombreQuerySeleccionada = "";

    private HorizontalLayout panelSuperior;
    private HorizontalLayout panelMedio;
    private HorizontalLayout panelInferior;
    private VerticalLayout panelBotones;
    private ListSelect listaConsultas;
    private TextArea areaConsulta;
    private VerticalLayout panelLista;
    private TextArea areaQuerys;
    private StringBuilder contenido;
    private Button botonNuevo;
    private Button botonBorrar;
    private Button botonEjecutar;

    /**
     * Constructor genérico de la clase
     */
    public GestionConsultas() {
        ApplicationContext contexto = new AnnotationConfigApplicationContext(Configuracion.class);
        repository = (SqliteRepository) contexto.getBean("repository");
        
        crearBBDD();
        crearPanelSuperior();
        crearPanelMedio();
        crearPanelInferior();
    }

    /**
     * Método encargado de la generación de la base de datos
     */
    private void crearBBDD() {
        repository.crearCarpeta();
        repository.crearBaseDatos();
    }

    //------------------------ Panel Superior ---------------------------------
    /**
     * Método encargado de la creacion y rellenado del panel superior de esta
     * ventana
     */
    private void crearPanelSuperior() {
        panelSuperior = new HorizontalLayout();
        panelSuperior.setWidth(100f, Unit.PERCENTAGE);
        panelSuperior.setHeight(100f, Unit.PERCENTAGE);

        crearPanelLista();
        crearPanelBotones();
        this.addComponent(panelSuperior);

    }

    /**
     * Método encargado de la creación del panel de la lista donde estarán las
     * consultas guardadas
     */
    private void crearPanelLista() {
        panelLista = new VerticalLayout();
        panelLista.setWidth(100f, Unit.PERCENTAGE);

        listaConsultas = new ListSelect("Listado Consultas", obtenerNombresLista());
        listaConsultas.setWidth(100f, Unit.PERCENTAGE);

        listaConsultas.setNullSelectionAllowed(false);

        listaConsultas.addValueChangeListener(event -> {

            limpiarCampos();

            botonEjecutar.setEnabled(true);
            botonBorrar.setEnabled(true);

            nombreQuerySeleccionada = (String) (listaConsultas.getValue());
            LOG.info("He cogido: ".concat(nombreQuerySeleccionada));
            String consultaRecuperada = repository.getRawQuery(nombreQuerySeleccionada);

            areaConsulta.setValue(consultaRecuperada);
        });

        panelLista.addComponent(listaConsultas);
        panelSuperior.addComponent(panelLista);
    }

    /**
     * Método encargado de la creación y rellenado del panel de botones
     */
    private void crearPanelBotones() {
        panelBotones = new VerticalLayout();

        crearBotonNuevo();
        crearBotonBorrar();
        crearBotonEjecutar();
        panelSuperior.addComponent(panelBotones);
    }

    /**
     * Método encargado de la creacion de un boton 'Nuevo'
     */
    private void crearBotonNuevo() {

        botonNuevo = new Button("Nuevo");
        botonNuevo.setIcon(FontAwesome.PLUS_SQUARE_O);

        botonNuevo.addClickListener((Button.ClickEvent e) -> {
            LOG.info("Creando Modal 'Nuevo'");
            ModalNuevo modalNuevo = new ModalNuevo();
            this.getUI().getUI().addWindow(modalNuevo);
            modalNuevo.addCloseListener((Window.CloseEvent ev) -> refrescar());
        });

        panelBotones.addComponent(botonNuevo);

    }

    /**
     * Método encargado de la creacion de un boton 'Borrar'
     */
    private void crearBotonBorrar() {
        botonBorrar = new Button("Borrar");
        botonBorrar.setIcon(FontAwesome.TRASH);
        botonBorrar.setEnabled(false);

        botonBorrar.addClickListener((Button.ClickEvent e) -> {

            limpiarCampos();

            LOG.info("Creando Modal 'Borrar'");
            ModalBorrar modalBorrar = new ModalBorrar(nombreQuerySeleccionada);
            this.getUI().getUI().addWindow(modalBorrar);

            modalBorrar.addCloseListener((Window.CloseEvent ev) -> refrescar());

        });

        panelBotones.addComponent(botonBorrar);

    }

    /**
     * Método encargado de la creacion de un boton 'Ejecutar'
     */
    private void crearBotonEjecutar() {
        botonEjecutar = new Button("Ejecutar");
        botonEjecutar.setIcon(FontAwesome.PLAY_CIRCLE);
        botonEjecutar.setEnabled(false);

        botonEjecutar.addClickListener((Button.ClickEvent e) -> {

            limpiarCampos();

            eliminarAntiguoCSV();

            LOG.info("Ejecutando Query");

            repository.updateConsulta(nombreQuerySeleccionada);
            String consulta = repository.getRawQuery(nombreQuerySeleccionada);

            listaMapasParams = new HashMap<>();
            listaMapasParams.putAll(extraerSelect(consulta));

            serializarLista();

            String comandoCMD = "java -Djavax.xml.accessExternalSchema=all -jar Main2.jar es.cic.cmunoz.Main2.class";
            Procesador proc = new Procesador();
            proc.lanzarEnCMD(comandoCMD);

            crearNotificacion("Leyendo CSV e imprimiendo en el Area De Resultados");
            LOG.info("Query Ejecutada... Imprimiendo Resultado En Pantalla");

            imprimirPantallaCSV();

        });

        panelBotones.addComponent(botonEjecutar);

    }

    /**
     * Método encargado de la eliminación de un archivo
     */
    private void eliminarAntiguoCSV() {
        String rutaFichero = crearRutaFicheroCSV();

        File fichero = new File(rutaFichero);

        if (fichero.exists()) {
            fichero.delete();
        }
    }

    private String crearRutaFicheroCSV() {
        String fechaActual = new SimpleDateFormat("dd-MM-yyyy")
                .format(Calendar.getInstance()
                        .getTime());

        String fichero = fechaActual.concat("/Consulta.csv");

        return fichero;
    }

    /**
     * Método encargado de rellenar el TextArea con los datos de un CSV
     */
    private void imprimirPantallaCSV() {

        String fichero = crearRutaFicheroCSV();

        try (CSVReader reader = new CSVReader(new FileReader(fichero))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                annadirCadenas(Arrays.toString(nextLine));

            }
        } catch (IOException ex) {
            annadirCadenas("Hubo un error al leer el CSV porque la consulta acabó "
                    .concat("con errores considere echar un ojo al LOG"));

            LOG.error("Hubo un error al leer El Archivo CSV: ".concat(ex.getMessage()));
        }
    }

    /**
     * Método encargado de la serialización de un Hashmap en un fichero
     */
    private void serializarLista() {

        try (FileOutputStream fos = new FileOutputStream("hashmap.ser", false);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(listaMapasParams);
            LOG.info("Objeto Serializado Con Éxito");

        } catch (FileNotFoundException ex) {
            LOG.info("Archivo No Encontrado, Razón " + ex.getMessage());
        } catch (IOException ex) {
            LOG.info("No se pudo escribir en el fichero, Razón " + ex.getMessage());
        }
    }

    // ------------------------ Panel Medio ---------------------------------
    /**
     * Método encargado de la creacion y rellenado del panel del medio de esta
     * ventana
     */
    private void crearPanelMedio() {

        panelMedio = new HorizontalLayout();
        panelMedio.setWidth(100f, Unit.PERCENTAGE);

        crearTextAreaConsultas();
        this.addComponent(panelMedio);

    }

    /**
     * Método encargado de la creación del TextArea donde se verá el contenido
     * de la consulta que selecciones
     */
    private void crearTextAreaConsultas() {
        areaConsulta = new TextArea("Query");
        areaConsulta.setWidth(100f, Unit.PERCENTAGE);
        areaConsulta.setHeight(100f, Unit.PERCENTAGE);
        areaConsulta.setRows(10);
        panelMedio.addComponent(areaConsulta);
    }

    // ------------------------ Panel Inferior ---------------------------------
    /**
     * Método encargado de la creacion y rellenado del panel inferior de esta
     * ventana
     */
    private void crearPanelInferior() {
        panelInferior = new HorizontalLayout();
        panelInferior.setWidth(100f, Unit.PERCENTAGE);

        crearTextAreaQuery();
        this.addComponent(panelInferior);
    }

    /**
     * Método encargado de la creación del TextArea donde se verá el contenido
     * del CSV generado
     */
    private void crearTextAreaQuery() {
        areaQuerys = new TextArea("Formato CSV");

        areaQuerys.setWidth(100f, Unit.PERCENTAGE);
        areaQuerys.setRows(10);
        areaQuerys.setWordwrap(true);
        panelInferior.addComponent(areaQuerys);
    }

    /**
     * Método encargado de la recuperación de base de datos de los nombres de
     * las consultas que hayas guardado
     */
    private List<String> obtenerNombresLista() {
        return repository.getNombresConsultas();
    }

    /**
     * Método encargado de la generación de una notificación en ventana
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
     * Método encargado de la extracción de la parte de la select de una
     * consulta dada
     *
     * @param consulta String que contiene la consulta completa
     * @return un HashMap del mapeado de la parte de las select de la consulta
     */
    private HashMap<String, String> extraerSelect(String consulta) {

        HashMap<String, String> mapaConsulta = new HashMap<>();

        StringTokenizer st = new StringTokenizer(consulta);

        int contador = 0;
        while (st.hasMoreElements()) {
            String tokenSacado = st.nextElement().toString();
            if (contador == 1) {
                mapaConsulta.put("sql.consulta", consulta);
                mapaConsulta.put("sql.select", tokenSacado);
                break;
            }
            contador++;
        }

        return mapaConsulta;
    }

    /**
     * Método encargado del refresco de la lista que contiene los nombres de las
     * consultas guardadas
     */
    private void refrescar() {
        LOG.info("Voy a refrescar");
        Container container = listaConsultas.getContainerDataSource();
        container.removeAllItems();
        panelLista.removeAllComponents();

        List<String> queries = obtenerNombresLista();
        for (String query : queries) {
            container.addItem(query);
        }

        // Duplicado
        listaConsultas = new ListSelect("Listado Consultas", container);
        listaConsultas.setWidth(100f, Unit.PERCENTAGE);
        listaConsultas.setNullSelectionAllowed(false);

        listaConsultas.addValueChangeListener(event -> {

            nombreQuerySeleccionada = (String) (listaConsultas.getValue());
            String consultaRecuperada = repository.getRawQuery(nombreQuerySeleccionada);

            areaConsulta.setValue(consultaRecuperada);
        });

        panelLista.addComponent(listaConsultas);
    }

    /**
     * Método encargado del añadido de Strings al TextArea
     *
     * @param cadena
     */
    private void annadirCadenas(String cadena) {

        contenido = new StringBuilder(areaQuerys.getValue());

        contenido.append(cadena)
                .append("\n");

        areaQuerys.setValue(contenido.toString());
    }

    /**
     * Método encargado del limpiado de los campos de la consulta y el area
     * donde se imprime el CSV
     */
    private void limpiarCampos() {
        areaConsulta.setValue("");
        areaQuerys.setValue("");
    }

}
