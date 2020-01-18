package com.example.jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBootJasyptApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
     void test1(){
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        //设置密钥
        encryptor.setPassword("password");
        //设置加密算法
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        //加密信息
        String encryptedText = encryptor.encrypt("conan");
        System.out.println("encryptedText: " + encryptedText);
        //解密
        String decryptedText = encryptor.decrypt(encryptedText);
        System.out.println("decryptedText: " + decryptedText);
    }

}
