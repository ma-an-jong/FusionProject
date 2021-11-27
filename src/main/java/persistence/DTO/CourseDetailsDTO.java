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

    //6.CourseDetailDTO에서 개설 교과목 정보 요청할때 출력해줄 메소드
    public String getSubjectInfo()
    {
        SubjectDTO subject = new SubjectDTO();

        String name = subject.getName();
        String subjectCode = subject.getSubject_code();
        int gradeInt = subject.getGrade();
        String grade = Integer.toString(gradeInt);

        return "[" + name + "] \n과목코드: " + subjectCode + "\n수강학년: " + grade;
    }

    // getLectureInfo();
    public String getLectureInfo()
    {
        String subjectInfo = getSubjectInfo();
        return subjectInfo + "\n강의 시간: " + lecture_time + "\n강의실: " + classroom + "\n신청가능인원/현재신청인원: " + maximum + "/" + current;
    }

}
