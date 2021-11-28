package persistence.DAO;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DTO.SubjectDTO;

import java.util.HashMap;
import java.util.List;

public class SubjectDAO {
    private SqlSessionFactory sqlSessionFactory = null;

    public SubjectDAO(SqlSessionFactory sqlSessionFactory) {this.sqlSessionFactory = sqlSessionFactory; }

    public List<SubjectDTO> selectAll(){
        List<SubjectDTO> list = null;

        SqlSession session = sqlSessionFactory.openSession();
        try {
            list = session.selectList("mapper.SubjectMapper.selectAll");
        } finally {
            session.close();
        }
        return list;
    }

    public int selectByCode(String Subject_Code){
        SqlSession session = sqlSessionFactory.openSession();

        int subject_idx = 0;
        try {
            subject_idx = session.selectOne("mapper.SubjectMapper.selectByCode");
        } finally {
            session.close();
        }

        return subject_idx;
    }

    public List<SubjectDTO> selectByGrade(int grade){
        List<SubjectDTO> list = null;

        SqlSession session = sqlSessionFactory.openSession();
        try {
            list = session.selectList("mapper.SubjectMapper.selectByGrade",grade);
        } finally {
            session.close();
        }
        return list;
    }
    //map에 ( #{new_name},#{old_name}) 등록
    public void updateSubjectName(HashMap<String,String> map){

        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.update("mapper.SubjectMapper.updateSubjectName",map);
            session.commit();
            System.out.println("업데이트 완료");
        }
        catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        finally {
            session.close();
        }

    }

    //map에 (#{subject_code},#{name},#{grade}) 등록
    public void insertSubject(HashMap<String,Object> map){

        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("mapper.SubjectMapper.insertSubject",map);
            session.commit();
            System.out.println("교과목 생성 완료");
        }
        catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        finally {
            session.close();
        }

    }

    // 8.SubjectDAO에 과목코드로 교과목 삭제하는 기능 -->> xml 기반이라서 resources.sqlmapper.subjectxml에도 추가
    public void deleteSubject(String subject_code){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("mapper.SubjectMapper.deleteSubject",subject_code);
            session.commit();
            System.out.println("교과목 삭제 완료");
        }
        catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        finally {
            session.close();
        }

    }
}
