package persistence.DAO;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DTO.CourseDetailsDTO;
import persistence.DTO.LectureDTO;
import persistence.DTO.Lecture_Subject_ProfessorDTO;
import persistence.DTO.ProfessorDTO;
import persistence.Mapper.LectureMapper;

import java.lang.invoke.LambdaConversionException;
import java.util.List;

public class LectureDAO {

    private SqlSessionFactory sqlSessionFactory = null;

    public LectureDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    //===========================================================================================================
    // SELECT

    // lecture 전제 조회
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

    // 학년으로 lecture 목록조회하기
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

    // professor_code 로 professor_idx 찾기
    public int searchProfessorIdxByProfessorCode(String professor_code){
        int professor_idx = 0;

        try(SqlSession session = sqlSessionFactory.openSession();){
            LectureMapper mapper = session.getMapper(LectureMapper.class);
            professor_idx = mapper.searchProfessorIdxByProfessorCode(professor_code);

        }catch(Exception e){
            e.printStackTrace();
        }

        return professor_idx;
    }

    //================================================================
    //========================== INSERT ==============================
    //================================================================

    // lecture 추가하기
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
    public boolean updateSyllabus(String subject_code, String newSyllabus){
        //String syllabus = searchSyllabusBySubjectCode(subject_code);
        boolean flag = true;
        LectureDTO dto = searchBySubjectCode(subject_code);
        int lecture_idx = dto.getLecture_idx();
        String syllabus = dto.getSyllabus();

        if(syllabus != null){
            flag = insertSyllabus(lecture_idx, newSyllabus);
        }else {
            flag = modifySyllabus(lecture_idx, newSyllabus);
        }
        return flag;
    }

    public boolean insertSyllabus(int lecture_idx, String newSyllabus){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);
        boolean flag = true;
        try{
            mapper.insertSyllabus(lecture_idx, newSyllabus);
            session.commit();
            System.out.println("강의계획서 등록이 완료 되었습니다.");
        }catch(Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;

        }finally {
            session.close();
        }

        return flag;
    }


    //===========================================================================================================
    // UPDATE

    //담당교수 변경
    public boolean updateChangeProfessor(String subject_code ,String professor_code){

        // lecture_idx 찾기
        LectureDTO dto = searchBySubjectCode(subject_code);
        int lectureIdx = dto.getLecture_idx();

        // professor_idx찾기
        int professorIdx = searchProfessorIdxByProfessorCode(professor_code);

        return modifyProfessor(lectureIdx, professorIdx);
    }

    public boolean modifyProfessor(int lectureIdx, int professorIdx){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);
        boolean flag = true;
        try{
            mapper.modifyProfessor(lectureIdx, professorIdx);
            session.commit();
            System.out.println("담당교수가 변경되었습니다.");

        }catch(Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;
        }finally {
            session.close();
        }
        return flag;
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
    public boolean updateSubjectByClassRoom(String classroom,String subject_code){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);
        boolean flag = true;
        try{
            mapper.updateSubjectByClassRoom(classroom,subject_code);
            session.commit();
            System.out.println("강의실 update 성공");

        }
        catch(Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;
        }
        finally{
            session.close();
        }
        return flag;
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
    public boolean updateSubjectByMaximum(String subject_code,int maximum){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);
        boolean flag = true;
        try{
            mapper.updateSubjectByMaximum(maximum,subject_code);
            session.commit();
            System.out.println("최대 강의 인원 update 성공");

        }
        catch(Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;
        }
        finally{
            session.close();
        }

        return flag;
    }

    public boolean modifySyllabus(int lecture_idx, String syllabus){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);
        boolean flag = true;
        try{
            mapper.modifySyllabus(lecture_idx, syllabus);
            session.commit();
            System.out.println("강의계획서가 변경되었습니다.");

        }catch(Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;
        }finally {
            session.close();
        }
        return flag;
    }


    //===========================================================================================================
    // DELETE

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

    // 과목코드로 syllabus 삭제하기
    public String deleteSyllabusBySubjectCode(String subject_code){
        LectureDTO dto = searchBySubjectCode(subject_code);
        int lecture_idx = dto.getLecture_idx();

        // return 할 URL
        String SyllabusURL = dto.getSyllabus();
        // 삭제 진행
        deleteSyllabus(lecture_idx);

        return SyllabusURL;
    }

    // 강의계획서 삭제
    public void deleteSyllabus(int lecture_idx){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);

        try {
            mapper.deleteSyllabus(lecture_idx);
            session.commit();
            System.out.println("강의계획서가 삭제 되었습니다.");

        }catch(Exception e){
            e.printStackTrace();
            session.rollback();

        }finally {
            session.close();
        }
    }


}