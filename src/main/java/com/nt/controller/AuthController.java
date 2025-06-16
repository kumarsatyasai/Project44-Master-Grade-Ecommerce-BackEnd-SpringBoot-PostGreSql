package com.nt.controller;

import com.nt.jwt.JwtUtils;
import com.nt.model.AppRole;
import com.nt.model.Role;
import com.nt.model.User;
import com.nt.payload.LoginRequest;
import com.nt.payload.MessageResponse;
import com.nt.payload.SignUpRequest;
import com.nt.payload.UserInfoResponse;
import com.nt.repository.IRoleRepository;
import com.nt.repository.IUserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.nt.model.AppRole.ROLE_USER;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
            );
         }catch (AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            map.put("error", "Bad Credentials");
            map.put("status", false);
             return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
         }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        UserInfoResponse userInfoResponse = new UserInfoResponse();

        userInfoResponse.setUserName(userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).toList();
        userInfoResponse.setRoles(roles);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(userInfoResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (userRepository.existsUserByUserName(signUpRequest.getUserName())) {
            MessageResponse msgResponse = new MessageResponse("User Already Exists with username: " + signUpRequest.getUserName());
            return new ResponseEntity<>(msgResponse, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsUserByEmail(signUpRequest.getEmail())) {
            MessageResponse msgResponse = new MessageResponse("User Already Exists with email: " + signUpRequest.getEmail());
            return new ResponseEntity<>(msgResponse, HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUserName(signUpRequest.getUserName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = (Role) roleRepository.findByRoleName(ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
            roles.add(userRole);


        } else {
            for (String strRole : strRoles) {
                if ("admin".equals(strRole)) {
                    // If admin role is requested, add all roles
                    roles.add((Role) roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found.")));
                    roles.add((Role) roleRepository.findByRoleName(AppRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role USER is not found.")));
                    roles.add((Role) roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                            .orElseThrow(() -> new RuntimeException("Error: Role SELLER is not found.")));
                    break;
                } else {
                    switch (strRole) {
                        case "user":
                            Role userRole = (Role) roleRepository.findByRoleName(AppRole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
                            roles.add(userRole);
                            break;
                        case "seller":
                            Role sellerRole = (Role) roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role SELLER is not found."));
                            roles.add(sellerRole);
                            break;
                        default:
                            Role defaultRole = (Role) roleRepository.findByRoleName(ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
                            roles.add(defaultRole);
                    }
                }
            }
        }
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(new MessageResponse("User Created Successfully."), HttpStatus.CREATED);
    }

    @GetMapping("/username")
    public String getUserName(Authentication authentication){
        if(authentication != null){
            return authentication.getName();
        }
        return "Not Logged In";

    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        if(authentication == null){
            return ResponseEntity.ok().body("Not Logged In");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setUserName(userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).toList();
        userInfoResponse.setRoles(roles);
        return ResponseEntity.ok().body(userInfoResponse);
    }

    @GetMapping("/signout")
    public ResponseEntity<?> logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("No user is currently logged in"));
        }

        ResponseCookie cookie = jwtUtils.getCleanedJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Logged Out Successfully."));
    }
}
