package xxl.java.support;

import java.util.Collection;

public interface Observable<T> {

    public void register(Observer<T> observer);

    public void unregister(Observer<T> observer);

    public void notifyObservers(T observable);

    public Collection<Observer<T>> observers();
}
