package cn.molu.app.config;


import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
//获取properties中的minio配置
@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioConfig {

    private String endpoint;
    private int port;
    private String accessKey;
    private String secretKey;
    private Boolean secure;
    private String bucketName;



    @Bean
   public MinioClient getMinioClient() throws Exception{
        System.out.println(endpoint);
       MinioClient minioClient = MinioClient.builder().endpoint(endpoint, port, secure).credentials(accessKey, secretKey).build();
       return minioClient;
   }
}
