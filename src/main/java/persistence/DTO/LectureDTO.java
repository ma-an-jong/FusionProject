package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import persistence.DAO.LectureRegistrationDateDAO;
import persistence.MyBatisConnectionFactory;

import java.sql.Date;
import java.text.SimpleDateFormat;

@Getter
@Setter
@ToString
public class LectureDTO {
    private int lecture_idx;
    private int lecture_professor_idx;
    private String syllabus;
    private String lecture_time;
    private int maximum;
    private int current;
    private String classroom;
    private  boolean activity;

}


