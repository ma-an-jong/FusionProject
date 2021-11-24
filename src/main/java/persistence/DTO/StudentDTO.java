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


    public String getInfo()
    {
        return "[학생] 이름:" + sname +" 학번:" + student_code +" 학과: " + department+ " 연락처: " + phone + "학년: " + grade;
    }
    //2.학생정보 담을거 student_code,sname,department,grade,phone 만 정리
    //3.관리자가 교수랑 학생정보를 조회할때 이쁘게 출력할 정보들



}
