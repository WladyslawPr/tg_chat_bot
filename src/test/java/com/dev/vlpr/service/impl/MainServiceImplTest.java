package com.dev.vlpr.service.impl;


import com.dev.vlpr.dao.RawDataDAO;
import com.dev.vlpr.entity.RawData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class MainServiceImplTest {
    @Autowired
    private RawDataDAO rawDataDAO;
    @Test
    public void testSaveRowDate() {
        Update update = new Update();
        Message message = new Message();
        message.setText("check");
        update.setMessage(message);

        RawData rawData = RawData.builder()
                .update(update)
                .build();
        Set<RawData> testData = new HashSet<>();

        testData.add(rawData);
        rawDataDAO.save(rawData);

        Assert.isTrue(testData.contains(rawData), "Entity not found in the set");
    }

}