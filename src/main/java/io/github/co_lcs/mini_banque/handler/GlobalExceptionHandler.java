package io.github.co_lcs.mini_banque.handler;

import io.github.co_lcs.mini_banque.service.CompteNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CompteNotFoundException.class)
    public ProblemDetail handleCompteNotFound(CompteNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problemDetail.setTitle("Compte Non Trouvé");

        return problemDetail;
    }
}
