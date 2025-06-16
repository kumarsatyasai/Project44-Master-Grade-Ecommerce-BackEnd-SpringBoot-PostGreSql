package com.nt.service;

import com.nt.model.User;
import com.nt.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface IAddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();


    AddressDTO getSpecificAddress(Long addressId);

    List<AddressDTO> getAllAddressesToSpecificUser();

    AddressDTO updateSpecificAddress(Long addressId, @Valid AddressDTO addressDTO);

    String deleteSpecificAddress(Long addressId);
}
