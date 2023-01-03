package com.dev.vlpr.service;

import com.dev.vlpr.entity.AppDocument;
import com.dev.vlpr.entity.AppPhoto;
import com.dev.vlpr.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface RestFileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
