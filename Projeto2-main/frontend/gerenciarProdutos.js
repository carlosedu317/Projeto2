
const API_URL = "http://localhost:8080/api/produtos";

// Elementos da página
const lista = document.getElementById("listaProdutos");
const contador = document.getElementById("contador");
const mensagem = document.getElementById("mensagem");

const modalProduto = document.getElementById("modalProduto");
const modalExcluir = document.getElementById("modalExcluir");

const formulario = document.getElementById("formProduto");
const campoImagem = document.getElementById("imagem");
const previewImagem = document.getElementById("previewImagem");

let produtoParaExcluir = null;

// Todas as requisições usam a sessão do usuário
const CONFIG_SESSAO = {
    credentials: "include"
};

// Lê respostas de erro mesmo quando o servidor não retorna JSON
async function obterMensagemErro(resposta, mensagemPadrao) {
    const texto = await resposta.text();

    if (!texto) {
        return `Erro ${resposta.status}: ${mensagemPadrao}`;
    }

    try {
        const erro = JSON.parse(texto);

        return (
            erro.detail ||
            erro.message ||
            erro.error ||
            erro.mensagem ||
            `Erro ${resposta.status}: ${mensagemPadrao}`
        );
    } catch {
        return `Erro ${resposta.status}: ${texto}`;
    }
}

// Mensagens
function mostrarMensagem(texto, tipo = "sucesso") {
    mensagem.textContent = texto;
    mensagem.className = `mensagem visivel ${tipo}`;

    window.setTimeout(() => {
        mensagem.classList.remove("visivel");
    }, 4500);
}

// Prévia da imagem
campoImagem.addEventListener("input", function () {
    const url = this.value.trim();

    if (!url) {
        previewImagem.src = "";
        previewImagem.style.display = "none";
        return;
    }

    previewImagem.onload = () => {
        previewImagem.style.display = "block";
    };

    previewImagem.onerror = () => {
        previewImagem.style.display = "none";
    };

    previewImagem.src = url;
});

// Modais
function abrirModal() {
    modalProduto.classList.add("aberta");
    modalProduto.setAttribute("aria-hidden", "false");

    document.getElementById("nome").focus();
}

function fecharModal() {
    modalProduto.classList.remove("aberta");
    modalProduto.setAttribute("aria-hidden", "true");

    formulario.reset();

    document.getElementById("produtoId").value = "";

    previewImagem.src = "";
    previewImagem.style.display = "none";
}

function abrirModalExcluir(produto) {
    if (!produto) return;

    produtoParaExcluir = produto;

    document.getElementById("nomeExcluir").textContent = produto.nome;

    modalExcluir.classList.add("aberta");
    modalExcluir.setAttribute("aria-hidden", "false");
}

function fecharModalExcluir() {
    modalExcluir.classList.remove("aberta");
    modalExcluir.setAttribute("aria-hidden", "true");

    produtoParaExcluir = null;
}

// Formatação
function formatarPreco(valor) {
    return Number(valor).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

function formatarData(data) {
    if (!data) return "—";

    const [ano, mes, dia] = data.split("-");

    return `${dia}/${mes}/${ano}`;
}

// Proteção dos textos exibidos no HTML
function escaparHTML(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, caractere => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[caractere]);
}

