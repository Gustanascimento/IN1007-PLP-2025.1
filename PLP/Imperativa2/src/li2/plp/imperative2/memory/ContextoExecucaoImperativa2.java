package li2.plp.imperative2.memory;

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

	@Override
	public void map(Id idArg, Valor valorId) {
		DefReativo reativo = new DefReativo();
		super.map(idArg, valorId);
		try {
			this.contextoReativo.map(idArg, reativo);
		} catch (Exception e) {
			throw new VariavelReativaJaDeclaradaException(idArg);
		}
		
	}

	public Valor get(Id idArg, Subscriber s) {
		try {
			DefReativo reativo = this.contextoReativo.get(idArg);
			if (s != null) reativo.subscribe(s);
		} catch (VariavelNaoDeclaradaException e) {
			throw new VariavelReativaNaoDeclaradaException(idArg);
		}
		return super.get(idArg);
	}

	@Override
	public void changeValor(Id idArg, Valor valorId) throws VariavelNaoDeclaradaException {
		super.changeValor(idArg, valorId);
		DefReativo reativo = this.contextoReativo.get(idArg);
		reativo.notifySubscribers(this);
	}

}
