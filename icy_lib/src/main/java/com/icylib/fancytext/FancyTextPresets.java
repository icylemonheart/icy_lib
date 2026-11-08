package com.icylib.fancytext;

/** 内置艺术字预设注册表，ID 均以 icy_lib: 前缀。 */
public final class FancyTextPresets {

    public static final String SKY_STRIPE = "icy_lib:sky_stripe";
    public static final String ICE_GLIMMER = "icy_lib:ice_glimmer";
    public static final String FROST_WAVE = "icy_lib:frost_wave";
    public static final String FLAME_BURST = "icy_lib:flame_burst";
    public static final String EMBER_PULSE = "icy_lib:ember_pulse";
    public static final String HOLY_RADIANCE = "icy_lib:holy_radiance";
    public static final String SHADOW_FLICKER = "icy_lib:shadow_flicker";
    public static final String VOID_WHISPER = "icy_lib:void_whisper";
    public static final String NATURE_SPRING = "icy_lib:nature_spring";
    public static final String LIFE_WEAVE = "icy_lib:life_weave";
    public static final String AURORA_FLOW = "icy_lib:aurora_flow";
    public static final String SUNSET_FLOW = "icy_lib:sunset_flow";
    public static final String SPECTRUM_FLOW = "icy_lib:spectrum_flow";
    public static final String TOXIC_DRIP = "icy_lib:toxic_drip";
    public static final String THUNDER_STRIKE = "icy_lib:thunder_strike";
    public static final String BLOOD_MOON = "icy_lib:blood_moon";
    public static final String SAKURA_DRIFT = "icy_lib:sakura_drift";
    public static final String OCEAN_TIDE = "icy_lib:ocean_tide";
    public static final String RAINBOW_FLOW = "icy_lib:rainbow_flow";
    public static final String CRYSTAL_PULSE = "icy_lib:crystal_pulse";
    public static final String GOLDEN_FLOW = "icy_lib:golden_flow";
    public static final String GHOST_BREATH = "icy_lib:ghost_breath";

    public static final String LIFE_THREAD = "icy_lib:life_thread";
    public static final String PRICE_GRADIENT = "icy_lib:price_gradient";
    public static final String JADE_PULSE = "icy_lib:jade_pulse";
    public static final String LIFE_TIDE = "icy_lib:life_tide";
    public static final String PUNISHING_CRIMSON = "icy_lib:punishing_crimson";
    public static final String HYPER_SPACE = "icy_lib:hyper_space";
    public static final String NEON_PULSE = "icy_lib:neon_pulse";
    public static final String STARLIGHT = "icy_lib:starlight";
    public static final String DREAM_WEAVE = "icy_lib:dream_weave";
    public static final String EMERALD_GLOW = "icy_lib:emerald_glow";
    public static final String AMETHYST = "icy_lib:amethyst";
    public static final String MOONLIGHT = "icy_lib:moonlight";
    public static final String ABYSS = "icy_lib:abyss";
    public static final String LAVA_CORE = "icy_lib:lava_core";
    public static final String BUTTERFLY_DANCE = "icy_lib:butterfly_dance";
    public static final String STARRY_NIGHT = "icy_lib:starry_night";

    public static final String YEARNING_VESSEL = "icy_lib:yearning_vessel";
    public static final String CRIMSON_GOLD = "icy_lib:crimson_gold";
    public static final String HUNGER_GRADIENT = "icy_lib:hunger_gradient";
    public static final String AZURE_GOLD = "icy_lib:azure_gold";

    public static final String SOUL_MOON_NEST = "icy_lib:soul_moon_nest";
    public static final String SOUL_PULSE = "icy_lib:soul_pulse";
    public static final String SOUL_PRICE = "icy_lib:soul_price";
    public static final String SOUL_TIDE = "icy_lib:soul_tide";

    public static final String WORLD_DEVOURER = "icy_lib:world_devourer";
    public static final String DEVOURER_MAW = "icy_lib:devourer_maw";
    public static final String DEVOURER_TIDE = "icy_lib:devourer_tide";
    public static final String APOCALYPSE_GRADIENT = "icy_lib:apocalypse_gradient";

    public static final String SAMSARA_END = "icy_lib:samsara_end";
    public static final String SAMSARA_TICK = "icy_lib:samsara_tick";
    public static final String SAMSARA_PRICE = "icy_lib:samsara_price";
    public static final String SAMSARA_TIDE = "icy_lib:samsara_tide";

