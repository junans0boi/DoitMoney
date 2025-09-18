package com.hollywood.doitmoney.user.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(
            @Value("${app.upload.dir:uploads/profiles}") String uploadDir) {
        // 상대경로(앱 홈 디렉터리 기준)로 설정
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉터리 생성 실패: " + this.root, e);
        }
    }

    public String store(MultipartFile file) {
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        try {
            Path target = root.resolve(filename);
            Files.copy(file.getInputStream(), target);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
        // 클라이언트에서 이 URL을 불러갈 수 있도록 경로를 리턴
        return "/static/profiles/" + filename;
    }
}