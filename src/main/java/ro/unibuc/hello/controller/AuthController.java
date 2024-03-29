package ro.unibuc.hello.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ro.unibuc.hello.data.entity.UserEntity;
import ro.unibuc.hello.data.repository.UserRepository;
import ro.unibuc.hello.dto.AuthResponseDto;
import ro.unibuc.hello.dto.LoginDto;
import ro.unibuc.hello.dto.RegisterDto;
import ro.unibuc.hello.security.JwtGenerator;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if(userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }
        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        List<String> roles = new ArrayList<String>();
        roles.add("USER");
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>("User register success", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        System.out.println("Se ajunge in endpoint /login");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(), loginDto.getPassword())
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        Cookie cookie = new Cookie("accessToken", token);
        System.out.println("Generated token: " + token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

}
