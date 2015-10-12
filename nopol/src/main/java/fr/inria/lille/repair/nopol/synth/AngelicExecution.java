package fr.inria.lille.repair.nopol.synth;

import java.util.ArrayList;
import java.util.List;

public final class AngelicExecution {

    public static List previousValue = new ArrayList<>();

    public static String invocation(String booleanSnippet) {
        return AngelicExecution.class.getName() + ".angelicValue(" + booleanSnippet + ")";
    }

    public static boolean angelicValue(boolean condition) {
        if (enabled()) {
            previousValue.add(booleanValue());
            return booleanValue();
        }
        previousValue.add(condition);
        return condition;
    }

    public static boolean booleanValue() {
        return booleanValue;
    }

    public static void setBooleanValue(boolean booleanValue) {
        AngelicExecution.booleanValue = booleanValue;
    }

    public static void flip() {
        booleanValue = !booleanValue;
        previousValue = new ArrayList<>();
    }

    public static void enable() {
        enabled = true;
        previousValue = new ArrayList<>();
    }

    public static void disable() {
        enabled = false;
        previousValue = new ArrayList<>();
    }

    private static boolean enabled() {
        return enabled;
    }

    private AngelicExecution() {
    }

    private static boolean enabled = false;
    private static boolean booleanValue = true;
}
