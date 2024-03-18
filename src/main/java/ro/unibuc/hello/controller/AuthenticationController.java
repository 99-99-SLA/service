package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ro.unibuc.hello.data.entity.AuthenticationResponse;
import ro.unibuc.hello.data.entity.User;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.service.AuthenticationService;
import ro.unibuc.hello.service.HelloWorldService;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;
    @Autowired
    private HelloWorldService helloWorldService;

    @GetMapping("/hello-man")
    @ResponseBody
    public Greeting sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
        return helloWorldService.hello("boss");
    }
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<AuthenticationResponse> register() {
        return new ResponseEntity<>(null, HttpStatus.OK);
        //return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity <AuthenticationResponse> login(@RequestBody User request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