// Carregar produtos
async function carregarProdutos() {
    lista.innerHTML = `
        <tr>
            <td colspan="7" class="vazio">
                Carregando produtos...
            </td>
        </tr>
    `;

    try {
        const resposta = await fetch(API_URL, {
            ...CONFIG_SESSAO
        });

        if (!resposta.ok) {
            throw new Error(
                await obterMensagemErro(
                    resposta,
                    "Não foi possível buscar os produtos."
                )
            );
        }

        const produtos = await resposta.json();

        contador.textContent =
            `${produtos.length} produto(s) cadastrado(s)`;

        if (!produtos.length) {
            lista.innerHTML = `
                <tr>
                    <td colspan="7" class="vazio">
                        Nenhum produto cadastrado ainda.
                    </td>
                </tr>
            `;
            return;
        }

        lista.innerHTML = produtos.map(produto => `
            <tr>
                <td>
                    <img
                        src="${escaparHTML(produto.imagem || "")}"
                        alt="${escaparHTML(produto.nome)}"
                        style="width:65px;height:65px;object-fit:cover;border-radius:8px;"
                        onerror="this.style.display='none'"
                    >
                </td>

                <td>
                    <div class="produto-nome">
                        ${escaparHTML(produto.nome)}
                    </div>

                    <div class="produto-desc">
                        ${escaparHTML(produto.descricao || "")}
                    </div>
                </td>

                <td>
                    <span class="tag">
                        ${produto.tipoProduto === "DOCE" ? "Doce" : "Salgado"}
                    </span>
                </td>

                <td>${formatarPreco(produto.preco)}</td>

                <td>${produto.quantidade ?? 0}</td>

                <td>${formatarData(produto.dataValidade)}</td>

                <td>
                    <div class="acoes">
                        <button
                            class="acao editar"
                            data-editar="${produto.id}">
                            Editar
                        </button>

                        <button
                            class="acao excluir"
                            data-excluir="${produto.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");

        // Eventos de edição
        lista.querySelectorAll("[data-editar]").forEach(botao => {
            botao.addEventListener("click", () => {
                editarProduto(botao.dataset.editar);
            });
        });

        // Eventos de exclusão
        lista.querySelectorAll("[data-excluir]").forEach(botao => {
            botao.addEventListener("click", () => {
                const produto = produtos.find(
                    p => String(p.id) === botao.dataset.excluir
                );

                abrirModalExcluir(produto);
            });
        });

    } catch (erro) {
        lista.innerHTML = `
            <tr>
                <td colspan="7" class="vazio">
                    Não foi possível carregar os produtos.
                    Confira se o Spring Boot está iniciado.
                </td>
            </tr>
        `;

        contador.textContent = "Falha ao carregar";

        mostrarMensagem(erro.message, "erro");
    }
}

// Editar produto
async function editarProduto(id) {
    try {
        const resposta = await fetch(`${API_URL}/${id}`, {
            ...CONFIG_SESSAO
        });

        if (!resposta.ok) {
            throw new Error(
                await obterMensagemErro(
                    resposta,
                    "Não foi possível buscar o produto."
                )
            );
        }

        const produto = await resposta.json();

        document.getElementById("tituloModal").textContent =
            "Editar produto";

        document.getElementById("salvarProduto").textContent =
            "Salvar alterações";

        document.getElementById("produtoId").value = produto.id;

        document.getElementById("nome").value = produto.nome || "";
        document.getElementById("preco").value = produto.preco ?? "";
        document.getElementById("quantidade").value =
            produto.quantidade ?? 0;
        document.getElementById("descricao").value =
            produto.descricao || "";
        document.getElementById("tipoProduto").value =
            produto.tipoProduto || "";
        document.getElementById("dataValidade").value =
            produto.dataValidade || "";

        campoImagem.value = produto.imagem || "";

        if (produto.imagem) {
            previewImagem.src = produto.imagem;
            previewImagem.style.display = "block";
        } else {
            previewImagem.src = "";
            previewImagem.style.display = "none";
        }

        abrirModal();

    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
}

// Abrir cadastro
document.getElementById("abrirNovo").addEventListener("click", () => {
    formulario.reset();

    document.getElementById("produtoId").value = "";

    document.getElementById("tituloModal").textContent =
        "Novo produto";

    document.getElementById("salvarProduto").textContent =
        "Cadastrar produto";

    previewImagem.src = "";
    previewImagem.style.display = "none";

    abrirModal();
});

// Eventos dos modais
document.getElementById("fecharModal")
    .addEventListener("click", fecharModal);

document.getElementById("cancelarModal")
    .addEventListener("click", fecharModal);

document.getElementById("fecharExcluir")
    .addEventListener("click", fecharModalExcluir);

document.getElementById("cancelarExcluir")
    .addEventListener("click", fecharModalExcluir);

document.getElementById("atualizarLista")
    .addEventListener("click", carregarProdutos);

modalProduto.addEventListener("click", evento => {
    if (evento.target === modalProduto) {
        fecharModal();
    }
});

modalExcluir.addEventListener("click", evento => {
    if (evento.target === modalExcluir) {
        fecharModalExcluir();
    }
});

document.addEventListener("keydown", evento => {
    if (evento.key === "Escape") {
        fecharModal();
        fecharModalExcluir();
    }
});

// Cadastrar ou atualizar produto
formulario.addEventListener("submit", async evento => {
    evento.preventDefault();

    const id = document.getElementById("produtoId").value;

    const produto = {
        nome: document.getElementById("nome").value.trim(),
        preco: Number(document.getElementById("preco").value),
        quantidade: Number(document.getElementById("quantidade").value),
        descricao: document.getElementById("descricao").value.trim(),
        tipoProduto: document.getElementById("tipoProduto").value,
        dataValidade: document.getElementById("dataValidade").value,
        imagem: campoImagem.value.trim()
    };

    const botao = document.getElementById("salvarProduto");
    const editando = Boolean(id);

    botao.disabled = true;
    botao.textContent = "Salvando...";

    try {
        const resposta = await fetch(
            id ? `${API_URL}/${id}` : API_URL,
            {
                method: id ? "PUT" : "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                credentials: "include",

                body: JSON.stringify(produto)
            }
        );

        if (!resposta.ok) {
            throw new Error(
                await obterMensagemErro(
                    resposta,
                    "Não foi possível salvar o produto."
                )
            );
        }

        fecharModal();

        mostrarMensagem(
            editando
                ? "Produto atualizado com sucesso!"
                : "Produto cadastrado com sucesso!"
        );

        await carregarProdutos();

    } catch (erro) {
        mostrarMensagem(erro.message, "erro");

    } finally {
        botao.disabled = false;

        botao.textContent = editando
            ? "Salvar alterações"
            : "Cadastrar produto";
    }
});

// Excluir produto
document.getElementById("confirmarExcluir")
    .addEventListener("click", async () => {

        if (!produtoParaExcluir) return;

        const botao = document.getElementById("confirmarExcluir");

        botao.disabled = true;
        botao.textContent = "Excluindo...";

        try {
            const resposta = await fetch(
                `${API_URL}/${produtoParaExcluir.id}`,
                {
                    method: "DELETE",
                    credentials: "include"
                }
            );

            if (!resposta.ok) {
                throw new Error(
                    await obterMensagemErro(
                        resposta,
                        "Não foi possível excluir o produto."
                    )
                );
            }

            fecharModalExcluir();

            mostrarMensagem("Produto excluído com sucesso!");

            await carregarProdutos();

        } catch (erro) {
            mostrarMensagem(erro.message, "erro");

        } finally {
            botao.disabled = false;
            botao.textContent = "Excluir produto";
        }
    });

// Carregar produtos ao abrir a página
carregarProdutos();
