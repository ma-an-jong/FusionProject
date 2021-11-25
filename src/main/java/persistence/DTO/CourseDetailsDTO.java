package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CourseDetailsDTO {
    private int idx;

    private int student_idx;
    private String student_code;
    private String department;
    private String name;
    private int grade;
    private String phone;

    private int lecture_idx;
    private int lecture_professor_idx;
    private String syllabus;
    private String lecture_time;
    private int maximum;
    private int current;
    private String classroom;

//6.CouserDetailDTO에서 개설 교과목 정보 요청할때 출력해줄 메소드


}
