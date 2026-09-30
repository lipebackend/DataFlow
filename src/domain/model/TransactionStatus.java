package domain.model;

public enum TransactionStatus {
    PENDING,
    PAID,
    CANCELLED,
    CANCELED;

    public static TransactionStatus fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Status cannot be blank");
        }
        String normalized = raw.trim().toUpperCase();
        if ("CANCELED".equals(normalized)) {
            return CANCELLED;
        }
        return TransactionStatus.valueOf(normalized);
    }
}
