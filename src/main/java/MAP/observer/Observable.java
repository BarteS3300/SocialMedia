package MAP.observer;

import java.util.ArrayList;
import java.util.List;

public interface Observable<E extends Observer> {
    void addObserver(E e);
    void removeObserver(E e);

    void notifyObservers();
}
