package xxl.java.support;

public abstract class GlobalToggle {

	protected abstract void reset();
	
	protected abstract String globallyAccessibleName();
	
	public GlobalToggle() {
		disable();
		lock = new Lockable<GlobalToggle>(this);
	}
	
	public String enableInvocation() {
		return globallyAccessibleName() + ".enable()";
	}
	
	public String disableInvocation() {
		return globallyAccessibleName() + ".disable()";
	}
	
	public String isEnabledInquiry() {
		return globallyAccessibleName() + ".isEnabled()";
	}
	
	public String isDisabledInquiry() {
		return globallyAccessibleName() + ".isDisabled()";
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isDisabled() { 
		return ! isEnabled();
	}
	
	public boolean enable() {
		reset();
		return setEnabled(true);
	}
	
	public boolean disable() {
		return setEnabled(false);
	}
	
	protected long thread() {
		return Thread.currentThread().getId();
	}
	
	protected void requestToggle() {
		lock().waitForLock();
	}
	
	protected void freeToggle() {
		lock().unlock();
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
