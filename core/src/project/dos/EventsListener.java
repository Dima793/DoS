package project.dos;

public interface EventsListener<T> {
    void listenEvent(int eventCase, T data);
}
