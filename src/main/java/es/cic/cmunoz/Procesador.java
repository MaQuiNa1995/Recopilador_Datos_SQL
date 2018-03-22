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
package es.cic.cmunoz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import static org.apache.log4j.Logger.getLogger;

/**
 * Clase usada para la ejecución de comandos en consola a traves de java
 * 
 * @autor cmunoz
 * @version 1.2
 */
public class Procesador {

    private static final Logger LOG = getLogger(Procesador.class.getName());

    /**
     * Método usado para iniciar la CMD
     */
    public void lanzarCmd() {
        LOG.info("Abriendo Consola...");

        try {
            String command = "cmd /C start cmd.exe";
            Process child = Runtime.getRuntime().exec(command);

            try (OutputStream out = child.getOutputStream()) {
                out.write("cd C:/ /r/n".getBytes());
                out.flush();
                out.write("dir /r/n".getBytes());
            }
        } catch (IOException e) {
            LOG.warn("Algo Sali\u00f3 Mal: " + e.getMessage());
        }
    }

    /**
     * Método usado apra lanzar comandos
     *
     * @param comandoEjecutar
     */
    public void lanzarComando(String comandoEjecutar) {

        LOG.warn("Lanzando Comando: ".concat(comandoEjecutar));

        String comandoEjecutarConsola = "cmd";
        Process proceso = null;
        try {
            proceso = Runtime.getRuntime().exec(comandoEjecutarConsola);
        } catch (IOException ex) {
            LOG.warn("Algo Sali\u00f3 Mal: ".concat(ex.getMessage()));
        }

        new Thread(new SyncPipe(proceso.getErrorStream(), System.err)).start();
        new Thread(new SyncPipe(proceso.getInputStream(), System.out)).start();

        try (PrintWriter printer = new PrintWriter(proceso.getOutputStream())) {
            printer.println(comandoEjecutar);
        }

        int codigoErrorReturn;

        try {
            codigoErrorReturn = proceso.waitFor();
        } catch (InterruptedException ex) {
            LOG.warn("Algo Sali\u00f3 Mal: ".concat(ex.getMessage()));
            codigoErrorReturn = -1;
        }

        LOG.warn("Codigo De Retorno = ".concat(String.valueOf(codigoErrorReturn)));
    }

    public void lanzarComandoDos(String comandoEjecutar) {

        LOG.warn("Lanzando Comando: {0} Sin Código De Error" + comandoEjecutar);

        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", comandoEjecutar);

        builder.redirectErrorStream(true);

        Process p = null;

        try {
            p = builder.start();
        } catch (IOException ex) {
            LOG.warn("Algo Salió Mal: ".concat(ex.getMessage()));
        }

        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String lineaLeida;
            while (true) {
                lineaLeida = r.readLine();
                if (lineaLeida == null) {
                    break;
                }
                System.out.println(lineaLeida);
            }
        } catch (IOException ex) {
            LOG.warn("Algo Salió Mal: ".concat(ex.getMessage()));
        }
    }

    public void lanzarEnCMD(String comandoEjecutar) {

        try {

            Process proceso = Runtime.getRuntime().exec("cmd.exe /C start ".concat(comandoEjecutar));
            BufferedWriter escribirConsola = new BufferedWriter(new OutputStreamWriter(proceso.getOutputStream()));

            escribirConsola.write("dir");
            escribirConsola.flush();

            BufferedReader salidaTrazas = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proceso.getErrorStream()));

            pintarResultados(salidaTrazas);
            pintarErrores(stdError);

        } catch (IOException e) {
            LOG.warn("Algo Salió Mal: ".concat(e.getMessage()));
        }

    }

    /**
     * Método usado para imprimir en pantalla los resultados del proceso
     *
     * @param salidaTrazas objeto usado para leer
     * @throws IOException
     */
    private void pintarResultados(BufferedReader salidaTrazas) throws IOException {
        String lineaLeida;

        LOG.info("Imprimiendo Resultados En Pantalla...");

        while ((lineaLeida = salidaTrazas.readLine()) != null) {
            LOG.info(lineaLeida);
        }
    }

    /**
     * Método usado para imprimir en pantalla los errores del proceso
     *
     * @param salidaTrazas objeto usado para leer los errores del proceso
     * @throws IOException
     */
    private void pintarErrores(BufferedReader stdError) throws IOException {
        String lineaLeida;

        LOG.info("Imprimiendo Errores En Pantalla...");

        while ((lineaLeida = stdError.readLine()) != null) {
            LOG.error(lineaLeida);
        }
    }

    /**
     * Clase usada para capturar la salida de los streams de los resultados
     */
    private class SyncPipe implements Runnable {

        private final OutputStream output;
        private final InputStream input;

        public SyncPipe(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public void run() {
            try {
                final byte[] buffer = new byte[1024];
                for (int length = 0; (length = input.read(buffer)) != -1;) {
                    output.write(buffer, 0, length);
                }
            } catch (IOException ex) {
                LOG.warn("Algo Sali\u00f3 Mal: ".concat(ex.getMessage()));
            }
        }

    }
}
