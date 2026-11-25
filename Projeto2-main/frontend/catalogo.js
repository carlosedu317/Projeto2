const API_URL = "http://localhost:8080/api/produtos";

const listaDoces = document.getElementById("listaDoces");
const listaSalgados = document.getElementById("listaSalgados");
const pesquisaProduto = document.getElementById("pesquisaProduto");

let produtosDisponiveis = [];

function formatarPreco(preco) {
    return Number(preco).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

// Carregar produtos do backend
async function carregarProdutos() {
    try {
        const resposta = await fetch(API_URL);

        if (!resposta.ok) {
            throw new Error("Erro ao buscar produtos");
        }

        const produtos = await resposta.json();

        produtosDisponiveis = produtos.filter(produto =>
            Number(produto.quantidade) > 0
        );

        exibirProdutos();

    } catch (erro) {
        console.error(erro);

        listaDoces.textContent = "Erro ao carregar doces.";
        listaSalgados.textContent = "Erro ao carregar salgados.";
    }
}

// Criar card do produto
function criarItemProduto(produto) {

    const item = document.createElement("div");
    item.classList.add("produto-item");

    // Imagem do produto
    const imagem = document.createElement("img");

    imagem.src = produto.imagem ||
        "https://placehold.co/400x300?text=Sem+Imagem";

    imagem.alt = produto.nome;

    imagem.classList.add("imagem-produto");

    imagem.onerror = function () {
        this.src = "https://placehold.co/400x300?text=Imagem+Indisponivel";
    };

    // Nome
    const titulo = document.createElement("h3");
    titulo.textContent = produto.nome;

    // Descrição
    const descricao = document.createElement("p");
    descricao.textContent = produto.descricao;

    // Preço
    const preco = document.createElement("p");
    preco.classList.add("produto-preco");
    preco.textContent = formatarPreco(produto.preco);

    // Estoque
    const estoque = document.createElement("p");
    estoque.textContent = "Disponíveis: " + produto.quantidade;

    // Botão adicionar ao carrinho
    const botao = document.createElement("button");

    botao.textContent = "Adicionar ao carrinho";
    botao.classList.add("btn-adicionar");

    botao.addEventListener("click", async () => {
        // Usa a função do carrinho.js para salvar no banco pelo Spring Boot.
        await adicionarProduto({
            id: produto.id,
            nome: produto.nome,
            preco: Number(produto.preco)
        });
    });

    // Montar card
    item.append(
        imagem,
        titulo,
        descricao,
        preco,
        estoque,
        botao
    );

    return item;
}

// Exibir produtos separados por categoria
function exibirProdutos() {

    const pesquisa = pesquisaProduto.value
        .toLowerCase()
        .trim();

    const doces = produtosDisponiveis.filter(produto =>
        produto.tipoProduto === "DOCE" &&
        produto.nome.toLowerCase().includes(pesquisa)
    );

    const salgados = produtosDisponiveis.filter(produto =>
        produto.tipoProduto === "SALGADO" &&
        produto.nome.toLowerCase().includes(pesquisa)
    );

    listaDoces.innerHTML = "<h3>Doces</h3>";
    listaSalgados.innerHTML = "<h3>Salgados</h3>";

    if (doces.length === 0) {
        listaDoces.textContent = "Nenhum doce disponível.";
    } else {
        doces.forEach(produto => {
            listaDoces.appendChild(criarItemProduto(produto));
        });
    }

    if (salgados.length === 0) {
        listaSalgados.textContent = "Nenhum salgado disponível.";
    } else {
        salgados.forEach(produto => {
            listaSalgados.appendChild(criarItemProduto(produto));
        });
    }
}

// Pesquisa de produtos
pesquisaProduto.addEventListener("input", exibirProdutos);

// Inicializar catálogo
carregarProdutos();
