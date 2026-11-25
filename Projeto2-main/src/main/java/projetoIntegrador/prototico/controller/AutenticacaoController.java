package projetoIntegrador.prototico.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import projetoIntegrador.prototico.dto.LoginRequest;
import projetoIntegrador.prototico.model.Usuario;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500"
    },
    allowCredentials = "true"
)
public class AutenticacaoController {

    private final AuthenticationManager manager;

    private final HttpSessionSecurityContextRepository repository =
        new HttpSessionSecurityContextRepository();

    public AutenticacaoController(AuthenticationManager manager) {
        this.manager = manager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(
            @RequestBody @Valid LoginRequest dados,
            HttpServletRequest request,
            HttpServletResponse response) {

        var token = new UsernamePasswordAuthenticationToken(
            dados.email(),
            dados.senha()
        );

        Authentication autenticacao = manager.authenticate(token);

        SecurityContext contexto =
            SecurityContextHolder.createEmptyContext();

        contexto.setAuthentication(autenticacao);
        SecurityContextHolder.setContext(contexto);

        repository.saveContext(contexto, request, response);

        Usuario usuario = (Usuario) autenticacao.getPrincipal();

        return ResponseEntity.ok(Map.of(
            "id", usuario.getId(),
            "nome", usuario.getNome(),
            "email", usuario.getEmail(),
            "tipoUsuario", usuario.getTipoUsuario().name()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request) {

        SecurityContextHolder.clearContext();

        HttpSession sessao = request.getSession(false);

        if (sessao != null) {
            sessao.invalidate();
        }

        return ResponseEntity.noContent().build();
    }
}