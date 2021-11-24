package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentDTO extends UserDTO{
    private int student_idx;
    private String student_code;
    private String department;
    private String sname;
    private int grade;
    private String phone;

}
