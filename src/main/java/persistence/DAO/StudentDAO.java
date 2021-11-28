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

    public StudentDTO searchByStudent_idx(int student_idx){

        List<StudentDTO> list = super.selectAllStudent();

        for(StudentDTO dto: list){
            int s_idx = dto.getStudent_idx();
            if(s_idx == student_idx){
                return dto;
            }
        }
        return null;
    }

    public StudentDTO searchByStudent_code(String student_code){

        List<StudentDTO> list = super.selectAllStudent();

        for(StudentDTO dto: list){
            String s_code = dto.getStudent_code();
            if(s_code == student_code){
                return dto;
            }
        }
        return null;
    }

    public void updateStudentInfo(StudentDTO studentDTO){
        PreparedStatement pstmt = null;

        int idx = studentDTO.getStudent_idx();
        String newName = studentDTO.getSname();
        String newCode = studentDTO.getStudent_code();
        String newDepartment = studentDTO.getDepartment();
        int newGrade = studentDTO.getGrade();
        String newPhone = studentDTO.getPhone();

        try{
            pstmt = conn.prepareStatement("UPDATE student SET sname = ?, student_code = ?, department = ?, grade = ?, phone = ? WHERE student_idx = ? ");
            pstmt.setString(1, newName);
            pstmt.setString(2, newCode);
            pstmt.setString(3, newDepartment);
            pstmt.setInt(4, newGrade);
            pstmt.setString(5, newPhone);
            pstmt.setInt(6, idx);

            pstmt.executeUpdate();
            conn.commit();
            System.out.println("학생 정보가 수정되었습니다.");

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
