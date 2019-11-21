package com.example.mail.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * Created by dengzhiming on 2019/5/4
 */
@RestController
@RequestMapping("/email")
public class MailController {

    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private TemplateEngine engine;

    @Value("${spring.mail.username}")
    private String from;

    @RequestMapping("sendSimpleEmail")
    public String sendSimpleEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            //message.setTo("zmdeng994@163.com"); // 接收地址
            message.setTo("1206291365@qq.com"); // 接收地址
            message.setSubject("一封简单的邮件");// 标题
            message.setText("使用Spring Boot发送简单邮件。"); // 内容
            javaMailSender.send(message);
            return "发送成功！";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping("sendHtmlEmail")
    public String sendHtmlEmail() {
        MimeMessage message = null;
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo("zmdeng994@163.com");
            //helper.setTo("1206291365@qq.com");
            helper.setSubject("一封HTML格式的邮件");
            // 带HTML格式的内容
            StringBuffer buffer = new StringBuffer("<p style='color:#42b983'>使用Spring Boot发送HTML格式邮件。</p>");
            helper.setText(buffer.toString(), true);
            javaMailSender.send(message);
            return "发送成功！";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping("sendAttachmentsMail")
    public String sendAttachmentsMail() {
        MimeMessage message = null;
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo("zmdeng994@163.com");
            //helper.setTo("1206291365@qq.com");
            helper.setSubject("一封带附件的邮件");
            helper.setText("详情参见附件内容！");
            // 传入附件
            FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/file/项目文档.docx"));
            helper.addAttachment("项目文档.docx", file);
            javaMailSender.send(message);
            return "发送成功！";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping("sendInlineMail")
    public String sendInlineMail() {
        MimeMessage message = null;
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo("zmdeng994@163.com");
            //helper.setTo("1206291365@qq.com");
            helper.setSubject("一封带静态资源的邮件");
            helper.setText("<html><body>很优秀：<img src='cid:img'/></body></html>", true);
            // 传入附件
            FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/img/优秀.jpg"));
            // helper.addInline("img", file);中的img和图片标签里cid后的名称相对应
            helper.addInline("img", file);
            javaMailSender.send(message);
            return "发送成功！";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping("sendTemplateEmail")
    public String sendTemplateEmail(String code) {
        MimeMessage message = null;
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo("zmdeng994@163.com");
            //helper.setTo("1206291365@qq.com");
            helper.setSubject("邮件摸板测试");
            // 处理邮件模板
            Context context = new Context();
            context.setVariable("code", code);
            String template = engine.process("emailTemplate", context);
            helper.setText(template, true);
            javaMailSender.send(message);
            return "发送成功！";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
