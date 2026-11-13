
const formulario = document.getElementById("cadastroForm");

formulario.addEventListener("submit", async function(event) {

    event.preventDefault();

    // Capturando os valores dos campos
    const nome = document.getElementById("nome").value;
    const telefone = document.getElementById("telefone").value;
    const email = document.getElementById("email").value;
    const endereco = document.getElementById("endereco").value;
    const tipoUsuario = document.getElementById("tipoUsuario").value;
    const senha = document.getElementById("senha").value;
    const confirmarSenha = document.getElementById("confirmarSenha").value;

    // Verificando se as senhas são iguais
    if (senha !== confirmarSenha) {
        alert("As senhas não coincidem!");
        return;
    }

    // Montando o objeto para enviar ao Spring Boot
    const usuario = {
        nome: nome,
        telefone: telefone,
        email: email,
        endereco: endereco,
        tipoUsuario: tipoUsuario,
        senha: senha
    };

    try {

        // Enviando os dados para o backend
        const resposta = await fetch("http://localhost:8080/api/usuarios", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(usuario)

        });

        if (resposta.ok) {

            const dados = await resposta.json();

            alert("Usuário cadastrado com sucesso!");

            console.log("Usuário salvo:", dados);

            formulario.reset();

        } else {

            const erro = await resposta.text();

            alert("Erro ao cadastrar usuário: " + erro);

            console.error("Erro:", erro);

        }

    } catch (erro) {

        console.error("Erro na comunicação com o servidor:", erro);

        alert("Não foi possível conectar ao servidor. Verifique se o Spring Boot está rodando.");

    }

});
