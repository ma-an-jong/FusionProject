package persistence.DAO;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DTO.LectureRegistrationDateDTO;
import persistence.DTO.Lecture_Subject_ProfessorDTO;
import persistence.Mapper.LectureMapper;
import persistence.Mapper.LectureRegistrationDateMapper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class LectureRegistrationDateDAO {
    private SqlSessionFactory sqlSessionFactory = null;

    public LectureRegistrationDateDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<LectureRegistrationDateDTO> selectAll(){
        List<LectureRegistrationDateDTO> list = null;

            try(SqlSession session = sqlSessionFactory.openSession()){
            LectureRegistrationDateMapper mapper = session.getMapper(LectureRegistrationDateMapper.class);
            list = mapper.selectAll();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public LectureRegistrationDateDTO selectByGrade(int grade){

        LectureRegistrationDateDTO dto = null;
        try(SqlSession session = sqlSessionFactory.openSession()){
            LectureRegistrationDateMapper mapper = session.getMapper(LectureRegistrationDateMapper.class);
            dto = mapper.selectByGrade(grade);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return dto;
    }

    public boolean updateSeason(int grade,Date startDate, Date endDate){

        SqlSession session = sqlSessionFactory.openSession();
        LectureRegistrationDateMapper mapper = session.getMapper(LectureRegistrationDateMapper.class);
        boolean flag = false;
        try{

            List<LectureRegistrationDateDTO> list = mapper.selectAll();

            for(int i = 0 ; i< list.size(); i++){
                LectureRegistrationDateDTO dto = list.get(i);

                if(dto.getGrade() == grade){
                    mapper.updateSeason(grade,startDate,endDate);
                    session.commit();
                    System.out.println("업데이트 완료: "+grade+"학년 " + startDate.toString() +  " ~ " + endDate.toString());
                    flag = true;
                    break;
                }
            }


            if(!flag){
                mapper.insertSeason(grade,startDate,endDate);
                session.commit();
                System.out.println("생성 완료: "+grade+"학년 " + startDate +  " ~ " + endDate);
            }

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

    public boolean setSeason(int grade,Date startDate,Date endDate){
        LectureRegistrationDateDAO lectureRegistrationDateDAO = new LectureRegistrationDateDAO(sqlSessionFactory);
        boolean flag ;
        if(0 < grade && grade < 5){
            flag = lectureRegistrationDateDAO.updateSeason(grade,startDate,endDate);
        }

        else{
            System.out.println("잘못된 학년정보");
            flag = false;
        }

        return flag;


    }



}
