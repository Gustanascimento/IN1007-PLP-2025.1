package li2.plp.imperative2.observer;

import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public interface Subscriber {
  
  public void update(String eventType, AmbienteExecucaoImperativa2 ambiente);
}
