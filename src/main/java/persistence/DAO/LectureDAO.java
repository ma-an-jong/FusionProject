package persistence.DAO;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DTO.CourseDetailsDTO;
import persistence.DTO.LectureDTO;
import persistence.DTO.Lecture_Subject_ProfessorDTO;
import persistence.Mapper.LectureMapper;

import java.util.List;

public class LectureDAO {

    private SqlSessionFactory sqlSessionFactory = null;

    public LectureDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    //================================================================
    //========================== SELECT ==============================
    //================================================================
    public List<Lecture_Subject_ProfessorDTO> selectAll(){
        List<Lecture_Subject_ProfessorDTO> list = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            LectureMapper mapper = session.getMapper(LectureMapper.class);

            list = mapper.selectAll();

            for(Lecture_Subject_ProfessorDTO dto : list){
                dto.setActivity();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }



    public List<Lecture_Subject_ProfessorDTO> selectByGrade(int grade){
        List<Lecture_Subject_ProfessorDTO> list = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            LectureMapper mapper = session.getMapper(LectureMapper.class);

            list = mapper.selectByGrade(grade);

            for(Lecture_Subject_ProfessorDTO dto : list){
                dto.setActivity();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    //교수가 담당한 교과목 조회
    public List<Lecture_Subject_ProfessorDTO> selectByProfessor(String p_name){
        List<Lecture_Subject_ProfessorDTO> list = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            LectureMapper mapper = session.getMapper(LectureMapper.class);

            list = mapper.selectByProfessor(p_name);


            for(Lecture_Subject_ProfessorDTO dto : list){
                dto.setActivity();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public List<Lecture_Subject_ProfessorDTO> selectByProfessorAndGrade(String p_name,int grade){
        List<Lecture_Subject_ProfessorDTO> list = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            LectureMapper mapper = session.getMapper(LectureMapper.class);

            list = mapper.selectByProfessorAndGrade(p_name,grade);

            for(Lecture_Subject_ProfessorDTO dto : list){
                dto.setActivity();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    // selectBySubjectCode
    public LectureDTO searchBySubjectCode(String subject_code){
        LectureDTO list = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            LectureMapper mapper = session.getMapper(LectureMapper.class);

            list = mapper.searchBySubjectCode(subject_code);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }


    //================================================================
    //========================== INSERT ==============================
    //================================================================
    public void insertSubject(LectureDTO lectureDTO) {

        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);

        try{
            mapper.insertSubject(lectureDTO);
            session.commit();
            System.out.println("Insert 성공");
        }
        catch (Exception e){
            e.printStackTrace();
            session.rollback();
        }
        finally {
            session.close();

        }

    }


    //================================================================
    //========================== UPDATE ==============================
    //================================================================

    // lecture_idx로 강의실 변경
//    public void updateSubjectByClassRoom(String classroom,int lecture_idx){
//        SqlSession session = sqlSessionFactory.openSession();
//        LectureMapper mapper = session.getMapper(LectureMapper.class);
//
//        try{
//            mapper.updateSubjectByClassRoom(classroom,lecture_idx);
//            session.commit();
//            System.out.println("강의실 update 성공");
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            session.rollback();
//        }
//        finally{
//            session.close();
//        }
//    }
    // 과목코드로 강의실 변경
    public void updateSubjectByClassRoom(String classroom,String subject_code){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);

        try{
            mapper.updateSubjectByClassRoom(classroom,subject_code);
            session.commit();
            System.out.println("강의실 update 성공");

        }
        catch(Exception e){
            e.printStackTrace();
            session.rollback();
        }
        finally{
            session.close();
        }
    }


//    public void updateSubjectByMaximum(int maximum,int lecture_idx){
//        SqlSession session = sqlSessionFactory.openSession();
//        LectureMapper mapper = session.getMapper(LectureMapper.class);
//
//        try{
//            mapper.updateSubjectByMaximum(maximum,subject_code);
//            session.commit();
//            System.out.println("최대 강의 인원 update 성공");
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            session.rollback();
//        }
//        finally{
//            session.close();
//        }
//    }
    public void updateSubjectByMaximum(int maximum,String subject_code){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);

        try{
            mapper.updateSubjectByMaximum(maximum,subject_code);
            session.commit();
            System.out.println("최대 강의 인원 update 성공");

        }
        catch(Exception e){
            e.printStackTrace();
            session.rollback();
        }
        finally{
            session.close();
        }
    }


    //================================================================
    //========================== DELETE ==============================
    //================================================================
    // 과목코드로 lecture 삭제하기
    public void deleteLectureBySubjectCode(String subject_code){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);

        try{
            mapper.deleteLectureBySubjectCode(subject_code);
            session.commit();
            System.out.println("해당 개설교과목이 삭제되었습니다.");
        }
        catch (Exception e){
            e.printStackTrace();
            session.rollback();
        }
        finally {
            session.close();
        }
    }

}
