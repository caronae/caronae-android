package br.ufrj.caronae.httpapis;

public interface ServiceCallback<T> {
    void success(T obj);
    void fail(Throwable t);
}
