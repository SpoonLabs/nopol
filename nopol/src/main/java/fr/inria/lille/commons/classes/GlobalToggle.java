package fr.inria.lille.commons.classes;

public abstract class GlobalToggle {

	protected abstract void reset();
	
	protected abstract String globallyAccessibleName();
	
	public GlobalToggle() {
		disable();
	}
	
	public String isEnabledInquiry() {
		return globallyAccessibleName() + ".isEnabled()";
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
