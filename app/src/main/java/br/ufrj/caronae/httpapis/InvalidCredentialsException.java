package br.ufrj.caronae.httpapis;

public class InvalidCredentialsException extends Exception {
    InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
