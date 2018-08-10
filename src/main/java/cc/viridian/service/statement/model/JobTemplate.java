package cc.viridian.service.statement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobTemplate {
    private Long id;
    private String account;
    private String currency;
    private String type;

    private String corebankAdapter;
    private String formatAdapter;
    private String sendAdapter;

    private String customerCode;
    private String recipient;

    private String frequency;

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private Integer attemptNumber;
}
