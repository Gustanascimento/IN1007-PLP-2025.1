package li2.plp.imperative2.declaration;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.declaration.Declaracao;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative1.memory.ErroTipoEntradaException;
import li2.plp.imperative2.command.ListaExpressao;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.memory.CicloDeDependenciaException;
import li2.plp.imperative2.observer.Subscriber;
import java.util.UUID;

public class DeclaracaoObservador extends Declaracao implements Subscriber {

  private Id id;
  private ListaExpressao expressoes; // possíveis variáveis reativas
	private DefProcedimento procedimento;

  public DeclaracaoObservador(ListaExpressao expressoes, DefProcedimento procedimento) {
		super();
    // Nome único aleatório para identificar esse observador
		this.id = new Id(UUID.randomUUID().toString());
    this.expressoes = expressoes;
		this.procedimento = procedimento;
	}

  @Override
  public AmbienteExecucaoImperativa elabora(AmbienteExecucaoImperativa ambiente)
      throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException, CicloDeDependenciaException {
    AmbienteExecucaoImperativa2 amb2 = (AmbienteExecucaoImperativa2) ambiente;
    amb2.iniciaMapReativo(id, this);
    expressoes.avaliar(ambiente);
    amb2.mapProcedimento(id, procedimento);
    amb2.terminaComandoReativo(id);
    return amb2;
  }

  @Override
  public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
      throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException {
		boolean resposta;

    // mapeia o tipo do procedimento
    ambiente.map(id, procedimento.getTipo());

    ambiente.incrementa();
    // checa o tipo do comando do procedimento
    resposta = procedimento.getComando().checaTipo(ambiente);
    ambiente.restaura();

    // tipo do procedimento e tipo da lista de expressões correto
    return resposta && expressoes.checaTipo(ambiente);
  }

  public void executar(AmbienteExecucaoImperativa amb) {
		AmbienteExecucaoImperativa2 ambiente = (AmbienteExecucaoImperativa2) amb;
    ambiente.incrementa();
    try {
      ambiente = (AmbienteExecucaoImperativa2) procedimento.getComando().executar(ambiente);
    } catch (IdentificadorJaDeclaradoException | IdentificadorNaoDeclaradoException | EntradaVaziaException
        | ErroTipoEntradaException | CicloDeDependenciaException e) {
      e.printStackTrace();
    }
    ambiente.restaura();
  }

  
  @Override
  public void update(String eventType, AmbienteExecucaoImperativa2 ambiente) {
    executar(ambiente);
  }
}