package projetoIntegrador.prototico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import projetoIntegrador.prototico.dto.LoginRequest;
import projetoIntegrador.prototico.model.Usuario;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {"http://localhost:5500", "http://127.0.0.1:5500"},
    allowCredentials = "true"
)
public class AutenticacaoController {
    /* adição de construtor */
    private final AuthenticationManager manager;

    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    AutenticacaoController(AuthenticationManager manager) {
        this.manager = manager;
    }

    /* até aqui */
    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(
            @RequestBody @Valid LoginRequest dados,
            HttpServletRequest request,
            HttpServletResponse response) {

        var authToken = new UsernamePasswordAuthenticationToken(
                dados.email(),
                dados.senha());

        var authentication = manager.authenticate(authToken);

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        return ResponseEntity.ok(java.util.Map.of(
                "id", usuarioLogado.getId(),
                "nome", usuarioLogado.getNome(),
                "email", usuarioLogado.getEmail(),
                "tipoUsuario", usuarioLogado.getTipoUsuario().name()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}