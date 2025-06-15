package com.netand.chatsystem.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;
    private final S3Properties s3Properties;

    public String uploadFile(MultipartFile file, String dirName) {
        String originalFilename = file.getOriginalFilename();
        String fileName = dirName + "/" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(
                    s3Properties.getBucket(),
                    fileName,
                    file.getInputStream(),
                    metadata
            );
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return amazonS3.getUrl(s3Properties.getBucket(), fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(s3Properties.getBucket(), fileName);
    }
}
