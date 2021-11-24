package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class CourseCompletionDTO {
    private int cc_subject_idx;
    private int cc_student_idx;
}
