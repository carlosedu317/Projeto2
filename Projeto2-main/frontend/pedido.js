
const API_URL = "http://localhost:8080/api/pedidos";

const formulario = document.getElementById("btnFinalizar");

formulario.addEventListener("click", async function() {

    const endereco = document.getElementById("enderecoEntrega").value;

    const usuario = JSON.parse(
        localStorage.getItem("usuarioLogado")
    );

    const carrinho = JSON.parse(
        localStorage.getItem("doceriaKarlaCarrinho")
    ) || [];

    const pedido = {
        usuarioId: usuario.id,
        enderecoEntrega: endereco,
        itens: carrinho.map(item => ({
            produtoId: item.produto.id,
            quantidade: item.quantidade
        }))
    };

    try {

        const resposta = await fetch(API_URL, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(pedido)

        });

        const resultado = await resposta.json();

        if (!resposta.ok) {
            throw new Error(resultado.message || "Erro ao realizar pedido.");
        }

        localStorage.removeItem("doceriaKarlaCarrinho");

        alert("Pedido realizado! Número: " + resultado.id);

        // Aqui você pode chamar a função que exibe o comprovante.

    } catch (erro) {

        alert(erro.message);

    }

});
