package com.code4ro.legalconsultation.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.code4ro.legalconsultation.service.api.StorageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@Profile("production")
public class AmazonS3StorageService implements StorageApi {
    private static final Logger LOG = LoggerFactory.getLogger(AmazonS3StorageService.class);

    @Value("${storage.aws.access.key.id}")
    private String awsKeyId;

    @Value("${storage.aws.access.key.secret}")
    private String secretKey;

    @Value("${storage.aws.region}")
    private String region;

    @Value("${storage.aws.s3.documentBucket}")
    private String documentBucket;

    private AmazonS3 amazonS3;

    @PostConstruct
    private void initializeS3() {
        final BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsKeyId, secretKey);
        amazonS3 =  AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        if(amazonS3.doesBucketExist(documentBucket)) {
            LOG.info("Bucket already created");
            return;
        }
        LOG.info("Creating aws s3 bucket {}.", documentBucket);
        amazonS3.createBucket(documentBucket);
    }

    @Override
    public String storeFile(final MultipartFile document) throws Exception {
        final ObjectMetadata data = new ObjectMetadata();
        data.setContentLength(document.getSize());
        final String uniqueDocumentName = StorageApi.resolveUniqueName(document);
        amazonS3.putObject(documentBucket, uniqueDocumentName, document.getInputStream(), data);
        return amazonS3.getUrl(documentBucket, uniqueDocumentName).toString();
    }

    @Override
    public byte[] loadFile(String documentURI) throws IOException {
        return amazonS3.getObject(documentBucket, documentURI)
                .getObjectContent()
                .readAllBytes();
    }

    @Override
    public void deleteFile(String documentURI) throws IOException {
        amazonS3.deleteObject(documentBucket, documentURI);
    }
}
