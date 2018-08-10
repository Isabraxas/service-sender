package cc.viridian.service.statement.model;

import cc.viridian.provider.model.Statement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SenderTemplate {
    private Long id;

    private String account;
    private String currency;
    private String type;

    private String formatAdapter;
    private String sendAdapter;

    private String customerCode;
    private String recipient;

    private String frequency;

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private Integer attemptNumber;

    private Statement statement;
}
