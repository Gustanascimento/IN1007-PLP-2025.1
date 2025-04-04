package li2.plp.imperative2.observer;

import java.util.List;
import java.util.Map;

public abstract class Publisher {
  
  Map<String, List<Subscriber>> listeners;

  public void subscribe(Subscriber s, String eventType) {
    List<Subscriber> subscribers = listeners.get(eventType);
    subscribers.add(s);
  }

  public void unsubscribe(Subscriber s, String eventType) {
    List<Subscriber> subscribers = listeners.get(eventType);
    subscribers.remove(s);
  }

  public void notifySubscribers(String eventType, Object context) {
    List<Subscriber> subscribers = listeners.get(eventType);
        for (Subscriber listener : subscribers) {
            listener.update(eventType, context);
        }
  }

}
