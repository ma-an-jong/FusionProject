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
import java.sql.Time;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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

        boolean flag = true;

        //초기 프로토콜상태 무시

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
                case Protocol.PT_REQ_LOGIN: //TODO: 학년 학번 보내주기 + 교번 + 카테고리 메소드 부족
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
                                packet = new String[4];
                                packet[0] = "2";
                                protocol.setPacket(packet);
                                writePacket(protocol.getPacket());
                                System.out.println("관리자 인증 성공");
                            }
                            else if(dto.getCategory() == 's')
                            {
                                StudentDAO studentDAO = new StudentDAO(jdbcConn);
                                StudentDTO studentDTO = studentDAO.searchByStudent_idx(dto.getIdx());

                                packet = new String[4];
                                packet[0] = "0";
                                packet[Protocol.PT_LOGIN_KEY_POS] = studentDTO.getStudent_code();
                                packet[Protocol.PT_LOGIN_GRADE_POS] = String.valueOf(studentDTO.getGrade());
                                packet[Protocol.PT_LOGIN_CATEGORY_POS] ="s";

                                protocol = new Protocol(Protocol.PT_LOGIN_RESULT);
                                protocol.setPacket(packet);
                                writePacket(protocol.getPacket());
                                System.out.println("학생 인증 성공");
                                break;
                            }
                            else if(dto.getCategory() == 'p') {
                                ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                                ProfessorDTO professorDTO = professorDAO.searchByProfessor_idx(dto.getIdx());

                                packet = new String[4];
                                packet[0] = "1";
                                packet[Protocol.PT_LOGIN_KEY_POS] = professorDTO.getProfessor_code();
                                packet[Protocol.PT_LOGIN_CATEGORY_POS] ="p";

                                protocol = new Protocol(Protocol.PT_LOGIN_RESULT);
                                protocol.setPacket(packet);
                                writePacket(protocol.getPacket());
                                System.out.println("교수 인증 성공");
                                break;
                            }
                            else
                            {
                                System.out.println("unsupported");
                            }
                            System.out.println("로그인 성공");
                            break;
                        }
                    }
                    System.out.println("일치하는 id/password가 없습니다");
                    break;
                }
                case Protocol.CS_REQ_REGISTRATION: //TODO: 할수있는데 왜 안했을까??? 마지막에
                {
                    break;
                }
                
                                                 //TODO: 인증토큰 만들기

                case Protocol.CS_REQ_MYSUBJECT_ENROLL: //TODO:메소드 부족
                {
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);

                    String subjectCode = packet[Protocol.PT_MYSUBJECT_SUBJECT_CODE_POS];
                    String studentCode = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];

                    StudentDTO studentDTO = studentDAO.searchByStudent_code(studentCode);
                    LectureDTO lectureDTO = lectureDAO.searchBySubject_code(subjectCode);
                    //11. LectureDAO -> searchBySubject_code(subjectCode); 과목코드로 개설 교과목 조회하는 기능

                    //학년 학번 강의시간  강의 idx 현재인원 최대인원
                    CourseDetailsDTO dto = new CourseDetailsDTO();
                    dto.setGrade(studentDTO.getGrade());
                    dto.setStudent_code(studentCode);

                    dao.addCoure(dto);

                    packet = new String[1];
                    packet[0] = "A";
                    protocol = new Protocol(Protocol.SC_RES_MYSUBJECT_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }

                case Protocol.CS_REQ_MYSUBJECT_MODIFY: //TODO : 인증이랑 합치는걸로
                {
                    break;
                }

                case Protocol.CS_REQ_MYSUBJECT_VIEW: //내 수강 목록 조회 TODO: 메소드 부족
                {
                    //클라이언트가 학번을 통해서 정보 조회하여 전달
                    System.out.println("클라이언트가 본인의 정보 요청");

                    try {
                        //수강신청 DAO생성
                        CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                        String key = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];
                        List<CourseDetailsDTO> list = dao.selectMyCourse(key);
                        
                        packet = new String[list.size() + 1];
                        packet[0] = "10";
                        int index = 1;
                        
                        for (CourseDetailsDTO dto : list) {
                            //CourseDetailsDTO에 이쁘게 출력하는 메소드
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
                case Protocol.CS_REQ_MYSUBJECT_DELETE: //학생 수강삭제
                    //TODO:메소드 부족
                {
                    String subjectCode = packet[Protocol.PT_MYSUBJECT_SUBJECT_CODE_POS];
                    String studentCode = packet[Protocol.PT_MYSUBJECT_STUDENT_CODE_POS];
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                    CourseDetailsDTO dto = new CourseDetailsDTO();
                    //학번 ,과목코드
                    dto.setStudent_code(studentCode);
                    //TODO:9.SUBJECTDAO에 과목코드를 입력하면 그에 해당하는 subject_idx 를 리턴하는 메소드
                    dto.setLecture_idx(subjectCode);

                    dao.deleteCourse(dto);

                    packet = new String[1];
                    packet[0] = "8";
                    protocol = new Protocol(Protocol.SC_RES_MYSUBJECT_DELETE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());

                    break;
                }
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
                //TODO: 메소드 부족
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

                        if(!name.equals(null)){
                            dao.updateName(key,name);
                        }
                        if(!dept.equals(null)){

                        }
                        if(!phone.equals(null)){

                        }
                        if(grade != 0){

                        }
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
                case Protocol.CS_REQ_TIMETABLE_VIEW: //TODO:일렬로 출력 같은과목 날짜분리 + 정렬
                {
                    //강의 시간 포멧은 금34/목67 등
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);

                    String key = packet[Protocol.PT_TIMETABLE_KEY_POS];
                    List<CourseDetailsDTO> list = dao.selectMyCourse(key);
                    List<TimeTableInfo> timeTable = new LinkedList<TimeTableInfo>();

                    for (CourseDetailsDTO dto : list) {
                        String time[] = dto.getLecture_time().split("/");
                        String name =dto.getName();
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
                case Protocol.CS_REQ_LECTURE_VIEW: //개설 교과목 목록 조회 TODO: 메소드 부족
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    List<Lecture_Subject_ProfessorDTO> list = dao.selectAll();

                    packet = new String[list.size() + 1];
                    int index= 1;
                    //13.Lecture_Subject_ProfessorDTO ->  이쁘게 출력문
                    for(Lecture_Subject_ProfessorDTO dto : list){
                        packet[index]  = dto.toString();
                        index ++;
                    }
                    packet[0] = "C";
                    protocol = new Protocol(Protocol.SC_RES_LECTURE_VIEW);
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
                case Protocol.CS_REQ_MYSTUDENT_VIEW:  //담당교과목 수강신청 학생 목록 조회 요청
                    // TODO: 페이징 기능
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
                        packet[index++] = d.getStudentInfo();
                    }

                    protocol = new Protocol(Protocol.SC_RES_MYSTUDENT_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_TEACHINGTABLE_VIEW: //TODO:담당 교과목 시간표 조회  + 학생시간표 조회와 동일하게
                {
                    LectureDAO dao = new LectureDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_TEACHING_KEY_POS];

                    List<Lecture_Subject_ProfessorDTO> list = dao.selectByProfessor(key);
                    List<TimeTableInfo> timeTable = new LinkedList<TimeTableInfo>();

                    //13.Lecture_Subject_ProfessorDTO ->  이쁘게 출력문
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
                    packet[0] = "A";

                    for(TimeTableInfo element : timeTable){
                        packet[index++] = element.toString();
                    }

                    protocol = new Protocol(Protocol.SC_RES_TEACHINGTABLE_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_MEMBER_VIEW:
                    //TODO: 교수와 학생 request 분리
                {
                    String key = packet[Protocol.PT_MEMBER_VIEW_KEY_POS];
                    StudentDAO studentDAO = new StudentDAO(jdbcConn);
                    StudentDTO studentDTO = studentDAO.searchByStudent_code(key);

                    packet = new String[2];
                    packet[0] = "0";
                    packet[Protocol.PT_MEMBER_VIEW_DATA_POS] = studentDTO.getStudentInfoForAdmin();
                    protocol = new Protocol(Protocol.SC_RES_MEMBER_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                    ProfessorDAO professorDAO = new ProfessorDAO(jdbcConn);
                    ProfessorDTO professorDTO = professorDAO.searchByProfessor_code(key);

                    packet = new String[2];
                    packet[0] = "0";
                    packet[Protocol.PT_MEMBER_VIEW_DATA_POS] = professorDTO.getProfessorInfoForAdmin();
                    protocol = new Protocol(Protocol.SC_RES_MEMBER_VIEW);
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
                case Protocol.CS_REQ_MEMBER_ENROLL:
                    //TODO:Professor 등록 Student 등록 세분화
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
                case Protocol.CS_REQ_OPENSUBINFO_VIEW: //개설 교과목 정보 조회 요청
                    // TODO:메소드 부족
                {
                    String key = packet[Protocol.PT_OPENSUBINFO_KEY_POS];
                    //5.CourseRegistration -> selectCourseByIdx 를 과목코드로 조회하는 기능 만들기
                    // lecture랑 subject JOIN해서 만들어야함
                    CourseRegistration dao = new CourseRegistration(sqlSessionFactory);
                    CourseDetailsDTO dto = dao.selectCourseByIdx(key);
                    packet = new String[2];
                    packet[0] = "2";

                    //6.CouserDetailDTO에서 개설 교과목 정보 요청할때 출력해줄 메소드
                    packet[1] = dto.toString();
                    protocol = new Protocol(Protocol.SC_RES_OPENSUBINFO_VIEW);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    System.out.println("개설 교과목 정보 조회 성공");
                    break;
                }
                case Protocol.CS_REQ_SUBJECT_ENROLL: //교과목 >> 관리자가 생성
                    // TODO: 메소드 부족 + 데이터부족 + 데이터포멧
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    HashMap<String,Object> map = new HashMap<String,Object>();
                    String key = packet[Protocol.PT_OPENSUBINFO_KEY_POS];
                    //#{subject_code},#{name},#{grade} 과목이름, 과목코드, , 학점
                    map.put("subject_code",packet[Protocol.PT_OPENSUBINFO_KEY_POS]);
                    map.put("name",packet[Protocol.PT_OPENSUBINFO_NAME_POS]);
                    map.put("grade",packet[Protocol.PT_OPENSUBINFO_GRADE_POS]);
                    subjectDAO.insertSubject(map);

                    packet = new String[1];
                    packet[0] = "10";
                    protocol = new Protocol(Protocol.SC_RES_SUBJECT_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }

                case Protocol.CS_REQ_SUBJECT_UPDATE:
                    //교과목 수정 요청인데 데이터가 없음
                    // TODO:  과목코드 보내는것이 아니라 이름 : 이름으로 
                    
                //7. 교과목 수정 요청 에서 SUBJECTDAO에 각각 정보 업데이트하는 메소드
                {

                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    HashMap<String,String> map = new HashMap<String , String>();

                    //#{new_name}
                    //#{old_name}

                    map.put("new_name",packet[Protocol.PT_Subject_NEW_NAME_POS]);
                    map.put("old_name",packet[Protocol.PT_Subject_OLD_NAME_POS]);

                    subjectDAO.updateSubjectName(map);

                    packet = new String[1];
                    packet[0] = "12";

                    protocol = new Protocol(Protocol.SC_RES_SUBJECT_UPDATE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());

                    break;
                }
                
                case Protocol.CS_REQ_SUBJECT_DELETE: // 8.SubjectDAO에 과목코드로 교과목 삭제하는 기능 TODO:메소드 부족
                {
                    SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
                    String key = packet[Protocol.PT_Subject_KEY_POS];
                    // 8.SubjectDAO에 과목코드로 교과목 삭제하는 기능
                    //subjectDAO.deleteByCode(key);

                    packet = new String[1];
                    packet[0] = "14";
                    protocol = new Protocol(Protocol.SC_RES_SUBJECT_DELETE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                //TODO: 개설교과목 응답코드 추가 필요
                case Protocol.CS_REQ_LECTURE_ENROLL: //TODO: 개설 교과목 등록 
                    // 데이터부족 메소드 부족
                {
                    //9.SUBJECTDAO에 과목코드를 입력하면 그에 해당하는 subject_idx 를 리턴하는 메소드
                    //9-1.PROFESSORDAO에 교번을 입력하면 그에 해당하는 professor_idx를 리턴하는 메소드

                    //#{lecture_idx},#{lecture_professor_idx},#{lecture_time},#{maximum},#{current},#{classroom}
                    // idx , professor_idx ,담당교수 교번, 강의시간, 최대 강의인원,현재 강의인원,강의실,과목코드
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    //String key = packet[1];
                    // TODO:데이터 부족
                    LectureDTO lectureDTO = new LectureDTO();
                    lectureDAO.inserSubject(lectureDTO);

                    packet = new String[1];
                    packet[0] = "12"; //TODO:응답코드 재확인 필요
                    protocol = new Protocol(Protocol.SC_RES_LECTURE_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                } 
                case Protocol.CS_REQ_LECTURE_UPDATE: //TODO: 업데이트하는거 추가적으로 데이터 + 메소드 필요
                {
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
                    //10.LectureDAO에서 idx기준 업데이트를 과목코드 기준 업데이트로 바꾸기
                    String key = packet[Protocol.PT_LECTURE_KEY_POS];
                   // lectureDAO.updateSubjectByClassRoom(); // 교실 변경
                   // lectureDAO.updateSubjectByMaximum(); //최대 수강인원 갱신
                    //TODO:12.담당교수가 변경되는 메소드 생성 필요.

                    packet = new String[1];
                    packet[0] = "14";
                    protocol = new Protocol(Protocol.SC_RES_LECTURE_UPDATE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;
                }
                case Protocol.CS_REQ_LECTURE_DELETE://교과목 삭제 요청 TODO:메소드 부족
                {
                    String key = packet[Protocol.PT_LECTURE_KEY_POS];
                    //10.LectureDAO에서 과목코드로 delete하는것 필요 근데 Subject까지 지우는것이 아니라 Lecture만 지워야함
                    LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);

                    //lectureDAO.delete(key);

                    packet = new String[1];
                    packet[0] = "14";
                    protocol = new Protocol(Protocol.SC_RES_LECTURE_DELETE);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());
                    break;

                }
                case Protocol.CS_REQ_SYLLABUSPERIOD_ENROLL: //pass
                {
                    break;
                }

                case Protocol.CS_REQ_REGISTRATIONPERIOD_ENROLL: // TODO:date 포맷 어떻게 할건지
                {
                    int grade = Integer.parseInt(packet[Protocol.PT_REGISTRATIONPERIOD_GRADE_POS]);
                    String start_date = packet[Protocol.PT_REGISTRATIONPERIOD_START_POS];
                    String end_date = packet[Protocol.PT_REGISTRATIONPERIOD_END_POS];

                    Date start = new Date(0);
                    Date end = new Date(0);
                    

                    LectureRegistrationDateDAO dao = new LectureRegistrationDateDAO(sqlSessionFactory);

                    dao.setSeason(grade,start,end);


                    packet = new String[1];
                    packet[0] = "18";
                    protocol = new Protocol(Protocol.SC_RES_REGISTRATIONPERIOD_ENROLL);
                    protocol.setPacket(packet);
                    writePacket(protocol.getPacket());

                    break;
                }

                //end

                default:
                    throw new IllegalStateException("Unexpected value: " + packetType);
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
