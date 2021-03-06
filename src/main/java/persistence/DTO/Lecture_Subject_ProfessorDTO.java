package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import persistence.DAO.LectureRegistrationDateDAO;
import persistence.MyBatisConnectionFactory;

import java.sql.Date;
import java.text.SimpleDateFormat;


@Setter
@Getter
@ToString
public class Lecture_Subject_ProfessorDTO {
    private int lecture_idx;
    private int lecture_professor_idx;
    private String syllabus;
    private String lecture_time;
    private int maximum;
    private int current;
    private String classroom;
    private  boolean activity;
    private String subject_code;
    private String subject_name;
    private int grade;
    private String department;
    private String professor_name;
    private String phone;

    public void setActivity(){
        LectureRegistrationDateDAO lectureRegistrationDateDAO = new LectureRegistrationDateDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String ss=sdf.format(new java.util.Date());
        Date date= Date.valueOf(ss);

        LectureRegistrationDateDTO dto = lectureRegistrationDateDAO.selectByGrade(grade);

        if(date.compareTo(dto.getStart_date()) >= 0 && date.compareTo(dto.getEnd_date())<= 0){
            activity = true;
        }
        else{
            activity = false;
        }

    }

    //1.본인 시간표 조회에서 -> CourseDetailDTO 에서 lecture_time 만 String으로 리턴하는 메소드


    // 출력메소드
    public String printInfo(){

        return
                "과목명: " + subject_name + "\t"
                + "과목코드: " + subject_code + "\t "
                + "강의실: " + classroom + "\t "
                + "강의시간: " + lecture_time + "\t"
                + "신청가능 학년: " + grade + "\t"
                + "신청가능여부: [" + activity + "] \t"
                + "수강가능인원: " + maximum + "\t"
                + "현재수강신청인원: " + current + "\t"
                + "== 담당교수 정보 == \t"
                + "교수명: " + professor_name + "\t"
                + "학과: " + department + "\t"
                + "연락처: " + phone + "\t"
                ;

    }

}


