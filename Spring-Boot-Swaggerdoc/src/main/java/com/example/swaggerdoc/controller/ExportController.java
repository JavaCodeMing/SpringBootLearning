package com.example.swaggerdoc.controller;

import com.example.swaggerdoc.util.SwaggerUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/export")
@ApiIgnore
public class ExportController {

  
  @RequestMapping("/ascii")
  public String exportAscii() throws MalformedURLException {
    SwaggerUtils.generateAsciiDocs();
    return "success";
  }
  
  @RequestMapping("/asciiToFile")
  public String asciiToFile() throws MalformedURLException{
    SwaggerUtils.generateAsciiDocsToFile();
    return "success";
  }
  
  @RequestMapping("/markdown")
  public String exportMarkdown() throws MalformedURLException{
    SwaggerUtils.generateMarkdownDocs();
    return "success";
  }
  
  @RequestMapping("/markdownToFile")
  public String exportMarkdownToFile() throws MalformedURLException{
    SwaggerUtils.generateMarkdownDocsToFile();
    return "success";
  }
  
  @RequestMapping("/confluence")
  public String confluence() throws MalformedURLException{
    SwaggerUtils.generateConfluenceDocs();
    return "success";
  }
  
  @RequestMapping("/confluenceToFile")
  public String confluenceToFile() throws MalformedURLException{
    SwaggerUtils.generateConfluenceDocsToFile();
    return "success";
  }
}