package io.github.vennarshulytz.jsonviewext.annotation;

import io.github.vennarshulytz.jsonviewext.config.JsonViewExtRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 JsonViewExt 功能注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JsonViewExtRegistrar.class)
public @interface EnableJsonViewExt {

    /**
     * 规则缓存最大容量
     */
    long cacheMaximumSize() default 1024;
}

