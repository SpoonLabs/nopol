package fr.inria.lille.commons.utils;

public class Lockable<T> {

	public Lockable(T object) {
		setLocked(false);
		this.object = object;
	}
	
	public void waitForLock() {
		while (! acquiresLock()) { 
			/* busy-waiting */
		}
	}
	
	public void unlock() {
		setLocked(false);
	}
	
	public T object() {
		return object;
	}
	
	private boolean acquiresLock() {
		boolean acquiredLock = false;
		synchronized (this) {
			if (! locked()) {
				setLocked(true);
			}
			acquiredLock = true;
		}
		return acquiredLock;
	}
	
	private boolean locked() {
		return locked;
	}
	
	private void setLocked(boolean value) {
		locked = value;
	}
	
	private T object;
	private boolean locked;
}
