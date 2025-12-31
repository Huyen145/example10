package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.payload.response.UploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    // Folder lưu ảnh
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            String originalName = file.getOriginalFilename();
            if (originalName == null) originalName = "unknown.jpg";

            String safeName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            String newFileName = System.currentTimeMillis() + "_" + safeName;

            File dest = new File(folder, newFileName);
            file.transferTo(dest);

            String fileUrl = "/uploads/" + newFileName;
            return ResponseEntity.ok(new UploadResponse(fileUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new UploadResponse(""));
        }
    }
}
