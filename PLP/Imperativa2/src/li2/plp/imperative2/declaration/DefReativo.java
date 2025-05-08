package li2.plp.imperative2.declaration;

import li2.plp.imperative2.observer.Subscriber;
import li2.plp.imperative2.observer.VariavelReativaPublisher;

public class DefReativo extends VariavelReativaPublisher {

  private Subscriber s;

  public DefReativo(Subscriber s) {
    super();
    this.s = s;
  }

  public Subscriber getSubscriber() {
    return this.s;
  }
  
}
