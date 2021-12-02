package Server;

import Client.Protocol;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DAO.*;
import persistence.DTO.*;
import persistence.MyBatisConnectionFactory;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServerThread extends Thread {

    //접속중인 클라이언트 수 확인
    private static int clientNum = 0;


    private Connection jdbcConn; //JDBC 연결
    private SqlSessionFactory sqlSessionFactory; //MYBATIS 연결

    private Socket socket; //클라이언트 소켓 및 입출력 버퍼
    private BufferedReader in = null;
    private BufferedWriter out = null;

    //서버 쓰레드를 생성하면 DB연결위한 커넥션 생성 및 소켓 연결
    public ServerThread(Socket socket)
    {
        this.socket = socket;
        jdbcConn = JDBCConnection.getConnection(JDBCConnection.url); //JDBC connetion
        sqlSessionFactory =  MyBatisConnectionFactory.getSqlSessionFactory(); //MyBatis Connection
        System.out.println("서버 Thread 생성");
    }

    @Override
    public void run()
    {
        //클라이언트 접속 후 실행
        clientNum++;
        System.out.println("접속중인 클라이언트 수:" + clientNum);

        //입출력 버퍼 연결
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
        
        
        boolean flag = true;
        Protocol protocol; //프로토콜
        String packetType; //프로토콜 타입 저장할 변수
        String packet[]; //패킷

        while (flag)
        {
            try
            {
                //버퍼에서 데이터 읽어오기
                packet = in.readLine().split(Protocol.splitter);
                packetType = packet[Protocol.TYPE_DEFINED_POS];
                System.out.println(packetType);
                protocol = new Protocol(packetType);
            }
            catch (IOException e)
            {
                continue;
            }

            //case문 시작
            switch (packetType)
            {
                case Protocol.PT_EXIT: //클라이언트 종료
                {
                    flag = false; //플래그 false로 반복문 제어
                    clientNum--;
                    packet = new String[1];
                    packet[0] = "0"; //응답코드
                    protocol = new Protocol(Protocol.PT_EXIT); //새 프로토콜 생성
                    protocol.setPacket(packet); //프로토콜에 패킷 설정
                    writePacket(protocol.getPacket()); //프로토콜 타입에 따른 패킷을 출력버퍼에 작성
                    System.out.println("로그아웃 성공");
                    System.out.println("현재 접속중인 클라이언트 수: "+ clientNum);
                    break;
                }
                case Protocol.PT_REQ_LOGIN: //로그인 요청
                {
                    System.out.println("클라이언트가 로그인 정보를 보냈습니다.");
                    //데이터 얻어오기
                    String id = packet[Protocol.PT_LOGIN_ID_POS];
                    String password = packet[Protocol.PT_LOGIN_PASSWORD_POS];
                    //유저정보 얻어와서 동일한 유저가 있는지 비교
                    UserDAO userDAO = new UserDAO(jdbcConn);
                    List<UserDTO> userList = userDAO.selectAllUser();
                    boolean except = true;

                    for(UserDTO dto : userList)
                    {
                        if(dto.getId().equals(id) && dto.getPassword().equals(password) )
                        { //아이디 비밀번호 정보 존재하면 카테고리에 따라서 클라이언트에 데이터를 보냄
                            except = false;
                            packet = new String[5];
                            if(dto.getCategory() == 'a')
                            {
                                packet[0] = "2";
                                packet[Protocol.PT_LOGIN_CATEGORY_POS] ="a";
                                System.out.println("관리자 인증 성공");
                            }
                            else if(dto.getCategory() == 's')
                            {
                                StudentDAO studentDAO = new StudentDAO(jdbcConn);
                                StudentDTO studentDTO = studentDAO.searchByStudent_idx(dto.getIdx());
                                packet[0] = "0";
                                packet[Protocol.PT_LOGIN_KEY_POS] = studentDTO.getStudent_code();
                                packet[Protocol.PT_LOGIN_GRADE_POS] = String.valueOf(studentDTO.getGrade());
                                packet[Protocol.PT_LOGIN_CATEGORY_POS] ="s";
                                packet[Protocol.PT_LOGIN_CODE_POS] = studentDTO.getStudent_code();
                                System.out.println("학생 인증 성공");
                            }
                            else if(dto.getCategory() == 'p')
                            {
                                ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                                ProfessorDTO professorDTO = professorDAO.searchByProfessor_idx(dto.getIdx());
                                packet[0] = "1";
                                packet[Protocol.PT_LOGIN_KEY_POS] = professorDTO.getProfessor_code();
                                packet[Protocol.PT_LOGIN_CATEGORY_POS] ="p";
                                packet[Protocol.PT_LOGIN_CODE_POS] = professorDTO.getProfessor_code();
                                System.out.println("교수 인증 성공");
                            }
                            else
                            {
                                packet[0] = "3";
                                System.out.println("unsupported");
                            }

                            protocol = new Protocol(Protocol.PT_LOGIN_RESULT);
                            protocol.setPacket(packet);
                            writePacket(protocol.getPacket());
                            System.out.println("로그인 성공");
                            break;
                        }
                    }
                    //아이디 비밀번호가 없으면 요청 실패 보냄
                    if(except) {
                        packet = new String[1];
                        packet[0] = "3";
                        protocol = new Protocol(Protocol.PT_LOGIN_RESULT);
                        protocol.setPacket(packet);
                        writePacket(protocol.getPacket());
                        System.out.println("일치하는 id/password가 없습니다");
                    }
                    break;
                }
                case Protocol.CS_REQ_REGISTRATION: //수강 신청 요청 인증
                {
                    //학년 정보 얻어오기
                    int grade = Integer.parseInt(packet[Protocol.PT_REGISTRATION_GRADE_POS]);
                    //서버기준 현재시간 얻어오기
                    LocalDate localDate = LocalDate.now();
                    SimpleDateFormat sdf=new SimpleDateFormat(localDate.toString());
                    String ss=sdf.format(new java.util.Date());
                    Date today= Date.valueOf(ss);

                    LectureRegistrationDateDAO dao = new LectureRegistrationDateDAO(sqlSessionFactory);
                    LectureRegistrationDateDTO dto = dao.selectByGrade(grade);

                    packet = new String[1];
                    //서버 현재 시간이 수강신청 가능 기간인지 확인하여 인증 여부 전달
                    if(today.compareTo(dto.getStart_date()) >=0  && today.compareTo(dto.getEnd_date()) <= 0 ){
                        packet[0] = "6";
                        System.out.println("인증 성공");
                    }
                    else{
                        packet[0] = "7";
                        System.out.println("인증 실패");
                    }

                    protocol = new Protocol(Protocol.SC_RES_REGISTRATION);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_MYSUBJECT_ENROLL: //수강신청 등록
                {
                    // 임계영역에 동시에 접근하지 못하게 동기화 블록 생성
                    synchronized (this){
                        CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                        StudentDAO studentDAO = new StudentDAO(jdbcConn);
                        LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                        
                        String studentCode = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];
                        String subjectCode = packet[Protocol.PT_MYSUBJECT_SUBJECT_CODE_POS];
                        
                        //학번으로 해당 학생정보 얻어오기, 과목코드로 과목정보 얻어오기
                        StudentDTO studentDTO = studentDAO.searchByStudent_code(studentCode);
                        LectureDTO lectureDTO = lectureDAO.searchBySubjectCode(subjectCode);

                        //수강신청할 과목 및 학생 정보를 DTO에입력 
                        CourseDetailsDTO dto = new CourseDetailsDTO();
                        dto.setStudent_idx(studentDTO.getStudent_idx());
                        dto.setStudent_code(studentCode);
                        dto.setLecture_professor_idx(lectureDTO.getLecture_professor_idx());
                        dto.setGrade(studentDTO.getGrade());
                        dto.setLecture_time(lectureDTO.getLecture_time());
                        dto.setLecture_idx(lectureDTO.getLecture_idx());
                        dto.setCurrent(lectureDTO.getCurrent());
                        dto.setMaximum(lectureDTO.getMaximum());

                        boolean bool;
                        //수강신청
                        bool = dao.addCoure(dto);

                        packet = new String[1];
    
                        packet[0] = bool ? "A" : "B";

                        protocol = new Protocol(Protocol.SC_RES_MYSUBJECT_ENROLL);
                        protocol.setPacket(packet);
                        writePacket(protocol.getPacket());
                        break;
                    }
                }
                case Protocol.CS_REQ_MYSUBJECT_VIEW: //내 수강 목록 조회 
                {
                    System.out.println("클라이언트가 본인의 정보 요청");
                    try
                    {
                        CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                        String key = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];
                        
                        //학번으로 내 수강 목록 얻어오기
                        List<CourseDetailsDTO> list = dao.selectMyCourse(key);

                        packet = new String[list.size() + 1];
                        packet[0] = list != null ? "10":"11";
                        int index = 1;

                        for (CourseDetailsDTO dto : list)
                        {
                            packet[index] = dto.getLectureInfo();
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
                case Protocol.CS_REQ_MYSUBJECT_DELETE: //학생 수강삭제 
                {
                    String subjectCode = packet[Protocol.PT_MYSUBJECT_SUBJECT_CODE_POS];
                    String studentCode = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];

                    System.out.println(subjectCode + " " + studentCode);

                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                    
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
                    //학번,과목코드로 과목 정보 및 학생 정보 얻어오기
                    int subjectIdx = subjectDAO.selectByCode(subjectCode);
                    StudentDTO studentDTO =  studentDAO.searchByStudent_code(studentCode);
                    //학생정보 과목코드 입력
                    CourseDetailsDTO dto = new CourseDetailsDTO();
                    dto.setStudent_idx(studentDTO.getStudent_idx());
                    dto.setStudent_code(studentDTO.getStudent_code());
                    dto.setLecture_idx(subjectIdx);
                    //삭제 요청
                    boolean bool = dao.deleteCourse(dto);
                    packet = new String[1];

                    if(bool)
                    {
                        packet[0] = "8";
                    }
                    else
                    {
                        packet[0] = "9";
                    }

                    protocol = new Protocol(Protocol.SC_RES_MYSUBJECT_DELETE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                } 
                case Protocol.CS_REQ_PROFESSOR_PERSONALINFO_VIEW:// 교수가 개인정보 요청
                {
                    String key = packet[Protocol.PT_PERSONALINFO_KEY_POS];
                    //교수 코드로 교수 정보 얻어오기
                    ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                    ProfessorDTO dto = professorDAO.searchByProfessor_code(key);
                    
                    packet = new String[2];
                    
                    if(dto !=null)
                    {
                        packet[0] = "14";
                        packet[1] = dto.getProfessorInfoForAdmin();
                    }
                    else
                    {
                        packet[0] = "15";
                        System.out.println("해당 정보를 찾을수 없습니다.");
                    }

                    protocol = new Protocol(Protocol.SC_RES_PROFESSOR_PERSONALINFO_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_STUDENT_PERSONALINFO_VIEW: //학생이 개인정보 요청
                {
                    String key = packet[Protocol.PT_PERSONALINFO_KEY_POS];
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
                    //학번으로 학생 정보 얻어오기
                    StudentDTO dto = studentDAO.searchByStudent_code(key);
                    packet = new String[2];
                    if(dto !=null)
                    {
                        packet[0]  = "14";
                        packet[1] = dto.getStudentInfoForAdmin();
                    }
                    else
                    {
                        System.out.println("해당 정보를 찾을수 없습니다.");
                        packet[0] = "15";
                    }

                    protocol = new Protocol(Protocol.SC_RES_STUDENT_PERSONALINFO_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_PROFESSOR_PERSONALINFO_UPDATE: //교수 개인정보 업뎃
                {
                    //교번으로 교수 정보 얻어오기
                    ProfessorDAO dao = new ProfessorDAO(jdbcConn);
                    String key = packet[Protocol.PT_PERSONALINFO_CODE_POS];
                    ProfessorDTO pDTO = dao.searchByProfessor_code(key);
                    //입력받지 못한 데이터는 갱신x
                    if(!packet[Protocol.PT_PERSONALINFO_ID_POS].equals("null"))
                        pDTO.setId(packet[Protocol.PT_PERSONALINFO_ID_POS]);

                    if(!packet[Protocol.PT_PERSONALINFO_PASSWORD_POS].equals("null"))
                        pDTO.setPassword( packet[Protocol.PT_PERSONALINFO_PASSWORD_POS]);

                    if(!packet[Protocol.PT_PERSONALINFO_NAME_POS].equals("null"))
                        pDTO.setPname(packet[Protocol.PT_PERSONALINFO_NAME_POS]);

                    if(!packet[Protocol.PT_PERSONALINFO_DEPARTMENT_POS].equals("null"))
                        pDTO.setDepartment(packet[Protocol.PT_PERSONALINFO_DEPARTMENT_POS]);

                    if(!packet[Protocol.PT_PERSONALINFO_PHONE_POS].equals("null")){
                        pDTO.setPhone(packet[Protocol.PT_PERSONALINFO_PHONE_POS]);
                    }
                    //업데이트
                    String code;
                    if(dao.updateProfessorInfo(pDTO)){
                        code = "0";
                    }
                    else
                    {
                        code = "1";
                    }

                    protocol = new Protocol(Protocol.SC_RES_PROFESSOR_PERSONALINFO_UPDATE);
                    packet = new String[1];
                    packet[0] = code;
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_STUDENT_PERSONALINFO_UPDATE: //학생 개인정보 업데이트
                {
                    StudentDAO dao = new StudentDAO(jdbcConn);
                    String key = packet[Protocol.PT_PERSONALINFO_CODE_POS];
                    //학번으로 학생정보 얻어오기
                    StudentDTO studentDTO = dao.searchByStudent_code(key);
                    //입력받지 못한 정보는 업데이트x
                    if(!packet[Protocol.PT_PERSONALINFO_ID_POS].equals("null"))
                        studentDTO.setId(packet[Protocol.PT_PERSONALINFO_ID_POS]);
                    if(!packet[Protocol.PT_PERSONALINFO_PASSWORD_POS].equals("null"))
                        studentDTO.setPassword(packet[Protocol.PT_PERSONALINFO_PASSWORD_POS]);
                    if(!packet[Protocol.PT_PERSONALINFO_NAME_POS].equals("null"))
                        studentDTO.setSname(packet[Protocol.PT_PERSONALINFO_NAME_POS]);
                    if(!packet[Protocol.PT_PERSONALINFO_DEPARTMENT_POS].equals("null"))
                        studentDTO.setDepartment(packet[Protocol.PT_PERSONALINFO_DEPARTMENT_POS]);
                    if(!packet[Protocol.PT_PERSONALINFO_PHONE_POS].equals("null"))
                        studentDTO.setPhone(packet[Protocol.PT_PERSONALINFO_PHONE_POS]);
                    if(!packet[Protocol.PT_PERSONALINFO_GRADE_POS].equals("null"))
                        studentDTO.setGrade(Integer.parseInt(packet[Protocol.PT_PERSONALINFO_GRADE_POS]));

                    String code;
                    //업데이트
                    if(dao.updateStudentInfo(studentDTO))
                    {
                        code = "0";
                    }
                    else
                    {
                        code = "1";
                    }

                    protocol = new Protocol(Protocol.SC_RES_STUDENT_PERSONALINFO_UPDATE);
                    packet = new String[1];
                    packet[0] = code;
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_TIMETABLE_VIEW: //시간표 조회
                {
                    //강의 시간 포멧은 금34/목67
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);

                    String key = packet[Protocol.PT_TIMETABLE_KEY_POS];
                    List<CourseDetailsDTO> list = dao.selectMyCourse(key);
                    List<TimeTableInfo> timeTable = new LinkedList<TimeTableInfo>();

                    for (CourseDetailsDTO dto : list) {
                        //강의 시간을 나눠서 TimeTableInfo 클래스 생성 및 리스트에 저장
                        String time[] = dto.getLecture_time().split("/");
                        String name =dto.getSubject_name();
                        String classRoom = dto.getClassroom();
                        for(int i = 0; i < time.length;i++){
                            timeTable.add(new TimeTableInfo(name,time[i],classRoom));
                        }
                    }

                    //강의 시간을 월 ~ 금 순으로 정렬
                    Collections.sort(timeTable);   

                    packet = new String[timeTable.size() + 1];
                    int index = 1;
                    for(TimeTableInfo element : timeTable){
                        packet[index++] = element.toString();
                    }

                    packet[0] = "10";
                    protocol = new Protocol(Protocol.SC_RES_TIMETABLE_VIEW);
                    protocol.setPacket(packet);
                    System.out.println("데이터 전송 승인");
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_LECTURE_VIEW: //개설 교과목 목록 조회
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    //모든 개설교과목 목록 조회 리스트에 저장
                    List<Lecture_Subject_ProfessorDTO> list = dao.selectAll();
                    packet = new String[list.size() + 1];
                    int index= 1;

                    for(Lecture_Subject_ProfessorDTO dto : list)
                    {
                        packet[index++]  = dto.printInfo();
                    }

                    packet[0] = list != null ? "C" : "D";
                    protocol = new Protocol(Protocol.SC_RES_LECTURE_VIEW);
                    protocol.setPacket(packet);
                    System.out.println("개설 교과목 조회");
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_SYLLABUS_VIEW: //강의 계획서 조회 학생
                {
                    String key = packet[Protocol.PT_SYLLABUS_VIEW_KEY_POS];
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    // 과목코드로 강의계획서가 저장된 경로를 얻어옴
                    String path = lectureDAO.searchSyllabusBySubjectCode(key);
                    String code = "E";
                    String context = new String();
                    //경로에 저장된 파일을 읽어옴
                    try (BufferedReader br = new BufferedReader(
                                             new InputStreamReader(
                                             new FileInputStream(path),"UTF-8")))
                    {
                        context = new String();
                        int s;
                        while((s = br.read()) != -1 )
                        {
                            context += (char)s;
                        }

                    }
                    catch (IOException e)
                    {
                        code = "F";
                    }

                    packet = new String[2];
                    packet[0] = code;
                    packet[Protocol.PT_SYLLABUS_VIEW_CONTEXT_POS] = context;
                    protocol = new Protocol(Protocol.SC_RES_SYLLABUS_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_TEACHING_VIEW: //담당 교과목 목록 조회
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_TEACHING_KEY_POS];
                    //교번으로 담당 교과목정보를 리스트로 얻어옴
                    List<Lecture_Subject_ProfessorDTO> list = dao.selectByProfessor(key);
                    int index = 1;
                    packet = new String[list.size() + 1];
                    packet[0] = list != null ? "4":"5";

                    for(Lecture_Subject_ProfessorDTO dto : list)
                    {
                        packet[index++] = dto.printInfo();

                    }

                    protocol = new Protocol(Protocol.SC_RES_TEACHING_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("교수 담당 과목 조회");
                    break;
                }
                case Protocol.CS_REQ_SYLLABUS_ENROLL: //강의 계획서 등록기간 인증
                {
                    SyllabusInsertTimeDAO dao = new SyllabusInsertTimeDAO(sqlSessionFactory);
                    //강의 계획서 등록기간 정보를 얻어옴
                    SyllabusInsertTimeDTO dto = dao.selectAll();
                    //서버기준 현재시간을 얻어옴
                    LocalDate localDate = LocalDate.now();
                    SimpleDateFormat sdf = new SimpleDateFormat(localDate.toString());
                    Date today = Date.valueOf(sdf.format(new java.util.Date()));

                    packet = new String[1];
                    //등록 가능 여부에 따라 인증
                    if(today.compareTo(dto.getStart_date()) >= 0 && today.compareTo(dto.getEnd_date()) <= 0 )
                    {
                        packet[0] = "6";
                        System.out.println("인증 성공");
                    }
                    else
                    {
                        packet[0] = "7";
                        System.out.println("인증 실패");
                    }

                    protocol = new Protocol(Protocol.SC_RES_SYLLABUS_FILE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_SYLLABUS_FILE: // 강의 계획서 등록  교수
                {
                    String code = packet[Protocol.PT_SYLLABUS_FILE_KEY_POS];
                    String context = packet[Protocol.PT_SYLLABUS_FILE_CONTEXT_POS];
                    //클라리언트로 넘어온 정보를 파일이름을 과목코드로 설정하여 저장
                    String path = Protocol.staticPath + code + ".txt";
                    System.out.println(context);

                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    //강의 계획서가 존재하면 삽입 없으면 갱신
                    boolean bool = lectureDAO.updateSyllabus(code,path);
                    packet = new String[1];

                    if(!bool){
                        packet[0] = "3";
                        protocol = new Protocol(Protocol.SC_RES_SYLLABUS_ENROLL);
                        protocol.setPacket(packet);
                        writePacket(protocol.getPacket());
                        break;
                    }
                    
                    //DB갱신에 성공하면 입력받은 내용을 파일로 저장
                    try (BufferedWriter bw = new BufferedWriter(
                                             new OutputStreamWriter(
                                             new FileOutputStream(path),"UTF-8")))
                    {
                        bw.write(context);
                        bw.flush();
                        packet[0] = "2";
                    }
                    catch (IOException e)
                    {
                        packet[0] = "3";
                        System.out.println("파일 입출력 실패");
                    }

                    protocol = new Protocol(Protocol.SC_RES_SYLLABUS_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_SYLLABUS_DELETE : //강의 계획서 삭제 교수
                {
                    String code = packet[Protocol.PT_SYLLABUS_FILE_KEY_POS];
                    //삭제할 강의 계획서의 과목코드를 얻어와서 DB에 지정된 경로를 삭제
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    String path = lectureDAO.deleteSyllabusBySubjectCode(code);
                    File file = new File(path);
                    packet = new String[1];
                    //삭제된 경로에 저장된 파일을 삭제
                    if(file.exists()){
                        file.delete();
                        packet[0] = "0";
                    }
                    else{
                        packet[0] = "1";
                    }

                    protocol = new Protocol(Protocol.SC_RES_SYLLABUS_DELETE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_MYSYLLABUS_VIEW: //담당교과목 강의계획서 조회 요청 교수
                {
                    String key = packet[Protocol.PT_SYLLABUS_VIEW_KEY_POS];
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    //과목코드로 강의계획서 조회
                    String path = lectureDAO.searchSyllabusBySubjectCode(key);
                    String code = "E";
                    String context = new String();
                    //지정된 경로에 있는 파일 읽어오기
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(path),"UTF-8")))
                    {
                        context = new String();
                        int s;

                        while((s = br.read()) != -1 )
                        {
                            context +=  (char)s;
                        }

                    }
                    catch (IOException e)
                    {
                        code = "F";
                    }

                    packet = new String[2];
                    packet[0] = code;
                    packet[Protocol.PT_SYLLABUS_VIEW_CONTEXT_POS] = context;
                    protocol = new Protocol(Protocol.SC_RES_MYSYLLABUS_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_MYSTUDENT_VIEW:  //담당교과목 수강신청 학생 목록 조회 요청
                {
                    String key = packet[Protocol.PT_MYSTUDENT_KEY_POS];
                    int pageNum = Integer.parseInt(packet[Protocol.PT_MYSTUDENT_PAGENUM_POS]);
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                    //요청받은 페이지 번호와 과목코드로 수강 신청 학생 목록을 리스트에 저장
                    List<StudentDTO> list = dao.selectWithPaging(key,pageNum);
                    packet = new String[list.size() + 1];
                    packet[0] = list.size() != 0 ? "8" : "9";
                    int index = 1;

                    for(StudentDTO d : list)
                    {
                        packet[index++] = d.getStudentInfo();
                    }

                    protocol = new Protocol(Protocol.SC_RES_MYSTUDENT_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_TEACHINGTABLE_VIEW: //담당 교과목 시간표 조회
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_TEACHING_KEY_POS];
                    // 교번으로 담당 교과목목록을 얻어옴
                    List<Lecture_Subject_ProfessorDTO> list = dao.selectByProfessor(key);
                    List<TimeTableInfo> timeTable = new LinkedList<TimeTableInfo>();
                    // TimeTableInfo에 순서대로 교과목 저장 후 리스트에 추가
                    for(Lecture_Subject_ProfessorDTO dto : list) {
                        String time[] = dto.getLecture_time().split("/");
                        String name = dto.getSubject_name();
                        String classRomm = dto.getClassroom();
                        for(int i = 0 ; i < time.length;i++){
                            timeTable.add(new TimeTableInfo(name,time[i],classRomm));
                        }
                    }
                    //시간순서대로 정렬
                    Collections.sort(timeTable);

                    int index = 1;
                    packet = new String[timeTable.size() + 1];
                    packet[0] = list != null ? "A" : "B";

                    for(TimeTableInfo element : timeTable){
                        packet[index++] = element.toString();
                    }

                    protocol = new Protocol(Protocol.SC_RES_TEACHINGTABLE_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_PROFESSOR_VIEW://교수 정보 조회 요청
                {
                    String key = packet[Protocol.PT_MEMBER_VIEW_KEY_POS];
                    ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                    //교번으로 교수 정보 얻어옴
                    ProfessorDTO professorDTO = professorDAO.searchByProfessor_code(key);

                    packet = new String[2];
                    packet[0] = professorDTO != null ?"0":"1";
                    packet[Protocol.PT_MEMBER_VIEW_DATA_POS] = professorDTO.getProfessorInfoForAdmin();
                    protocol = new Protocol(Protocol.SC_RES_PROFESSOR_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_STUDENT_VIEW://학생 정보 조회 요청
                {
                    String key = packet[Protocol.PT_MEMBER_VIEW_KEY_POS];
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
                    //학번으로 학생정보 얻어옴
                    StudentDTO studentDTO = studentDAO.searchByStudent_code(key);

                    packet = new String[2];
                    packet[0] = studentDTO != null ? "0" : "1";
                    packet[Protocol.PT_MEMBER_VIEW_DATA_POS] = studentDTO.getStudentInfoForAdmin();
                    protocol = new Protocol(Protocol.SC_RES_STUDENT_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_ALLMEMBER_VIEW://모든 교수,학생 정보 조회 요청
                {
                    AdminDAO dao = new AdminDAO(jdbcConn);
                    //학생과 교수목록을 얻어와서 전달
                    List<ProfessorDTO> plist = dao.selectAllProfessor();
                    List<StudentDTO> slist = dao.selectAllStudent();
                    int index = 1;
                    packet = new String[plist.size() + slist.size() + 1];

                    packet[0] = plist != null || slist != null ? "16" : "15";

                    for(ProfessorDTO dto : plist)
                    {
                        packet[index++] = dto.getProfessorInfoForAdmin();
                    }
                    for(StudentDTO dto : slist)
                    {
                        packet[index++] = dto.getStudentInfoForAdmin();
                    }

                    protocol = new Protocol(Protocol.SC_RES_ALLMEMBER_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;


                }
                case Protocol.CS_REQ_PROFESSOR_ENROLL: //교수 계정 생성
                {
                    //교수 생성에 필요한 정보를 모두 얻어옴
                    String key = packet[Protocol.PT_MEMBER_ENROLL_KEY_POS];
                    String name = packet[Protocol.PT_MEMBER_ENROLL_NAME_POS];
                    String password = packet[Protocol.PT_MEMBER_ENROLL_PASSWORD_POS];
                    String department =packet[Protocol.PT_MEMBER_ENROLL_DEPT_POS];
                    String phone = packet[Protocol.PT_MEMBER_ENROLL_PHONE_POS];

                    AdminDAO dao = new AdminDAO(jdbcConn);

                    packet = new String[1];
                    
                    //생성 요청
                    if(dao.createProfessor(key,password,department,name,phone))
                    {
                        packet[0] = "E";
                    }
                    else
                    {
                        packet[0] = "F";
                    }

                    protocol = new Protocol(Protocol.SC_RES_PROFESSOR_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("사용자 계정 생성 완료");
                    break;
                }
                case Protocol.CS_REQ_STUDENT_ENROLL://학생 계정 등록
                {
                    //계정 등록에 필요한 정보를 저장
                    String key = packet[Protocol.PT_MEMBER_ENROLL_KEY_POS];
                    String name = packet[Protocol.PT_MEMBER_ENROLL_NAME_POS];
                    String password = packet[Protocol.PT_MEMBER_ENROLL_PASSWORD_POS];
                    String department =packet[Protocol.PT_MEMBER_ENROLL_DEPT_POS];
                    String phone =packet[Protocol.PT_MEMBER_ENROLL_PHONE_POS];
                    int grade = Integer.parseInt(packet[Protocol.PT_MEMBER_ENROLL_GRADE_POS]);

                    AdminDAO dao = new AdminDAO(jdbcConn);
                    packet = new String[1];
                    //학생계정 생성 요청
                    if(dao.createStudent(key,password,department,name,grade,phone))
                    {
                        packet[0] = "E";
                    }
                    else
                    {
                        packet[0] = "F";
                    }


                    protocol = new Protocol(Protocol.SC_RES_STUDENT_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("사용자 계정 생성 완료");
                    break;

                }
                case Protocol.CS_REQ_SUBJECT_VIEW: //전체 교과목 정보 요청
                {
                    //모든 교과목정보를 얻어옴
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    List<SubjectDTO> list = subjectDAO.selectAll();
                    packet = new String[list.size() + 1];
                    packet[0] = list != null ? "A":"B";
                    int index = 1;

                    for(SubjectDTO dto : list){
                        packet[index++] = dto.printSubjectInfo();
                    }

                    protocol = new Protocol(Protocol.SC_RES_SUBJECT_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_LECTUREINFO_VIEW: //개설 교과목 정보 조회 요청
                {
                    // 모든 개설 교과목 정보 리스트로 얻어옴
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    List<Lecture_Subject_ProfessorDTO> list = lectureDAO.selectAll();

                    packet = new String[list.size() + 1];
                    packet[0] = list != null ? "2" : "3";

                    int index = 1;
                    for(Lecture_Subject_ProfessorDTO dto : list)
                    {
                        packet[index++] = dto.printInfo();
                    }

                    protocol = new Protocol(Protocol.SC_RES_LECTUREINFO_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("개설 교과목 정보 조회 성공");
                    break;
                }
                case Protocol.CS_REQ_SUBJECT_ENROLL: //교과목 >> 관리자가 생성
                {
                    
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    HashMap<String,Object> map = new HashMap<String,Object>();
                    //클라이언트로 부터 입력받은 정보를 MAP에 저장하여 과목 생성
                    map.put("subject_code",packet[Protocol.PT_SUBINFO_KEY_POS]);
                    map.put("name",packet[Protocol.PT_SUBINFO_NAME_POS]);
                    map.put("grade",packet[Protocol.PT_SUBINFO_GRADE_POS]);

                    packet = new String[1];

                    if(subjectDAO.insertSubject(map)){
                        packet[0] = "10";
                    }
                    else{
                        packet[0] = "11";
                    }

                    protocol = new Protocol(Protocol.SC_RES_SUBJECT_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_SUBJECT_UPDATE: //교과목 수정 요청
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    HashMap<String,String> map = new HashMap<String , String>();
                    //클라이언트로 부터 입력받은 정보를 MAP에 저장하여 과목 수정
                    map.put("old_name",packet[Protocol.PT_Subject_OLD_NAME_POS]);
                    map.put("new_name",packet[Protocol.PT_Subject_NEW_NAME_POS]);

                    packet = new String[1];
                    if(subjectDAO.updateSubjectName(map))
                    {
                         packet[0] = "12";
                    }
                    else
                    {
                        packet[0] = "13";
                    }
                    protocol = new Protocol(Protocol.SC_RES_SUBJECT_UPDATE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());

                    break;
                }
                case Protocol.CS_REQ_SUBJECT_DELETE: // 교과목 삭제
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_Subject_KEY_POS];
                    //삭제할 과목의 과목코드를 입력받고 삭제
                    packet = new String[1];

                    if(subjectDAO.deleteSubject(key))
                    {
                        packet[0] = "14";
                    }
                    else
                    {
                        packet[0] = "15";
                    }

                    protocol = new Protocol(Protocol.SC_RES_SUBJECT_DELETE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_LECTURE_ENROLL: //개설 교과목 등록
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                    //과목코드 , 담당교수 교번 등 필요한 정보를 입력받음
                    String subjectCode = packet[Protocol.PT_LECTURE_ENROLL_SUBJECT_KEY_POS];
                    String professorCode = packet[Protocol.PT_LECTURE_ENROLL_PROFESSOR_KEY_POS];
                    String lectureTime = packet[Protocol.PT_LECTURE_ENROLL_LECTURE_TIME_POS];
                    int maximum = Integer.parseInt(packet[Protocol.PT_LECTURE_ENROLL_MAXIMUM_POS]);
                    String classRoom = packet[Protocol.PT_LECTURE_ENROLL_CLASSROOM_POS];

                    //과목의 기본키와 교수의 기본키를 얻어옴
                   int subjectIdx =subjectDAO.selectByCode(subjectCode);
                   int professorIdx = professorDAO.selectByProfessor_code(professorCode);
                
                    
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    //개설 교과목 정보를 DTO에 입력
                    LectureDTO lectureDTO = new LectureDTO();
                    lectureDTO.setLecture_idx(subjectIdx);
                    lectureDTO.setLecture_professor_idx(professorIdx);
                    lectureDTO.setLecture_time(lectureTime);
                    lectureDTO.setMaximum(maximum);
                    lectureDTO.setCurrent(0);
                    lectureDTO.setClassroom(classRoom);
                    packet = new String[1];
                    //개설 교과목 생성
                    if(lectureDAO.insertSubject(lectureDTO))
                    {
                        packet[0] = "12";
                    }
                    else{
                        packet[0] = "13";
                    }

                    protocol = new Protocol(Protocol.SC_RES_LECTURE_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                } 
                case Protocol.CS_REQ_LECTURE_UPDATE: //개설 교과목 수정
                {
                    //수정할 개설 교과목 정보를 클라이언트로부터 입력받음
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_LECTURE_KEY_POS];
                    String classRoom = packet[Protocol.PT_LECTURE_CLASSROOM_POS];
                    String max = packet[Protocol.PT_LECTURE_MAXIMUM_POS];
                    String pCode = packet[Protocol.PT_LECTURE_PROFESSOR_POS];

                    boolean bool = true;
                    //입력받은 정보만 업데이트를 실행
                    if(!classRoom.equals("null")) bool = lectureDAO.updateSubjectByClassRoom(key,classRoom); // 교실 변경
                    if(!max.equals("null") && bool) bool = lectureDAO.updateSubjectByMaximum(key,Integer.parseInt(max)); //최대 수강인원 갱신
                    if(!pCode.equals("null") && bool) bool = lectureDAO.updateChangeProfessor(key,pCode); //교수 변경

                    packet = new String[1];
                    packet[0] = bool ? "14" : "15";
                    protocol = new Protocol(Protocol.SC_RES_LECTURE_UPDATE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_LECTURE_DELETE://개설 교과목 삭제 요청
                {
                    String key = packet[Protocol.PT_LECTURE_KEY_POS];
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    packet = new String[1];
                    //과목코드를 입력받아 해당 개설 교과목을 삭제
                    if(lectureDAO.deleteLectureBySubjectCode(key)){
                        packet[0] = "14";
                    }
                    else{
                        packet[0] = "15";
                    }


                    protocol = new Protocol(Protocol.SC_RES_LECTURE_DELETE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_SYLLABUSPERIOD_ENROLL://강의 계획서 기간 설정
                {
                    //2021-12-25 기간 설정 포맷
                    String start_date = packet[Protocol.PT_SYLLABUSPERIOD_START_POS];
                    String end_date = packet[Protocol.PT_SYLLABUSPERIOD_END_POS];

                    //사용자로부터 입력받은 기간을 Date로 변경
                    SimpleDateFormat sdf=new SimpleDateFormat(start_date);
                    String ss=sdf.format(new java.util.Date());
                    Date startDate= Date.valueOf(ss);

                    sdf=new SimpleDateFormat(end_date);
                    ss=sdf.format(new java.util.Date());
                    Date endDate = Date.valueOf(ss);
                    
                    //기간설정 이미 강의 계획서 기간이 설정되어있으면 업데이트
                    SyllabusInsertTimeDAO dao = new SyllabusInsertTimeDAO(sqlSessionFactory);
                    boolean bool = dao.setSeason(startDate,endDate);

                    packet = new String[1];
                    packet[0] = bool ? "16" : "17";
                    protocol = new Protocol(Protocol.SC_RES_SYLLABUSPERIOD_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_REGISTRATIONPERIOD_ENROLL: //수강신청 기간 설정
                {
                    String start_date = packet[Protocol.PT_REGISTRATIONPERIOD_START_POS];
                    String end_date = packet[Protocol.PT_REGISTRATIONPERIOD_END_POS];
                    int grade = Integer.parseInt(packet[Protocol.PT_REGISTRATIONPERIOD_GRADE_POS]);
                    //수강 신청 기간을 업데이트할 정보를 얻어옴
                    
                    //Date 포맷으로 변경
                    SimpleDateFormat sdf=new SimpleDateFormat(start_date);
                    String ss=sdf.format(new java.util.Date());
                    Date startDate= Date.valueOf(ss);

                    sdf=new SimpleDateFormat(end_date);
                    ss=sdf.format(new java.util.Date());
                    Date endDate = Date.valueOf(ss);
                    //강의 계획서 기간 설정 이미 설정되어있으면 업데이트
                    LectureRegistrationDateDAO dao = new LectureRegistrationDateDAO(sqlSessionFactory);
                    packet = new String[1];
                    packet[0] = dao.setSeason(grade,startDate,endDate) ? "18" : "19";
                    protocol = new Protocol(Protocol.SC_RES_REGISTRATIONPERIOD_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                
                default:
                    throw new IllegalStateException("지원하지 않는  프로토콜 타입");
            }
        }
        this.interrupt(); // 쓰레드 종료
        }

        //client에게 전송할 데이터를 버퍼에 작성할때 이용한 메소드
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
