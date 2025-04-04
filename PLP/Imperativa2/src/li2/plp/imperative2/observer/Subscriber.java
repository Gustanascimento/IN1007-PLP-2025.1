package li2.plp.imperative2.observer;

public interface Subscriber {
  
  public void update(String eventType, Object context);
}
