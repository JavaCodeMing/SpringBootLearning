package com.example.elasticsearch.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.elasticsearch.domain.BookDto;
import com.example.elasticsearch.domain.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author dengzhiming
 * @date 2020/5/1 13:09
 */
@Slf4j
@RestController
@RequestMapping("/low")
public class LowLevelRestController {
    private static Pattern PATTERN = Pattern.compile("\\s*|\t|\r|\n");
    @Autowired
    private RestClient restClient;
    /**
     * 同步执行HTTP请求
     *
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     * @throws IOException 异常
     */
    @GetMapping("/es")
    public ResponseBean getEsInfo() throws IOException {
        Request request = new Request("GET", "/");
        // performRequest是同步的，将阻塞调用线程并在请求成功时返回Response，如果失败则抛出异常
        Response response = restClient.performRequest(request);
        // 获取请求行
        RequestLine requestLine = response.getRequestLine();
        // 获取host
        HttpHost host = response.getHost();
        // 获取状态码
        int statusCode = response.getStatusLine().getStatusCode();
        // 获取响应头
        Header[] headers = response.getHeaders();
        // 获取响应体
        String responseBody = EntityUtils.toString(response.getEntity());
        return new ResponseBean(HttpStatus.OK.value(), "查询成功", JSON.parseObject(responseBody));
    }

