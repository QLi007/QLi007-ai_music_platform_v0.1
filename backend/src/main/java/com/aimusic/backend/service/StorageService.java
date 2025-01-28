package com.aimusic.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 文件存储服务接口
 * 定义文件存储的基本操作
 */
public interface StorageService {
    
    /**
     * 初始化存储
     */
    void init();
    
    /**
     * 存储文件
     *
     * @param file 要存储的文件
     * @return 文件的相对路径
     */
    String store(MultipartFile file);
    
    /**
     * 加载所有文件
     *
     * @return 文件路径流
     */
    Stream<Path> loadAll();
    
    /**
     * 加载指定文件
     *
     * @param filename 文件名
     * @return 文件路径
     */
    Path load(String filename);
    
    /**
     * 加载文件作为资源
     *
     * @param filename 文件名
     * @return 文件资源
     */
    Resource loadAsResource(String filename);
    
    /**
     * 删除文件
     *
     * @param filename 文件名
     */
    void delete(String filename);
    
    /**
     * 删除所有文件
     */
    void deleteAll();
    
    /**
     * 获取文件URL
     *
     * @param filename 文件名
     * @return 文件URL
     */
    String getFileUrl(String filename);
    
    /**
     * 检查文件是否存在
     *
     * @param filename 文件名
     * @return 是否存在
     */
    boolean exists(String filename);
    
    /**
     * 获取文件大小
     *
     * @param filename 文件名
     * @return 文件大小（字节）
     */
    long getFileSize(String filename);
} 