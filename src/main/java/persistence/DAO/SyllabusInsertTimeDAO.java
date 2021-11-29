package persistence.DAO;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DTO.SyllabusInsertTimeDTO;
import persistence.DTO.Lecture_Subject_ProfessorDTO;
import persistence.Mapper.LectureMapper;
import persistence.Mapper.LectureRegistrationDateMapper;
import persistence.Mapper.SyllabusInsertTimeMapper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class SyllabusInsertTimeDAO {
    private SqlSessionFactory sqlSessionFactory = null;

    public SyllabusInsertTimeDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }


    public SyllabusInsertTimeDTO selectAll() {
        SyllabusInsertTimeDTO dto = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            SyllabusInsertTimeMapper mapper = session.getMapper(SyllabusInsertTimeMapper.class);
            dto = mapper.selectAll();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return dto;
    }

    public boolean setSeason(Date startDate, Date endDate)
    {
        SyllabusInsertTimeDTO dto = selectAll();
        boolean flag;
        if(dto == null)
        {
            flag = setSyllabusInsertDate(startDate,endDate);
        }
        else
        {
            flag = modifySyllabusInsertDate(startDate,endDate);
        }
        return flag;

    }

    public boolean setSyllabusInsertDate(Date startDate, Date endDate){
        SqlSession session = sqlSessionFactory.openSession();
        SyllabusInsertTimeMapper mapper = session.getMapper(SyllabusInsertTimeMapper.class);
        boolean flag = true;
        try{
            mapper.setSyllabusInsertDate(startDate, endDate);
            session.commit();
            System.out.println("강의계획서 등록기간 설정(" + startDate + " ~ " + endDate + ")이 완료되었습니다.");

        }catch(Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;
        }finally {
            session.close();
        }
        return flag;
    }

    public boolean modifySyllabusInsertDate(Date startDate, Date endDate){
        SqlSession session = sqlSessionFactory.openSession();
        SyllabusInsertTimeMapper mapper = session.getMapper(SyllabusInsertTimeMapper.class);
        boolean flag = true;
        try{
            mapper.modifySyllabusInsertDate(startDate, endDate);
            session.commit();
            System.out.println("강의계획서 등록기간이 변경되었습니다.");

        }catch(Exception e){
            e.printStackTrace();
            session.rollback();
            flag = false;
        }finally {
            session.close();
        }
        return flag;
    }

}
