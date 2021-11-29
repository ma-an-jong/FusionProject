package Server;

import Client.Protocol;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.spi.NOPLoggerRepository;
import persistence.DAO.*;
import persistence.DTO.*;
import persistence.MyBatisConnectionFactory;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServerThread extends Thread {
    private static int clientNum = 0;

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
        clientNum++;
        System.out.println("접속중인 클라이언트 수:" + clientNum);

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

        // TODO:초기 프로토콜상태 무시??
        Protocol protocol;
        String packetType;
        String packet[] = new String[1000];

        while (flag)
        {
            try
            {
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
                case Protocol.PT_EXIT:
                {
                    writePacket(Protocol.PT_EXIT);
                    flag = false;
                    clientNum--;
                    System.out.println("서버종료");
                    System.out.println("현재 클라이언트 수:" + clientNum);
                    break;
                }
                case Protocol.PT_REQ_LOGIN: //로그인 요청 Clear
                {
                    System.out.println("클라이언트가 로그인 정보를 보냈습니다.");

                    String id = packet[Protocol.PT_LOGIN_ID_POS];
                    String password = packet[Protocol.PT_LOGIN_PASSWORD_POS];

                    UserDAO userDAO = new UserDAO(jdbcConn);
                    List<UserDTO> userList = userDAO.selectAllUser();
                    System.out.println(userList.size());
                    boolean except = true;

                    for(UserDTO dto : userList)
                    {
                        if(dto.getId().equals(id) && dto.getPassword().equals(password) )
                        {
                            except = false;
                            packet = new String[4];
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
                                System.out.println("학생 인증 성공");
                            }
                            else if(dto.getCategory() == 'p')
                            {
                                ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                                ProfessorDTO professorDTO = professorDAO.searchByProfessor_idx(dto.getIdx());
                                packet[0] = "1";
                                packet[Protocol.PT_LOGIN_KEY_POS] = professorDTO.getProfessor_code();
                                packet[Protocol.PT_LOGIN_CATEGORY_POS] ="p";
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
                case Protocol.CS_REQ_REGISTRATION: //수강 신청 요청 인증 Clear
                {
                    int grade = Integer.parseInt(packet[Protocol.PT_REGISTRATION_GRADE_POS]);

                    LocalDate localDate = LocalDate.now();
                    SimpleDateFormat sdf=new SimpleDateFormat(localDate.toString());
                    String ss=sdf.format(new java.util.Date());
                    Date today= Date.valueOf(ss);

                    LectureRegistrationDateDAO dao = new LectureRegistrationDateDAO(sqlSessionFactory);
                    LectureRegistrationDateDTO dto = dao.selectByGrade(grade);

                    packet = new String[1];

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
                case Protocol.CS_REQ_MYSUBJECT_ENROLL: //교과목 등록 Clear
                {
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);

                    String studentCode = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];
                    String subjectCode = packet[Protocol.PT_MYSUBJECT_SUBJECT_CODE_POS];

                    StudentDTO studentDTO = studentDAO.searchByStudent_code(studentCode);
                    LectureDTO lectureDTO = lectureDAO.searchBySubjectCode(subjectCode);

                    //학년 학번 강의시간  강의 idx 현재인원 최대인원
                    CourseDetailsDTO dto = new CourseDetailsDTO();
                    dto.setStudent_idx(studentDTO.getStudent_idx());
                    dto.setStudent_code(studentCode);
                    dto.setLecture_professor_idx(lectureDTO.getLecture_professor_idx());
                    dto.setGrade(studentDTO.getGrade());
                    dto.setLecture_time(lectureDTO.getLecture_time());
                    dto.setLecture_idx(lectureDTO.getLecture_idx());
                    dto.setCurrent(0);
                    dto.setMaximum(lectureDTO.getMaximum());


                    boolean bool = dao.addCoure(dto);
                    packet = new String[1];

                    packet[0] = bool ? "A" : "B";

                    protocol = new Protocol(Protocol.SC_RES_MYSUBJECT_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_MYSUBJECT_VIEW: //내 수강 목록 조회 Clear
                {
                    System.out.println("클라이언트가 본인의 정보 요청");
                    try
                    {
                        CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                        String key = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];

                        List<CourseDetailsDTO> list = dao.selectMyCourse(key);

                        packet = new String[list.size() + 1];
                        packet[0] = list != null ? "10":"11";
                        int index = 1;
                        //출력문 다시
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
                case Protocol.CS_REQ_MYSUBJECT_DELETE: //학생 수강삭제 Clear
                {
                    String subjectCode = packet[Protocol.PT_MYSUBJECT_SUBJECT_CODE_POS];
                    String studentCode = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];

                    System.out.println(subjectCode + " " + studentCode);

                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                    CourseDetailsDTO dto = new CourseDetailsDTO();

                    //학번 ,과목코드
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
                    int subjectIdx = subjectDAO.selectByCode(subjectCode);
                    StudentDTO studentDTO =  studentDAO.searchByStudent_code(studentCode);
                    System.out.println("과목인덱스 " + subjectIdx);
                    System.out.println(studentDTO.toString());

                    dto.setStudent_idx(studentDTO.getStudent_idx());
                    dto.setStudent_code(studentDTO.getStudent_code());
                    dto.setLecture_idx(subjectIdx);

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
                case Protocol.CS_REQ_PROFESSOR_PERSONALINFO_VIEW:// 교수가 개인정보 요청 Clear
                {
                    String key = packet[Protocol.PT_PERSONALINFO_KEY_POS];

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
                case Protocol.CS_REQ_STUDENT_PERSONALINFO_VIEW: //학생이 개인정보 요청 Clear
                {
                    String key = packet[Protocol.PT_PERSONALINFO_KEY_POS];
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
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
                case Protocol.CS_REQ_PROFESSOR_PERSONALINFO_UPDATE: //교수 개인정보 업뎃 Clear //값수정필요
                {
                    ProfessorDTO professorDTO = new ProfessorDTO();
                    String id = packet[Protocol.PT_PERSONALINFO_ID_POS];professorDTO.setId(id);
                    String password = packet[Protocol.PT_PERSONALINFO_PASSWORD_POS];professorDTO.setPassword(password);
                    String key = packet[Protocol.PT_PERSONALINFO_CODE_POS];professorDTO.setProfessor_code(key);
                    String name = packet[Protocol.PT_PERSONALINFO_NAME_POS];professorDTO.setPname(name);
                    String dept = packet[Protocol.PT_PERSONALINFO_DEPARTMENT_POS];professorDTO.setDepartment(dept);
                    String phone = packet[Protocol.PT_PERSONALINFO_PHONE_POS];professorDTO.setPhone(phone);

                    ProfessorDAO dao = new ProfessorDAO(jdbcConn);
                    String code;
                    if(dao.updateProfessorInfo(professorDTO)){
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
                case Protocol.CS_REQ_STUDENT_PERSONALINFO_UPDATE: //학생 개인정보 업데이트  Clear   //값수정필요
                {
                    StudentDTO studentDTO = new StudentDTO();
                    String id = packet[Protocol.PT_PERSONALINFO_ID_POS];studentDTO.setId(id);
                    String password = packet[Protocol.PT_PERSONALINFO_PASSWORD_POS];studentDTO.setPassword(password);
                    String key = packet[Protocol.PT_PERSONALINFO_CODE_POS];studentDTO.setStudent_code(key);
                    String name = packet[Protocol.PT_PERSONALINFO_NAME_POS];studentDTO.setSname(name);
                    String dept = packet[Protocol.PT_PERSONALINFO_DEPARTMENT_POS];studentDTO.setDepartment(dept);
                    String phone = packet[Protocol.PT_PERSONALINFO_PHONE_POS];studentDTO.setPhone(phone);
                    int grade = Integer.parseInt(packet[Protocol.PT_PERSONALINFO_GRADE_POS]);studentDTO.setGrade(grade);

                    StudentDAO dao = new StudentDAO(jdbcConn);
                    String code;

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
                case Protocol.CS_REQ_TIMETABLE_VIEW: //시간표 조회 Clear
                {
                    //강의 시간 포멧은 금34/목67 등
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);

                    String key = packet[Protocol.PT_TIMETABLE_KEY_POS];
                    List<CourseDetailsDTO> list = dao.selectMyCourse(key);
                    List<TimeTableInfo> timeTable = new LinkedList<TimeTableInfo>();

                    for (CourseDetailsDTO dto : list) {
                        String time[] = dto.getLecture_time().split("/");
                        String name =dto.getSubject_name();
                        String classRoom = dto.getClassroom();
                        for(int i = 0; i < time.length;i++){
                            timeTable.add(new TimeTableInfo(name,time[i],classRoom));
                        }
                    }

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
                case Protocol.CS_REQ_LECTURE_VIEW: //개설 교과목 목록 조회 TODO: Clear 출력 살짝?
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    List<Lecture_Subject_ProfessorDTO> list = dao.selectAll();

                    packet = new String[list.size() + 1];
                    int index= 1;

                    for(Lecture_Subject_ProfessorDTO dto : list)
                    {
                        packet[index]  = dto.printInfo();
                        index ++;
                    }

                    packet[0] = list != null ? "C" : "D";
                    protocol = new Protocol(Protocol.SC_RES_LECTURE_VIEW);
                    protocol.setPacket(packet);
                    System.out.println("개설 교과목 조회");
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_SYLLABUS_VIEW: //강의 계획서 조회 by과목코드
                {
                    String key = packet[Protocol.PT_SYLLABUS_VIEW_KEY_POS];
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);

                    String path = lectureDAO.searchSyllabusBySubjectCode(key);
                    String code = "E";
                    String context = new String();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)
                            ,"UTF-8")))
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
                case Protocol.CS_REQ_TEACHING_VIEW: //담당 교과목 목록 조회 Clear
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_TEACHING_KEY_POS];
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
                    SyllabusInsertTimeDTO dto = dao.selectAll();

                    LocalDate localDate = LocalDate.now();
                    SimpleDateFormat sdf = new SimpleDateFormat(localDate.toString());
                    Date today = Date.valueOf(sdf.format(new java.util.Date()));

                    packet = new String[1];

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
                case Protocol.CS_REQ_SYLLABUS_FILE: // 강의 계획서 등록 
                    // TODO:메소드 부족 -> bool 지정
                {
                    String code = packet[Protocol.PT_SYLLABUS_FILE_KEY_POS];
                    String context = packet[Protocol.PT_SYLLABUS_FILE_CONTEXT_POS];

                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);

                    //lectureDAO.updateSyllabus(code,context);

                    packet = new String[1];
                    packet[0] = "2";
                    protocol = new Protocol(Protocol.SC_RES_SYLLABUS_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_MYSYLLABUS_VIEW: //담당교과목 강의계획서 조회 요청
                    //TODO : 전체 출력?? pass
                {
                    break;
                }
                case Protocol.CS_REQ_MYSTUDENT_VIEW:  //담당교과목 수강신청 학생 목록 조회 요청
                    //TODO : 페이징 기능
                {
                    String key = packet[Protocol.PT_MYSTUDENT_KEY_POS];
                    int pageNum = Integer.parseInt(packet[Protocol.PT_MYSTUDENT_PAGENUM_POS]);
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);

                    List<StudentDTO> list = dao.selectWithPaging(key,pageNum);
                    packet = new String[list.size() + 1];
                    packet[0] = list != null ? "8" : "9";
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
                case Protocol.CS_REQ_TEACHINGTABLE_VIEW: //담당 교과목 시간표 조회 Clear
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_TEACHING_KEY_POS];

                    List<Lecture_Subject_ProfessorDTO> list = dao.selectByProfessor(key);
                    List<TimeTableInfo> timeTable = new LinkedList<TimeTableInfo>();

                    for(Lecture_Subject_ProfessorDTO dto : list) {
                        String time[] = dto.getLecture_time().split("/");
                        String name = dto.getSubject_name();
                        String classRomm = dto.getClassroom();
                        for(int i = 0 ; i < time.length;i++){
                            timeTable.add(new TimeTableInfo(name,time[i],classRomm));
                        }
                    }

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
                case Protocol.CS_REQ_PROFESSOR_VIEW://교수 정보 조회 요청 Clear
                {
                    String key = packet[Protocol.PT_MEMBER_VIEW_KEY_POS];
                    ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                    ProfessorDTO professorDTO = professorDAO.searchByProfessor_code(key);

                    packet = new String[2];
                    packet[0] = professorDTO != null ?"0":"1";
                    packet[Protocol.PT_MEMBER_VIEW_DATA_POS] = professorDTO.getProfessorInfoForAdmin();
                    protocol = new Protocol(Protocol.SC_RES_PROFESSOR_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_STUDENT_VIEW://학생 정보 조회 요청 Clear
                {
                    String key = packet[Protocol.PT_MEMBER_VIEW_KEY_POS];
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
                    StudentDTO studentDTO = studentDAO.searchByStudent_code(key);

                    packet = new String[2];
                    packet[0] = studentDTO != null ? "0" : "1";
                    packet[Protocol.PT_MEMBER_VIEW_DATA_POS] = studentDTO.getStudentInfoForAdmin();
                    protocol = new Protocol(Protocol.SC_RES_STUDENT_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_ALLMEMBER_VIEW://모든 교수,학생 정보 조회 요청 Clear
                {
                    AdminDAO dao = new AdminDAO(jdbcConn);
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
                case Protocol.CS_REQ_PROFESSOR_ENROLL: //교수 계정 생성 Clear
                {
                    String key = packet[Protocol.PT_MEMBER_ENROLL_KEY_POS];
                    String name = packet[Protocol.PT_MEMBER_ENROLL_NAME_POS];
                    String password = packet[Protocol.PT_MEMBER_ENROLL_PASSWORD_POS];
                    String department =packet[Protocol.PT_MEMBER_ENROLL_DEPT_POS];
                    String phone = packet[Protocol.PT_MEMBER_ENROLL_PHONE_POS];

                    AdminDAO dao = new AdminDAO(jdbcConn);

                    packet = new String[1];

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
                case Protocol.CS_REQ_STUDENT_ENROLL://학생 계정 등록 Clear
                {
                    String key = packet[Protocol.PT_MEMBER_ENROLL_KEY_POS];
                    String name = packet[Protocol.PT_MEMBER_ENROLL_NAME_POS];
                    String password = packet[Protocol.PT_MEMBER_ENROLL_PASSWORD_POS];
                    String department =packet[Protocol.PT_MEMBER_ENROLL_DEPT_POS];
                    String phone =packet[Protocol.PT_MEMBER_ENROLL_PHONE_POS];
                    int grade = Integer.parseInt(packet[Protocol.PT_MEMBER_ENROLL_GRADE_POS]);

                    AdminDAO dao = new AdminDAO(jdbcConn);
                    packet = new String[1];

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
                case Protocol.CS_REQ_SUBJECT_VIEW: //전체 교과목 정보 요청 Clear
                {
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
                case Protocol.CS_REQ_LECTUREINFO_VIEW: //개설 교과목 정보 조회 요청 TODO: 전체출력으로 고치기
                {
                    String key = packet[Protocol.PT_LECTUREINFO_KEY_POS];

                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                    CourseDetailsDTO dto = dao.selectCourseByCode(key);

                    ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                    int pIdx = dto.getLecture_professor_idx();

                    ProfessorDTO professorDTO = professorDAO.searchByProfessor_idx(pIdx);
                    packet = new String[2];
                    packet[0] = professorDTO != null || dto != null ? "2" : "3";
                    packet[1] = dto.getLectureInfo() + professorDTO.getProfessorInfoForAdmin() ;
                    protocol = new Protocol(Protocol.SC_RES_LECTUREINFO_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("개설 교과목 정보 조회 성공");
                    break;
                }
                case Protocol.CS_REQ_SUBJECT_ENROLL: //교과목 >> 관리자가 생성 Clear
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    HashMap<String,Object> map = new HashMap<String,Object>();

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
                case Protocol.CS_REQ_SUBJECT_UPDATE: //교과목 수정 요청 -> 이름만 변경 Clear
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    HashMap<String,String> map = new HashMap<String , String>();

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
                case Protocol.CS_REQ_SUBJECT_DELETE: // 교과목 삭제 Clear
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_Subject_KEY_POS];

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
                //TODO: 개설교과목 응답코드 추가 필요
                case Protocol.CS_REQ_LECTURE_ENROLL: //개설 교과목 등록 Clear
                    //TODO:응답코드 재확인 필요
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);

                    // 수67/금34
                    String subjectCode = packet[Protocol.PT_LECTURE_ENROLL_SUBJECT_KEY_POS];
                    String professorCode = packet[Protocol.PT_LECTURE_ENROLL_PROFESSOR_KEY_POS];
                    String lectureTime = packet[Protocol.PT_LECTURE_ENROLL_LECTURE_TIME_POS];
                    int maximum = Integer.parseInt(packet[Protocol.PT_LECTURE_ENROLL_MAXIMUM_POS]);
                    String classRoom = packet[Protocol.PT_LECTURE_ENROLL_CLASSROOM_POS];

                   int subjectIdx =subjectDAO.selectByCode(subjectCode);
                   int professorIdx = professorDAO.selectByProfessor_code(professorCode);
                    System.out.println(subjectIdx + " " + professorIdx);
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);

                    LectureDTO lectureDTO = new LectureDTO();
                    lectureDTO.setLecture_idx(subjectIdx);
                    lectureDTO.setLecture_professor_idx(professorIdx);
                    lectureDTO.setLecture_time(lectureTime);
                    lectureDTO.setMaximum(maximum);
                    lectureDTO.setCurrent(0);
                    lectureDTO.setClassroom(classRoom);
                    packet = new String[1];

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
                case Protocol.CS_REQ_LECTURE_UPDATE: //개설 교과목 수정 TODO : bool 추가
                    // TODO: 업데이트하는거 추가적으로 데이터 + 메소드 부족
                {
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_LECTURE_KEY_POS];

                   // lectureDAO.updateSubjectByClassRoom(); // 교실 변경
                   // lectureDAO.updateSubjectByMaximum(); //최대 수강인원 갱신
                    // TODO:12.담당교수가 변경되는 메소드 생성 필요.

                    packet = new String[1];
                    packet[0] = "14";
                    protocol = new Protocol(Protocol.SC_RES_LECTURE_UPDATE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_LECTURE_DELETE://교과목 삭제 요청 Clear
                {
                    String key = packet[Protocol.PT_LECTURE_KEY_POS];
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    packet = new String[1];
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
                case Protocol.CS_REQ_SYLLABUSPERIOD_ENROLL://강의 계획서 기간 설정 Clear
                {   //2021-11-10
                    String start_date = packet[Protocol.PT_SYLLABUSPERIOD_START_POS];
                    String end_date = packet[Protocol.PT_SYLLABUSPERIOD_END_POS];

                    SimpleDateFormat sdf=new SimpleDateFormat(start_date);
                    String ss=sdf.format(new java.util.Date());
                    Date startDate= Date.valueOf(ss);

                    sdf=new SimpleDateFormat(end_date);
                    ss=sdf.format(new java.util.Date());
                    Date endDate = Date.valueOf(ss);

                    SyllabusInsertTimeDAO dao = new SyllabusInsertTimeDAO(sqlSessionFactory);
                    boolean bool = dao.setSeason(startDate,endDate);

                    packet = new String[1];
                    packet[0] = bool ? "16" : "17";
                    protocol = new Protocol(Protocol.SC_RES_SYLLABUSPERIOD_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_REGISTRATIONPERIOD_ENROLL: //수강신청 기간 설정 Clear
                {
                    String start_date = packet[Protocol.PT_REGISTRATIONPERIOD_START_POS];
                    String end_date = packet[Protocol.PT_REGISTRATIONPERIOD_END_POS];
                    int grade = Integer.parseInt(packet[Protocol.PT_REGISTRATIONPERIOD_GRADE_POS]);

                    SimpleDateFormat sdf=new SimpleDateFormat(start_date);
                    String ss=sdf.format(new java.util.Date());
                    Date startDate= Date.valueOf(ss);

                    sdf=new SimpleDateFormat(end_date);
                    ss=sdf.format(new java.util.Date());
                    Date endDate = Date.valueOf(ss);

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
