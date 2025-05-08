package li2.plp.imperative2.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public class VariavelReativaPublisher extends Publisher {
  private String eventType = "update";

  public VariavelReativaPublisher() {
    this.listeners = new HashMap<>();    
  }

  @Override
  List<Subscriber> getSubscribers(String eventType) {
    List<Subscriber> subscribers = listeners.get(eventType);
    if (subscribers == null) {
      subscribers = new ArrayList<>();
    }
    return subscribers;
  }

  public void subscribe(Subscriber s) {
    subscribe(s, eventType);
  }

  public void unsubscribe(Subscriber s) {
    unsubscribe(s, eventType);
  }

  public void notifySubscribers(AmbienteExecucaoImperativa2 amb) {
    notifySubscribers(eventType, amb);
  }
}
