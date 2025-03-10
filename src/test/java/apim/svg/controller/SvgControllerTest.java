package apim.svg.controller;

import apim.svg.service.SvgService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SvgController.class)
public class SvgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SvgService svgService;

    @Test
    public void testGetSvgFiles() throws Exception {
        when(svgService.getSvgContents(anyString(), anyInt(), anyInt()))
                .thenReturn(Arrays.asList("<svg>test</svg>", "<svg>test2</svg>"));

        mockMvc.perform(get("/api/svg/500/2"))
                .andExpect(status().isOk());
    }
} 