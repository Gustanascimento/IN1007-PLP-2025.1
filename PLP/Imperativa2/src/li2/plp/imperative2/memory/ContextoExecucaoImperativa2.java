package li2.plp.imperative2.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.Contexto;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.ContextoExecucaoImperativa;
import li2.plp.imperative1.memory.ListaValor;
import li2.plp.imperative2.declaration.DefProcedimento;
import li2.plp.imperative2.declaration.DefReativo;
import li2.plp.imperative2.observer.Subscriber;

public class ContextoExecucaoImperativa2 extends ContextoExecucaoImperativa
		implements AmbienteExecucaoImperativa2 {

	/**
	 * O contexto de procedimentos faz as vezes de um contexto de execu��o que
	 * armazena apenas procedimentos.
	 */
	private Contexto<DefProcedimento> contextoProcedimentos;
	private Contexto<DefReativo> contextoReativo;
	private List<Subscriber> subscribers;

	/**
	 * Construtor da classe.
	 */
	public ContextoExecucaoImperativa2(ListaValor entrada) {
		super(entrada);
		contextoProcedimentos = new Contexto<DefProcedimento>();
		contextoReativo = new Contexto<DefReativo>();
	}

	@Override
	public void incrementa() {
		super.incrementa();
		this.contextoProcedimentos.incrementa();
		this.contextoReativo.incrementa();
	}

	@Override
	public void restaura() {
		super.restaura();
		this.contextoProcedimentos.restaura();
		this.contextoReativo.restaura();
	}

	/**
	 * Mapeia o id no procedimento dado.
	 * 
	 * @exception ProcedimentoJaDeclaradoException
	 *                se j� existir um mapeamento do identificador nesta tabela.
	 */
	public void mapProcedimento(Id idArg, DefProcedimento procedimentoId)
			throws ProcedimentoJaDeclaradoException {
		try {
			this.contextoProcedimentos.map(idArg, procedimentoId);
		} catch (VariavelJaDeclaradaException e) {
			throw new ProcedimentoJaDeclaradoException(idArg);
		}

	}

	/**
	 * Retorna o procedimento mapeado ao id dado.
	 * 
	 * @exception ProcedimentoNaoDeclaradoException
	 *                se n�o existir nenhum procedimento mapeado ao id dado
	 *                nesta tabela.
	 */
	public DefProcedimento getProcedimento(Id idArg)
			throws ProcedimentoNaoDeclaradoException {
		try {
			return this.contextoProcedimentos.get(idArg);
		} catch (VariavelNaoDeclaradaException e) {
			throw new ProcedimentoNaoDeclaradoException(idArg);
		}
	}

	public void iniciaMapReativo(Id idArg, Subscriber s) {
		DefReativo reativo = new DefReativo(s);
		try {
			this.contextoReativo.map(idArg, reativo);
			this.subscribers = new ArrayList<>();
		} catch (Exception e) {
			throw new VariavelReativaJaDeclaradaException(idArg);
		}
	}

	public void terminaMapReativo(Id idArg) {
		DefReativo reativo = this.contextoReativo.get(idArg);
		for (Subscriber s : subscribers) {
			reativo.subscribe(s);
		}
		subscribers = null;
	}

	/**
	 * Limpa todas as dependências de um subscriber.
	 * 
	 */
	public void limpaDependencias(Id idArg) {
		try {
			DefReativo reativo = this.contextoReativo.get(idArg);
			Stack<HashMap<Id,DefReativo>> auxStack = new Stack<HashMap<Id,DefReativo>>();
			Stack<HashMap<Id,DefReativo>> stack = this.contextoReativo.getPilha();
			
			while (!stack.empty()) {
				HashMap<Id,DefReativo> aux = stack.pop();
				auxStack.push(aux);
				
				Subscriber s = reativo.getSubscriber();
				for (DefReativo react : aux.values()) {
					react.unsubscribe(s);
				}
			}
			while (!auxStack.empty()) {
				stack.push(auxStack.pop());
			}
		} catch (VariavelNaoDeclaradaException e) {
			// Variável não é reativa
			return;
		}
	}

	@Override
	public Valor get(Id idArg) throws VariavelNaoDeclaradaException {
		if (subscribers != null) {
			try {
				DefReativo reativo = this.contextoReativo.get(idArg);
				subscribers.add(reativo.getSubscriber());
			} catch (VariavelNaoDeclaradaException e) {
				// Variável não é reativa
			}
		}
		return super.get(idArg);
	}

	// // isso aqui serve pra, quando vc vai pegar o valor de um id, vc se inscreve nesse id, pois vc depende dele.
	// // Não sei se vai dar certo.
	// public Valor get(Id idArg, Id idTarget) {
	// 	try {
	// 		DefReativo reativoTarget = this.contextoReativo.get(idTarget);
	// 		if (reativoTarget != null) {
	// 			Subscriber s = reativoTarget.getSubscriber();
	// 			DefReativo reativo = this.contextoReativo.get(idArg);
	// 			if (s != null) reativo.subscribe(s);
	// 		}
	// 	} catch (VariavelNaoDeclaradaException e) {
	// 		throw new VariavelReativaNaoDeclaradaException(idArg);
	// 	}
	// 	return super.get(idArg);
	// }

	@Override
	public void changeValor(Id idArg, Valor valorId) throws VariavelNaoDeclaradaException {
		// id se atualiza
		super.changeValor(idArg, valorId);
		try {
			// se ele for reativo, atualiza as variáveis reativas que dependem dele
			DefReativo reativo = this.contextoReativo.get(idArg);
			reativo.notifySubscribers(this);
		} catch (VariavelNaoDeclaradaException e) {
			// não é reativo
			return;
		}
	}

}
