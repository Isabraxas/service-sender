package cc.viridian.service.statement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateJobTemplate {
    private Long id;
    private String account;
    private String adapterType;
    private String adapterCode;
    private String errorCode;
    private String errorDesc;
    private LocalDateTime localDateTime;
    private Boolean shouldTryAgain;
}
