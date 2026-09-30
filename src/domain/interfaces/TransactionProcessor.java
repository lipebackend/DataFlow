package domain.interfaces;

import domain.model.ProcessingResult;
import domain.model.Transaction;

import java.util.List;

public interface TransactionProcessor {
    ProcessingResult process(List<Transaction> transactions);
}
