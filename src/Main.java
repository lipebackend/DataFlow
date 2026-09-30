import domain.model.Transaction;
import domain.model.TransactionStatus;
import utils.Extractor;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

void main() {
    Path csvPath = Path.of("src/resources/transactions.csv");
    if (!Files.exists(csvPath)) {
        csvPath = Path.of("transactions.csv");
    }

    System.out.println("Reading transactions from: " + csvPath.toAbsolutePath());

    Extractor extractor = new Extractor(csvPath);
    List<Transaction> transactions = extractor.extract();

    System.out.println("\n--------------------------------------------------");
    System.out.printf("Total transactions extracted: %d%n", transactions.size());
    System.out.println("--------------------------------------------------");

    // 1. Contagem por status usando groupingBy
    Map<TransactionStatus, Long> countByStatus = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::status, Collectors.counting()));

    System.out.println("\nDistribution by status:");
    countByStatus.forEach((status, count) ->
            System.out.printf("  - %-10s : %d%n", status, count)
    );

    // 2. Particionamento: Válidas & Pagas vs Rejeitadas / Outros
    Map<Boolean, List<Transaction>> partitioned = transactions.stream()
            .collect(Collectors.partitioningBy(
                    t -> t.status() == TransactionStatus.PAID && t.amount().compareTo(BigDecimal.ZERO) > 0
            ));

    System.out.printf("\nValid & Paid transactions: %d%n", partitioned.get(true).size());
    System.out.printf("Other / Invalid / Non-Paid: %d%n", partitioned.get(false).size());

    // 3. Volume financeiro total pago
    BigDecimal totalPaid = partitioned.get(true).stream()
            .map(Transaction::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    System.out.printf("\nTotal Paid Volume: R$ %,.2f%n", totalPaid);
}
