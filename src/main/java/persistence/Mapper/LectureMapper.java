package persistence.Mapper;

import org.apache.ibatis.annotations.*;
import persistence.DTO.CourseDetailsDTO;
import persistence.DTO.LectureDTO;
import persistence.DTO.Lecture_Subject_ProfessorDTO;

import java.util.List;

public interface LectureMapper {

    // SELECT
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

    // 학년으로 L.S.P 목록 탐색
    @Select("SELECT * FROM Lecture JOIN Subject ON lecture_idx = idx " +
            "JOIN Professor ON lecture_professor_idx = professor_idx WHERE grade = #{grade}")
    @ResultMap("JoinResultSet")
    List<Lecture_Subject_ProfessorDTO> selectByGrade(@Param("grade") int grade);

    // 학년, 교수이름 으로 L.S.P 목록 탐색
    @Select("SELECT * FROM Lecture JOIN SUBJECT ON lecture_idx = idx " +
            "JOIN PROFESSOR ON lecture_professor_idx = professor_idx" +
            " WHERE pname = #{p_name} AND grade = #{grade}")
    @ResultMap("JoinResultSet")
    List<Lecture_Subject_ProfessorDTO> selectByProfessorAndGrade(@Param("name") String p_name,@Param("grade") int grade);


    // Lecture_idx로 L.S.P 목록 탐색
    @Select( "SELECT * FROM Lecture JOIN SUBJECT ON lecture_idx = idx " +
            "JOIN PROFESSOR ON lecture_professor_idx = professor_idx" +
            "WHERE lecture_idx = #{idx}")
    List<Lecture_Subject_ProfessorDTO> selectByLecture_idx(@Param("idx") int idx);

    // 교수코드로 L.S.P 목록 탐색
    @Select("SELECT * FROM Lecture JOIN SUBJECT ON lecture_idx = idx " +
            "JOIN PROFESSOR ON lecture_professor_idx = professor_idx" +
            " WHERE professor_code = #{professor_code}")
    @ResultMap("JoinResultSet")
    List<Lecture_Subject_ProfessorDTO> selectByProfessor(String professor_code);

    // 과목코드로 LectureDTO 탐색
    @Select("SELECT * FROM lecture JOIN subject ON lecture_idx = idx where subject_code = #{subject_code}")
    LectureDTO searchBySubjectCode(String subject_code);

    // 교수코드로 professor_idx 탐색(담당교수 변경할 때 사용됨)
    @Select("SELECT professor_idx FROM professor WHERE professor_code = #{professor_code}")
    int searchProfessorIdxByProfessorCode(String professor_code);

    // END OF SELECT


    //===========================================================================================================
    // INSERT

    // lecture 삽입
    @Insert("INSERT INTO LECTURE(lecture_idx,lecture_professor_idx,lecture_time,maximum,current,classroom)" +
            " VALUE (#{lecture_idx},#{lecture_professor_idx},#{lecture_time},#{maximum},#{current},#{classroom})")
    void insertSubject(LectureDTO lectureDTO);

    // 강의계획서 삽입 (강의계획서가 비어있을 때)
    @Insert("INSERT INTO lecture(syllabus) VALUE (#{syllabus}) WHERE lecture_idx = #{lecture_idx} ")
    public void insertSyllabus(@Param("lecture_idx") int lecture_idx, @Param("syllabus") String syllabus);

    // END OF INSERT


    //===========================================================================================================
    // UPDATE

    // 담당교수 변경
    @Update("UPDATE LECTURE SET lecture_professor_idx = #{professor_idx} WHERE lecture_idx = lecture_idx")
    public void modifyProfessor(@Param("lecture_idx") int lecture_idx , @Param("professor_idx") int professor_idx);

    // lecture_idx로 classroom 변경
    //@Update("UPDATE LECTURE SET classroom = #{classroom} WHERE lecture_idx = #{lecture_idx}" )
    //public void updateSubjectByClassRoom(@Param("classroom") String classroom,@Param("lecture_idx") int lecture_idx);
    // 과목코드로 강의실 변경
    @Update("UPDATE LECTURE JOIN SUBJECT ON lecture_idx = idx SET classroom = #{classroom} WHERE subject_code = #{subject_code} ")
    public void updateSubjectByClassRoom(@Param("classroom") String classroom,@Param("subject_code") String subject_code);


    // lecture_idx로 수강가능인원변경
    //@Update("UPDATE LECTURE SET maximum = #{maximum} WHERE lecture_idx = #{lecture_idx} ")
    //public void updateSubjectByMaximum(@Param("maximum") int maximum,@Param("lecture_idx") int lecture_idx);
    // 과목코드로 최대수강가능인원수 변경
    @Update("UPDATE LECTURE JOIN SUBJECT ON lecture_idx = idx SET maximum = #{maximum} WHERE subject_code = #{subject_code}")
    public void updateSubjectByMaximum(@Param("maximum") int maximum,@Param("subject_code") String subject_code);

    // 강의계획서 수정
    @Update("UPDATE LECTURE SET syllabus = #{syllabus} WHERE lecture_idx = #{lecture_idx}")
    public void modifySyllabus(@Param("lecture_idx") int lecture_idx, @Param("syllabus") String syllabus);


    // END OF UPDATE


    //===========================================================================================================
    // DELETE

    // 과목코드로 lecture 삭제하기
    @Delete("DELETE lecture FROM lecture LEFT JOIN subject ON lecture_idx = idx where subject_code = #{subject_code}")
    public void deleteLectureBySubjectCode(@Param("subject_code") String subject_code);

    // 과목코드로 강의계획서 삭제하기(NULL update)
    @Delete("UPDATE lecture SET syllabus = NULL WHERE lecture_idx = #{lecture_idx}")
    public void deleteSyllabus(@Param("lecture_idx") int lecture_idx);

    // END OF DELETE


    //paging은......
    //public List<CourseDetailsDTO> selectStudentInfo(List<Lecture_Subject_ProfessorDTO> list,LectureDTO lectureDTO,int page);

}