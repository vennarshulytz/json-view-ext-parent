package io.github.vennarshulytz.jsonviewext.utils;

import io.github.vennarshulytz.jsonviewext.annotation.JsonViewExt;
import io.github.vennarshulytz.jsonviewext.template.JsonViewExtTemplate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * JsonViewExt 注解解析工具类
 *
 * @author vennarshulytz
 * @since 1.2.1
 */
public class JsonViewExtUtils {

    /**
     * 解析 JsonViewExt 注解，收集所有 JsonViewExt 数组
     *
     * @param jsonViewExt 目标 JsonViewExt 注解
     * @return 收集到的 JsonViewExt 数组
     */
    public static JsonViewExt[] resolve(JsonViewExt jsonViewExt) {
        if (jsonViewExt == null) {
            return new JsonViewExt[0];
        }

        List<JsonViewExt> collectedRules = new ArrayList<>();
        // 用于检测死循环：记录已处理过的 template Class（排除默认值）
        Set<Class<? extends JsonViewExtTemplate>> visitedTemplates = new LinkedHashSet<>();

        doResolve(jsonViewExt, collectedRules, visitedTemplates);

        // 按顺序合并所有 ValidationRule 数组
        return mergeRules(collectedRules);
    }

    /**
     * 递归解析 JsonViewExt 注解
     *
     * @param jsonViewExt  当前处理的 JsonViewExt 注解
     * @param collectedRules   按顺序收集的 ValidationRule 数组列表
     * @param visitedTemplates 已访问的 template Class 集合，用于死循环检测
     */
    private static void doResolve(
            JsonViewExt jsonViewExt,
            List<JsonViewExt> collectedRules,
            Set<Class<? extends JsonViewExtTemplate>> visitedTemplates) {

        // 先收集当前 JsonViewExt
        collectedRules.add(jsonViewExt);

        Class<? extends JsonViewExtTemplate> templateClass = jsonViewExt.template();

        // 步骤1：template 是默认值，直接终止递归
        if (JsonViewExtTemplate.class.equals(templateClass)) {
            return;
        }

        // 死循环检测：template 非默认值且已被访问过，则报错
        if (visitedTemplates.contains(templateClass)) {
            throw new IllegalStateException(
                    String.format(
                            "检测到 JsonViewExt 注解的 template 属性存在循环引用，template class: [%s]，" +
                                    "已访问的 template 链路: %s",
                            templateClass.getName(),
                            buildVisitedChain(visitedTemplates, templateClass)
                    )
            );
        }

        // 记录当前 template Class
        visitedTemplates.add(templateClass);

        // 检索 template Class 上是否标记了 JsonViewExt 注解
        JsonViewExt templateAnnotation = templateClass.getAnnotation(JsonViewExt.class);

        // template Class 上没有标记 JsonViewExt，终止递归
        if (templateAnnotation == null) {
            return;
        }

        // 递归处理 template Class 上的 JsonViewExt
        doResolve(templateAnnotation, collectedRules, visitedTemplates);
    }

    /**
     * 合并 JsonViewExt 集合为 JsonViewExt 数组
     *
     * @param collectedRules 按顺序收集的 JsonViewExt 集合列表
     * @return 合并后的 JsonViewExt 数组
     */
    private static JsonViewExt[] mergeRules(List<JsonViewExt> collectedRules) {
        int totalLength = collectedRules.size();

        JsonViewExt[] result = new JsonViewExt[totalLength];

        int length = totalLength - 1;
        for (int i = length; i >= 0; i--) {
            JsonViewExt rule = collectedRules.get(i);
            result[length - i] = rule;
        }
        return result;
    }

    /**
     * 构建已访问 template 链路描述，用于错误提示
     *
     * @param visitedTemplates 已访问的 template Class 集合
     * @param currentTemplate  当前引发死循环的 template Class
     * @return 链路描述字符串
     */
    private static String buildVisitedChain(
            Set<Class<? extends JsonViewExtTemplate>> visitedTemplates,
            Class<? extends JsonViewExtTemplate> currentTemplate) {

        StringBuilder chain = new StringBuilder();
        for (Class<? extends JsonViewExtTemplate> clazz : visitedTemplates) {
            chain.append(clazz.getName()).append(" -> ");
        }
        chain.append(currentTemplate.getName()).append("（循环）");
        return chain.toString();
    }
}
