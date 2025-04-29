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
	private List<DefReativo> publishers;
	private Id idReativoCorrente;

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

	public DefReativo getReativo(Id idArg) {
		try {
			return this.contextoReativo.get(idArg);
		} catch (VariavelNaoDeclaradaException e) {
			return null;
		}
	}

	private void iniciaReativo(Id idArg) {
		this.publishers = new ArrayList<>();
		this.idReativoCorrente = idArg;
	}

	public void iniciaMapReativo(Id idArg, Subscriber s) {
		DefReativo reativo = new DefReativo(s);
		try {
			this.contextoReativo.map(idArg, reativo);
			iniciaReativo(idArg);
		} catch (Exception e) {
			throw new VariavelReativaJaDeclaradaException(idArg);
		}
	}

	public void iniciaAtribuicaoReativa(Id idArg) {
		DefReativo reativo = getReativo(idArg);
		if (reativo != null) {
			iniciaReativo(idArg);
		}
	}

	public void terminaComandoReativo(Id idArg) {
		DefReativo reativo = getReativo(idArg);
		if (reativo != null) {
			Subscriber s = reativo.getSubscriber();
			for (DefReativo p : publishers) {
				p.subscribe(s);
			}
			publishers = null;
			idReativoCorrente = null;
		}
	}

	/**
	 * Limpa todas as dependências de um reativo.
	 * 
	 */
	public void limpaDependencias(Id idArg) {
		DefReativo reativo = getReativo(idArg);
		if (reativo != null) {
			Stack<HashMap<Id,DefReativo>> auxStack = new Stack<HashMap<Id,DefReativo>>();
			Stack<HashMap<Id,DefReativo>> stack = this.contextoReativo.getPilha();
			
			while (!stack.empty()) {
				HashMap<Id,DefReativo> aux = stack.pop();
				auxStack.push(aux);
				
				Subscriber s = reativo.getSubscriber();
				for (DefReativo react : aux.values()) {
					react.unsubscribe(s); // se desinscreve de todos que ele depende
				}
			}
			while (!auxStack.empty()) {
				stack.push(auxStack.pop());
			}
		}
	}

	@Override
	public Valor get(Id idArg) throws VariavelNaoDeclaradaException {
		if (publishers != null) {
			DefReativo reativo = getReativo(idArg);
			// é reativo e não é igual ao reativo corrente (variável reativa não pode depender dela mesma)
			if (reativo != null && !idArg.equals(this.idReativoCorrente)) publishers.add(reativo);
		}
		return super.get(idArg);
	}

	@Override
	public void changeValor(Id idArg, Valor valorId) throws VariavelNaoDeclaradaException {
		// id se atualiza
		super.changeValor(idArg, valorId);
		// se ele for reativo, atualiza as variáveis reativas que dependem dele
		DefReativo reativo = getReativo(idArg);
		if (reativo != null) reativo.notifySubscribers(this);
	}

}
