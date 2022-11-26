package com.dev.vlpr.dao;

import com.dev.vlpr.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
