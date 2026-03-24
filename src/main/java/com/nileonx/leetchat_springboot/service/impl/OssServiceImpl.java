package com.nileonx.leetchat_springboot.service.impl;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.config.OSSConfig;
import com.nileonx.leetchat_springboot.entities.FileEntity;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.mapper.IFileMapper;
import com.nileonx.leetchat_springboot.service.IOssService;
import com.nileonx.leetchat_springboot.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
public class OssServiceImpl implements IOssService {


    @Value("${oss.bucketName}")
    private String bucketName;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private IFileMapper IFileMapper;
//    @Autowired
//    private IUserService userService;

    @Override
    public String uploadAvatar(InputStream file, String fileName,Long uid) {
        OSS ossClient = ossConfig.OSSClient();
        String destKey = null;
        //create Folder
        try {
            String type = fileName.contains(".")?
                    fileName.substring(fileName.lastIndexOf(".")+1) : "";
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("meta", "meta-value");
            metadata.setContentLength(file.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(type);
            String osskey = UUID.randomUUID()+"."+type;
            destKey = "Avatar/" + osskey;
//            res = destKey;
            PutObjectResult res =  ossClient.putObject(bucketName, destKey, file, metadata);
            log.info("ETag: " + res.getETag());

            FileEntity newFile = new FileEntity();
            newFile.setType(type);
            newFile.setFname(fileName);
            newFile.setOsskey(destKey);
            newFile.setUploadtime(new java.sql.Timestamp(System.currentTimeMillis()));
            //30 days expire time
            newFile.setExpiretime(new java.sql.Timestamp(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000));
            newFile.setUid(uid);
//            newFile.setUid(userService.getUserByUsername(uid).getUid());
            IFileMapper.insert(newFile);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
            throw new BusinessException(ErrorCode.OSS_ERROR, oe.getMessage());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
            throw new BusinessException(ErrorCode.OSS_ERROR, ce.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());

        }

        return destKey;
    }

    @Override
    public void uploadFile(String localPath) {
        OSS ossClient = ossConfig.OSSClient();
        File file = new File(localPath);
        try {
            String fname =file.getName();
            String type = fname.contains(".")?
                    fname.substring(fname.lastIndexOf(".")+1) : "";

            String osskey = UUID.randomUUID().toString();
            UploadFileRequest uploadFileRequest = getUploadFileRequest(localPath, osskey);

            UploadFileResult uploadResult = ossClient.uploadFile(uploadFileRequest);

            CompleteMultipartUploadResult multipartUploadResult =
                    uploadResult.getMultipartUploadResult();
            System.out.println(multipartUploadResult.getETag());
            FileEntity newFile = new FileEntity();
            newFile.setType(type);
            newFile.setFname(fname);
            newFile.setOsskey(osskey);
            newFile.setUploadtime(new java.sql.Timestamp(System.currentTimeMillis()));
            //30 days expire time
            newFile.setExpiretime(new java.sql.Timestamp(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000));
            IFileMapper.insert(newFile);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
            throw new BusinessException(ErrorCode.OSS_ERROR, oe.getErrorMessage());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
            throw new BusinessException(ErrorCode.OSS_ERROR, ce.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public String uploadFile(InputStream file, String fileName,Long uid,String sid) {
        OSS ossClient = ossConfig.OSSClient();
        String destKey = null;
        //create Folder
        try {
            String type = fileName.contains(".")?
                    fileName.substring(fileName.lastIndexOf(".")+1) : "";
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("meta", "meta-value");
            metadata.setContentLength(file.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(type);
            String osskey = UUID.randomUUID()+"."+type;
            destKey = "Server"+sid + "/" + osskey;
//            res = destKey;
            PutObjectResult res =  ossClient.putObject(bucketName, destKey, file, metadata);
            log.info("ETag: " + res.getETag());

            FileEntity newFile = new FileEntity();
            newFile.setType(type);
            newFile.setFname(fileName);
            newFile.setOsskey(destKey);
            newFile.setUploadtime(new java.sql.Timestamp(System.currentTimeMillis()));
            //30 days expire time
            newFile.setExpiretime(new java.sql.Timestamp(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000));
            newFile.setUid(uid);
//            newFile.setUid(userService.getUserByUsername(uname).getUid());
            IFileMapper.insert(newFile);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
            throw new BusinessException(ErrorCode.OSS_ERROR, oe.getMessage());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
            throw new BusinessException(ErrorCode.OSS_ERROR, ce.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());

        }

        return destKey;
    }

    @NotNull
    private UploadFileRequest getUploadFileRequest(String localPath, String key) {
        UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
        // The local file to upload---it must exist.
        uploadFileRequest.setUploadFile(localPath);
        // Sets the concurrent upload task number to 5.
        uploadFileRequest.setTaskNum(5);
        // Sets the part size to 1MB.
        uploadFileRequest.setPartSize(1024 * 1024 * 1);
        // Enables the checkpoint file. By default it's off.
        uploadFileRequest.setEnableCheckpoint(true);
        return uploadFileRequest;
    }

    @Override
    public void downloadFile(String downloadKey, String localPath) {
        OSS ossClient = ossConfig.OSSClient();
        try {
            DownloadFileRequest downloadFileRequest = getDownloadFileRequest(downloadKey, localPath);

            DownloadFileResult downloadResult = ossClient.downloadFile(downloadFileRequest);

            ObjectMetadata objectMetadata = downloadResult.getObjectMetadata();

            System.out.println(objectMetadata.getETag());
            System.out.println(objectMetadata.getLastModified());
            System.out.println(objectMetadata.getUserMetadata().get("meta"));

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
            throw new BusinessException(ErrorCode.OSS_ERROR, oe.getMessage());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
            throw new BusinessException(ErrorCode.OSS_ERROR, ce.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }



    @NotNull
    private DownloadFileRequest getDownloadFileRequest(String downloadKey, String localPath) {
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, downloadKey);
        downloadFileRequest.setDownloadFile(localPath);
        downloadFileRequest.setTaskNum(5);
        downloadFileRequest.setPartSize(1024 * 1024 * 1);
        downloadFileRequest.setEnableCheckpoint(true);
        return downloadFileRequest;
    }
//import com.aliyun.oss.*;
//import com.aliyun.oss.common.auth.*;
//import com.aliyun.oss.model.GeneratePresignedUrlRequest;
//import java.net.URL;
//import java.util.Date;
//
//    public class Demo {
//        public static void main(String[] args) throws Throwable {
//            // 浠ュ崕涓?锛堟澀宸烇級鐨勫缃慐ndpoint涓轰緥锛屽叾瀹僐egion璇锋寜瀹為檯鎯呭喌濉啓銆?
//            String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
//            // 浠庣幆澧冨彉閲忎腑鑾峰彇璁块棶鍑瘉銆傝繍琛屾湰浠ｇ爜绀轰緥涔嬪墠锛岃纭繚宸茶缃幆澧冨彉閲廜SS_ACCESS_KEY_ID鍜孫SS_ACCESS_KEY_SECRET銆?
//            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
//            // 濉啓Bucket鍚嶇О锛屼緥濡俥xamplebucket銆?
//            String bucketName = "examplebucket";
//            // 濉啓Object瀹屾暣璺緞锛屼緥濡俥xampleobject.txt銆侽bject瀹屾暣璺緞涓笉鑳藉寘鍚獴ucket鍚嶇О銆?
//            String objectName = "exampleobject.txt";
//
//            // 鍒涘缓OSSClient瀹炰緥銆?
//            OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
//
//            URL signedUrl = null;
//            try {
//                // 鎸囧畾鐢熸垚鐨勭鍚峌RL杩囨湡鏃堕棿锛屽崟浣嶄负姣銆傛湰绀轰緥浠ヨ缃繃鏈熸椂闂翠负1灏忔椂涓轰緥銆?
//                Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
//
//                // 鐢熸垚绛惧悕URL銆?
//                GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.GET);
//                // 璁剧疆杩囨湡鏃堕棿銆?
//                request.setExpiration(expiration);
//
//                // 閫氳繃HTTP GET璇锋眰鐢熸垚绛惧悕URL銆?
//                signedUrl = ossClient.generatePresignedUrl(request);
//                // 鎵撳嵃绛惧悕URL銆?
//                System.out.println("signed url for getObject: " + signedUrl);
//            } catch (OSSException oe) {
//                System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                        + "but was rejected with an error response for some reason.");
//                System.out.println("Error Message:" + oe.getErrorMessage());
//                System.out.println("Error Code:" + oe.getErrorCode());
//                System.out.println("Request ID:" + oe.getRequestId());
//                System.out.println("Host ID:" + oe.getHostId());
//            } catch (ClientException ce) {
//                System.out.println("Caught an ClientException, which means the client encountered "
//                        + "a serious internal problem while trying to communicate with OSS, "
//                        + "such as not being able to access the network.");
//                System.out.println("Error Message:" + ce.getMessage());
//            }
//        }
//    }
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public String getUrl(String downloadKey) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(downloadKey))) {
                log.info("[OSS] Redis HIT  key={}", downloadKey);
                return redisTemplate.opsForValue().get(downloadKey);
            }
        } catch (Exception e) {
            log.warn("[OSS] Redis read error, key={}, fallback to OSS", downloadKey, e);
        }

        log.info("[OSS] Redis MISS key={}, generating signed URL", downloadKey);
        OSS ossClient = ossConfig.OSSClient();
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, downloadKey);
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            request.setExpiration(expiration);
            URL signedUrl = ossClient.generatePresignedUrl(request);
            try {
                redisTemplate.opsForValue().set(downloadKey, signedUrl.toString(), 3600, TimeUnit.SECONDS);
                log.debug("[OSS] Redis SET  key={} (ttl=3600s)", downloadKey);
            } catch (Exception e) {
                // Redis is optional cache; never block main flow (login/avatar URL fetch) on cache failure.
                log.warn("[OSS] Redis write error, key={}, continue without cache", downloadKey, e);
            }
            return signedUrl.toString();
        } catch (OSSException oe) {
            log.error("[OSS] OSS error key={}, code={}, msg={}", downloadKey, oe.getErrorCode(), oe.getErrorMessage());
            throw new BusinessException(ErrorCode.OSS_ERROR, oe.getMessage());
        } catch (ClientException ce) {
            log.error("[OSS] OSS client error key={}, msg={}", downloadKey, ce.getMessage());
            throw new BusinessException(ErrorCode.OSS_ERROR, ce.getMessage());
        } catch (Throwable e) {
            log.error("[OSS] Unexpected error key={}", downloadKey, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

}