    private static boolean registered;

    private FancyTextPresets() {
    }

    public static synchronized void registerAll() {
        if (registered) {
            return;
        }
        registered = true;

        register(SKY_STRIPE, FancyTextStyle.builder(0x55AAFF)
                .effect(FancyTextEffects.stripeSweep(0xFFDD44, 14.0F, 8.0F, 35.0F, true))
                .effect(FancyTextEffects.floatEffect(2.0F, 0.8, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
                .tooltipBackground(TooltipBackground.solid(0xF0102040, 0x504488FF, 0x502244AA))
                .build());

        register(ICE_GLIMMER, FancyTextStyle.builder(0x7FD4FF)
                .effect(FancyTextEffects.stripeSweep(0xFFFFFF, 10.0F, 7.0F, 18.0F, true))
                .effect(FancyTextEffects.wave(1.0F, 0.5, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF0102030, 0x507FD4FF, 0x504499CC))
                .build());

        register(FROST_WAVE, FancyTextStyle.builder(0xA0E8FF)
                .effect(FancyTextEffects.stripeSweep(0xE0FFFF, 12.0F, 6.0F, 24.0F, true))
                .effect(FancyTextEffects.wave(1.5F, 0.6, 20.0F))
                .tooltipBackground(TooltipBackground.solid(0xF0152530, 0x50A0E8FF, 0x506699BB))
                .build());

        register(FLAME_BURST, FancyTextStyle.builder(0xFF4500)
                .effect(FancyTextEffects.flowingGradient(new int[]{0xFF2200, 0xFF6600, 0xFFAA00, 0xFFDD44}, 3.5, 0.7))
                .effect(FancyTextEffects.wave(1.2F, 1.2, 14.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF02A0A00, 0xF0150500, 0x50FF7700, 0x50883300))
                .build());

        register(EMBER_PULSE, FancyTextStyle.builder(0x8B0000)
                .effect(FancyTextEffects.pulse(0xFF5522, 0.7, 0.15F))
                .tooltipBackground(TooltipBackground.solid(0xF0200808, 0x50FF5522, 0x50882211))
                .build());

        register(HOLY_RADIANCE, FancyTextStyle.builder(0xFFD700)
                .effect(FancyTextEffects.pulse(0xFFFFEE, 0.5, 0.1F))
                .effect(FancyTextEffects.wave(1.5F, 0.4, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF02A2005, 0x50FFD700, 0x50AA7700))
                .build());

        register(SHADOW_FLICKER, FancyTextStyle.builder(0x550088)
                .effect(FancyTextEffects.flicker(0x1144L, 0.5F, 1.5))
                .effect(FancyTextEffects.stripeSweep(0xAA00AA, 12.0F, 6.0F, 12.0F, false))
                .tooltipBackground(TooltipBackground.solid(0xF01A0A2A, 0x50AA00AA, 0x50550055))
                .build());

        register(VOID_WHISPER, FancyTextStyle.builder(0x330033)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x441155, 0x7733AA, 0x330033}, 3.5, 0.4))
                .effect(FancyTextEffects.alphaPulse(0.4, 0.55F))
                .tooltipBackground(TooltipBackground.starrySky(StarfieldConfig.COOL_WHITE))
                .build());

