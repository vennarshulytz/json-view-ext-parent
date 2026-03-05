package io.github.vennarshulytz.jsonviewext.sensitive.impl;


import io.github.vennarshulytz.jsonviewext.sensitive.SensitiveType;

/**
 * 邮箱脱敏处理器
 * 保留邮箱前3个字符和@后的域名
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class EmailType implements SensitiveType {

    private static final int PREFIX_LENGTH = 3;

    @Override
    public String desensitize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        int atIndex = value.indexOf('@');
        if (atIndex <= 0) {
            return value;
        }
        if (atIndex <= PREFIX_LENGTH) {
            return value;
        }

        return desensitize(value, PREFIX_LENGTH, value.length() - atIndex);
    }

    @Override
    public String getTypeName() {
        return "EMAIL";
    }
}
