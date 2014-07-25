package fr.inria.lille.commons.classes;

public abstract class GlobalToggle {

	public abstract void reset();
	
	protected abstract String instanceName();
	
	public GlobalToggle() {
		disable();
	}
	
	public String isEnabledInquiry() {
		return globallyAccessibleName() + ".isEnabled()";
	}
	
	public String globallyAccessibleName() {
		return getClass().getName() + "." + instanceName();
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
	
	private boolean setEnabled(boolean value) {
		boolean oldValue = enabled;
		enabled = value;
		return oldValue;
	}
	
	private boolean enabled;
}
