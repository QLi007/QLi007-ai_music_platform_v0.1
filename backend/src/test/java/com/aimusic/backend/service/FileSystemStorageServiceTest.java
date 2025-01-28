package com.aimusic.backend.service;

import com.aimusic.backend.config.StorageConfig;
import com.aimusic.backend.exception.StorageException;
import com.aimusic.backend.service.impl.FileSystemStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件系统存储服务测试类
 */
class FileSystemStorageServiceTest {

    private StorageConfig storageConfig;
    private FileSystemStorageService storageService;
    private Path uploadDir;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // 创建临时目录
        uploadDir = Files.createTempDirectory("uploads");
        tempDir = Files.createTempDirectory("temp");

        // 配置存储服务
        storageConfig = new StorageConfig();
        storageConfig.setLocation(uploadDir.toString());
        storageConfig.setTempDir(tempDir.toString());
        storageConfig.setMaxFileSize(1024 * 1024); // 1MB
        storageConfig.setAllowedFileTypes(Set.of(".mp3", ".wav"));
        storageConfig.setUrlPrefix("/files");
        storageConfig.setMaxFilenameLength(100);
        storageConfig.setUseOriginalFilename(false);

        storageService = new FileSystemStorageService(storageConfig);
        storageService.init();
    }

    @AfterEach
    void tearDown() {
        try {
            // 清理测试文件
            FileSystemUtils.deleteRecursively(uploadDir);
            FileSystemUtils.deleteRecursively(tempDir);
        } catch (IOException e) {
            System.err.println("清理测试文件失败: " + e.getMessage());
        }
    }

    @Test
    void shouldStoreFile() throws IOException {
        // 准备测试数据
        String filename = UUID.randomUUID().toString() + ".mp3";
        MultipartFile file = new MockMultipartFile(
                "file",
                filename,
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "Test file content".getBytes()
        );

        // 执行测试
        String storedFilename = storageService.store(file);

        // 验证结果
        assertNotNull(storedFilename);
        assertTrue(Files.exists(uploadDir.resolve(storedFilename)));
        assertTrue(Files.size(uploadDir.resolve(storedFilename)) > 0);
    }

    @Test
    void shouldThrowException_whenFileEmpty() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.mp3",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[0]
        );

        assertThrows(StorageException.class, () -> storageService.store(emptyFile));
    }

    @Test
    void shouldThrowException_whenFileSizeExceedsLimit() {
        byte[] content = new byte[2 * 1024 * 1024]; // 2MB
        MultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.mp3",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                content
        );

        assertThrows(StorageException.class, () -> storageService.store(largeFile));
    }

    @Test
    void shouldThrowException_whenFileTypeNotAllowed() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Test content".getBytes()
        );

        assertThrows(StorageException.class, () -> storageService.store(file));
    }

    @Test
    void shouldLoadNonExistentFile() {
        assertThrows(StorageException.class, () -> 
            storageService.loadAsResource("nonexistent.mp3")
        );
    }

    @Test
    void shouldDeleteFile() throws IOException {
        // 准备测试数据
        String filename = UUID.randomUUID().toString() + ".mp3";
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, "Test content".getBytes());

        // 执行删除
        storageService.delete(filename);

        // 验证文件已删除
        assertFalse(Files.exists(filePath));
    }

    @Test
    void shouldGetFileUrl() {
        String filename = "test.mp3";
        String expectedUrl = "/files/" + filename;
        assertEquals(expectedUrl, storageService.getFileUrl(filename));
    }

    @Test
    void shouldCheckFileExists() throws IOException {
        // 准备测试数据
        String filename = UUID.randomUUID().toString() + ".mp3";
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, "Test content".getBytes());

        // 验证文件存在
        assertTrue(storageService.exists(filename));
        assertFalse(storageService.exists("nonexistent.mp3"));
    }

    @Test
    void shouldGetFileSize() throws IOException {
        // 准备测试数据
        String content = "Test content";
        String filename = UUID.randomUUID().toString() + ".mp3";
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, content.getBytes());

        // 验证文件大小
        assertEquals(content.getBytes().length, storageService.getFileSize(filename));
    }

    @Test
    void shouldThrowException_whenFilenameTooLong() {
        String longFilename = "a".repeat(200) + ".mp3";
        MultipartFile file = new MockMultipartFile(
                "file",
                longFilename,
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "Test content".getBytes()
        );

        assertThrows(StorageException.class, () -> storageService.store(file));
    }
} 