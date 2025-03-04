package apim.svg.service;

import apim.svg.exception.SvgNotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SvgService {

    private final Random random = new Random();
    
    public List<String> getSvgContents(String size, int count) throws IOException {
        // 유효한 크기인지 확인
        if (!isValidSize(size) && !"random".equals(size)) {
            throw new SvgNotFoundException("유효하지 않은 SVG 크기: " + size);
        }
        
        // 유효한 개수인지 확인
        if (count <= 0 || count > 100) {
            throw new IllegalArgumentException("유효하지 않은 개수: " + count + " (1-100 사이여야 합니다)");
        }
        
        List<String> svgContents = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            String svgContent = loadSvgContent(size);
            // 주석 제거
            svgContent = removeComments(svgContent);
            // 줄바꿈 제거 및 공백 정리
            svgContent = cleanupSvg(svgContent);
            svgContents.add(svgContent);
        }
        
        return svgContents;
    }
    
    private boolean isValidSize(String size) {
        return "500".equals(size) || "1000".equals(size) || "4000".equals(size);
    }
    
    private String loadSvgContent(String size) throws IOException {
        String resourcePath;
        
        if ("random".equals(size)) {
            // 랜덤으로 500, 1000, 4000 중 하나 선택
            String[] sizes = {"500", "1000", "4000"};
            String randomSize = sizes[random.nextInt(sizes.length)];
            resourcePath = "static/svg" + randomSize + "_1.svg";
        } else {
            resourcePath = "static/svg" + size + "_1.svg";
        }
        
        try {
            Resource resource = new ClassPathResource(resourcePath);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SvgNotFoundException("SVG 파일을 찾을 수 없습니다: " + resourcePath);
        }
    }
    
    /**
     * SVG 파일에서 주석을 제거합니다.
     * @param svgContent SVG 파일 내용
     * @return 주석이 제거된 SVG 내용
     */
    private String removeComments(String svgContent) {
        return svgContent.replaceAll("<!--[\\s\\S]*?-->", "");
    }
    
    /**
     * SVG 파일에서 줄바꿈과 불필요한 공백을 정리합니다.
     * @param svgContent SVG 파일 내용
     * @return 정리된 SVG 내용
     */
    private String cleanupSvg(String svgContent) {
        // 줄바꿈 제거
        String cleaned = svgContent.replaceAll("\\n", " ");
        // 연속된 공백을 하나로 줄임
        cleaned = cleaned.replaceAll("\\s+", " ");
        // 태그 사이의 불필요한 공백 제거
        cleaned = cleaned.replaceAll("> <", "><");
        return cleaned.trim();
    }
} 