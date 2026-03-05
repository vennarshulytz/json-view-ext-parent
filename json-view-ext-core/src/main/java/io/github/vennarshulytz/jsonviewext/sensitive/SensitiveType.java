package io.github.vennarshulytz.jsonviewext.sensitive;

import static io.github.vennarshulytz.jsonviewext.constant.DesensitizationConstants.MASK_CHAR;

/**
 * 脱敏类型接口
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public interface SensitiveType {

    /**
     * 对敏感数据进行脱敏处理
     *
     * @param value 原始值
     * @return 脱敏后的值
     */
    String desensitize(String value);


    /**
     * 字符串脱敏处理
     *
     * @param original 原始字符串
     * @param prefixLen 保留前缀位数
     * @param suffixLen 保留后缀位数
     * @param maskChar 脱敏字符
     * @return 脱敏后的字符串
     * @throws IllegalArgumentException 参数非法时抛出
     */
    default String desensitize(String original, int prefixLen, int suffixLen, char maskChar) {
        // 参数校验
        if (original == null) {
            return null;
        }

        if (original.isEmpty()) {
            return original;
        }

        if (prefixLen < 0) {
            throw new IllegalArgumentException("前缀位数不能为负数");
        }

        if (suffixLen < 0) {
            throw new IllegalArgumentException("后缀位数不能为负数");
        }

        int length = original.length();
        int totalKeepLen = prefixLen + suffixLen;

        // 如果保留位数大于等于原字符串长度，直接返回原字符串
        if (totalKeepLen >= length) {
            return original;
        }

        // 计算需要脱敏的字符数量
        int maskLen = length - totalKeepLen;

        StringBuilder result = new StringBuilder(length);

        // 追加前缀
        if (prefixLen > 0) {
            result.append(original, 0, prefixLen);
        }

        // 追加脱敏字符
        for (int i = 0; i < maskLen; i++) {
            result.append(maskChar);
        }

        // 追加后缀
        if (suffixLen > 0) {
            result.append(original, length - suffixLen, length);
        }

        return result.toString();
    }


    /**
     * 字符串脱敏处理（使用默认脱敏字符 *）
     *
     * @param original 原始字符串
     * @param prefixLen 保留前缀位数
     * @param suffixLen 保留后缀位数
     * @return 脱敏后的字符串
     */
    default String desensitize(String original, int prefixLen, int suffixLen) {
        return desensitize(original, prefixLen, suffixLen, MASK_CHAR);
    }

    /**
     * 获取脱敏类型名称
     *
     * @return 类型名称
     */
    default String getTypeName() {
        return this.getClass().getSimpleName();
    }
}
