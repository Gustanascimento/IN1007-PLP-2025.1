package li2.plp.imperative1.command;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.memory.CicloDeDependenciaException;
import li2.plp.imperative2.memory.ObservadorException;
import li2.plp.imperative2.observer.Subscriber;

public class Atribuicao implements Comando, Subscriber {

	private Id id;

	private Expressao expressao;

	public Atribuicao(Id id, Expressao expressao) {
		this.id = id;
		this.expressao = expressao;
	}

	/**
	 * Executa a atribui��o.
	 * 
	 * @param ambiente
	 *            o ambiente que contem o mapeamento entre identificadores e
	 *            valores.
	 * 
	 * @return o ambiente modificado pela execu��o da atribui��o.
	 * @throws CicloDeDependenciaException 
	 * @throws ObservadorException 
	 * 
	 */
	public AmbienteExecucaoImperativa executar(
			AmbienteExecucaoImperativa ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException, CicloDeDependenciaException, ObservadorException {
		AmbienteExecucaoImperativa2 amb2 = (AmbienteExecucaoImperativa2) ambiente;
		// como a variável vai receber uma nova expressão, se ela for reativa, limpa as dependências dela
		amb2.limpaDependencias(id);
		amb2.iniciaAtribuicaoReativa(id, expressao, this);
		Valor expressaoAvaliada = expressao.avaliar(amb2);
		amb2.terminaComandoReativo(id);
		amb2.changeValor(id, expressaoAvaliada);
		return amb2;
	}

	/**
	 * Um comando de atribui��o est� bem tipado, se o tipo do identificador � o
	 * mesmo da express�o. O tipo de um identificador � determinado pelo tipo da
	 * express�o que o inicializou (na declara��o).
	 * 
	 * @param ambiente
	 *            o ambiente que contem o mapeamento entre identificadores e
	 *            valores.
	 * 
	 * @return <code>true</code> se os tipos da atribui��o s�o v�lidos;
	 *         <code>false</code> caso contrario.
	 * 
	 */
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return expressao.checaTipo(ambiente)
				&& id.getTipo(ambiente).eIgual(expressao.getTipo(ambiente));
	}

	@Override
	public void update(String eventType, AmbienteExecucaoImperativa2 ambiente) throws ObservadorException {
		ambiente.atualizaVariavelReativa(id);
	}

}
