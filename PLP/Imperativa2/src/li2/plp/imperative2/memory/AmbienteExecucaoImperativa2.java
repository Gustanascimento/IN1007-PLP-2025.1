package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.declaration.DefProcedimento;
import li2.plp.imperative2.observer.Subscriber;

public interface AmbienteExecucaoImperativa2 extends AmbienteExecucaoImperativa {

	public void mapProcedimento(Id idArg, DefProcedimento procedimentoId)
			throws ProcedimentoJaDeclaradoException;

	public DefProcedimento getProcedimento(Id idArg)
			throws ProcedimentoNaoDeclaradoException;

	public void iniciaMapReativo(Id idArg, Expressao exp, Subscriber s);

	public void iniciaAtribuicaoReativa(Id idArg, Expressao exp, Subscriber s);

	public void terminaComandoReativo(Id idArg) throws CicloDeDependenciaException;

	public void limpaDependencias(Id idArg);

	public void atualizaVariavelReativa(Id id) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException, ObservadorException;

}
