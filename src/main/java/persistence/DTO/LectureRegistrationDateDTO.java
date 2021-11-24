package persistence.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@Getter
@Setter
@ToString
public class LectureRegistrationDateDTO {
    private int grade;
    private Date start_date;
    private Date end_date;
}
