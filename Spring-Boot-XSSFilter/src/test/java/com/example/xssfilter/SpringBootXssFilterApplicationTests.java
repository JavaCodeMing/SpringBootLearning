package com.example.xssfilter;

import com.example.xssfilter.utils.JsoupUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootXssFilterApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void test1() {
        String s = "<p>\n" +
                "\t<a href=\"www.test.xhtml\">test</a>\n" +
                "\t<a title=\"哈哈哈\" href=\"/aaaa.bbv.com\" href1=\"www.baidu.com\" href2=\"www.baidu.com\" onclick=\"click()\"></a>\n" +
                "\t<script>ss</script>\n" +
                "\t<img script=\"xxx\" onclick=function src=\"https://www.xxx.png\" title=\"\" width=\"100%\" alt=\"\"/>\n" +
                "\t<br/>\n" +
                "</p>\n" +
                "<p>电饭锅进口量的说法</p>\n" +
                "<p>————————</p>\n" +
                "<p><span style=\"text-decoration: line-through;\">大幅度发</span></p>\n" +
                "<p><em>sd</em></p>\n" +
                "<p><em><span style=\"text-decoration: underline;\">dsf</span></em></p>\n" +
                "<p><em><span style=\"border: 1px solid rgb(0, 0, 0);\">撒地方</span></em></p>\n" +
                "<p><span style=\"color: rgb(255, 0, 0);\">似懂非懂</span><br/></p>\n" +
                "<p><span style=\"color: rgb(255, 0, 0);\"><strong>撒地方</strong></span></p>\n" +
                "<p><span style=\"color: rgb(221, 217, 195);\"><br/></span></p>\n" +
                "<p style=\"text-align: center;\"><span style=\"color: rgb(0, 0, 0); font-size: 20px;\">撒旦法</span></p>\n" +
                "<p><br/></p>";
        System.out.println(JsoupUtil.clean(s));
    }
}
