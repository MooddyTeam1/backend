// src/main/java/com/moa/backend/domain/ai/controller/AiListingController.java
package com.moa.backend.domain.openai.controller;

import com.moa.backend.domain.openai.dto.ImageListingResultResponse;
import com.moa.backend.domain.openai.service.ImageListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiListingController {

    private final ImageListingService imageListingService;

    @PostMapping(
            value = "/listing",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ImageListingResultResponse generateListing(
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "hint", required = false) String hint,
            @RequestPart(value = "tone", required = false) String tone
    ) {
        return imageListingService.generateListingFromImage(image, hint, tone);
    }
}
