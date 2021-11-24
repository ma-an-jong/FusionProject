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


}


