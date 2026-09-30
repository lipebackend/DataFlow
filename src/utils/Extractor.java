package utils;

import domain.interfaces.TransactionReader;
import domain.model.Transaction;
import domain.model.TransactionStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Extrator/Leitor de transações a partir de arquivos CSV.
 * Implementa {@link TransactionReader} para integração com o domínio.
 */
public class Extractor implements TransactionReader {

    private final Path filePath;
    private final InputStream inputStream;

    public Extractor() {
        this.filePath = null;
        this.inputStream = null;
    }

    public Extractor(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "filePath não pode ser nulo");
        this.inputStream = null;
    }

    public Extractor(InputStream inputStream) {
        this.inputStream = Objects.requireNonNull(inputStream, "inputStream não pode ser nulo");
        this.filePath = null;
    }

    /**
     * Extrai transações baseado na fonte configurada no construtor.
     */
    public List<Transaction> extract() {
        if (filePath != null) {
            return read(filePath);
        }
        if (inputStream != null) {
            return extract(inputStream);
        }
        throw new IllegalStateException("Nenhuma fonte de dados (Path ou InputStream) foi fornecida no construtor.");
    }

    /**
     * Lê e converte transações a partir de um {@link Path}.
     */
    @Override
    public List<Transaction> read(Path file) {
        Objects.requireNonNull(file, "Arquivo não pode ser nulo");
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + file);
        }

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return extract(reader);
        } catch (IOException e) {
            throw new RuntimeException("Falha de E/S ao ler arquivo: " + file, e);
        }
    }

    /**
     * Extrai transações a partir de um {@link InputStream}.
     */
    public List<Transaction> extract(InputStream stream) {
        Objects.requireNonNull(stream, "Stream não pode ser nulo");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return extract(reader);
    }

    /**
     * Processa o leitor linha a linha de forma sequencial e descarta o cabeçalho.
     */
    public List<Transaction> extract(BufferedReader reader) {
        Objects.requireNonNull(reader, "BufferedReader não pode ser nulo");
        List<Transaction> transactions = new ArrayList<>();

        try {
            String header = reader.readLine(); // Descarta a primeira linha (cabeçalho)
            if (header == null) {
                return transactions;
            }

            String line;
            long lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                parseLine(line, lineNumber).ifPresent(transactions::add);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar conteúdo CSV", e);
        }

        return transactions;
    }

    /**
     * Faz o parsing de uma linha CSV com tratamento de dados inconsistentes.
     */
    private Optional<Transaction> parseLine(String line, long lineNumber) {
        String[] tokens = line.split(",", -1);

        if (tokens.length < 5) {
            System.err.printf("[WARN] Linha %d: Quantidade de colunas inválida (%d/5): '%s'%n", lineNumber, tokens.length, line);
            return Optional.empty();
        }

        try {
            Long id = Long.valueOf(tokens[0].trim());
            Long customerId = Long.valueOf(tokens[1].trim());
            BigDecimal amount = new BigDecimal(tokens[2].trim());
            LocalDate date = LocalDate.parse(tokens[3].trim());
            TransactionStatus status = TransactionStatus.fromString(tokens[4].trim());

            return Optional.of(new Transaction(id, customerId, amount, date, status));
        } catch (DateTimeParseException | IllegalArgumentException e) {
            System.err.printf("[WARN] Linha %d: Registro corrompido (%s) -> '%s'%n", lineNumber, e.getMessage(), line);
            return Optional.empty();
        }
    }
}
