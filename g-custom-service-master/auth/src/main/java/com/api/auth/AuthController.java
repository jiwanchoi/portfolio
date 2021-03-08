package com.api.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.api.auth.model.Clientnfo;
import com.api.auth.model.ClienttRepository;

@RestController
public class AuthController {	
	
    @GetMapping("/api/session")
    public Authentication session() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    @GetMapping("/hello")
	public String home() {
		return "Hello world";
	}
	
	@Autowired
    private ClienttRepository clienttRepository;

    @GetMapping("/clients")
    public List<Clientnfo> getAlluers() {
        return clienttRepository.findAll();
    }
    
    @GetMapping("/approve/{apino}")
    public ResponseEntity<Clientnfo> approve(@PathVariable(value = "apino") String apino)
        throws Exception {    	
    	
    	Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	String clientid = a.getPrincipal().toString();
    	
    	Optional<Clientnfo> info = clienttRepository.findAllByClientidAndApino(clientid,apino);
    	
    	Clientnfo cf = new Clientnfo();		
    	if(info.equals(Optional.empty())) {
    		System.out.println("info : "+info.toString());    		
    		cf.setClientid(clientid);
    		cf.setApino(apino);
    		cf.setUseyn("N");    		
    	}else {
    		cf = info.get();
    	}
    	
    	
        return ResponseEntity.ok().body(cf);
    }    
    
    
    @GetMapping("/approveTest")
	public String approve_() {    	
    	Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	String clientid = a.getPrincipal().toString();
    	
    	
		return clientid;
	}
}
