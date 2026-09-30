package domain.model;

public record ProcessingError (
		Long transactionId,
		String reason
) {}
