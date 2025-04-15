package li2.plp.imperative2.observer;

import java.util.List;
import java.util.Map;

import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

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

  public void notifySubscribers(String eventType, AmbienteExecucaoImperativa2 amb) {
    List<Subscriber> subscribers = listeners.get(eventType);
        for (Subscriber listener : subscribers) {
            listener.update(eventType, amb);
        }
  }

}
