package com.example.swaggerdoc.util;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class SwaggerUtils {

    private static final String url = "http://127.0.0.1:8080/v2/api-docs";

    /**
     * 生成AsciiDocs格式文档
     *
     * @throws MalformedURLException
     */
    public static void generateAsciiDocs() throws MalformedURLException {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                //指定了要输出的最终格式:ASCIIDOC
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                //指定输出文档的语言环境
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                //生成文档带接口案例
                .withGeneratedExamples()
                .withoutInlineSchema().build();

        Swagger2MarkupConverter
                //指定了生成静态部署文档的源头配置
                //可以是URL形式,也可以是符合Swagger规范的String类型或者从文件中读取的流
                .from(new URL(url))
                .withConfig(config)
                .build()
                //指定最终生成文件的具体目录位置
                .toFolder(Paths.get("./docs/asciidoc/generated"));
    }

    /**
     * 生成AsciiDocs格式文档,并汇总成一个文件
     *
     * @throws MalformedURLException
     */
    public static void generateAsciiDocsToFile() throws MalformedURLException {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();

        Swagger2MarkupConverter.from(new URL(url))
                .withConfig(config)
                .build()
                //将转换结果输出到一个单一的文件中
                .toFile(Paths.get("./docs/asciidoc/generated/all"));
    }

    /**
     * 生成Markdown格式文档
     *
     * @throws MalformedURLException
     */
    public static void generateMarkdownDocs() throws MalformedURLException {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();

        Swagger2MarkupConverter.from(new URL(url))
                .withConfig(config)
                .build()
                .toFolder(Paths.get("./docs/markdown/generated"));
    }

    /**
     * 生成Markdown格式文档,并汇总成一个文件
     *
     * @throws MalformedURLException
     */
    public static void generateMarkdownDocsToFile() throws MalformedURLException {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();

        Swagger2MarkupConverter.from(new URL(url))
                .withConfig(config)
                .build()
                .toFile(Paths.get("./docs/markdown/generated/all"));
    }

    /**
     * 生成Confluence格式文档
     *
     * @throws MalformedURLException
     */
    public static void generateConfluenceDocs() throws MalformedURLException {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.CONFLUENCE_MARKUP)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();

        Swagger2MarkupConverter.from(new URL(url))
                .withConfig(config)
                .build()
                .toFolder(Paths.get("./docs/confluence/generated"));
    }

    /**
     * 生成Confluence格式文档,并汇总成一个文件
     *
     * @throws MalformedURLException
     */
    public static void generateConfluenceDocsToFile() throws MalformedURLException {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.CONFLUENCE_MARKUP)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();

        Swagger2MarkupConverter.from(new URL(url))
                .withConfig(config)
                .build()
                .toFile(Paths.get("./docs/confluence/generated/all"));
    }

}