package com.dev.vlpr.controller.rest;

import com.dev.vlpr.service.RestFileService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j
@RequestMapping("/file")
@Service
public class FileController {

    private final RestFileService restFileService;

    public FileController(RestFileService restFileService) {
        this.restFileService = restFileService;
    }
}
