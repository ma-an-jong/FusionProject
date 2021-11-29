package persistence.Mapper;

import org.apache.ibatis.annotations.*;
import persistence.DTO.SyllabusInsertTimeDTO;

import java.sql.Date;
import java.util.List;

public interface SyllabusInsertTimeMapper {
    @Select("SELECT * FROM syllabus_insert_time")
    public SyllabusInsertTimeDTO selectAll();

    @Insert("INSERT INTO syllabus_insert_time(start_date, end_date)VALUES(#{startDate}, #{endDate})")
    public void setSyllabusInsertDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Update("UPDATE syllabus_insert_time SET start_date = #{startDate}, end_date = #{endDate}")
    public void modifySyllabusInsertDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
