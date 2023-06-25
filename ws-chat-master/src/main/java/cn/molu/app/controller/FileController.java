package cn.molu.app.controller;


import cn.hutool.core.io.FileUtil;
import cn.molu.app.pojo.ReponseResult;
import cn.molu.app.config.MinioConfig;
import cn.molu.app.utils.MinioClientUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.Instant;

@RestController
@RequestMapping("file")
public class FileController {



    @Resource
    private MinioClientUtils minioClientUtils;

    @Resource
    private MinioConfig minioConfig;

   private static Logger logger= LoggerFactory.getLogger(FileController.class);
    @PostMapping(value = "/uploadFile")
    public ReponseResult uploadFile(@RequestParam(value = "file", required = false) MultipartFile file){
        logger.info(file.toString());
        if (StringUtils.isEmpty(file)){
           return ReponseResult.fail("未选择文件") ;
        }
        String originalFilename = file.getOriginalFilename();
        logger.info("originalFilename:"+originalFilename);
//            获取文件拓展名
        String extName = FileUtil.extName(originalFilename);
        logger.info("文件拓展名:"+extName);
//            生成新的文件名，存入到minio
        long millSeconds = Instant.now().toEpochMilli();
        String minioFileName=millSeconds+ RandomStringUtils.randomNumeric(12)+"."+extName;
        String contentType = file.getContentType();
        logger.info("文件mime:{}",contentType);
//            返回文件大小,单位字节
        long size = file.getSize();
        logger.info("文件大小："+size);
        String fileUrl="";
        try {
            String bucketName = minioConfig.getBucketName();
            minioClientUtils.putObject(bucketName,file,minioFileName);
             fileUrl = minioClientUtils.getObjectUrl(bucketName, minioFileName);
        }catch (Exception e){
            e.printStackTrace();
            ReponseResult.fail("上传失败") ;
        }
        System.out.println(fileUrl);
         return  ReponseResult.ok(fileUrl) ;

    }

}
