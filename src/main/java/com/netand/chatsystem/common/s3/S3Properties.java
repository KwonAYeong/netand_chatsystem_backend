package com.netand.chatsystem.common.s3;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class S3Properties {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
}
