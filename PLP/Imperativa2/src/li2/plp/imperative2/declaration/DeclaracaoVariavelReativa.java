package li2.plp.imperative2.declaration;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.declaration.DeclaracaoVariavel;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.observer.Subscriber;

public class DeclaracaoVariavelReativa extends DeclaracaoVariavel implements Subscriber {

  public DeclaracaoVariavelReativa(Id id, Expressao expressao) {
    super(id, expressao);
  }

  @Override
  public void update(String eventType, AmbienteExecucaoImperativa2 amb) {
    amb.changeValor(getId(), getExpressao().avaliar(amb));
  }

  /**
	 * Cria um mapeamento do identificador para o valor da express�o desta
	 * declara��o no AmbienteExecucao
	 * 
	 * @param ambiente
	 *            o ambiente que contem o mapeamento entre identificadores e
	 *            valores.
	 * 
	 * @return o ambiente modificado pela inicializa��o da vari�vel.
	 */
	@Override
	public AmbienteExecucaoImperativa elabora(
			AmbienteExecucaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException {
		AmbienteExecucaoImperativa2 amb2 = (AmbienteExecucaoImperativa2) ambiente;
		amb2.iniciaMapReativo(getId(), this);
		amb2.map(getId(), getExpressao().avaliar(amb2));
		amb2.terminaMapReativo(getId());
		return amb2;
	}

	/**
	 * Vai ter que verificar dependências cíclicas
	 * 
	 * @param ambiente
	 *            o ambiente que contem o mapeamento entre identificadores e
	 *            seus tipos.
	 * 
	 * @return <code>true</code> se os tipos da declara��o s�o v�lidos;
	 *         <code>false</code> caso contrario.
	 * 
	 */
	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException {
		boolean result = getExpressao().checaTipo(ambiente);
		if (result) {
			ambiente.map(getId(), getExpressao().getTipo(ambiente));
		}
		return result;
	}

}
