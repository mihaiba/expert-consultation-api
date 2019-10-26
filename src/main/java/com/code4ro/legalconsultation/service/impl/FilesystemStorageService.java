package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.service.api.StorageApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Profile("dev")
public class FilesystemStorageService implements StorageApi {

    @Value("${storage.filesystem.directory}")
    private String customStoreDirPath;

    private File storeDir;

    @PostConstruct
    private void prepareStoreDir() {
        if (StringUtils.isNotBlank(customStoreDirPath)) {
            storeDir = new File(customStoreDirPath);
        } else {
            final String home = System.getProperty("user.home");
            storeDir = new File(home, "legal_consult_uploads");
        }
        if (!storeDir.exists()) {
            storeDir.mkdir();
        }
    }

    @Override
    public String storeFile(final MultipartFile document) throws IOException {
        // add a random string to each file in roder to avoid duplicates
        final String fileName = StorageApi.resolveUniqueName(document);
        final Path filepath = Paths.get(storeDir.getAbsolutePath(), fileName);
        document.transferTo(filepath);
        return filepath.toString();
    }

    @Override
    public byte[] loadFile(final String documentURI) throws IOException {
        return Files.readAllBytes(Paths.get(documentURI));
    }

    @Override
    public void deleteFile(String documentURI) throws IOException {
        Files.delete(Paths.get(documentURI));
    }
}
