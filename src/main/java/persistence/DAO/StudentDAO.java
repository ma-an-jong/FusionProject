package persistence.DAO;

import persistence.DTO.ProfessorDTO;
import persistence.DTO.StudentDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StudentDAO extends UserDAO{

    public StudentDAO(Connection conn){
        super(conn);
    }

    public StudentDTO searchByStudent_code(String student_code){

        List<StudentDTO> list = super.selectAllStudent();

        for(StudentDTO dto: list){
            String p_code = dto.getStudent_code();
            if(p_code == student_code){
                return dto;
            }
        }
        return null;
    }

    public void updateName(String student_code,String newName){

        PreparedStatement pstmt = null;

        try{
            pstmt = conn.prepareStatement("UPDATE Student SET sname = ? WHERE student_code = ?");

            pstmt.setString(1,newName);
            pstmt.setString(2,student_code);

            pstmt.executeUpdate();

            conn.commit();
            System.out.println("Update 성공");

        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
            System.out.println("COMMIT 실패");

            try{
                conn.rollback();
                System.out.println("ROLLBACK 성공");
            }
            catch (SQLException e){
                e.printStackTrace();
                System.out.println("ROLLBACK 실패");
            }
        }

        try{
            pstmt.close();
        }
        catch (SQLException e ){
            System.out.println("close 실패");
        }

    }
}
