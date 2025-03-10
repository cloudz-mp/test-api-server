package apim.svg.controller;

import apim.svg.service.SvgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/svg")
@Tag(name = "SVG API", description = "SVG 파일 제공 API")
public class SvgController {

    private final SvgService svgService;

    public SvgController(SvgService svgService) {
        this.svgService = svgService;
    }

    @Operation(
        summary = "SVG 파일 목록 조회",
        description = "지정된 크기와 개수의 SVG 파일 내용을 반환합니다. 크기는 500, 1000, 4000 또는 random이 가능합니다."
    )
    @GetMapping(value = "/{size}/{count}", produces = "image/svg+xml")
    public ResponseEntity<Resource> getSvgFiles(
            @Parameter(description = "SVG 파일 크기 (500, 1000, 4000 또는 random)") @PathVariable String size,
            @Parameter(description = "반환할 SVG 파일 개수 (1-100)") @PathVariable int count,
            @Parameter(description = "스레드 테스트를 위한 대기 시간(ms)") @RequestParam(required = false, defaultValue = "0") Integer sleepTime) throws IOException {
        
        List<String> svgContents = svgService.getSvgContents(size, count, sleepTime);
        
        // 여러 SVG 파일을 하나로 합치기
        StringBuilder combinedSvg = new StringBuilder();
        combinedSvg.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        combinedSvg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100%\" height=\"100%\">");
        
        for (String svgContent : svgContents) {
            // XML 선언과 루트 svg 태그를 제거하고 내부 내용만 추출
            String innerContent = svgContent
                .replaceAll("<?xml[^>]*>", "")
                .replaceAll("<svg[^>]*>", "")
                .replaceAll("</svg>", "");
            
            combinedSvg.append("<g>").append(innerContent).append("</g>");
        }
        
        combinedSvg.append("</svg>");
        
        ByteArrayResource resource = new ByteArrayResource(combinedSvg.toString().getBytes(StandardCharsets.UTF_8));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/svg+xml"));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
    
    @Operation(
        summary = "단일 SVG 파일 조회",
        description = "지정된 크기의 SVG 파일을 직접 XML 형식으로 반환합니다."
    )
    @GetMapping(value = "/single/{size}", produces = "image/svg+xml")
    public ResponseEntity<String> getSingleSvg(
            @Parameter(description = "SVG 파일 크기 (500, 1000, 4000 또는 random)") @PathVariable String size,
            @Parameter(description = "스레드 테스트를 위한 대기 시간(ms)") @RequestParam(required = false, defaultValue = "0") Integer sleepTime) throws IOException {
        
        List<String> svgContents = svgService.getSvgContents(size, 1, sleepTime);
        String svgContent = svgContents.get(0);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/svg+xml"));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(svgContent);
    }
} 