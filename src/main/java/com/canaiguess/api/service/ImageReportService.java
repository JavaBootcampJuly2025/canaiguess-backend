package com.canaiguess.api.service;

import com.canaiguess.api.dto.ImageReportResponseDTO;
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

    public void submitReport(String imageId, User user, String description) {

        Image image = imageRepository.findByPublicId(imageId)
                .orElseThrow(() -> new GameDataIncompleteException("Image not found with id: " + imageId));

        ImageReport report = new ImageReport();
        report.setImage(image);
        report.setUser(user);
        report.setDescription(description);

        imageReportRepository.save(report);
    }

    public List<ImageReportResponseDTO> getUnresolvedReports() {
        return imageReportRepository.findByResolvedFalseOrderByTimestampDesc()
            .stream()
            .map(report -> ImageReportResponseDTO.builder()
                    .reportId(report.getId())
                    .imageId(report.getImage().getPublicId())
                    .imageUrl(report.getImage().getUrl())
                    .username(report.getUser().getUsername())
                    .description(report.getDescription())
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
