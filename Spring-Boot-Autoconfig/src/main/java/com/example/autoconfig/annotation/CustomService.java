package com.example.autoconfig.annotation;

import org.springframework.stereotype.Service;
import java.lang.annotation.*;

/**
 * @author dengzhiming
 * @date 2020/02/03
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface CustomService {
    String value() default "";
}
