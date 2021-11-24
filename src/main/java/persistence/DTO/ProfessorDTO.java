package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfessorDTO extends UserDTO{
    private int professor_idx;
    private String department;
    private String pname;
    private String phone;
    private String professor_code;
}
