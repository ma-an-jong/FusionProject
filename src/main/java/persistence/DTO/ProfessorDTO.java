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


    //3.관리자가 교수랑 학생정보를 조회할때 이쁘게 출력할 정보들
    public String getProfessorInfoForAdmin()
    {
        return "["+ pname +" 교수 정보] 이름: " + pname + "  교번: " + professor_code + " 학과: " + department + " 연락처: " + phone;
    }
}

