package com.nt.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {

    public String uploadImage(String path, MultipartFile image) throws IOException;
}