    /**
     * 异步执行HTTP请求
     *
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     */
    @GetMapping("/es/async")
    public ResponseBean asynchronous() {
        Request request = new Request("GET", "/");
        restClient.performRequestAsync(request, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                log.info("异步执行HTTP请求并成功");
            }
            @Override
            public void onFailure(Exception exception) {
                log.info("异步执行HTTP请求并失败");
            }
        });
        return new ResponseBean(HttpStatus.OK.value(), "异步请求中", null);
    }

    /**
     * 分词分页查询列表
     *
     * @param page
     * @param rows
     * @param keyword 查询条件
     * @return com.example.common.ResponseBean
     */
    @GetMapping("/book")
    public ResponseBean getBookList(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer rows,
                                    String keyword) {
        Request request = new Request("POST", "/_search");
        // 添加Json返回优化
        request.addParameter("pretty", "true");
        // 拼接查询Json
        IndexRequest indexRequest = new IndexRequest();
        XContentBuilder builder = null;
        Response response = null;
        String responseBody = null;
        try {
            builder = JsonXContent.contentBuilder()
                    .startObject()
                        .startObject("query")
                            .startObject("multi_match")
                            .field("query", keyword)
                            .array("fields", "name", "desc")
                            .endObject()
                        .endObject()
                        .startObject("sort")
                            .startObject("id")
                            .field("order", "desc")
                            .endObject()
                        .endObject()
                    .endObject();
            indexRequest.source(builder);
            // 设置请求体并指定ContentType，如果不指定会乱码
            request.setEntity(new NStringEntity(indexRequest.source().utf8ToString(), ContentType.APPLICATION_JSON));
            // 执行HTTP请求
            response = restClient.performRequest(request);
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return new ResponseBean(HttpStatus.NOT_FOUND.value(), "can not found the book by your id", null);
        }
        return new ResponseBean(HttpStatus.OK.value(), "查询成功", JSON.parseObject(responseBody));
    }

    /**
     * 根据Id获取ES对象
     *
     * @param id id
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     */
    @GetMapping("/book/{id}")
    public ResponseBean getBookById(@PathVariable("id") String id) {
        Request request = new Request("GET", "/book/book/" + id);
        // 添加Json返回优化
        request.addParameter("pretty", "true");
        Response response = null;
        String responseBody = null;
        try {
            // 执行HTTP请求
            response = restClient.performRequest(request);
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return new ResponseBean(HttpStatus.NOT_FOUND.value(), "can not found the book by your id", null);
        }
        return new ResponseBean(HttpStatus.OK.value(), "查询成功", JSON.parseObject(responseBody));
    }

    /**
     * 添加ES对象, Book的ID就是ES中存储的Document的ID，ES的POST和PUT可以看下面这个文章
     * https://blog.csdn.net/z457181562/article/details/93470152
     *
     * @param bookDto
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     * @throws IOException 异常
     */
    @PostMapping("/book")
    public ResponseBean add(@RequestBody BookDto bookDto) throws IOException {
        // Endpoint直接指定为Index/Type的形式
        /*Request request = new Request("POST", new StringBuilder("/book/book/").toString());*/
        // 防重复新增数据
        bookDto.setId(System.currentTimeMillis());
        Request request = new Request("PUT", "/book/book/" + bookDto.getId() + "/_create");
        // 设置其他一些参数比如美化Json
        request.addParameter("pretty", "true");
        // 设置请求体并指定ContentType,如果不指定会乱码
        request.setEntity(new NStringEntity(JSONObject.toJSONString(bookDto), ContentType.APPLICATION_JSON));
        // 发送HTTP请求
        Response response = restClient.performRequest(request);
        // 获取响应体
        String responseBody = EntityUtils.toString(response.getEntity());
        return new ResponseBean(HttpStatus.OK.value(), "添加成功", JSON.parseObject(responseBody));
    }

    /**
     * 根据Id更新Book，ES的POST和PUT可以看下面这个文章
     *
     * https://blog.csdn.net/z457181562/article/details/93470152
     * @param bookDto
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     * @throws IOException 异常
     */
    @PutMapping("/book")
    public ResponseBean update(@RequestBody BookDto bookDto) throws IOException {
        // 构造HTTP请求
        /*Request request = new Request("POST", new StringBuilder("/book/book/")
                .append(bookDto.getId()).append("/_update").toString());*/
        Request request = new Request("PUT", "/book/book/" + bookDto.getId());
        // 设置其他一些参数比如美化Json
        request.addParameter("pretty", "true");
        /*// 将数据丢进去，这里一定要外包一层'doc'，否则内部不能识别
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("doc", new JSONObject(bookDto));*/
        // 设置请求体并指定ContentType，如果不指定会乱码
        request.setEntity(new NStringEntity(JSONObject.toJSONString(bookDto), ContentType.APPLICATION_JSON));
        // 执行HTTP请求
        Response response = restClient.performRequest(request);
        // 获取返回的内容
        String responseBody = EntityUtils.toString(response.getEntity());
        return new ResponseBean(HttpStatus.OK.value(), "更新成功", JSON.parseObject(responseBody));
    }

    /**
     * 使用脚本更新Name
     *
     * @param id
     * @param bookDto
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     * @throws IOException
     */
    @PutMapping("/book/{id}")
    public ResponseEntity<String> update2(@PathVariable("id") String id, @RequestBody BookDto bookDto) throws IOException {
        // 构造HTTP请求
        Request request = new Request("POST", "/book/book/" + id + "/_update");
        // 设置其他一些参数比如美化Json
        request.addParameter("pretty", "true");
        JSONObject jsonObject = new JSONObject();
        // 创建脚本语言，如果是字符变量，必须加单引号
        StringBuilder op1 = new StringBuilder("ctx._source.name=").append("'").append(bookDto.getName()).append("'");
        jsonObject.put("script", op1);
        request.setEntity(new NStringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON));
        // 执行HTTP请求
        Response response = restClient.performRequest(request);
        // 获取返回的内容
        String responseBody = EntityUtils.toString(response.getEntity());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     * @throws IOException
     */
    @DeleteMapping("/book/{id}")
    public ResponseBean deleteById(@PathVariable("id") String id) throws IOException {
        Request request = new Request("DELETE", "/book/book/" + id);
        request.addParameter("pretty", "true");
        // 执行HTTP请求
        Response response = restClient.performRequest(request);
        // 获取结果
        String responseBody = EntityUtils.toString(response.getEntity());
        return new ResponseBean(HttpStatus.OK.value(), "删除成功", JSON.parseObject(responseBody));
    }
}
