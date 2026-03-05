package io.github.vennarshulytz.jsonviewext.sensitive.impl;

import io.github.vennarshulytz.jsonviewext.sensitive.SensitiveType;

/**
 * 手机号脱敏处理器
 * 保留前3位和后4位，中间用*号替换
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class PhoneType implements SensitiveType {

    private static final int PREFIX_LENGTH = 3;
    private static final int SUFFIX_LENGTH = 4;

    @Override
    public String desensitize(String value) {
        return desensitize(value, PREFIX_LENGTH, SUFFIX_LENGTH);
    }

    @Override
    public String getTypeName() {
        return "PHONE";
    }
}
