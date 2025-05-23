package li2.plp.imperative2.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.Contexto;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.ContextoExecucaoImperativa;
import li2.plp.imperative1.memory.ListaValor;
import li2.plp.imperative2.declaration.DeclaracaoVariavelReativa;
import li2.plp.imperative2.declaration.DefProcedimento;
import li2.plp.imperative2.declaration.DefReativo;
import li2.plp.imperative2.observer.Subscriber;
import li2.plp.imperative2.util.LoggerConfig;

public class ContextoExecucaoImperativa2 extends ContextoExecucaoImperativa
		implements AmbienteExecucaoImperativa2 {
	private static final Logger logger = LoggerConfig.getLogger();

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

	private DefReativo getReativo(Id idArg) {
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

	public void iniciaMapReativo(Id idArg, Expressao exp, Subscriber s) {
		DefReativo reativo = new DefReativo(s, exp);
		try {
			this.contextoReativo.map(idArg, reativo);
			iniciaReativo(idArg);
		} catch (Exception e) {
			throw new VariavelReativaJaDeclaradaException(idArg);
		}
	}

	public void iniciaAtribuicaoReativa(Id idArg, Expressao exp, Subscriber s) {
    DefReativo defGlobal = getReativo(idArg);
    if (defGlobal != null) {
        HashMap<Id, DefReativo> escopoAtual = contextoReativo.getPilha().peek();
        DefReativo defLocal = escopoAtual.get(idArg);
        if (defLocal == null) {
            DefReativo defNovo = new DefReativo(s, exp);
            defNovo.copiarSubscribersDe(defGlobal);
            escopoAtual.put(idArg, defNovo);
            defLocal = defNovo;
        } else {
            defLocal.setSubscriber(s);
            defLocal.setExpressao(exp);
        }
        iniciaReativo(idArg);
    }
}

	public void terminaComandoReativo(Id idArg) throws CicloDeDependenciaException {
		DefReativo reativo = getReativo(idArg);
		if (reativo != null) {
			Subscriber s = reativo.getSubscriber();
			for (DefReativo p : publishers) {
				p.subscribe(s);
			}
			publishers = null;
			idReativoCorrente = null;
		}

    verificaCiclosDeDependencia();
	}

	public void atualizaVariavelReativa(Id id) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException, ObservadorException {
		DefReativo def = getReativo(id);
    if (def != null) {
        Expressao expAtual = def.getExpressao();
        changeValor(id, expAtual.avaliar(this));
    }
	}

	/**
	 * Limpa todas as dependências de um reativo.
	 * 
	 */
	public void limpaDependencias(Id idArg) {
    // Pega o escopo atual (topo da pilha)
    HashMap<Id, DefReativo> escopoAtual = contextoReativo.getPilha().peek();
    DefReativo reativo = escopoAtual.get(idArg);
    if (reativo == null) return;

    Subscriber s = reativo.getSubscriber();
    if (s == null) return;

    // Remove o subscriber apenas dos DefReativos do escopo atual
    for (DefReativo react : escopoAtual.values()) {
        react.unsubscribe(s);
    }
}

	@Override
	public Valor get(Id idArg) throws VariavelNaoDeclaradaException {
		if (publishers != null) {
			DefReativo reativo = getReativo(idArg);
			// é reativo e não é igual ao reativo corrente (variável reativa não pode depender dela mesma) e não depende já desse reativo
			if (reativo != null && !idArg.equals(this.idReativoCorrente) && !publishers.contains(reativo)) publishers.add(reativo);
		}
		return super.get(idArg);
	}

	@Override
	public void changeValor(Id idArg, Valor valorId) throws VariavelNaoDeclaradaException, ObservadorException {
		// id se atualiza
		super.changeValor(idArg, valorId);
		// se ele for reativo, atualiza as variáveis reativas que dependem dele
		DefReativo reativo = getReativo(idArg);
		if (reativo != null) reativo.notifySubscribers(this);
	}

  /**
   * Verifica se há ciclos de dependências entre variáveis reativas.
   * 
   * @throws CicloDeDependenciaException se um ciclo for detectado.
   */
  public void verificaCiclosDeDependencia() throws CicloDeDependenciaException {
    HashMap<Id, Boolean> visitados = new HashMap<>();
    HashMap<Id, Boolean> pilhaRecursiva = new HashMap<>();

    // Só verifica o escopo atual (topo da pilha)
    if (!contextoReativo.getPilha().isEmpty()) {
        HashMap<Id, DefReativo> mapa = contextoReativo.getPilha().peek();
        for (Id id : mapa.keySet()) {
            List<Id> caminho = new ArrayList<>();
            if (detectaCiclo(id, visitados, pilhaRecursiva, caminho)) {
                Collections.reverse(caminho);
                throw new CicloDeDependenciaException("Ciclo de dependência detectado: " + caminhoToString(caminho));
            }
        }
    }
	}

  /**
   * Função auxiliar para detectar ciclos usando busca em profundidade (DFS).
   */
  private boolean detectaCiclo(Id id, HashMap<Id, Boolean> visitados, HashMap<Id, Boolean> pilhaRecursiva, List<Id> caminho) {
    // Se a variável já está na pilha de recursão, há um ciclo
    if (pilhaRecursiva.getOrDefault(id, false)) {
			caminho.add(id); // Adiciona o nó atual ao caminho para exibir o ciclo completo
			return true; // Ciclo detectado
    }

    // Se já foi visitado e não está na pilha de recursão, não há ciclo
    if (visitados.getOrDefault(id, false)) {
			return false;
    }

    // Marca a variável como visitada e adiciona à pilha de recursão
    visitados.put(id, true);
    pilhaRecursiva.put(id, true);
    caminho.add(id); // Adiciona o nó atual ao caminho

    // Obtém o DefReativo associado à variável
    DefReativo reativo = getReativo(id);
    if (reativo != null) {
			// Itera sobre os dependentes (subscribers) da variável
			for (Subscriber subscriber : reativo.getSubscribers("update")) {
				if (subscriber instanceof DeclaracaoVariavelReativa) {
					DeclaracaoVariavelReativa variavelReativa = (DeclaracaoVariavelReativa) subscriber;
					if (detectaCiclo(variavelReativa.getId(), visitados, pilhaRecursiva, caminho)) {
						return true; // Ciclo detectado
					}
				}
			}
    }

    // Remove a variável da pilha de recursão
    pilhaRecursiva.put(id, false);
    caminho.remove(caminho.size() - 1); // Remove o nó do caminho ao sair da recursão
    return false;
}

  /**
   * Converte o caminho do ciclo em uma string legível.
   */
  private String caminhoToString(List<Id> caminho) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < caminho.size(); i++) {
      sb.append(caminho.get(i));
      if (i < caminho.size() - 1) {
        sb.append(" -> ");
      }
    }
    return sb.toString();
  }

}
