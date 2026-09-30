package domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProcessingResult (
		List<Transaction> successful,
		List<ProcessingError> errors)
{
	public int totalProcessed() {
		return successful.size() + errors.size();
	}
	public BigDecimal totalPaidAmount() {
		return successful.stream()
				.filter(t -> t.status() == TransactionStatus.PAID)
				.map(Transaction::amount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	public Map<TransactionStatus, Long> countByStatus() {
		return successful.stream()
				.collect(java.util.stream.Collectors.groupingBy(Transaction::status, java.util.stream.Collectors.counting()));
	}
}