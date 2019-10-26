package com.code4ro.legalconsultation.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.code4ro.legalconsultation.service.impl.AmazonS3StorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AmazonS3StorageServiceTest {

    @Mock
    private AmazonS3 client;

    @InjectMocks
    private AmazonS3StorageService storageService;

    private String documentBucket = "documentBucket";

    @Before
    public void before() {
        ReflectionTestUtils.setField(storageService, "documentBucket", documentBucket);
    }

    @Test
    public void storeFile() throws Exception {
        final MultipartFile randomFile = new MockMultipartFile("file", "file", "text/plain",
                "text".getBytes());
        when(client.getUrl(eq(documentBucket), anyString())).thenReturn(new URL("http://url"));

        storageService.storeFile(randomFile);

        verify(client).putObject(eq(documentBucket), anyString(), any(), any());
        verify(client).getUrl(eq(documentBucket), anyString());
    }

    @Test
    public void loadFile() throws IOException {
        final String uri = "uri";
        final S3Object object = new S3Object();
        object.setObjectContent(new ByteArrayInputStream("text".getBytes()));
        when(client.getObject(documentBucket, uri)).thenReturn(object);

        storageService.loadFile(uri);

        verify(client).getObject(documentBucket, uri);
    }

    @Test
    public void deleteFile() throws IOException {
        final String uri = "uri";
        final S3Object object = new S3Object();
        object.setObjectContent(new ByteArrayInputStream("text".getBytes()));
        doNothing().when(client).deleteObject(documentBucket, uri);

        storageService.deleteFile(uri);

        verify(client).deleteObject(documentBucket, uri);
    }
}
