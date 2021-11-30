package Server;

import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DAO.*;
import persistence.DTO.StudentDTO;
import persistence.DTO.SubjectDTO;
import persistence.DTO.UserDTO;
import persistence.MyBatisConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class SQLMain {
    public static void main(String args[]){
        Connection conn = JDBCConnection.getConnection(JDBCConnection.url);
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();

        AdminDAO adminDAO = new AdminDAO(conn);
        adminDAO.createProfessor("2777",
            "8475","기계공학과" ,"리처드 파인만","010-3141-4122");

//        LectureRegistrationDateDAO dao = new LectureRegistrationDateDAO(sqlSessionFactory);
//
//
//        SimpleDateFormat sdf=new SimpleDateFormat("2021-10-11");
//        String ss=sdf.format(new java.util.Date());
//        Date sd= Date.valueOf(ss);
//
//         sdf=new SimpleDateFormat("2021-12-25");
//         ss=sdf.format(new java.util.Date());
//        Date ed= Date.valueOf(ss);
//
//
//
//
//        dao.setSeason(1,sd,ed);
//        dao.setSeason(2,sd,ed);
//        dao.setSeason(3,sd,ed);
//        dao.setSeason(4,sd,ed);

//        SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
//        HashMap<String,Object> map = new HashMap<String,Object>();
//        map.put("subject_code","0909");
//        map.put("name","융프싫어");
//        map.put("grade",3);
//        subjectDAO.insertSubject(map);
//    ProfessorDAO professorDAO = new ProfessorDAO(conn);
//    AdminDAO adminDAO = new AdminDAO(conn);
//    adminDAO.createProfessor("4321",
//            "9999","컴퓨터 공학과" ,"김성렬","010-1234-1234");


//        adminDAO.createAdmin("20180167","991113");
//        List<UserDTO> list = adminDAO.selectAllUser();
//        System.out.println(list.size());


//        SubjectDAO subjectDAO = new SubjectDAO(MyBatisConnectionFactory.getSqlSessionFactory());
//        HashMap<String,Object> map = new HashMap<String,Object>();
//        map.put("subject_code","1234");
//        map.put("name","과목명");
//        map.put("grade",2);
//        subjectDAO.insertSubject(map);
//           List<SubjectDTO> list = subjectDAO.selectAll();
//        UserDAO userDAO = new UserDAO(JDBCConnection.getConnection(JDBCConnection.url));
//        List<UserDTO> list = userDAO.selectAllUser();
//        System.out.println(list.size());
 //       list.stream().forEach(v -> System.out.println("v.toString() = " + v.toString()));





    }
}
