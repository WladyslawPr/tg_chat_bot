package com.dev.vlpr.service.impl;

import com.dev.vlpr.crypto.CryptoTool;
import com.dev.vlpr.dao.AppDocumentDAO;
import com.dev.vlpr.dao.AppPhotoDAO;
import com.dev.vlpr.entity.AppDocument;
import com.dev.vlpr.entity.AppPhoto;
import com.dev.vlpr.entity.BinaryContent;
import com.dev.vlpr.service.RestFileService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
public class RestFileServiceImpl implements RestFileService {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    private final CryptoTool cryptoTool;

    public RestFileServiceImpl(AppDocumentDAO appDocumentDAO,
                        AppPhotoDAO appPhotoDAO,
                               CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }
    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);
      //  var id = Long.parseLong(docId);
        if (id == null) {
            return null;
        }
        return appDocumentDAO.findById(id)
                .orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
       // var id = Long.parseLong(photoId);
        return appPhotoDAO.findById(id)
                .orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException exception) {
            log.error(exception);
            return null;
        }
    }
}
