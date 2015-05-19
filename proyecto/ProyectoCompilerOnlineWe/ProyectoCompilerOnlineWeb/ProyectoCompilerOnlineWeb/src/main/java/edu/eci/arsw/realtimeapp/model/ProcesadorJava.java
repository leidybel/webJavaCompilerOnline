package edu.eci.arsw.realtimeapp.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ProcesadorJava {
    
    private final static String RUTA_GENERICA = "javaTemp.java";
    
    public static String compilar(String codigo, boolean eliminarArchivos) {
        FileOutputStream fop = null;
        File archivo;
        String rutaArchivo = null;
        String resultado = "";
        
        try {
            // Obtener nombre del archivo
            rutaArchivo = obtenerNombreClase(codigo);
            
            // Guardar archivo temporalmente en disco
            archivo = new File(rutaArchivo);
            fop = new FileOutputStream(archivo);

            System.out.println("Ruta: " + archivo.getAbsolutePath());
            
            fop.write(codigo.getBytes());
            fop.flush();
            fop.close();
            
            // Procesar JAVAC
            Process compilacion = Runtime.getRuntime().exec("javac " + rutaArchivo);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(compilacion.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(compilacion.getErrorStream()));
            
            String s = stdInput.readLine();
            if (s == null && stdError.readLine() == null) {
                resultado = "Compilacion Exitosa";
            } else {
                resultado = "Errores en Compilacion: " + "\n";
                
                while ((s = stdError.readLine()) != null)
                    resultado += "\n" + s + "\n";
            }
            
            resultado += "\n" + "Termino compilacion.....";
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (fop != null)
                    fop.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            
            // Eliminar archivos temporales
            if (eliminarArchivos) {
                if (rutaArchivo != null) {
                    archivo = new File(rutaArchivo);
                    if (archivo.exists())
                        archivo.delete();

                    archivo = new File(rutaArchivo.replace(".java", ".class"));
                    if (archivo.exists())
                        archivo.delete();
                }
            }
        }
        
        return resultado;
    }
    
    public static String ejecutar(String codigo) {
        File archivo;
        String rutaArchivo = null;
        String resultadoCompilacion;
        String resultado = "";
        String linea;
        
        try {
            rutaArchivo = obtenerNombreClase(codigo).replace(".java", "");
            
            resultadoCompilacion = compilar(codigo, false);
            if (resultadoCompilacion != null) {
                archivo = new File(rutaArchivo + ".class");
                if (archivo.exists()) {
                    // Procesar JAVA
                    Process ejecucion = Runtime.getRuntime().exec("java -classpath . " + rutaArchivo);
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(ejecucion.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(ejecucion.getErrorStream()));

                    // Resultado
                    StringBuffer output = new StringBuffer();
                    linea = "";
                    while ((linea = stdInput.readLine()) != null)
                        output.append(linea + "\n");
                    resultado = output.toString();
                    
                    // Errores
                    StringBuffer errors = new StringBuffer();
                    linea = "";
                    while ((linea = stdError.readLine()) != null)
                        errors.append(linea + "\n");
                    if (!errors.toString().equals(""))
                        resultado += "\n" + "Errores:" + "\n" + errors.toString();
                } else {
                    return resultadoCompilacion;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            // Eliminar archivos temporales
            if (rutaArchivo != null) {
                archivo = new File(rutaArchivo + ".java");
                if (archivo.exists())
                    archivo.delete();
                
                archivo = new File(rutaArchivo + ".class");
                if (archivo.exists())
                    archivo.delete();
            }
        }
        
        return resultado;
    }
    
    private static String obtenerNombreClase(String codigo) {
        int pos1 = -1, pos2 = -1;
        String rutaArchivo = RUTA_GENERICA;
        
        pos1 = codigo.indexOf("class");
        if (pos1 != -1)
            pos2 = codigo.indexOf("{", codigo.indexOf("class"));

        if (pos1 != -1 && pos2 != -1)
            rutaArchivo = codigo.substring(pos1 + 6, pos2).trim() + ".java";
        
        return rutaArchivo;
    }
    
}
