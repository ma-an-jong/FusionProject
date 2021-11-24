import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import persistence.DAO.*;
import persistence.DTO.*;
import persistence.MyBatisConnectionFactory;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String args[]){
/*
        CourseRegistration courseRegistrationDAO = new CourseRegistration(MyBatisConnectionFactory.getSqlSessionFactory());
        CourseDetailsDTO courseDetailsDTO = new CourseDetailsDTO();
        courseDetailsDTO.setLecture_idx(1);

        List<StudentDTO> DTOS = courseRegistrationDAO.selectWithPaging(courseDetailsDTO,0);

        DTOS.stream().forEach(v -> System.out.println("v.toString() = " + v.toString()));
*/
      //  AdminDAO adminDAO = new AdminDAO();
      // adminDAO.createAdmin("authority","0000");

       // StudentDAO studentDAO = new StudentDAO();

      //  studentDAO.updateName(11,"이병헌");
      //  List list = adminDAO.selectAllStudent();
      //  list.stream().forEach(v -> System.out.println("v.toString() = " + v.toString()));




    }
}