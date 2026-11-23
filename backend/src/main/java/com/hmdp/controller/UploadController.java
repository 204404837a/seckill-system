package com.hmdp.controller;

import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.Result;
import com.hmdp.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {

    @PostMapping("blog")
    public Result uploadImage(@RequestParam("file") MultipartFile image) {
        try {
            // 获取原始文件名称
            String originalFilename = image.getOriginalFilename();
            String suffix = StrUtil.subAfter(originalFilename, ".", true);
            if (suffix == null || !suffix.matches("[A-Za-z0-9]{1,10}")) {
                return Result.fail("错误的文件名称");
            }
            // 生成新文件名
            String fileName = createNewFileName(suffix);
            // 保存文件
            image.transferTo(new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName.substring(1)));
            // 返回结果
            log.debug("文件上传成功，{}", fileName);
            return Result.ok(fileName);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @GetMapping("/blog/delete")
    public Result deleteBlogImg(@RequestParam("name") String filename) throws IOException {
        String relativeName = StrUtil.removePrefix(StrUtil.removePrefix(filename, "/imgs/"), "/");
        Path uploadRoot = Paths.get(SystemConstants.IMAGE_UPLOAD_DIR).toAbsolutePath().normalize();
        Path file = uploadRoot.resolve(relativeName).normalize();
        if (!file.startsWith(uploadRoot.resolve("blogs")) || Files.isDirectory(file)) {
            return Result.fail("错误的文件名称");
        }
        Files.deleteIfExists(file);
        return Result.ok();
    }

    private String createNewFileName(String suffix) throws IOException {
        // 生成目录
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        Files.createDirectories(Paths.get(SystemConstants.IMAGE_UPLOAD_DIR, "blogs",
                String.valueOf(d1), String.valueOf(d2)));
        // 生成文件名
        return StrUtil.format("/blogs/{}/{}/{}.{}", d1, d2, name, suffix);
    }
}
