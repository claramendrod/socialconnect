package br.com.socialconnect.api.produtos.exception;

import br.com.socialconnect.api.exception.ProblemDetail;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.controller.ProdutoController;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.List;

@Order(0)
@RestControllerAdvice(assignableTypes = ProdutoController.class)
public class ProdutoExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> validar(MethodArgumentNotValidException ex, WebRequest request) {
        boolean estoqueNegativo = ex.getBindingResult().getFieldErrors().stream()
                .anyMatch(erro -> "EstoqueNaoNegativo".equals(erro.getCode()));
        List<ProblemDetail.FieldError> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ProblemDetail.FieldError(erro.getField(), erro.getDefaultMessage())).toList();
        return resposta(estoqueNegativo ? 422 : 400, "Erro de validação",
                "Um ou mais campos são inválidos.", request, erros);
    }

    @ExceptionHandler(EstoqueNegativoException.class)
    public ResponseEntity<ProblemDetail> estoque(EstoqueNegativoException ex, WebRequest request) {
        return resposta(422, "Estoque negativo", ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(NomeProdutoDuplicadoException.class)
    public ResponseEntity<ProblemDetail> duplicado(NomeProdutoDuplicadoException ex, WebRequest request) {
        return resposta(409, "Nome já cadastrado", ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> inexistente(RecursoNaoEncontradoException ex, WebRequest request) {
        return resposta(404, "Produto não encontrado", ex.getMessage(), request, List.of());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class})
    public ResponseEntity<ProblemDetail> entradaInvalida(Exception ex, WebRequest request) {
        return resposta(400, "Requisição inválida", "Verifique o corpo e os parâmetros da requisição.",
                request, List.of());
    }

    private ResponseEntity<ProblemDetail> resposta(int status, String titulo, String detalhe,
            WebRequest request, List<ProblemDetail.FieldError> erros) {
        ProblemDetail problem = new ProblemDetail("https://socialconnect.api/errors/" + status,
                titulo, status, detalhe, request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(), erros);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }
}