        register(NATURE_SPRING, FancyTextStyle.builder(0x33AA33)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x2FBF4F, 0x7CFC5A, 0xA6F25E}, 3.0, 0.45))
                .effect(FancyTextEffects.wave(1.0F, 0.5, 22.0F))
                .tooltipBackground(TooltipBackground.solid(0xF00A1F0A, 0x5033AA33, 0x50117711))
                .build());

        register(LIFE_WEAVE, FancyTextStyle.builder(0x2FBF4F)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x2FBF4F, 0xA6F25E, 0x54E0C8}, 3.0, 0.6))
                .effect(FancyTextEffects.wave(1.0F, 0.5, 22.0F))
                .tooltipBackground(TooltipBackground.solid(0xF00A1F1A, 0x5033CC88, 0x50118855))
                .build());

        register(AURORA_FLOW, FancyTextStyle.builder(0x40E0D0)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x40E0D0, 0x7FFFAA, 0x5599FF}, 3.5, 0.5))
                .effect(FancyTextEffects.wave(1.0F, 0.45, 24.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF00A1F1F, 0xF00A1020, 0x5040E0D0, 0x502288AA))
                .build());

        register(SUNSET_FLOW, FancyTextStyle.builder(0xFF7043)
                .effect(FancyTextEffects.flowingGradient(new int[]{0xFF7043, 0xFFB74D, 0xE65FB0}, 3.5, 0.5))
                .effect(FancyTextEffects.wave(1.0F, 0.45, 24.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF02A1505, 0xF01A0A1A, 0x50FF7043, 0x50CC4488))
                .build());

        register(SPECTRUM_FLOW, FancyTextStyle.builder(0xFF3860)
                .effect(FancyTextEffects.flowingGradient(new int[]{
                        0xFF3860, 0xFFB020, 0xFFE84A, 0x37D67A, 0x2F9BFF, 0x9B5CFF}, 4.0, 0.7))
                .effect(FancyTextEffects.wave(1.2F, 0.6, 20.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF01A0A1A, 0xF00A1A1A, 0x50FF4488, 0x504488FF))
                .build());

        register(TOXIC_DRIP, FancyTextStyle.builder(0x55AA00)
                .effect(FancyTextEffects.stripeSweep(0xAAFF00, 12.0F, 6.0F, 20.0F, true))
                .effect(FancyTextEffects.wave(2.0F, 0.9, 16.0F))
                .tooltipBackground(TooltipBackground.solid(0xF0102005, 0x50AAFF00, 0x50558800))
                .build());

        register(THUNDER_STRIKE, FancyTextStyle.builder(0xFFEE44)
                .effect(FancyTextEffects.stripeSweep(0xFFFFFF, 14.0F, 5.0F, 60.0F, true))
                .effect(FancyTextEffects.flicker(777L, 0.35F, 2.0))
                .tooltipBackground(TooltipBackground.solid(0xF02A2505, 0x50FFEE44, 0x50AA8800))
                .build());

        register(BLOOD_MOON, FancyTextStyle.builder(0xAA0000)
                .effect(FancyTextEffects.pulse(0xFF2222, 0.35, 0.2F))
                .effect(FancyTextEffects.wave(1.0F, 0.3, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF01F0505, 0x50FF2222, 0x50661111))
                .build());

        register(SAKURA_DRIFT, FancyTextStyle.builder(0xFF99CC)
                .effect(FancyTextEffects.stripeSweep(0xFFFFFF, 12.0F, 8.0F, 14.0F, true))
                .effect(FancyTextEffects.floatEffect(2.0F, 0.45, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
                .tooltipBackground(TooltipBackground.solid(0xF02A0A1A, 0x50FF99CC, 0x50AA4477))
                .build());

        register(OCEAN_TIDE, FancyTextStyle.builder(0x0066CC)
                .effect(FancyTextEffects.gradient(new int[]{0x003377, 0x0077BB, 0x00AACC}, false, 0.0))
                .effect(FancyTextEffects.stripeSweep(0x66EEFF, 14.0F, 8.0F, 22.0F, true))
                .tooltipBackground(TooltipBackground.gradient(0xF0050A2A, 0xF002051A, 0x5066EEFF, 0x502277AA))
                .build());

        register(RAINBOW_FLOW, FancyTextStyle.builder(0xFFFFFF)
                .effect(FancyTextEffects.rainbow(0.85F, 1.0F, 0.08, 1.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF01A0A1A, 0xF00A1A1A, 0x50FF44AA, 0x5044AAFF))
                .build());

        register(CRYSTAL_PULSE, FancyTextStyle.builder(0x55EEEE)
                .effect(FancyTextEffects.pulse(0xFFFFFF, 0.6, 0.1F))
                .effect(FancyTextEffects.alphaPulse(0.8, 0.75F))
                .tooltipBackground(TooltipBackground.solid(0xF0052020, 0x5055EEEE, 0x50229999))
                .build());

        register(GOLDEN_FLOW, FancyTextStyle.builder(0xAA6600)
                .effect(FancyTextEffects.stripeSweep(0xFFCC33, 14.0F, 7.0F, 26.0F, true))
                .effect(FancyTextEffects.floatEffect(1.5F, 0.7, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
                .tooltipBackground(TooltipBackground.solid(0xF01F1500, 0x50FFCC33, 0x50AA7700))
                .build());

        register(GHOST_BREATH, FancyTextStyle.builder(0xCCDDEE)
                .effect(FancyTextEffects.alphaPulse(0.3, 0.35F))
                .effect(FancyTextEffects.wave(1.0F, 0.25, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF01A1F25, 0x50CCDDEE, 0x50556677))
                .build());

        register(PUNISHING_CRIMSON, FancyTextStyle.builder(0x991111)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x880000, 0xFF2222, 0xFF6633}, 3.0, 0.65))
                .effect(FancyTextEffects.wave(1.2F, 1.0, 16.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF02A0505, 0xF0150202, 0x50CC3333, 0x50661111))
                .build());

        register(HYPER_SPACE, FancyTextStyle.builder(0x00BBFF)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x00BBFF, 0x88FFFF, 0x4488FF}, 3.0, 0.7))
                .effect(FancyTextEffects.wave(0.8F, 0.8, 20.0F))
                .tooltipBackground(TooltipBackground.solid(0xF0051525, 0x5044CCFF, 0x502277CC))
                .build());

        register(NEON_PULSE, FancyTextStyle.builder(0xFF2288)
                .effect(FancyTextEffects.flowingGradient(new int[]{0xFF2288, 0x00EEFF, 0xAA44FF}, 3.0, 0.75))
                .effect(FancyTextEffects.flicker(0x9999L, 0.25F, 2.5))
                .tooltipBackground(TooltipBackground.gradient(0xF01A0A1A, 0xF00A1A1A, 0x50FF44AA, 0x5044CCFF))
                .build());

        register(STARLIGHT, FancyTextStyle.builder(0x88BBFF)
                .effect(FancyTextEffects.stripeSweep(0xFFFFFF, 10.0F, 7.0F, 16.0F, true))
                .effect(FancyTextEffects.floatEffect(1.0F, 0.5, FancyTextEffects.FloatMode.ALL))
                .tooltipBackground(TooltipBackground.solid(0xF00A1025, 0x5088BBFF, 0x504477AA))
                .build());

        register(DREAM_WEAVE, FancyTextStyle.builder(0xFF88CC)
                .effect(FancyTextEffects.flowingGradient(new int[]{0xFF88CC, 0xBB88FF, 0x88CCFF}, 3.0, 0.5))
                .effect(FancyTextEffects.wave(1.2F, 0.55, 22.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF0200A20, 0xF0100A25, 0x50FF88CC, 0x508888FF))
                .build());

        register(EMERALD_GLOW, FancyTextStyle.builder(0x00AA66)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x008855, 0x22DD88, 0x66FFCC}, 3.0, 0.55))
                .effect(FancyTextEffects.wave(1.0F, 0.5, 22.0F))
                .tooltipBackground(TooltipBackground.solid(0xF0051F15, 0x5022DD88, 0x50118855))
                .build());

        register(AMETHYST, FancyTextStyle.builder(0x7733CC)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x6622AA, 0xAA66FF, 0xDDAAFF}, 3.0, 0.5))
                .effect(FancyTextEffects.wave(1.0F, 0.45, 24.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF0150525, 0xF00A0520, 0x50AA66FF, 0x506633AA))
                .build());

        register(MOONLIGHT, FancyTextStyle.builder(0xAACCFF)
                .effect(FancyTextEffects.pulse(0xFFFFEE, 0.35, 0.1F))
                .effect(FancyTextEffects.floatEffect(1.0F, 0.3, FancyTextEffects.FloatMode.ALL))
                .tooltipBackground(TooltipBackground.solid(0xF0101828, 0x50AACCFF, 0x505577AA))
                .build());

        register(ABYSS, FancyTextStyle.builder(0x3344AA)
                .effect(FancyTextEffects.flowingGradient(new int[]{0x334499, 0x6655CC, 0x4477BB}, 3.5, 0.4))
                .effect(FancyTextEffects.alphaPulse(0.35, 0.6F))
                .tooltipBackground(TooltipBackground.starrySky(StarfieldConfig.DEFAULT))
                .build());

        register(LAVA_CORE, FancyTextStyle.builder(0xCC3300)
                .effect(FancyTextEffects.flowingGradient(new int[]{0xDD2200, 0xFF7700, 0xFFCC33}, 3.0, 0.6))
                .effect(FancyTextEffects.wave(1.5F, 1.3, 12.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF02A0A00, 0xF0150500, 0x50FF6611, 0x50882200))
                .build());

        register(BUTTERFLY_DANCE, FancyTextStyle.builder(0xDD66FF)
                .effect(FancyTextEffects.flowingGradient(new int[]{0xCC55EE, 0xFF77BB, 0x9966FF}, 3.0, 0.55))
                .effect(FancyTextEffects.wave(1.5F, 0.7, 18.0F))
                .tooltipBackground(TooltipBackground.gradient(0xF0200520, 0xF0150515, 0x50DD66FF, 0x50FF77BB))
                .build());

        register(STARRY_NIGHT, FancyTextStyle.builder(0xDDEEFF)
                .effect(FancyTextEffects.flowingGradient(new int[]{0xFFFFFF, 0x88CCFF, 0x4488FF}, 3.0, 0.5))
                .effect(FancyTextEffects.wave(0.8F, 0.4, 24.0F))
                .tooltipBackground(TooltipBackground.starrySky(StarfieldConfig.COOL_WHITE))
                .build());

        register(LIFE_THREAD, FancyTextStyle.builder(0x2FBF4F)
                .effect(FancyTextEffects.stripeSweep(0xFFD700, 12.0F, 8.0F, 22.0F, true))
                .effect(FancyTextEffects.floatEffect(1.5F, 0.5, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
                .tooltipBackground(TooltipBackground.solid(0xF00A1F0A, 0x50FFD700, 0x502FBF4F))
                .build());

        register(PRICE_GRADIENT, FancyTextStyle.builder(0x1F7A2E)
                .effect(FancyTextEffects.gradient(new int[]{0x1F7A2E, 0x3BA351, 0x1F7A2E}, false, 0.0))
                .tooltipBackground(TooltipBackground.solid(0xF0051505, 0x501F7A2E, 0x500F4518))
                .build());

        register(JADE_PULSE, FancyTextStyle.builder(0x1F8A38)
                .effect(FancyTextEffects.pulse(0x55DD77, 0.35, 0.2F))
                .effect(FancyTextEffects.wave(1.0F, 0.3, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF0051505, 0x5055DD77, 0x501F8A38))
                .build());

        register(LIFE_TIDE, FancyTextStyle.builder(0x33CC55)
                .effect(FancyTextEffects.gradient(new int[]{0x33CC55, 0x54E0C8, 0xA6F25E}, true, 0.08))
                .tooltipBackground(TooltipBackground.gradient(0xF005150A, 0xF0051A15, 0x5054E0C8, 0x5033CC55))
                .build());

        register(YEARNING_VESSEL, FancyTextStyle.builder(0xCC8800)
                .effect(FancyTextEffects.stripeSweep(0xFFD700, 12.0F, 8.0F, 22.0F, true))
                .effect(FancyTextEffects.floatEffect(1.5F, 0.5, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
                .tooltipBackground(TooltipBackground.solid(0xF0140819, 0x50FFD700, 0x50804A00))
                .build());

        register(CRIMSON_GOLD, FancyTextStyle.builder(0xB8541A)
                .effect(FancyTextEffects.pulse(0xFFAA33, 0.35, 0.2F))
                .effect(FancyTextEffects.wave(1.0F, 0.3, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF0140819, 0x50FFD700, 0x50804A00))
                .build());

        register(HUNGER_GRADIENT, FancyTextStyle.builder(0x9C5A1E)
                .effect(FancyTextEffects.gradient(new int[]{0x9C5A1E, 0xC97A2E, 0x9C5A1E}, false, 0.0))
                .tooltipBackground(TooltipBackground.solid(0xF0140819, 0x50FFD700, 0x50804A00))
                .build());

        register(AZURE_GOLD, FancyTextStyle.builder(0x4488CC)
                .effect(FancyTextEffects.gradient(new int[]{0x4466BB, 0x77AAEE, 0x33CCFF}, true, 0.08))
                .tooltipBackground(TooltipBackground.solid(0xF0140819, 0x50FFD700, 0x50804A00))
                .build());

        register(SOUL_MOON_NEST, FancyTextStyle.builder(0x8899DD)
                .effect(FancyTextEffects.stripeSweep(0xEEF4FF, 12.0F, 8.0F, 22.0F, true))
                .effect(FancyTextEffects.floatEffect(1.5F, 0.5, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
                .tooltipBackground(TooltipBackground.solid(0xF00B1026, 0x50AABBEE, 0x504455AA))
                .build());

        register(SOUL_PULSE, FancyTextStyle.builder(0x3D4488)
                .effect(FancyTextEffects.pulse(0x8899DD, 0.35, 0.2F))
                .effect(FancyTextEffects.wave(1.0F, 0.3, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF00B1026, 0x50AABBEE, 0x504455AA))
                .build());

        register(SOUL_PRICE, FancyTextStyle.builder(0x2A2E66)
                .effect(FancyTextEffects.gradient(new int[]{0x2A2E66, 0x465099, 0x2A2E66}, false, 0.0))
                .tooltipBackground(TooltipBackground.solid(0xF00B1026, 0x50AABBEE, 0x504455AA))
                .build());

        register(SOUL_TIDE, FancyTextStyle.builder(0x66AAEE)
                .effect(FancyTextEffects.gradient(new int[]{0x5599DD, 0x99CCFF, 0xAAA0FF}, true, 0.08))
                .tooltipBackground(TooltipBackground.solid(0xF00B1026, 0x50AABBEE, 0x504455AA))
                .build());

        register(WORLD_DEVOURER, FancyTextStyle.builder(0x8B1A1A)
                .effect(FancyTextEffects.stripeSweep(0xFFD700, 12.0F, 8.0F, 20.0F, true))
                .effect(FancyTextEffects.floatEffect(1.5F, 0.5, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
                .tooltipBackground(TooltipBackground.solid(0xF0140505, 0x50FF3333, 0x50641E1E))
                .build());

        register(DEVOURER_MAW, FancyTextStyle.builder(0xB22222)
                .effect(FancyTextEffects.pulse(0xFF6B4A, 0.35, 0.2F))
                .effect(FancyTextEffects.wave(1.0F, 0.3, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF0140505, 0x50FF3333, 0x50641E1E))
                .build());

        register(DEVOURER_TIDE, FancyTextStyle.builder(0xCC4433)
                .effect(FancyTextEffects.gradient(new int[]{0x8B1A1A, 0xEE6644, 0x9A3355}, true, 0.08))
                .tooltipBackground(TooltipBackground.solid(0xF0140505, 0x50FF3333, 0x50641E1E))
                .build());

        register(APOCALYPSE_GRADIENT, FancyTextStyle.builder(0xC84848)
                .effect(FancyTextEffects.gradient(new int[]{0xA83232, 0xE87070, 0xA83232}, false, 0.0))
                .tooltipBackground(TooltipBackground.solid(0xF0140505, 0x50FF3333, 0x50641E1E))
                .build());

        register(SAMSARA_END, FancyTextStyle.builder(0xC9B86A)
                .effect(FancyTextEffects.stripeSweep(0xFFF6DC, 12.0F, 8.0F, 22.0F, true))
                .effect(FancyTextEffects.floatEffect(1.5F, 0.5, FancyTextEffects.FloatMode.HIGHLIGHT_ONLY))
                .tooltipBackground(TooltipBackground.solid(0xF014111A, 0x50C9B86A, 0x50665A88))
                .build());

        register(SAMSARA_TICK, FancyTextStyle.builder(0x8A7530)
                .effect(FancyTextEffects.pulse(0xFFE9A0, 0.35, 0.2F))
                .effect(FancyTextEffects.wave(1.0F, 0.3, 24.0F))
                .tooltipBackground(TooltipBackground.solid(0xF014111A, 0x50C9B86A, 0x50665A88))
                .build());

        register(SAMSARA_PRICE, FancyTextStyle.builder(0x5A5270)
                .effect(FancyTextEffects.gradient(new int[]{0x4A4358, 0x6B6280, 0x4A4358}, false, 0.0))
                .tooltipBackground(TooltipBackground.solid(0xF014111A, 0x50C9B86A, 0x50665A88))
                .build());

        register(SAMSARA_TIDE, FancyTextStyle.builder(0xD9CFA8)
                .effect(FancyTextEffects.gradient(new int[]{0xE8E0C8, 0xC9B86A, 0x8A8298}, true, 0.08))
                .tooltipBackground(TooltipBackground.solid(0xF014111A, 0x50C9B86A, 0x50665A88))
                .build());
    }

    private static void register(String id, FancyTextStyle style) {
        FancyTextRegistry.register(id, style);
    }
}
