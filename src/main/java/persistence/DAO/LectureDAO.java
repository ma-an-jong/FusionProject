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
    public List<Lecture_Subject_ProfessorDTO> selectByProfessor(String professor_code){
        List<Lecture_Subject_ProfessorDTO> list = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            LectureMapper mapper = session.getMapper(LectureMapper.class);

            list = mapper.selectByProfessor(professor_code);


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
        LectureDTO dto = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            LectureMapper mapper = session.getMapper(LectureMapper.class);

            dto = mapper.searchBySubjectCode(subject_code);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return dto;
    }

    // 강의계획서 조회
    public String searchSyllabusBySubjectCode(String subject_code){
        LectureDTO dto = searchBySubjectCode(subject_code);
        String Syllabus = null;
        if(dto.getSyllabus().length() > 0){
            Syllabus = dto.getSyllabus();
        }

        return Syllabus;
    }


    //================================================================
    //========================== INSERT ==============================
    //================================================================
    public boolean insertSubject(LectureDTO lectureDTO) {

        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);
        boolean flag = true;
        try{
            mapper.insertSubject(lectureDTO);
            session.commit();
            System.out.println("Insert 성공");
        }
        catch (Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;
        }
        finally {
            session.close();
        }
        return flag;
    }

    // 강의계획서 create + update
    public void updateSyllabus(String subject_code, String newSyllabus){
        String syllabus = searchSyllabusBySubjectCode(subject_code);



        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);



        try{
            if(syllabus == null){

            }
        }catch(Exception e){
            e.printStackTrace();
            session.rollback();
        }finally {
            session.close();
        }
    }


    //================================================================
    //========================== UPDATE ==============================
    //================================================================

    //담당교수 변경
    public void updateChangeProfessor(int lecture_idx ,int professor_idx){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);

        try{
            mapper.updateChangeProfessor(lecture_idx, professor_idx);
            session.commit();
            System.out.println("담당교수가 변경되었습니다.");
        }
        catch(Exception e){
            e.printStackTrace();
            //System.out.println("담당교수 변경에 실패하였습니다.");
            session.rollback();
        }
        finally
        {
            session.close();
        }
    }

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
    public boolean deleteLectureBySubjectCode(String subject_code){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);
        boolean flag = true;

        try{
            mapper.deleteLectureBySubjectCode(subject_code);
            session.commit();
            System.out.println("해당 개설교과목이 삭제되었습니다.");
        }
        catch (Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;
        }
        finally {
            session.close();
        }
        return flag;
    }

}
