package Server;

import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DTO.*;
import persistence.DAO.*;
import persistence.MyBatisConnectionFactory;
import Client.*;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {

    private static final int TYPE_DEFINED = 1;

    private Connection jdbcConn; //JDBC 연결
    private SqlSessionFactory sqlSessionFactory; //MYBATIS 연결

    private Socket socket;
    private BufferedReader in = null;
    private BufferedWriter out = null;

    public ServerThread(Socket socket)
    {
        this.socket = socket;

        jdbcConn = JDBCConnection.getConnection(JDBCConnection.url);
        sqlSessionFactory =  MyBatisConnectionFactory.getSqlSessionFactory();
        System.out.println("서버 Thread 생성");
    }

    @Override
    public void run()
    {
        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("SocketBuffer 가져오기 실패");
        }

        // TODO: 로그인 정보 요청
        
        boolean flag = true;

        Protocol protocol;
        String packetType;
        String packet[];

        while (flag)
        {
            try
            {
                packet = in.readLine().split(Protocol.splitter);
                packetType = packet[Protocol.TYPE_DEFINED_POS];
                protocol = new Protocol(packetType);

            }
            catch (IOException e)
            {
                System.out.println("버퍼 readline 실패");
                continue;
            }

            //case문 시작
            switch (packetType)
            {
                case Protocol.PT_EXIT: // 0번대
                {
                    writePacket(Protocol.PT_EXIT);
                    flag = false;
                    System.out.println("서버종료");
                    break;
                }
                case Protocol.PT_REQ_LOGIN: //TODO : 1번 - 2번
                {
                    System.out.println("클라이언트가 로그인 정보를 보냈습니다.");

                    String id = packet[Protocol.PT_LOGIN_ID_POS];
                    String password = packet[Protocol.PT_LOGIN_PASSWORD_POS];

                    UserDAO userDAO = new UserDAO(jdbcConn);
                    List<UserDTO> userList = userDAO.selectAllUser();

                    for(UserDTO dto : userList)
                    {
                        if(dto.getId() == id && dto.getPassword() == password )
                        {
                            if(dto.getCategory() == 'a')
                            {
                                protocol = new Protocol(Protocol.PT_LOGIN_RESULT);
                                packet = new String[Protocol.PT_LOGIN_RESULT_LENGTH];
                                packet[Protocol.PT_LOGIN_CODE_POS] = "2";
                                protocol.setPacket(packet);
                                writePacket(protocol.getPacket());
                                System.out.println("관리자 인증 성공");
                            }
                            else if(dto.getCategory() == 's')
                            {
                                protocol = new Protocol(Protocol.PT_LOGIN_RESULT);
                                packet = new String[Protocol.PT_LOGIN_RESULT_LENGTH];
                                packet[Protocol.PT_LOGIN_CODE_POS] = "0";
                                protocol.setPacket(packet);
                                writePacket(protocol.getPacket());
                            }
                            else if(dto.getCategory() == 'p') {
                                protocol = new Protocol(Protocol.PT_LOGIN_RESULT);
                                packet = new String[Protocol.PT_LOGIN_RESULT_LENGTH];
                                packet[Protocol.PT_LOGIN_CODE_POS] = "1";
                                protocol.setPacket(packet);
                                writePacket(protocol.getPacket());
                                System.out.println("교수 인증 성공");
                            }
                            else
                            {
                                System.out.println("unsupported");
                            }
                            System.out.println("로그인 성공");
                            break;
                        }

                    }

                    protocol = new Protocol(Protocol.PT_LOGIN_RESULT);
                    packet = new String[Protocol.PT_LOGIN_RESULT_LENGTH];
                    packet[Protocol.PT_LOGIN_CODE_POS] = "3";
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("일치하는 id/password가 없습니다");
                    break;
                }
                case Protocol.PT_REQ_SENDFILE: //TODO: 3번 -4번
                {

                    break;
                }
                case Protocol.PT_REQ_FILE: //TODO: 5번 - 6번
                {
                    break;
                }
                //TODO : 7번
                case Protocol.CS_REQ_MYSUBJECT_VIEW: //내 수강 목록 조회
                {
                    //클라이언트가 학번을 통해서 정보 조회하여 전달
                    System.out.println("클라이언트가 본인의 정보 요청");

                    try {
                        CourseRegistration dao = new CourseRegistration(sqlSessionFactory);

                        String key = packet[Protocol.PT_CLIENT_KEY];
                        List<CourseDetailsDTO> list = dao.selectMyCourse(key);
                        packet = new String[list.size() + 1];
                        packet[0] = "10";
                        int index = 1;
                        for (CourseDetailsDTO dto : list) {
                            packet[index] = dto.toString();
                            index++;
                        }

                        protocol = new Protocol(Protocol.SC_RES_MYSUBJECT_VIEW);
                        protocol.setPacket(packet);
                        System.out.println("데이터 전송 승인");
                        writePacket(protocol.getPacket());
                    }
                    catch (Exception e)
                    {
                        protocol = new Protocol(Protocol.SC_RES_MYSUBJECT_VIEW);
                        packet = new String[1];
                        packet[0] = "11";
                        protocol.setPacket(packet);
                        System.out.println("데이터 전송 거절");
                        writePacket(protocol.getPacket());
                    }

                    break;
                }
                //TODO:학생 개인정보 요청 본인의 카테고리까지 전송하는걸로
                case Protocol.CS_REQ_PERSONALINFO_VIEW:
                {
                    char category = packet[Protocol.PT_PERSONALINFO_CATEGORY_POS].charAt(0);
                    String key = packet[Protocol.PT_PERSONALINFO_KEY_POS];

                    if(category == 's')
                    {
                        StudentDAO studentDAO = new StudentDAO(jdbcConn);
                        StudentDTO dto = studentDAO.searchByStudent_code(key);

                        if(dto !=null)
                        {
                            protocol = new Protocol(Protocol.SC_RES_PERSONALINFO_VIEW);
                            String data = "14" + dto.getSname() + Protocol.splitter;
                            data += dto.getDepartment() + Protocol.splitter;
                            data += dto.getPhone() + Protocol.splitter;
                            data += dto.getGrade();
                            packet = data.split(Protocol.splitter);
                            protocol.setPacket(packet);
                            writePacket(protocol.getPacket());
                            break;
                        }
                        else
                        {
                            System.out.println("해당 정보를 찾을수 없습니다.");
                            break;
                        }
                    }
                    else if(category == 'p')
                    {
                        ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                        ProfessorDTO dto = professorDAO.searchByProfessor_code(key);

                        if(dto !=null)
                        {
                            protocol = new Protocol(Protocol.SC_RES_PERSONALINFO_VIEW);
                            String data ="14" + dto.getPname() + Protocol.splitter;
                            data += dto.getDepartment() + Protocol.splitter;
                            data += dto.getPhone();
                            packet = data.split(Protocol.splitter);
                            protocol.setPacket(packet);
                            writePacket(protocol.getPacket());
                        }
                        else
                        {
                            protocol = new Protocol(Protocol.SC_RES_PERSONALINFO_VIEW);
                            packet = new String[1];
                            packet[0] = "15";
                            protocol.setPacket(packet);
                            writePacket(protocol.getPacket());
                            System.out.println("해당 정보를 찾을수 없습니다.");
                            break;
                        }

                    }
                    else
                    {
                        System.out.println("잘못된 카테고리");
                        break;
                    }
                    break;

                }
                //TODO:개인정보 수정 요청하면 데이터전송 어떤거 하는지??
                case Protocol.CS_REQ_PERSONALINFO_UPDATE:
                {
                    String key = packet[Protocol.PT_PERSONALINFO_KEY_POS];
                    String name = packet[Protocol.PT_PERSONALINFO_NAME_POS];
                    String dept = packet[Protocol.PT_PERSONALINFO_DEPARTMENT_POS];
                    String phone = packet[Protocol.PT_PERSONALINFO_PHONE_POS];
                    int grade = Integer.parseInt(packet[Protocol.PT_PERSONALINFO_GRADE_POS]);

                    StudentDAO dao = new StudentDAO(jdbcConn);

                    String code;
                    try
                    { //업데이트 할거 추가
                        dao.updateName(key,name);
                        code = "0";
                        protocol = new Protocol(Protocol.SC_RES_PERSONALINFO_UPDATE);
                    }
                    catch (Exception e)
                    {
                        code = "1";
                        protocol = new Protocol(Protocol.SC_RES_PERSONALINFO_UPDATE);
                    }

                    packet = new String[1];
                    packet[0] = code;
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_TIMETABLE_VIEW: //TODO:본인 시간표 조회 그러나 본인 수강 내역 조회와 내용은 같음
                {
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);

                    String key = packet[Protocol.PT_TIMETABLE_KEY_POS];
                    List<CourseDetailsDTO> list = dao.selectMyCourse(key);
                    packet = new String[list.size() + 1];
                    packet[0] = "10";
                    int index = 1;
                    for (CourseDetailsDTO dto : list) {
                        packet[index] = dto.toString();
                        index++;
                    }

                    protocol = new Protocol(Protocol.SC_RES_TIMETABLE_VIEW);
                    protocol.setPacket(packet);
                    System.out.println("데이터 전송 승인");
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_OPENSUBJECT_VIEW:
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    List<Lecture_Subject_ProfessorDTO> list = dao.selectAll();

                    packet = new String[list.size() + 1];
                    int index= 1;
                    for(Lecture_Subject_ProfessorDTO dto : list){
                        packet[index]  = dto.toString();
                        index ++;
                    }
                    packet[0] = "C";
                    protocol = new Protocol(Protocol.SC_RES_OPENSUBJECT_VIEW);
                    protocol.setPacket(packet);
                    System.out.println("개설 교과목 조회 성공");
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_SYLLABUS_VIEW: //포기
                {
                    break;
                }
                case Protocol.CS_REQ_TEACHING_VIEW: //담당 교과목 목록 조회
                {

                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_TEACHING_KEY_POS];
                    List<Lecture_Subject_ProfessorDTO> list = dao.selectByProfessor(key);
                    int index = 1;
                    packet = new String[list.size() + 1];
                    packet[0] = "4";

                    for(Lecture_Subject_ProfessorDTO dto : list)
                    {
                        packet[index++] = dto.toString();
                    }

                    protocol = new Protocol(Protocol.SC_RES_TEACHING_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("교수 담당 과목 조회 성공");
                    break;
                }
                case Protocol.CS_REQ_SYLLABUS_ENROLL: //강의계획서 관련된것은 제일 나중에
                {
                    break;
                }
                case Protocol.CS_REQ_MYSTUDENT_VIEW:  //담당교과목 수강신청 학생 목록 조회 요청 TODO: 페이징 기능인데
                {
                    String key = packet[Protocol.PT_MYSTUDENT_KEY_POS];
                    int pageNum = Integer.parseInt(packet[Protocol.PT_MYSTUDENT_PAGENUM_POS]);
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);

                    List<StudentDTO> list = dao.selectWithPaging(key,pageNum);
                    packet = new String[list.size() + 1];
                    packet[0] = "8";
                    int index = 1;

                    for(StudentDTO d : list)
                    {
                        //2.학생정보 담을거 student_code,sname,department,grade,phone 만 정리
                        packet[index++] = d.toString();
                    }

                    protocol = new Protocol(Protocol.SC_RES_MYSTUDENT_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;




                }
                case Protocol.CS_REQ_TEACHINGTABLE_VIEW: //TODO:담당 교과목 시간표 조회 = 담당 교과목 목록 조회
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_TEACHING_KEY_POS];

                    List<Lecture_Subject_ProfessorDTO> list = dao.selectByProfessor(key);
                    int index = 1;
                    packet = new String[list.size() + 1];
                    packet[0] = "A";

                    for(Lecture_Subject_ProfessorDTO dto : list)
                    {
                        packet[index++] = dto.toString();
                    }

                    protocol = new Protocol(Protocol.SC_RES_TEACHINGTABLE_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;


                }
                case Protocol.CS_REQ_MEMBER_VIEW: //4.교수랑 학생을 교번이랑 학번으로 찾아서 조회하는 메소드 필요
                {
                    AdminDAO dao = new AdminDAO(jdbcConn);
                    //4.교수랑 학생을 교번이랑 학번으로 찾아서 조회하는 메소드 필요
                    //ProfessorDTO pdto = dao.selectAllProfessor();
                    //StudentDTO sdto = dao.selectAllStudent();
                    int index = 1;
                    packet = new String[2];
                    packet[0] = "0";
                    //3.관리자가 교수랑 학생정보를 조회할때 이쁘게 출력할 정보들

                    //packet[1] = pdto or sdto

                    protocol = new Protocol(Protocol.SC_RES_ALLMEMBER_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_ALLMEMBER_VIEW://모든 교수,학생 정보 조회 요청
                {
                    AdminDAO dao = new AdminDAO(jdbcConn);
                    List<ProfessorDTO> plist = dao.selectAllProfessor();
                    List<StudentDTO> slist = dao.selectAllStudent();
                    int index = 1;
                    packet = new String[plist.size() + slist.size() + 1];
                    packet[0] = "16";
                    //3.관리자가 교수랑 학생정보를 조회할때 이쁘게 출력할 정보들
                    for(ProfessorDTO dto : plist)
                    {
                        packet[index++] = dto.toString();
                    }
                    for(StudentDTO dto : slist)
                    {
                        packet[index++] = dto.toString();
                    }

                    protocol = new Protocol(Protocol.SC_RES_ALLMEMBER_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;


                }
                case Protocol.CS_REQ_MEMBER_ENROLL:
                    //TODO:사용자 계정 등록 요청 학생 만들건지 교수 만들건지 등 데이터 부족
                {
                    //교원번호 or 학번, 이름, id, password + 부서 학년 전화번호 - id
                    String key = packet[Protocol.PT_MEMBER_ENROLL_KEY_POS];
                    String name = packet[Protocol.PT_MEMBER_ENROLL_NAME_POS];
                    String password = packet[Protocol.PT_MEMBER_ENROLL_PASSWORD_POS];
                    String department ="";
                    int grade = 1;
                    String phone ="";
                    AdminDAO dao = new AdminDAO(jdbcConn);

                    dao.createStudent(key,password,department,name,grade,phone);
                    packet = new String[1];
                    packet[0] = "E";
                    protocol = new Protocol(Protocol.SC_RES_MEMBER_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("사용자 계정 생성 완료");
                    break;

                }


            }
        }

        }

    //방법론
    public void writePacket(String source)
    {
        try
        {
            out.write(source + "\n");
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("write에 실패하였습니다.");
        }
    }

}
