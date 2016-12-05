package project.dos;

/**
 * Created by ASUS on 28.11.2016.
 */

public class Pair<T, U> {
    public T first;
    public U second;
    public Pair(T a, U b) {
        first = a;
        second = b;
    }

    @Override
    public int hashCode() {
        return 11500 * (Integer)first + (Integer)second;
    }

    @Override
    public boolean equals(Object a) {
        Pair<T, U> b = (Pair<T, U>) a;
        return (first == b.first) && (second == b.second);
    }
}
