package org.example.Error;

public final class Safe {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Safe.class);

    public static void run(Block block, String userAction) {
        try {
            block.run();
        } catch (ErrorHandling.Validation ex) {
            log.warn("{} - validation: {}", userAction, ex.getMessage());
            System.out.println("Invalid input: " + ex.getMessage());
        } catch (ErrorHandling.Duplicate ex) {
            log.warn("{} - duplicate: {}", userAction, ex.getMessage());
            System.out.println("Duplicate: " + ex.getMessage());
        } catch (ErrorHandling.NotFound ex) {
            log.warn("{} - not found: {}", userAction, ex.getMessage());
            System.out.println("Not found: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("{} - unexpected error", userAction, ex);
            System.out.println("Unexpected error, please try again.");
        }
    }

    public interface Block {
        void run() throws Exception;
    }
}
