package com.nileonx.leetchat_springboot.service;

import java.io.InputStream;

public interface IOssService {
/*
    @param fileName
    @param content
    @return
 */
    public String uploadAvatar(InputStream content, String fileName,Long uid);
    public void uploadFile(String localPath);
    public String uploadFile(InputStream file, String fileName,Long uid,String sid);
    public void downloadFile(String downloadKey,String localPath);
    public String getUrl(String downloadKey);
}
