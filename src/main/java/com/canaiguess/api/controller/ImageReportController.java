package com.canaiguess.api.controller;

import com.canaiguess.api.dto.ImageReportResponseDTO;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.ImageReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ImageReportController {

    private final ImageReportService imageReportService;

    @Operation(summary = "List unresolved image reports (ADMIN only)")
    @GetMapping("/unresolved")
    public List<ImageReportResponseDTO> listUnresolvedReports() {
        return imageReportService.getUnresolvedReports();
    }

    @Operation(summary = "Mark a report as resolved (ADMIN only)")
    @PostMapping("/{reportId}/resolve")
    public ResponseEntity<Void> resolveReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal User reviewer
    ) {
        imageReportService.resolveReport(reportId, reviewer);
        return ResponseEntity.noContent().build();
    }

}
