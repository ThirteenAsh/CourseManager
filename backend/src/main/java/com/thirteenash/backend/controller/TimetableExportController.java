package com.thirteenash.backend.controller;

import com.thirteenash.backend.service.TimetableService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/timetable")
public class TimetableExportController {

    private static final MediaType XLSX_MEDIA_TYPE = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private final TimetableService timetableService;

    public TimetableExportController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) Integer semesterId,
                                         @RequestParam(required = false) Integer classId,
                                         @RequestParam(required = false) Integer teacherId,
                                         @RequestParam(required = false, defaultValue = "class") String type) {
        byte[] data = "teacher".equalsIgnoreCase(type)
                ? timetableService.exportTeacherTable(semesterId, teacherId)
                : timetableService.exportClassTable(semesterId, classId);
        String encodedFilename = URLEncoder.encode("课程表.xlsx", StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(XLSX_MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(data);
    }
}
