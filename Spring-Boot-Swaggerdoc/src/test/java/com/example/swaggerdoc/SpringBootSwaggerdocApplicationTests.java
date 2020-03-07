package com.example.swaggerdoc;

import com.example.swaggerdoc.util.SwaggerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;

@SpringBootTest
public class SpringBootSwaggerdocApplicationTests {

    //生成AsciiDocs格式文档
    @Test
    public void exportAscii() throws MalformedURLException {
        SwaggerUtils.generateAsciiDocs();
    }

    //生成AsciiDocs格式文档,并汇总成一个文件
    @Test
    public void asciiToFile() throws MalformedURLException{
        SwaggerUtils.generateAsciiDocsToFile();
    }

    //生成Markdown格式文档
    @Test
    public void exportMarkdown() throws MalformedURLException{
        SwaggerUtils.generateMarkdownDocs();
    }

    //生成Markdown格式文档,并汇总成一个文件
    @Test
    public void exportMarkdownToFile() throws MalformedURLException{
        SwaggerUtils.generateMarkdownDocsToFile();
    }

    //生成Confluence格式文档
    @Test
    public void confluence() throws MalformedURLException{
        SwaggerUtils.generateConfluenceDocs();
    }

    //生成Confluence格式文档,并汇总成一个文件
    @Test
    public void confluenceToFile() throws MalformedURLException{
        SwaggerUtils.generateConfluenceDocsToFile();
    }
}
