package persistence.Mapper;

import org.apache.ibatis.annotations.*;
import persistence.DTO.LectureRegistrationDateDTO;

import java.sql.Date;
import java.util.List;

public interface LectureRegistrationDateMapper {

    @Select("Select * From lecture_registration_date  Order By grade")
    public List<LectureRegistrationDateDTO> selectAll();

    @Select("Select * From lecture_registration_date WHERE grade = #{grade} Order  By grade")
    public LectureRegistrationDateDTO selectByGrade(@Param("grade") int grade);

    @Update("Update lecture_registration_date Set start_date = #{startDate} , end_date = #{endDate} WHERE grade = #{grade}")
    public void updateSeason(@Param("grade") int grade,@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    @Insert("INSERT INTO lecture_registration_date(grade,start_date,end_date)" +
            " VALUE (#{grade},#{startDate},#{endDate})")
    public void insertSeason(@Param("grade") int grade,@Param("startDate") Date startDate,@Param("endDate") Date endDate );


}
