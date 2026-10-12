package io.github.co_lcs.mini_banque.service;

public class CompteNotFoundException extends RuntimeException {
    public CompteNotFoundException(String message) {
        super(message);
    }
}
