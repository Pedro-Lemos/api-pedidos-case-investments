package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web;

public enum CodigoErro {
    PEM("PEM"), // PedidoEmAndamentoException
    PND("PND"), // ProdutoNaoDisponivelException
    PNE("PNE"), // ProdutoNaoEncontradoException ou PedidoNaoEcnontradoException
    PIE("PIE"), // PedidosInativosException
    DPI("DPI"), // DadosProdutoInconsistentesException
    SER("SI"), // ServicoIndisponivel
    VLD("VLD"); // ValidacaoInvalida

    private final String codigo;

    CodigoErro(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}