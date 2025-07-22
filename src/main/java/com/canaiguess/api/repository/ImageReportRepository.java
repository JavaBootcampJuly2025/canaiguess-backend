package com.canaiguess.api.repository;

import com.canaiguess.api.model.ImageReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageReportRepository extends JpaRepository<ImageReport, Long> {
    List<ImageReport> findByResolvedFalseOrderByTimestampDesc();
}
