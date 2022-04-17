package com.example.timesheet.restControllers;

import com.example.timesheet.ExcelExporter;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class ExcelExportController {

    @RequestMapping(value = "/api/excel/get/{id}", method = RequestMethod.GET)
    ResponseEntity<Resource> fetchFile(@PathVariable("id") Integer periodId) {
        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.exportAll(periodId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Description", "File Transfer");
        headers.add("Content-Disposition", "attachment; filename=Timesheet-vypis.xlsx");
        headers.add("Content-Transfer-Encoding", "binary");
        headers.add("Connection", "Keep-Alive");
        headers.setContentType(
                MediaType.parseMediaType("application/excel"));

        Path path = Paths.get("timesheet-export.xlsx");
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok()
                    .contentLength(path.toFile().length())
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
