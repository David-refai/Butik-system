package org.example.Error;

public class ErrorHandling {

    private void Errors() {
    } // prevent instantiation

    /**
     * Thrown when an entity could not be found.
     */
    public static class NotFound extends RuntimeException {
        public NotFound(String message) {
            super(message);
        }

        public NotFound(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Thrown when an operation violates a uniqueness constraint (e.g., duplicate id).
     */
    public static class Duplicate extends RuntimeException {
        public Duplicate(String message) {
            super(message);
        }

        public Duplicate(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Thrown when input or state is invalid for the requested operation.
     */
    public static class Validation extends RuntimeException {
        public Validation(String message) {
            super(message);
        }

        public Validation(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
