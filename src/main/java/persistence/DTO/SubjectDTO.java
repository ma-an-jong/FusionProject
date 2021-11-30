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

    // 출력
    public String printSubjectInfo(){

        return "===== 강좌조회 =====\t"
               + "[" + name + "]\t"
               + "과목코드: " + subject_code + "\t"
               + "해당 학년: " + grade + "\t"
               ;

    }
}
