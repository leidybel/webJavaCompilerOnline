package edu.eci.arsw.realtimeapp.controller;

import edu.eci.arsw.realtimeapp.model.Message;
import java.io.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping("/messages")
public class MessagesAPIController {
    
    
    @Autowired 
    private SimpMessagingTemplate template;  
    String a="";
    //String ruta="C:\\Users\\ASUS\\Documents\\Noveno\\ARSW\\ProyectoCompilerOnlineWeb_VersionCasiFinal\\Hello.java";
    String ruta = "javaTemp.java";
    
    @RequestMapping(method = RequestMethod.POST)        
    public ResponseEntity<?> addProduct(@RequestBody Message p) {  
        FileOutputStream fop = null;
        File file;
        int pos1 = -1, pos2 = -1;
        
        if (p.getDestiny().equals("compilar")){
            try {
                String mensaje = p.getBody();
                String clase;
                
                // Obtener nombre del archivo
                pos1 = mensaje.indexOf("class");
                if (pos1 != -1)
                    pos2 = mensaje.indexOf("{", mensaje.indexOf("class"));
                
                if (pos1 != -1 && pos2 != -1)
                    clase = mensaje.substring(pos1 + 6, pos2).trim() + ".java";
                else
                    clase = ruta;

                // Guardar archivo temporalmente en disco
                file = new File(clase);
                fop = new FileOutputStream(file);
                
                System.out.println("Ruta: " + file.getAbsolutePath());

                if (!file.exists()) {
                    file.createNewFile();
                }
                
                byte[] contentInBytes = mensaje.getBytes();

                fop.write(contentInBytes);
                fop.flush();
                fop.close();
                
                // Procesar JAVAC
                Process compilacion = Runtime.getRuntime().exec("javac " + clase);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(compilacion.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(compilacion.getErrorStream()));
                String s = null;
                //System.out.println("Standard output: ");
                s = stdInput.readLine();
                if (s == null && stdError.readLine() == null) {
                    a=a+"Compilacion Exitosa ";

                } else {
                    a=a+"\n" + "Errores en Compilacion: " + "\n";
                   
                    while ((s = stdError.readLine()) != null) {
                        a=a+"\n" + s + "\n";
    
                    }
                }
                a=a+" Termino compilacion...";
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (fop != null) {
                        fop.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            p.setDestiny(a);
        }
        template.convertAndSend("/topic/newmessage",p);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
        
    }
    
    @RequestMapping(value = "/check",method = RequestMethod.GET)        
    public String check() {        
        return "REST API OK"+a;                
    }
    
    @MessageMapping("/rutaMensajesEntrantes")
    public void webSocketMsgHandler(Message m) {
       
    
    }
    
}

