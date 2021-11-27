package persistence.Mapper;

import org.apache.ibatis.annotations.*;
import persistence.DTO.CourseDetailsDTO;
import persistence.DTO.LectureDTO;
import persistence.DTO.Lecture_Subject_ProfessorDTO;

import java.util.List;

public interface LectureMapper {
    @Select( "SELECT * FROM Lecture JOIN SUBJECT ON lecture_idx = idx " +
                                    "JOIN PROFESSOR ON lecture_professor_idx = professor_idx ORDER BY lecture_idx")
    @Results(id = "JoinResultSet", value = {
            @Result(property = "lecture_idx", column = "lecture_idx"), //subject_id
            @Result(property = "lecture_professor_idx", column = "lecture_professor_idx"),
            @Result(property = "syllabus", column = "syllabus"),
            @Result(property = "lecture_time", column = "lecture_time"),
            @Result(property = "maximum", column = "maximum"),
            @Result(property = "current", column = "current"),
            @Result(property = "subject_code", column = "subject_code"),
            @Result(property = "subject_name", column = "name"),
            @Result(property = "grade", column = "grade"),
            @Result(property = "professor_name", column = "pname"),
    })
    List<Lecture_Subject_ProfessorDTO> selectAll();

    @Select("SELECT * FROM Lecture JOIN Subject ON lecture_idx = idx " +
            "JOIN Professor ON lecture_professor_idx = professor_idx WHERE grade = #{grade}")
    @ResultMap("JoinResultSet")
    List<Lecture_Subject_ProfessorDTO> selectByGrade(@Param("grade") int grade);

    @Select("SELECT * FROM Lecture JOIN SUBJECT ON lecture_idx = idx " +
            "JOIN PROFESSOR ON lecture_professor_idx = professor_idx" +
            " WHERE pname = #{p_name} AND grade = #{grade}")
    @ResultMap("JoinResultSet")
    List<Lecture_Subject_ProfessorDTO> selectByProfessorAndGrade(@Param("name") String p_name,@Param("grade") int grade);

    @Select("SELECT * FROM Lecture JOIN SUBJECT ON lecture_idx = idx " +
            "JOIN PROFESSOR ON lecture_professor_idx = professor_idx" +
            " WHERE pname = #{p_name}")
    @ResultMap("JoinResultSet")
    List<Lecture_Subject_ProfessorDTO> selectByProfessor(String p_name);

    @Insert("INSERT INTO LECTURE(lecture_idx,lecture_professor_idx,lecture_time,maximum,current,classroom)" +
            " VALUE (#{lecture_idx},#{lecture_professor_idx},#{lecture_time},#{maximum},#{current},#{classroom})")
    void insertSubject(LectureDTO lectureDTO);

    @Update("UPDATE LECTURE SET classroom = #{classroom} WHERE lecture_idx = #{lecture_idx}" )
    public void updateSubjectByClassRoom(@Param("classroom") String classroom,@Param("lecture_idx") int lecture_idx);

    @Update("UPDATE LECTURE SET maximum = #{maximum} WHERE lecture_idx = #{lecture_idx} ")
    public void updateSubjectByMaximum(@Param("maximum") int maximum,@Param("lecture_idx") int lecture_idx);

    @Select( "SELECT * FROM Lecture JOIN SUBJECT ON lecture_idx = idx " +
            "JOIN PROFESSOR ON lecture_professor_idx = professor_idx" +
            "WHERE lecture_idx = #{idx}")
    List<Lecture_Subject_ProfessorDTO> selectByLecture_idx(@Param("idx") int idx);
    //paging은......
//    public List<CourseDetailsDTO> selectStudentInfo(List<Lecture_Subject_ProfessorDTO> list,LectureDTO lectureDTO,int page);
    @Select("SELECT * FROM lecture JOIN subject ON lecture_idx = idx where subject_code = #{subject_code}")
    LectureDTO searchBySubjectCode(String subject_code);


}
