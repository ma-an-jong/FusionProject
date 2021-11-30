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
    private static HashMap<String,Boolean> duplicatedMap = new HashMap<String,Boolean>();


    private Connection jdbcConn; //JDBC 연결
    private SqlSessionFactory sqlSessionFactory; //MYBATIS 연결
    private Socket socket;

    private BufferedReader in = null;
    private BufferedWriter out = null;

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
        clientNum++;
        System.out.println("접속중인 클라이언트 수:" + clientNum);

        try
        {
            //입출력 버퍼 연결
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("SocketBuffer 가져오기 실패");
        }

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

                    flag = false;
                    clientNum--;
                    packet = new String[1];
                    packet[0] = "0";
                    protocol = new Protocol(Protocol.PT_EXIT);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("클라이언트 종료");
                    break;
                }
                case Protocol.PT_REQ_LOGIN: //로그인 요청 Clear
                {
                    System.out.println("클라이언트가 로그인 정보를 보냈습니다.");
                    //데이터 얻어오기
                    String id = packet[Protocol.PT_LOGIN_ID_POS];
                    String password = packet[Protocol.PT_LOGIN_PASSWORD_POS];

                    UserDAO userDAO = new UserDAO(jdbcConn);
                    List<UserDTO> userList = userDAO.selectAllUser();
                    boolean except = true;

                    for(UserDTO dto : userList)
                    {
                        if(dto.getId().equals(id) && dto.getPassword().equals(password) )
                        {
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
                case Protocol.CS_REQ_PROFESSOR_PERSONALINFO_UPDATE: //교수 개인정보 업뎃 Clear
                {
                    ProfessorDAO dao = new ProfessorDAO(jdbcConn);
                    String key = packet[Protocol.PT_PERSONALINFO_CODE_POS];
                    ProfessorDTO pDTO = dao.searchByProfessor_code(key);

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
                case Protocol.CS_REQ_STUDENT_PERSONALINFO_UPDATE: //학생 개인정보 업데이트  Clear   //값수정필요 학생
                {
                    StudentDAO dao = new StudentDAO(jdbcConn);

                    String key = packet[Protocol.PT_PERSONALINFO_CODE_POS];
                    StudentDTO studentDTO = dao.searchByStudent_code(key);
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
                case Protocol.CS_REQ_LECTURE_VIEW: //개설 교과목 목록 조회 Clear TODO:  \t -> \n replaceAll
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
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

                    String path = lectureDAO.searchSyllabusBySubjectCode(key);
                    String code = "E";
                    String context = new String();
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
                case Protocol.CS_REQ_TEACHING_VIEW: //담당 교과목 목록 조회 Clear TODO \t -> \n replaceAll
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
                case Protocol.CS_REQ_SYLLABUS_ENROLL: //강의 계획서 등록기간 인증 Clear
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
                case Protocol.CS_REQ_SYLLABUS_FILE: // 강의 계획서 등록  교수 Clear
                {
                    String code = packet[Protocol.PT_SYLLABUS_FILE_KEY_POS];
                    String context = packet[Protocol.PT_SYLLABUS_FILE_CONTEXT_POS];
                    String path = Protocol.staticPath + code + ".txt";
                    System.out.println(context);

                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);

                    boolean bool = lectureDAO.updateSyllabus(code,path);
                    packet = new String[1];

                    if(!bool){
                        packet[0] = "3";
                        protocol = new Protocol(Protocol.SC_RES_SYLLABUS_ENROLL);
                        protocol.setPacket(packet);
                        writePacket(protocol.getPacket());
                        break;
                    }

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
                case Protocol.CS_REQ_SYLLABUS_DELETE : //강의 계획서 삭제 교수 TODO: 메소드 부족
                {
                    String code = packet[Protocol.PT_SYLLABUS_FILE_KEY_POS];

                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    String path = lectureDAO.deleteSyllabusBySubjectCode(code);
                    System.out.println(path);
                    File file = new File(path);
                    packet = new String[1];
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
                case Protocol.CS_REQ_MYSYLLABUS_VIEW: //담당교과목 강의계획서 조회 요청 교수 Clear
                {
                    String key = packet[Protocol.PT_SYLLABUS_VIEW_KEY_POS];
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);

                    String path = lectureDAO.searchSyllabusBySubjectCode(key);
                    String code = "E";
                    String context = new String();
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
                    protocol = new Protocol(Protocol.SC_RES_MYSYLLABUS_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_MYSTUDENT_VIEW:  //담당교과목 수강신청 학생 목록 조회 요청
                    //TODO : 페이징 기능 \t -> \n replaceAll
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
                case Protocol.CS_REQ_PROFESSOR_VIEW://교수 정보 조회 요청 Clear TODO \t -> \n replaceAll
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
                case Protocol.CS_REQ_STUDENT_VIEW://학생 정보 조회 요청 Clear TODO \t -> \n replaceAll
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
                case Protocol.CS_REQ_ALLMEMBER_VIEW://모든 교수,학생 정보 조회 요청 Clear TODO \t -> \n replaceAll
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
                case Protocol.CS_REQ_SUBJECT_VIEW: //전체 교과목 정보 요청 Clear TODO \t -> \n replaceAll
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
                case Protocol.CS_REQ_LECTUREINFO_VIEW: //개설 교과목 정보 조회 요청
                    // TODO: 전체출력으로 고치기 TODO \t -> \n replaceAll
                {
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
                case Protocol.CS_REQ_LECTURE_ENROLL: //개설 교과목 등록 Clear
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
                case Protocol.CS_REQ_LECTURE_UPDATE: //개설 교과목 수정 관리자 Clear
                {
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_LECTURE_KEY_POS];
                    String classRoom = packet[Protocol.PT_LECTURE_CLASSROOM_POS];
                    String max = packet[Protocol.PT_LECTURE_MAXIMUM_POS];
                    String pCode = packet[Protocol.PT_LECTURE_PROFESSOR_POS];

                    for(String s : packet){
                        System.out.println(s);
                    }

                    boolean bool = true;
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

    public void discountClient()
    {
        clientNum--;
        System.out.println("현재 클라이언트 수:" + clientNum);
    }


}
