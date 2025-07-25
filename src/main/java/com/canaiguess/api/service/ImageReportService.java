package com.canaiguess.api.service;

import com.canaiguess.api.dto.ImageReportDTO;
import com.canaiguess.api.dto.SubmitReportRequestDTO;
import com.canaiguess.api.exception.GameDataIncompleteException;
import com.canaiguess.api.exception.InvalidReportException;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.ImageReport;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.ImageReportRepository;
import com.canaiguess.api.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageReportService {

    private final ImageReportRepository imageReportRepository;
    private final ImageRepository imageRepository;

    public void submitReport(String imageId, User user, SubmitReportRequestDTO dto) {
        Image image = imageRepository.findByPublicIdAndDeletedFalse(imageId)
                .orElseThrow(() -> new GameDataIncompleteException("Image not found with id: " + imageId));

        ImageReport report = new ImageReport();
        report.setImage(image);
        report.setUser(user);
        report.setDescription(dto.getDescription());
        report.setTitle(dto.getTitle());

        imageReportRepository.save(report);
    }

    public List<ImageReportDTO> getUnresolvedReports() {
        return imageReportRepository.findByResolvedFalseOrderByTimestampDesc()
            .stream()
            .map(report -> ImageReportDTO.builder()
                    .reportId(report.getId())
                    .imageId(report.getImage().getPublicId())
                    .imageUrl(report.getImage().getUrl())
                    .username(report.getUser().getUsername())
                    .description(report.getDescription())
                    .title(report.getTitle())
                    .timestamp(report.getTimestamp())
                    .resolved(report.isResolved())
                    .build())
            .toList();
    }

    public void resolveReport(Long reportId, User reviewer) {
        ImageReport report = imageReportRepository.findById(reportId)
                .orElseThrow(() -> new InvalidReportException("Report not found"));

        report.setResolved(true);
        report.setReviewer(reviewer);
        imageReportRepository.save(report);
    }

}
