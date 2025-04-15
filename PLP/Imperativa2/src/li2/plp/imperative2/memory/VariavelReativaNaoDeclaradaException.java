package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;

public class VariavelReativaNaoDeclaradaException extends IdentificadorNaoDeclaradoException {
  
	public VariavelReativaNaoDeclaradaException(Id id) {
		super("Variável Reativa " + id + " nao declarada.");
	}

}
