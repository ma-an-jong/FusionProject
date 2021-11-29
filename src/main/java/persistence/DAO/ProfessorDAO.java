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

    public ProfessorDTO searchByProfessor_idx(int professor_idx) {

        List<ProfessorDTO> list = super.selectAllProfessor();

        for (ProfessorDTO dto : list) {
            int p_idx = dto.getProfessor_idx();
            if (p_idx == professor_idx) {
                return dto;
            }
        }
        return null;
    }

    public int selectByProfessor_code(String professor_code) {
        List<ProfessorDTO> list = super.selectAllProfessor();

        for (ProfessorDTO dto : list) {
            String p_code = dto.getProfessor_code();
            if (p_code.equals(professor_code)) {
                return dto.getProfessor_idx();
            }
        }

        return 0;

    }

    public ProfessorDTO searchByProfessor_code(String professor_code ) {

        List<ProfessorDTO> list = super.selectAllProfessor();

        for (ProfessorDTO dto : list) {
            String p_code = dto.getProfessor_code();
            if (p_code.equals(professor_code)) {
                return dto;
            }
        }
        return null;
    }

    public boolean updateProfessorInfo(ProfessorDTO professorDTO){
        // user정보 update
        ProfessorDTO pDTO = searchByProfessor_code(professorDTO.getProfessor_code());

        int userIdx = pDTO.getProfessor_idx();
        String id = pDTO.getId();
        String pw = pDTO.getPassword();
        String newId = id;
        String newPw = pw;

        // professorDTO.getId, .getPassword => 새로운 정보, (id,pw) => 기존 정보
        if(!id.equals(professorDTO.getId()) && pw.equals(professorDTO.getPassword())){
            newId = professorDTO.getId();

        }else if(id.equals(professorDTO.getId()) && !pw.equals(professorDTO.getPassword())){
            newPw = professorDTO.getPassword();

        }else if(!id.equals(professorDTO.getId()) && !pw.equals(professorDTO.getPassword())){
            newId = professorDTO.getId();
            newPw = professorDTO.getPassword();
        }

        if(!updateAccount(userIdx, newId, newPw))
        {
            return false;
        }

        // professor정보 update
        PreparedStatement pstmt = null;

        int idx = pDTO.getProfessor_idx();
        String newName = professorDTO.getPname();
        String newCode = professorDTO.getProfessor_code();
        String newDepartment = professorDTO.getDepartment();
        String newPhone = professorDTO.getPhone();

        try{
            pstmt = conn.prepareStatement("UPDATE professor SET pname = ?, professor_code = ?, department = ?, phone = ? WHERE professor_idx = ?");
            pstmt.setString(1 , newName);
            pstmt.setString(2, newCode);
            pstmt.setString(3, newDepartment);
            pstmt.setString(4, newPhone);
            pstmt.setInt(5, idx);

            pstmt.executeUpdate();
            conn.commit();
            System.out.println("교수정보가 수정되었습니다.");
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
            return false;
        }

        try{
            pstmt.close();
        }
        catch (SQLException e ){
            System.out.println("close 실패");
        }

        return true;
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
