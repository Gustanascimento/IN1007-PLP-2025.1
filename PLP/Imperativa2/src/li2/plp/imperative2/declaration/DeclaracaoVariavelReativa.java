package li2.plp.imperative2.declaration;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.imperative1.declaration.DeclaracaoVariavel;
import li2.plp.imperative2.observer.Publisher;
import li2.plp.imperative2.observer.Subscriber;

public class DeclaracaoVariavelReativa extends DeclaracaoVariavel implements Subscriber {

  private Publisher pub;

  public DeclaracaoVariavelReativa(Id id, Expressao expressao, Publisher pub) {
    super(id, expressao);
    this.pub = pub;
  }

  @Override
  public void update(String eventType, Object context) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  public void subscribe(Subscriber s, String eventType) {
    pub.subscribe(s, eventType);
  }

  public void unsubscribe(Subscriber s, String eventType) {
    pub.unsubscribe(s, eventType);
  }

  public void notifySubscribers(String eventType, Object context) {
    pub.notifySubscribers(eventType, context);
  }

}
