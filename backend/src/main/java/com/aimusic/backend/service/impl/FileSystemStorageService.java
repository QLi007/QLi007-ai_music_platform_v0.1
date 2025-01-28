package com.aimusic.backend.service.impl;

import com.aimusic.backend.config.StorageConfig;
import com.aimusic.backend.exception.StorageException;
import com.aimusic.backend.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * 文件系统存储服务实现类
 * 实现基于文件系统的文件存储
 */
@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final StorageConfig storageConfig;

    public FileSystemStorageService(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
        this.rootLocation = Paths.get(storageConfig.getLocation());
    }

    @Override
    public void init() {
        try {
            // 确保目录存在
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                log.info("创建存储根目录: {}", rootLocation);
            }

            Path tempDir = Paths.get(storageConfig.getTempDir());
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
                log.info("创建临时目录: {}", tempDir);
            }

            // 确保目录可写
            if (!Files.isWritable(rootLocation)) {
                throw new StorageException("存储目录不可写: " + rootLocation);
            }
            if (!Files.isWritable(tempDir)) {
                throw new StorageException("临时目录不可写: " + tempDir);
            }

            log.info("存储服务初始化成功");
        } catch (IOException e) {
            throw new StorageException("无法初始化存储位置", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("无法存储空文件");
            }

            // 验证文件大小
            if (file.getSize() > storageConfig.getMaxFileSize()) {
                throw new StorageException("文件大小超过限制");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.length() > storageConfig.getMaxFilenameLength()) {
                throw new StorageException("文件名长度超过限制");
            }

            // 验证文件类型
            validateFileType(originalFilename);

            // 生成文件名
            String filename = generateFilename(originalFilename);

            // 存储文件
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
                log.info("文件存储成功: {}", filename);
                return filename;
            }
        } catch (IOException e) {
            throw new StorageException("文件存储失败", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("无法读取存储的文件", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("无法读取文件: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("无法读取文件: " + filename, e);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = load(filename);
            Files.deleteIfExists(file);
            log.info("文件删除成功: {}", filename);
        } catch (IOException e) {
            throw new StorageException("无法删除文件: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
        log.info("所有文件删除成功");
    }

    @Override
    public String getFileUrl(String filename) {
        return storageConfig.getUrlPrefix() + "/" + filename;
    }

    @Override
    public boolean exists(String filename) {
        return Files.exists(load(filename));
    }

    @Override
    public long getFileSize(String filename) {
        try {
            return Files.size(load(filename));
        } catch (IOException e) {
            throw new StorageException("无法获取文件大小: " + filename, e);
        }
    }

    /**
     * 生成唯一的文件名
     *
     * @param originalFilename 原始文件名
     * @return 生成的文件名
     */
    private String generateFilename(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        String filename = storageConfig.isUseOriginalFilename() ?
                StringUtils.cleanPath(originalFilename) :
                UUID.randomUUID().toString() + "." + extension;

        if (filename.length() > storageConfig.getMaxFilenameLength()) {
            throw new StorageException("文件名长度超过限制");
        }

        return filename;
    }

    /**
     * 检查文件类型是否允许
     *
     * @param filename 文件名
     */
    private void validateFileType(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        if (extension == null || !storageConfig.getAllowedFileTypes().stream()
                .map(type -> type.toLowerCase().replace(".", ""))
                .anyMatch(type -> type.equals(extension.toLowerCase()))) {
            throw new StorageException("不支持的文件类型: " + extension);
        }
    }
} 