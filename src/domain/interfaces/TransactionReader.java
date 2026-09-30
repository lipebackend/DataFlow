package domain.interfaces;

import domain.model.Transaction;

import java.nio.file.Path;
import java.util.List;

public interface TransactionReader {
    List<Transaction> read(Path file);
}
