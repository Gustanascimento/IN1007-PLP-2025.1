package li2.plp.imperative2.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public class VariavelReativaPublisher extends Publisher {
  protected String eventType = "update";

  public VariavelReativaPublisher() {
    this.listeners = new HashMap<>();    
  }

  @Override
  public List<Subscriber> getSubscribers(String eventType) {
    List<Subscriber> subscribers = listeners.get(eventType);
    if (subscribers == null) {
      subscribers = new ArrayList<>();
      listeners.put(eventType, subscribers);
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("VariavelReativaPublisher [eventType=").append(eventType).append("]\n");
    List<Subscriber> subscribers = getSubscribers(eventType);
    if (subscribers.isEmpty()) {
      sb.append("Nenhum listener registrado.");
    } else {
      sb.append("Listeners registrados:\n");
      for (Subscriber subscriber : subscribers) {
        sb.append(" - ").append(subscriber.toString()).append("\n");
      }
    }
  return sb.toString();
}
}
