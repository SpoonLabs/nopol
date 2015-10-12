package xxl.java.support;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static xxl.java.library.ClassLibrary.hasMethod;
import static xxl.java.library.StringLibrary.join;

public abstract class GlobalToggle {

    protected abstract void reset();

    protected abstract String globallyAccessibleName();

    public GlobalToggle() {
        disable();
        lock = new Lockable<GlobalToggle>(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected String invocationMessageFor(String methodName) {
        return invocationMessageFor(methodName, (List) asList(), (List) asList());
    }

    protected String invocationMessageFor(String methodName, List<? extends Class<?>> parameterTypes, List<String> parameterNames) {
        assertTrue("Nonexistent method: " + methodName, hasMethod(getClass(), methodName, parameterTypes));
        String message = format("%s(%s)", methodName, join(parameterNames, ','));
        return globallyAccessibleName() + '.' + message;
    }

    public String enableInvocation() {
        return invocationMessageFor("enable");
    }

    public String disableInvocation() {
        return invocationMessageFor("disable");
    }

    public String isEnabledInquiry() {
        return invocationMessageFor("isEnabled");
    }

    public String isDisabledInquiry() {
        return invocationMessageFor("isDisabled");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !isEnabled();
    }

    public boolean enable() {
        reset();
        return setEnabled(true);
    }

    public boolean disable() {
        return setEnabled(false);
    }

    protected void acquireToggle() {
        lock().acquire();
    }

    protected void releaseToggle() {
        lock().release();
    }

    private Lockable<GlobalToggle> lock() {
        return lock;
    }

    private boolean setEnabled(boolean value) {
        boolean oldValue = enabled;
        enabled = value;
        return oldValue;
    }

    private boolean enabled;
    private Lockable<GlobalToggle> lock;
}
