package persistence.DAO;

import org.apache.ibatis.session.SqlSessionFactory;
import persistence.Mapper.LectureRegistrationDateMapper;

import java.sql.*;


public class AdminDAO extends UserDAO{

    public AdminDAO(Connection conn){
        super(conn);
    }



    /*

        교과목 생성/수정/삭제

        강의 계획서 입력 기간 설정
          설정된 입력 기간 내에만 강의 계획서 입력 기능 활성화

        학년별 수강 신청 기간 설정
          - 학년 단위로 교과목 수강 신청 기간을 설정할 수 있음

     */

    //교수·학생 계정 생성

    public void createStudent(String student_code,String password,String department,String sname,int grade, String phone)  {

        int idx = super.createUser(student_code,password,STUDENT_CATEGORY);
        ResultSet rs = null;

        try(PreparedStatement pstmt = conn.prepareStatement("INSERT INTO STUDENT(student_idx,department,sname,grade,phone,student_code)VALUES(? , ? , ? , ? , ? , ? )"))
        {
            System.out.println("생성된 유저의 기본키값: " + idx);
            pstmt.setInt(1, idx);
            pstmt.setString(2,department);
            pstmt.setString(3,sname);
            pstmt.setInt(4,grade);
            pstmt.setString(5,phone);
            pstmt.setString(6,student_code);

            pstmt.executeUpdate();
            conn.commit();

            System.out.println("Insert 성공");

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



    }

    public void createProfessor(String professor_code,String password,String department,String pname,String phone)  {

        int idx = super.createUser(professor_code,password,PROFESSOR_CATEGORY);

        try(PreparedStatement pstmt = conn.prepareStatement("INSERT INTO PROFESSOR(professor_idx,department,pname,phone,professor_code)VALUES(? , ? , ? , ?,? )"))
        {
            System.out.println("생성된 유저의 기본키값: " + idx);
            pstmt.setInt(1,idx);
            pstmt.setString(2,department);
            pstmt.setString(3,pname);
            pstmt.setString(4,phone);
            pstmt.setString(5,professor_code);

            pstmt.executeUpdate();
            conn.commit();

            System.out.println("Insert 성공");

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

    }

    public void createAdmin(String id,String password){

        int idx = super.createUser(id,password,ADMIN_CATEGORY);

        try(PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ADMIN(admin_idx)VALUES(?)"))
        {
            System.out.println("생성된 유저의 기본키값: " + idx);
            pstmt.setInt(1,idx);

            pstmt.executeUpdate();
            conn.commit();

            System.out.println("Insert 성공");

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

    }



}
