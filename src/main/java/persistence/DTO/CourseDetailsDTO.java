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
    private String subject_name;


    // getLectureInfo();
    public String getLectureInfo()
    {
        //String subjectInfo = getSubjectInfo();
        return  "강의 시간: " + lecture_time + "강의명: " + subject_name + "강의실: " + classroom + "신청가능인원/현재신청인원: " + maximum + "/" + current;
    }

}
