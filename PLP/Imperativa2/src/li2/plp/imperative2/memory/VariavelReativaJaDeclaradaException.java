package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;

public class VariavelReativaJaDeclaradaException extends IdentificadorJaDeclaradoException {
  
	public VariavelReativaJaDeclaradaException(Id id) {
		super("Variável Reativa " + id + " já declarada.");
	}

}
