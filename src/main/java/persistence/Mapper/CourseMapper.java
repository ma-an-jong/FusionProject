package persistence.Mapper;

import org.apache.ibatis.annotations.*;
import persistence.DTO.CourseDetailsDTO;
import persistence.DTO.StudentDTO;

import java.util.List;

public interface CourseMapper {

    @Select( "SELECT * FROM Course_Details JOIN Lecture ON lecture_idx = cd_lecture_idx " +
            "JOIN Student ON student_idx = cd_student_idx WHERE student_code = #{myCode}")
    @Results(id = "ResultSet", value = {
            @Result(property = "lecture_idx", column = "lecture_idx"), //subject_id
            @Result(property = "lecture_professor_idx", column = "lecture_professor_idx"),
            @Result(property = "syllabus", column = "syllabus"),
            @Result(property = "lecture_time", column = "lecture_time"),
            @Result(property = "maximum", column = "maximum"),
            @Result(property = "current", column = "current"),
            @Result(property = "student_idx", column = "student_idx"),
            @Result(property = "department", column = "department"),
            @Result(property = "name", column = "sname"),
            @Result(property = "grade", column = "grade"),
            @Result(property = "phone", column = "phone"),
    })
    public List<CourseDetailsDTO> selectMyCourse(String myCode);

    @Select( "SELECT * FROM Subject JOIN Lecture ON lecture_idx = idx WHERE subject_code = #{subject_code}")
    public CourseDetailsDTO selectCourseByCode(String subject_code);

    @Insert("Insert Into Course_details(cd_student_idx,cd_lecture_idx) Value (#{student_idx},#{lecture_idx})")
    public void addCourse(CourseDetailsDTO courseDetailsDTO);

    @Delete("DELETE FROM course_details WHERE cd_lecture_idx = #{lecture_idx} AND cd_student_idx = #{student_idx}")
    public void deleteCourse(CourseDetailsDTO courseDetailsDTO);

    @Update("UPDATE LECTURE SET current = current+1 WHERE lecture_idx = #{lecture_idx}")
    public void addCurrent(int lecture_idx);

    @Update("UPDATE LECTURE SET current = current-1 WHERE lecture_idx = #{lecture_idx}")
    public void discountCurrent(int lecture_idx);

    public List<StudentDTO> selectWithPaging(CourseDetailsDTO courseDetailsDTO);


}
