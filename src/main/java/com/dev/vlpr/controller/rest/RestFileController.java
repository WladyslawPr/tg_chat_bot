package com.dev.vlpr.controller.rest;

import com.dev.vlpr.service.RestFileService;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j
@RequestMapping("/file")
@Service
public class RestFileController {

    private final RestFileService restFileService;

    public RestFileController(RestFileService restFileService) {
        this.restFileService = restFileService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
        var doc = restFileService.getDocument(id);
        if (doc == null) {
            return ResponseEntity.badRequest().build();
        }
        var binaryContent = doc.getBinaryContent();

        var fileSystemResource = restFileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .header("Content-disposition", "attachment; filename=" + doc.getDocName())
                .body(fileSystemResource);
    }


    @RequestMapping(method = RequestMethod.GET, value = "get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        var photo = restFileService.getPhoto(id);
        if (photo == null) {
            return ResponseEntity.badRequest().build();
        }
        var binaryContent = photo.getBinaryContent();
        var fileSystemResource = restFileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);

    }
}
