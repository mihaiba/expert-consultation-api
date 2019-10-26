package com.code4ro.legalconsultation.service.api;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public interface StorageApi {

    static String resolveUniqueName(final MultipartFile document){
        return RandomStringUtils.randomAlphabetic(10) +
                StringUtils.cleanPath(document.getOriginalFilename());
    }

    String storeFile(final MultipartFile document) throws IOException, URISyntaxException, Exception;

    byte[] loadFile(String documentURI) throws IOException;

    void deleteFile(String documentURI) throws IOException;
}
