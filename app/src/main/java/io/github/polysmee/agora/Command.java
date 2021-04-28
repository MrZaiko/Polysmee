package io.github.polysmee.agora;

/**
 * Generic interface for representing the command pattern
 * @param <T>
 * @param <K>
 */
public interface Command<T, K> {
    void execute(T value, K key);
}
