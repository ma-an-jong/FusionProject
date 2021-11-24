package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SyllabusInsertTimeDTO {
    private String start_date;
    private String end_date;
    private int idx;
}
