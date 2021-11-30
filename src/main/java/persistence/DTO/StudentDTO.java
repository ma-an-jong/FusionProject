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

    //2.학생정보 담을거 student_code,sname,department,grade,phone 만 정리
    public String getStudentInfo()
    {
        return "[학생] \t이름:" + sname +"\t학번:" + student_code +"\t학과: " + department+ "\t연락처: " + phone + "\t학년: " + grade;
    }

    //3.관리자가 교수랑 학생정보를 조회할때 이쁘게 출력할 정보들
    public String getStudentInfoForAdmin()
    {
      return "[학생 정보] \t이름: " + sname + "\t학번: " + student_code + "\t학과: " + department + "\t학년: " + grade + "\t연락처: " + phone;

    }


}
