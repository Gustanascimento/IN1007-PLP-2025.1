package li2.plp.imperative2.declaration;

import java.util.List;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.imperative2.observer.Subscriber;
import li2.plp.imperative2.observer.VariavelReativaPublisher;

public class DefReativo extends VariavelReativaPublisher {

  private Subscriber s;
  private Expressao expressao;

  public DefReativo(Subscriber s, Expressao exp) {
    super();
    this.s = s;
    this.expressao = exp;
  }

  public Subscriber getSubscriber() {
    return this.s;
  }

  public void setSubscriber(Subscriber s) {
    this.s = s;
  }

  public Expressao getExpressao() {
    return expressao;
}

  public void setExpressao(Expressao expressao) {
    this.expressao = expressao;
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
