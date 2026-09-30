package domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(
        Long id,
        Long customerId,
        BigDecimal amount,
        LocalDate date,
        TransactionStatus status
) {
}
