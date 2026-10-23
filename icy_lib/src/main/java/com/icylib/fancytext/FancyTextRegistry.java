package com.icylib.fancytext;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 艺术字样式注册表。
 *
 * 以字符串 ID（建议 modid:style_name 格式）注册和查找样式，
 * 首次访问时自动加载内置预设（{@link FancyTextPresets}）。
 *
 * 服务端也可安全使用（样式为纯数据 + 数学计算，无客户端类依赖），
 * 可配合 {@link FancyTextRenderer#toLegacyString} 生成带 § 颜色代码的静态文本用于聊天等场景。
 */
public final class FancyTextRegistry {

    private static final Map<String, FancyTextStyle> STYLES = new ConcurrentHashMap<>();
    private static volatile boolean bootstrapped;

    private FancyTextRegistry() {
    }

    /** 注册样式，ID 重复时覆盖 */
    public static void register(String id, FancyTextStyle style) {
        STYLES.put(id.toLowerCase(Locale.ROOT), style);
    }

    /** 按 ID 查找样式，未找到返回 null */
    public static FancyTextStyle get(String id) {
        ensureBootstrapped();
        if (id == null) {
            return null;
        }
        return STYLES.get(id.toLowerCase(Locale.ROOT));
    }

    public static boolean has(String id) {
        return get(id) != null;
    }

    /** 所有已注册的样式 ID（小写） */
    public static Set<String> ids() {
        ensureBootstrapped();
        return Set.copyOf(STYLES.keySet());
    }

    private static void ensureBootstrapped() {
        if (!bootstrapped) {
            bootstrapped = true;
            FancyTextPresets.registerAll();
        }
    }
}
