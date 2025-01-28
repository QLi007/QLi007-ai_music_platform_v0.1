package com.aimusic.backend.controller;

import com.aimusic.backend.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件存储控制器
 * 处理文件上传下载请求
 */
@Tag(name = "文件存储", description = "文件上传下载相关接口")
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
class StorageController {

    private final StorageService storageService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = storageService.store(file);
        return ResponseEntity.ok(filename);
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download/{filename}")
    ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    /**
     * 获取文件列表
     *
     * @return 文件URL列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        List<String> files = storageService.loadAll()
                .map(path -> MvcUriComponentsBuilder
                        .fromMethodName(StorageController.class, "downloadFile", path.getFileName().toString())
                        .build().toString())
                .collect(Collectors.toList());
        return ResponseEntity.ok(files);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{filename}")
    ResponseEntity<Void> deleteFile(@PathVariable String filename) {
        storageService.delete(filename);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取文件信息
     *
     * @param filename 文件名
     * @return 文件信息
     */
    @GetMapping("/{filename:.+}/info")
    public ResponseEntity<FileInfo> getFileInfo(@PathVariable String filename) {
        FileInfo info = new FileInfo();
        info.setFilename(filename);
        info.setUrl(storageService.getFileUrl(filename));
        info.setSize(storageService.getFileSize(filename));
        info.setExists(storageService.exists(filename));
        return ResponseEntity.ok(info);
    }

    /**
     * 文件信息类
     */
    private static class FileInfo {
        private String filename;
        private String url;
        private long size;
        private boolean exists;

        // Getters and setters
        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }
} 