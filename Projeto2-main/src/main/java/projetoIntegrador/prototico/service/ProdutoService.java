package projetoIntegrador.prototico.service;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import projetoIntegrador.prototico.model.Produto;
import projetoIntegrador.prototico.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    // Listar todos os produtos
    public List<Produto> listar() {
        return repository.findAll();
    }

    // Buscar produto pelo ID
    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado."
                ));
    }

    // Cadastrar produto
    public Produto cadastrar(Produto produto) {

        validarProduto(produto);

        return repository.save(produto);
    }

    // Atualizar produto
    public Produto atualizar(Long id, Produto produto) {

        Produto produtoExistente = buscarPorId(id);

        validarProduto(produto);

        produtoExistente.setNome(produto.getNome().trim());
        produtoExistente.setPreco(produto.getPreco());
        produtoExistente.setDescricao(produto.getDescricao().trim());
        produtoExistente.setQuantidade(produto.getQuantidade());
        produtoExistente.setDataValidade(produto.getDataValidade());
        produtoExistente.setTipoProduto(produto.getTipoProduto());
        produtoExistente.setImagem(produto.getImagem().trim());

        return repository.save(produtoExistente);
    }

    // Excluir produto
    public void excluir(Long id) {

        Produto produto = buscarPorId(id);

        repository.delete(produto);
    }

    // Validar informações do produto
    private void validarProduto(Produto produto) {

        if (produto.getNome() == null ||
                produto.getNome().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do produto é obrigatório."
            );
        }

        if (produto.getDescricao() == null ||
                produto.getDescricao().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A descrição do produto é obrigatória."
            );
        }

        if (produto.getPreco() == null ||
                !Double.isFinite(produto.getPreco()) ||
                produto.getPreco() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O preço deve ser maior que zero."
            );
        }

        if (produto.getQuantidade() == null ||
                produto.getQuantidade() < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade não pode ser negativa."
            );
        }

        if (produto.getDataValidade() == null ||
                produto.getDataValidade().isBefore(LocalDate.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de validade não pode ser anterior à data atual."
            );
        }

        if (produto.getTipoProduto() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione o tipo do produto."
            );
        }

        validarImagem(produto.getImagem());
    }

    // Validar URL da imagem
    private void validarImagem(String imagem) {

        if (imagem == null || imagem.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A URL da imagem é obrigatória."
            );
        }

        try {
            URI uri = URI.create(imagem.trim());

            String esquema = uri.getScheme();

            if (esquema == null ||
                    !(esquema.equalsIgnoreCase("http") ||
                      esquema.equalsIgnoreCase("https")) ||
                    uri.getHost() == null) {

                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException erro) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe uma URL de imagem válida."
            );
        }
    }
}