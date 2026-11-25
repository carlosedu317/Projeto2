
const formulario = document.getElementById("loginForm");

formulario.addEventListener("submit", async function(event) {

    event.preventDefault();

    const email = formulario.elements["email"].value.trim().toLowerCase();
    const senha = formulario.elements["senha"].value;

    try {

        const resposta = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({
                email: email,
                senha: senha
            })
        });

        const dados = await resposta.json();

        if (!resposta.ok) {
            throw new Error(dados.message || "E-mail ou senha incorretos.");
        }

        // Salva os dados básicos do usuário.
        sessionStorage.setItem("usuarioLogado", JSON.stringify({
            id: dados.id,
            nome: dados.nome,
            email: dados.email,
            tipoUsuario: dados.tipoUsuario
        }));

        alert("Login realizado com sucesso!");

        window.location.href = "home.html";

    } catch (erro) {

        console.error("Erro ao realizar login:", erro);

        alert(erro.message || "Não foi possível realizar o login.");

    }

});
