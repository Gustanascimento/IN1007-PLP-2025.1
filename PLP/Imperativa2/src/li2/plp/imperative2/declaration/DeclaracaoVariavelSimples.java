package li2.plp.imperative2.declaration;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.declaration.DeclaracaoVariavel;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public class DeclaracaoVariavelSimples extends DeclaracaoVariavel {

  public DeclaracaoVariavelSimples(Id id, Expressao expressao) {
    super(id, expressao);
  }
  
  @Override
	public AmbienteExecucaoImperativa elabora(
			AmbienteExecucaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException {
		((AmbienteExecucaoImperativa2) ambiente).mapReativo(getId(), getExpressao().avaliar(ambiente), null);
		return ambiente;
	}
  
}
