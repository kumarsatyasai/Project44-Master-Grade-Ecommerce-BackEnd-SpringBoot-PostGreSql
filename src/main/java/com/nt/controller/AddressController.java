package com.nt.controller;

import com.nt.model.User;
import com.nt.payload.AddressDTO;
import com.nt.repository.IUserRepository;
import com.nt.service.IAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAddressService addressService;


    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO, org.springframework.http.HttpStatus.CREATED);

    }
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> createAddress(){

        List<AddressDTO> savedAddressDTOs = addressService.getAllAddresses();
        return new ResponseEntity<>(savedAddressDTOs, org.springframework.http.HttpStatus.OK);

    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getSpecificAddress(@PathVariable Long addressId){

        AddressDTO savedAddressDTO = addressService.getSpecificAddress(addressId);
        return new ResponseEntity<>(savedAddressDTO, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/addresses/specificUser")
    public ResponseEntity<List<AddressDTO>> getAllAddressesToSpecificUser(){

        List<AddressDTO> savedAddressDTOs = addressService.getAllAddressesToSpecificUser();
        return new ResponseEntity<>(savedAddressDTOs, org.springframework.http.HttpStatus.OK);

    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getSpecificAddress(@PathVariable Long addressId, @Valid @RequestBody AddressDTO addressDTO){

        AddressDTO savedAddressDTO = addressService.updateSpecificAddress(addressId, addressDTO);
        return new ResponseEntity<>(savedAddressDTO, org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteSpecificAddress(@PathVariable Long addressId){

        String status = addressService.deleteSpecificAddress(addressId);
        return new ResponseEntity<>(status, org.springframework.http.HttpStatus.OK);
    }

}
