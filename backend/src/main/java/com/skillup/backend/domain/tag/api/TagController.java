package com.skillup.backend.domain.tag.api;

import com.skillup.backend.domain.tag.service.TagService;
import com.skillup.backend.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<String>>> searchTags(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(tagService.search(keyword))
        );
    }
}
