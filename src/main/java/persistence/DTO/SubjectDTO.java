package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubjectDTO {
    private int idx;
    private String subject_code;
    private String name;
    private int grade;
}
