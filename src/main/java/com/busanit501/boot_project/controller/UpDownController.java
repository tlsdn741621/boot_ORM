package com.busanit501.boot_project.controller;

import com.busanit501.boot_project.dto.upload.UploadFileDTO;
import com.busanit501.boot_project.dto.upload.UploadResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;


import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UpDownController {

    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    @Tag(name = "파일등록", description = "멀티파트 타입 형식으로, 업로드 테스트, post 방식")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
        log.info("UpDownController에서 작업중, upload메소드에서, uploadFileDTO 확인 : " + uploadFileDTO);

        if (uploadFileDTO.getFiles() != null && uploadFileDTO.getFiles().size() > 0) {
            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile -> {
                log.info("업로드한 파일명 : " + multipartFile.getOriginalFilename());
                String originalName = multipartFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

                boolean image = false;

                try {
                    multipartFile.transferTo(savePath);
                    if (Files.probeContentType(savePath).startsWith("image")) {
                        image = true;
                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                UploadResultDTO dto = UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(image)
                        .build();

                list.add(dto);

            });
            return list;
        }

        return null;
    }

    @Tag(name = "파일 확인하기. view모드", description = "첨부된 파일 조회 get 방식")
    @GetMapping(value = "/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();
        log.info("viewFileGet에서 작업중. resourceName :  " + resourceName);
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
            log.info("Files.probeContentType(resource.getFile().toPath()) 확인 : " + Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @Tag(name = "remove 파일 삭제", description = "delete 방식으로 파일 삭제 ")
    @DeleteMapping(value = "/remove/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();
        log.info("viewFileGet에서 작업중. resourceName :  " + resourceName);

        Map<String,Boolean> resultMap = new HashMap<>();
        boolean removed = false;
        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            if(contentType.startsWith("image")) {
                File thumbFile = new File(uploadPath, File.separator + "s_" + fileName);
                thumbFile.delete();
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
        resultMap.put("result",removed);
        return resultMap;
    }


}
