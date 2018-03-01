package com.mfg.config;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by I309908 on 7/20/2017.
 */
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureRestDocs
@SpringBootTest
public class Swagger2MarkupTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createStaticFiles() throws Exception {
        //String designFirstSwaggerLocation = Swagger2MarkupTest.class.getResource("/swagger.yaml").getPath();

//        String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
        String outputDir = "C:\\Users\\I309908\\IdeaProjects\\ASEDashboard_Backend\\target\\swagger";
        MvcResult mvcResult = this.mockMvc.perform(get("/v2/api-docs")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String swaggerJson = response.getContentAsString();
        Files.createDirectories(Paths.get(outputDir));
        //save the content of api doc into swagger.json
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)){
            writer.write(swaggerJson);
        }

        //convert content in swagger.json into markdown format, which can be used in github readme
        String markdown_outputDir = "C:\\Users\\I309908\\IdeaProjects\\ASEDashboard_Backend\\target\\markdown\\apiDoc_markdown";
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                .withOutputLanguage(Language.EN)
                .withPathsGroupedBy(GroupBy.TAGS)
                .build();

        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(Paths.get(outputDir+"\\swagger.json"))
                .withConfig(config)
                .build();

//        Files.createDirectories(Paths.get(markdown_outputDir));
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(markdown_outputDir, "apiDoc.txt"), StandardCharsets.UTF_8)){
//            writer.write(converter.toString());
//        }
        converter.toFileWithoutExtension(Paths.get(markdown_outputDir));
    }
}
