
const formulario = document.getElementById("loginForm");

formulario.addEventListener("submit", async function(event) {

    event.preventDefault();

    const email = formulario.elements["email"].value.trim().toLowerCase();
    const senha = formulario.elements["senha"].value;

    try {

        // Busca os usuários cadastrados no banco de dados
        const resposta = await fetch("http://localhost:8080/api/usuarios");

        if (!resposta.ok) {
            throw new Error("Não foi possível consultar os usuários.");
        }

        const usuarios = await resposta.json();

        // Verifica se existe um usuário com esse email e senha
        const usuarioEncontrado = usuarios.find(usuario =>
            usuario.email.toLowerCase() === email &&
            usuario.senha === senha
        );

        if (usuarioEncontrado) {

            alert("Login realizado com sucesso!");

            // Salva os dados básicos do usuário na sessão
            sessionStorage.setItem("usuarioLogado", JSON.stringify({
                id: usuarioEncontrado.id,
                nome: usuarioEncontrado.nome,
                email: usuarioEncontrado.email,
                tipoUsuario: usuarioEncontrado.tipoUsuario
            }));

            // Redireciona para a página inicial
            window.location.href = "home.html";

        } else {

            alert("E-mail ou senha incorretos!");

        }

    } catch (erro) {

        console.error("Erro ao realizar login:", erro);

        alert("Não foi possível realizar o login. Verifique se o servidor está funcionando.");

    }

});
