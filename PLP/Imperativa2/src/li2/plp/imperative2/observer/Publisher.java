package li2.plp.imperative2.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public abstract class Publisher {
  
  Map<String, List<Subscriber>> listeners;

  private List<Subscriber> getSubscribers(String eventType) {
    List<Subscriber> subscribers = listeners.get(eventType);
    if (subscribers == null) {
      subscribers = new ArrayList<>();
    }
    return subscribers;
  }

  public void subscribe(Subscriber s, String eventType) {
    getSubscribers(eventType).add(s);
  }

  public void unsubscribe(Subscriber s, String eventType) {
    getSubscribers(eventType).remove(s);
  }

  public void notifySubscribers(String eventType, AmbienteExecucaoImperativa2 amb) {
    List<Subscriber> subscribers = getSubscribers(eventType);
        for (Subscriber subscriber : subscribers) {
            subscriber.update(eventType, amb);
        }
  }

}
