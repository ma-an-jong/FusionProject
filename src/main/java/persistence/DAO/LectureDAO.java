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

    //select
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

    //insert
    public void inserSubject(LectureDTO lectureDTO) {

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

    //update
    public void updateSubjectByClassRoom(String classroom,int lecture_idx){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);

        try{
            mapper.updateSubjectByClassRoom(classroom,lecture_idx);
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

    public void updateSubjectByMaximum(int maximum,int lecture_idx){
        SqlSession session = sqlSessionFactory.openSession();
        LectureMapper mapper = session.getMapper(LectureMapper.class);

        try{
            mapper.updateSubjectByMaximum(maximum,lecture_idx);
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


}
