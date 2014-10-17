package xxl.java.support;

import static java.lang.String.format;

public class Lockable<T> {

	public Lockable(T object) {
		this.object = object;
		setLocked(false);
	}
	
	public void acquire() {
		while (! acquiresLock()) { 
			/* busy-waiting */
		}
	}
	
	public void release() {
		setLocked(false);
	}
	
	public T object() {
		return object;
	}
	
	private boolean acquiresLock() {
		boolean acquiredLock = false;
		synchronized (this) {
			if (! locked) {
				setLocked(true);
				acquiredLock = true;
			}
		}
		return acquiredLock;
	}
	
	private void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	@Override
	public String toString() {
		return format("Lock[%s]", object().toString());
	}
	
	private T object;
	private boolean locked;
}
