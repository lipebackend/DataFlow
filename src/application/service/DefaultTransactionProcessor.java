package application.service;

import domain.interfaces.TransactionProcessor;
import domain.model.ProcessingError;
import domain.model.ProcessingResult;
import domain.model.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class DefaultTransactionProcessor implements TransactionProcessor {

    @Override
    public ProcessingResult process(List<Transaction> transactions) {
        Objects.requireNonNull(transactions, "transactions não pode ser nulo");

        List<Transaction> successful = new ArrayList<>();
        List<ProcessingError> errors = new ArrayList<>();

        for (Transaction transaction : transactions) {
            String error = validationError(transaction);
            if (error == null) {
                successful.add(transaction);
            } else {
                errors.add(new ProcessingError(transaction == null ? null : transaction.id(), error));
            }
        }

        return new ProcessingResult(successful, errors);
    }

    private String validationError(Transaction transaction) {
        if (transaction == null) {
            return "Transação não pode ser nula";
        }
        if (transaction.id() == null) {
            return "ID da transação é obrigatório";
        }
        if (transaction.customerId() == null) {
            return "ID do cliente é obrigatório";
        }
        if (transaction.amount() == null) {
            return "Valor da transação é obrigatório";
        }
        if (transaction.amount().signum() < 0) {
            return "Valor da transação não pode ser negativo";
        }
        if (transaction.date() == null) {
            return "Data da transação é obrigatória";
        }
        if (transaction.status() == null) {
            return "Status da transação é obrigatório";
        }
        return null;
    }
}
