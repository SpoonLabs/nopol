package xxl.java.support;

import static java.lang.String.format;

public class Lockable<T> {

    public Lockable(T object) {
        this.object = object;
        setLocked(false);
    }

    public void acquire() {
        do { /* nothing */ } while (!acquiresLock());
    }

    public T object() {
        return object;
    }

    private synchronized boolean acquiresLock() {
        boolean acquiredLock = false;
        if (!locked()) {
            setLocked(true);
            acquiredLock = true;
        }
        return acquiredLock;
    }

    public synchronized void release() {
        setLocked(false);
    }

    private void setLocked(boolean locked) {
        this.locked = locked;
    }

    private boolean locked() {
        return locked;
    }

    @Override
    public String toString() {
        return format("Lock[%s]", object().toString());
    }

    private T object;
    private boolean locked;
}
