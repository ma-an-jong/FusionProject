package persistence.DAO;

import persistence.DTO.ProfessorDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProfessorDAO extends UserDAO {

    public ProfessorDAO(Connection conn){
        super(conn);
    }

    public ProfessorDTO searchByProfessor_code(String professor_code ){

        List<ProfessorDTO> list = super.selectAllProfessor();

        for(ProfessorDTO dto: list){
            String p_code = dto.getProfessor_code();
            if(p_code == professor_code){
                return dto;
            }
        }
        return null;
    }

    public void updatePhone(int idx,String newPhone){

        PreparedStatement pstmt = null;

        try{
            pstmt = conn.prepareStatement("UPDATE Professor SET phone = ? WHERE professor_idx = ?");

            pstmt.setString(1,newPhone);
            pstmt.setInt(2,idx);

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
