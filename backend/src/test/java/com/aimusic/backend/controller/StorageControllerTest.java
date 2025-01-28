package com.aimusic.backend.controller;

import com.aimusic.backend.exception.StorageException;
import com.aimusic.backend.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aimusic.backend.exception.StorageFileNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 存储控制器测试类
 */
@WebMvcTest(StorageController.class)
@ExtendWith(MockitoExtension.class)
public class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StorageService storageService;

    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testFile = new MockMultipartFile(
                "file",
                "test.mp3",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "test data".getBytes()
        );
    }

    @Test
    void shouldUploadFile() throws Exception {
        String fileUrl = "http://example.com/uploads/test.mp3";
        when(storageService.store(any(MultipartFile.class))).thenReturn(fileUrl);

        mockMvc.perform(multipart("/api/storage/upload")
                        .file(testFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(fileUrl));
    }

    @Test
    void shouldUploadFile_whenFileEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.mp3",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[0]
        );

        when(storageService.store(any(MultipartFile.class)))
                .thenThrow(new StorageException("文件为空"));

        mockMvc.perform(multipart("/api/storage/upload")
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("文件为空"));
    }

    @Test
    void shouldUploadFile_whenFileSizeExceedsLimit() throws Exception {
        when(storageService.store(any(MultipartFile.class)))
                .thenThrow(new StorageException("文件大小超过限制"));

        mockMvc.perform(multipart("/api/storage/upload")
                        .file(testFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("文件大小超过限制"));
    }

    @Test
    void shouldUploadFile_whenUnsupportedFileType() throws Exception {
        MockMultipartFile unsupportedFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test data".getBytes()
        );

        when(storageService.store(any(MultipartFile.class)))
                .thenThrow(new StorageException("不支持的文件类型"));

        mockMvc.perform(multipart("/api/storage/upload")
                        .file(unsupportedFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("不支持的文件类型"));
    }

    @Test
    void shouldDownloadFile() throws Exception {
        String filename = "test.mp3";
        Resource fileResource = new ByteArrayResource("test data".getBytes());
        when(storageService.loadAsResource(filename)).thenReturn(fileResource);

        mockMvc.perform(get("/api/storage/download/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().bytes("test data".getBytes()));
    }

    @Test
    void shouldDownloadFile_whenFileNotFound() throws Exception {
        String filename = "nonexistent.mp3";
        when(storageService.loadAsResource(filename))
                .thenThrow(new StorageFileNotFoundException("文件不存在"));

        mockMvc.perform(get("/api/storage/download/{filename}", filename))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("文件不存在"));
    }

    @Test
    void shouldListFiles() throws Exception {
        // 准备测试数据
        Path file1 = Paths.get("test1.mp3");
        Path file2 = Paths.get("test2.mp3");
        given(storageService.loadAll()).willReturn(Stream.of(file1, file2));

        // 执行测试
        mockMvc.perform(get("/api/storage/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());

        // 验证服务调用
        verify(storageService).loadAll();
    }

    @Test
    void shouldDeleteFile() throws Exception {
        // 准备测试数据
        String filename = "test.mp3";
        doNothing().when(storageService).delete(filename);

        // 执行测试
        mockMvc.perform(delete("/api/storage/{filename}", filename))
                .andExpect(status().isOk());

        // 验证服务调用
        verify(storageService).delete(filename);
    }

    @Test
    void shouldDeleteFile_whenFileNotFound() throws Exception {
        // 准备测试数据
        String filename = "nonexistent.mp3";
        doThrow(new StorageException("无法删除文件: " + filename))
                .when(storageService).delete(filename);

        // 执行测试
        mockMvc.perform(delete("/api/storage/{filename}", filename))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGetFileInfo() throws Exception {
        // 准备测试数据
        String filename = "test.mp3";
        String fileUrl = "/api/storage/download/" + filename;
        long fileSize = 1024L;

        given(storageService.getFileUrl(filename)).willReturn(fileUrl);
        given(storageService.getFileSize(filename)).willReturn(fileSize);
        given(storageService.exists(filename)).willReturn(true);

        // 执行测试
        mockMvc.perform(get("/api/storage/{filename}/info", filename))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value(filename))
                .andExpect(jsonPath("$.url").value(fileUrl))
                .andExpect(jsonPath("$.size").value(fileSize))
                .andExpect(jsonPath("$.exists").value(true));

        // 验证服务调用
        verify(storageService).getFileUrl(filename);
        verify(storageService).getFileSize(filename);
        verify(storageService).exists(filename);
    }

    @Test
    void shouldGetFileInfo_whenFileNotFound() throws Exception {
        // 准备测试数据
        String filename = "nonexistent.mp3";
        given(storageService.exists(filename)).willReturn(false);
        given(storageService.getFileUrl(filename)).willReturn("/api/storage/download/" + filename);
        given(storageService.getFileSize(filename))
                .willThrow(new StorageException("无法获取文件大小: " + filename));

        // 执行测试
        mockMvc.perform(get("/api/storage/{filename}/info", filename))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }
} 