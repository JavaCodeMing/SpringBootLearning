package com.example.xssfilter.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * Xss过滤工具
 * Created by dengzhiming on 2019/5/24
 */
public class JsoupUtil {

    //根据jsoup内置常用白名单，构建白名单对象
    private static final Whitelist WHITELIST = Whitelist.relaxed();

    //配置过滤化参数,不对代码进行格式化
    private static final Document.OutputSettings OUTPUT_SETTINGS = new Document.OutputSettings().prettyPrint(false);

    //设置自定义的标签和属性
    static {
        /*
         * addTags(java.lang.String...): 添加标签
         * addAttributes(java.lang.String, java.lang.String...): 往指定标签中添加属性
         * addEnforcedAttribute(java.lang.String, java.lang.String, java.lang.String): 往指定标签指定属性添加值
         * addProtocols(java.lang.String, java.lang.String, java.lang.String...): 往指定标签指定属性添加协议
         * preserveRelativeLinks(): 是否保留元素的URL属性中的相对链接，或将它们转换为绝对链接,默认为false
         *                          为false时将会把baseUri和元素的URL属性拼接起来
         */
        WHITELIST.addAttributes(":all", "style");
        WHITELIST.preserveRelativeLinks(true);
    }

    public static String clean(String content) {
        /* clean(String bodyHtml, String baseUri, Whitelist whitelist, OutputSettings outputSettings)
         * baseUri ,非空
         * 如果baseUri为空字符串或者不符合Http://xx类似的协议开头,属性中的URL链接将会被删除,如<a href='xxx'/>会变成<a/>
         * 如果WHITELIST.preserveRelativeLinks(false),会将baseUri和属性中的URL链接进行拼接
         */
        return Jsoup.clean(content, "", WHITELIST, OUTPUT_SETTINGS);
    }

}
