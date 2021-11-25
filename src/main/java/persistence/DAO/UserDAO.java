package persistence.DAO;

import persistence.DTO.AdminDTO;
import persistence.DTO.ProfessorDTO;
import persistence.DTO.StudentDTO;
import persistence.DTO.UserDTO;
import persistence.PooledDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    protected static String PROFESSOR_CATEGORY = "p";
    protected static String STUDENT_CATEGORY = "s";
    protected static String ADMIN_CATEGORY = "a";

    private static String url = "jdbc:mysql://localhost/lecture_registration?characterEncoding=utf8&serverTimezone=UTC&useSSL = false";
    protected Connection conn = null;

    public UserDAO(Connection conn){
        this.conn = conn;
    }

    public int createUser(String id,String password,String category) {
        int idx = -1;

        try(PreparedStatement pstmt = conn.prepareStatement("INSERT INTO User(id, password,category) VALUES( ? , ?, ?)",Statement.RETURN_GENERATED_KEYS))
        {
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            pstmt.setString(3, category);

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            idx = rs.getInt(1);
            conn.commit();
            System.out.println("COMMIT 성공");

        }

        catch (SQLException e)
        {
            e.printStackTrace();
            try{
                conn.rollback();
                System.out.println("ROLLBACK 성공");
            }
            catch (SQLException e1) {
                System.out.println("ROLLBACK 실패");
            }
        }
    return idx;
    }

    public List<StudentDTO> selectAllStudent(){

        ResultSet rs = null;
        List<StudentDTO> list = new ArrayList<StudentDTO>();
        Statement stmt = null;

        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT *  FROM STUDENT");
            System.out.println("select 성공");
        }
        catch (SQLException e )
        {
            System.out.println("select 실패");
        }
        StudentDTO dto = null;

        try{
            while(rs.next()){
                dto = new StudentDTO();

                dto.setStudent_idx(rs.getInt("student_idx"));
                dto.setDepartment(rs.getString("department"));
                dto.setStudent_code(rs.getString("student_code"));
                dto.setSname(rs.getString("sname"));
                dto.setGrade(rs.getInt("grade"));
                dto.setPhone(rs.getString("phone"));

                list.add(dto);
            }
        }
        catch (SQLException e){
            System.out.println("값 복사도중 오류발생");
            e.printStackTrace();
        }

        try{
            rs.close();
            stmt.close();
        }
        catch (SQLException  e){
            System.out.println("close 실패");
        }
        return list;
    }

    public  List<ProfessorDTO> selectAllProfessor(){
        ResultSet rs = null;
        List<ProfessorDTO> list = new ArrayList<ProfessorDTO>();

        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT *  FROM PROFESSOR");
            System.out.println("select 성공");

        }
        catch (SQLException e )
        {
            System.out.println("select 실패");
        }

        ProfessorDTO dto = null;
        try{
            while(rs.next()){
                dto = new ProfessorDTO();

                dto.setProfessor_idx(rs.getInt("professor_idx"));
                dto.setDepartment(rs.getString("department"));
                dto.setPname(rs.getString("pname"));
                dto.setPhone(rs.getString("phone"));
                dto.setProfessor_code(rs.getString("professor_code"));

                list.add(dto);
            }
        }
        catch (SQLException e){
            System.out.println("값 복사도중 오류발생");
            e.printStackTrace();

        }

        try{
            stmt.close();
            rs.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("close 실패");
        }


        return list;
    }

    public  List<AdminDTO> selectAllAdmin(){
        ResultSet rs = null;
        List<AdminDTO> list = new ArrayList<AdminDTO>();

        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT *  FROM ADMIN");
            System.out.println("select 성공");
        }
        catch (SQLException e )
        {
            System.out.println("select 실패");
        }

        AdminDTO dto = null;
        try{
            while(rs.next()){
                dto = new AdminDTO();

                dto.setAdmin_idx(rs.getInt("admin_idx"));

                list.add(dto);
            }
        }
        catch (SQLException e){
            System.out.println("값 복사도중 오류발생");
            e.printStackTrace();

        }

        try{
            stmt.close();
            rs.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("close 실패");
        }


        return list;
    }

    public  List<UserDTO> selectAllUser(){
        ResultSet rs = null;
        List<UserDTO> list = new ArrayList<UserDTO>();

        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT *  FROM User");
            System.out.println("select 성공");
        }
        catch (SQLException e )
        {
            System.out.println("select 실패");
        }

        try{
            stmt.close();
            rs.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("close 실패");
        }

        return list;
    }
    
    //4.교수랑 학생을 교번이랑 학번으로 찾아서 조회하는 메소드 필요
    // => 교번으로 교수찾기, 학번으로 학생찾기 2개로 따로 진행
//    public List<StudentDTO> selectStudentWithCode(){
//
//    }



}
