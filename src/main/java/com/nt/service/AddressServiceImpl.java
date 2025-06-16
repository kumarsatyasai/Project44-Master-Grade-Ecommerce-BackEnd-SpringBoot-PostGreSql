package com.nt.service;

import com.nt.exceptions.APIException;
import com.nt.exceptions.ResourceNotFoundException;
import com.nt.model.Address;
import com.nt.model.User;
import com.nt.payload.AddressDTO;
import com.nt.repository.IAddressRepository;
import com.nt.repository.IUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private IAddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        user.getAddresses().add(address);
        userRepository.save(user);

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        if (addresses.isEmpty()) {
            throw new APIException("No addresses found");
        }
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getSpecificAddress(Long addressId) {

        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isEmpty()) {
            throw new ResourceNotFoundException("Address", "addressId", addressId);
        }
        AddressDTO addressDTO = modelMapper.map(address.get(), AddressDTO.class);
        return addressDTO;
    }

    @Override
    public List<AddressDTO> getAllAddressesToSpecificUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Address> addresses = addressRepository.findByUser(user);

        if (addresses.isEmpty()) {
            throw new APIException("No addresses found for user: " + user.getUserName());
        }

        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateSpecificAddress(Long addressId, AddressDTO addressDTO) {
        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isEmpty()) {
            throw new ResourceNotFoundException("Address", "addressId", addressId);
        }
        Address addressFromDB = address.get();
        addressFromDB.setCity(addressDTO.getCity());
        addressFromDB.setCountry(addressDTO.getCountry());
        addressFromDB.setBuildingName(addressDTO.getBuildingName());
        addressFromDB.setState(addressDTO.getState());
        addressFromDB.setZipCode(addressDTO.getZipCode());
        Address savedAddress = addressRepository.save(addressFromDB);


        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public String deleteSpecificAddress(Long addressId) {
        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isEmpty()) {
            throw new ResourceNotFoundException("Address", "addressId", addressId);
        }
        Address addressFromDB = address.get();
        addressRepository.delete(addressFromDB);
        return "Address deleted successfully with : " + addressId + " id.";
    }
}
