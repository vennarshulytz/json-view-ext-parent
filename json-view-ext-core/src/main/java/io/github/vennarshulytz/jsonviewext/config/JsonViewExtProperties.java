package io.github.vennarshulytz.jsonviewext.config;

/**
 * JsonViewExt 配置属性
 *
 * @author vennarshulytz
 * @since 1.2.0
 */
public class JsonViewExtProperties {

    private final long cacheMaximumSize;

    public JsonViewExtProperties(long cacheMaximumSize) {
        if (cacheMaximumSize <= 0) {
            throw new IllegalArgumentException(
                    "cacheMaximumSize must be positive, got: " + cacheMaximumSize);
        }
        this.cacheMaximumSize = cacheMaximumSize;
    }

    public long getCacheMaximumSize() {
        return cacheMaximumSize;
    }

    @Override
    public String toString() {
        return "JsonViewExtProperties{cacheMaximumSize=" + cacheMaximumSize + "}";
    }
}