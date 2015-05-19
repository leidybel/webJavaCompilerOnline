package edu.eci.arsw.realtimeapp.controller;

import edu.eci.arsw.realtimeapp.model.Message;
import edu.eci.arsw.realtimeapp.model.ProcesadorJava;
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
    
    //String ruta="C:\\Users\\ASUS\\Documents\\Noveno\\ARSW\\ProyectoCompilerOnlineWeb_VersionCasiFinal\\Hello.java";
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addProduct(@RequestBody Message p) {
        String resultado = "";
        
        if (p.getDestiny().equals("compilar")){
            try {
                String mensaje = p.getBody();
                
                resultado = ProcesadorJava.compilar(mensaje, true);
                if (resultado == null)
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (Exception ex) {
                ex.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            p.setDestiny(resultado);
        }
        
        template.convertAndSend("/topic/newmessage",p);
        return new ResponseEntity<String>(HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(value = "/ejecutar", method = RequestMethod.POST)
    public ResponseEntity<?> ejecutarCodigo(@RequestBody Message p) {
        String resultado = "";
        
        if (p.getDestiny().equals("ejecutar")){
            try {
                String mensaje = p.getBody();
                
                resultado = ProcesadorJava.ejecutar(mensaje);
                if (resultado == null)
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (Exception ex) {
                ex.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            p.setDestiny(resultado);
        }
        
        template.convertAndSend("/topic/newmessage",p);
        return new ResponseEntity<String>(HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(value = "/check",method = RequestMethod.GET)
    public String check() {
        return "REST API OK";
    }
    
    @MessageMapping("/rutaMensajesEntrantes")
    public void webSocketMsgHandler(Message m) {
        
    }
    
}

