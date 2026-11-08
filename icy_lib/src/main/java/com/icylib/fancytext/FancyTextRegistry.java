package com.icylib.fancytext;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** 艺术字样式注册表：ID 不区分大小写，首次访问自动加载内置预设。 */
public final class FancyTextRegistry {

    private static final Map<String, FancyTextStyle> STYLES = new ConcurrentHashMap<>();
    private static volatile boolean bootstrapped;

    private FancyTextRegistry() {
    }

    public static void register(String id, FancyTextStyle style) {
        STYLES.put(id.toLowerCase(Locale.ROOT), style);
    }

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
