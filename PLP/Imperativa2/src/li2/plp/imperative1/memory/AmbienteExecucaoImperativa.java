package li2.plp.imperative1.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative2.memory.ObservadorException;

public interface AmbienteExecucaoImperativa extends AmbienteExecucao {

	public void changeValor(Id idArg, Valor valorId)
			 throws VariavelNaoDeclaradaException, ObservadorException;

	public Valor read() throws EntradaVaziaException;

	public void write(Valor v);

	public ListaValor getSaida();
}
