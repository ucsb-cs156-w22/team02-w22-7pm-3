package edu.ucsb.cs156.team02.controllers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

@Profile("!development")
@Controller
public class FrontendController {
  @GetMapping("/**/{path:[^\\.]*}")
  public String index() {
    return "forward:/index.html";
  }
  
  @GetMapping("/csrf")
  public ResponseEntity<String> csrf() {
      return ResponseEntity.notFound().build();
  }


}
