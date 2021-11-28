package Client;

public class Protocol {

    private String packet = "";
    private String protocolType;

    public Protocol()
    {
        this(PT_UNDEFINED);
    }

    public Protocol(String protocolType)
    {
        this.protocolType = protocolType;
        getPacket(protocolType);
    }

    public static final int TYPE_DEFINED_POS = 0;
    public static final String splitter ="!@#%!@#%";
    public static final String PT_UNDEFINED = "undefined";

    //로그인 정보 위치
    public static final int PT_LOGIN_ID_POS = 1; //로그인 아이디 위치
    public static final int PT_LOGIN_PASSWORD_POS = 2; //로그인 비밀번호 위치
    public static final int PT_LOGIN_KEY_POS = 1;  //로그인 응답 학번/교번 위치
    public static final int PT_LOGIN_GRADE_POS = 2; //로그인 응답 학년 위치
    public static final int PT_LOGIN_CATEGORY_POS = 3; //로그인 응답 카테고리 위치

    //개인정보 수정 위치
    public static final int PT_PERSONALINFO_ID_POS = 1;
    public static final int PT_PERSONALINFO_PASSWORD_POS = 2;
    public static final int PT_PERSONALINFO_NAME_POS = 3;
    public static final int PT_PERSONALINFO_DEPARTMENT_POS = 4;
    public static final int PT_PERSONALINFO_PHONE_POS = 5;
    public static final int PT_PERSONALINFO_GRADE_POS = 6;

    //학생 수강신청 인증 관련 위치
    public static final int PT_REGISTRATION_GRADE_POS = 1;

    //학생 수강신청 관련 우치
    public static final int PT_MYSUBJECT_STUDENT_CODE_POS = 1;
    public static final int PT_MYSUBJECT_SUBJECT_CODE_POS = 2;

    //학생 개인정보 조회 위치
    public static final int PT_PERSONALINFO_KEY_POS = 1;

    //학생 수강 과목 조회 요청


    //학생 본인 시간표 조회 요청 위치
    public static final int PT_TIMETABLE_KEY_POS = 1;

    //학생 -> 강의 계획서 조회
    public static final int PT_SYLLABUS_VIEW_KEY_POS= 1;
    public static final int PT_SYLLABUS_VIEW_CONTEXT_POS = 2;
    
    //교수 -> 강의 계획서 등록
    public static final int PT_SYLLABUS_FILE_KEY_POS= 1;
    public static final int PT_SYLLABUS_FILE_CONTEXT_POS= 2;

    //교수 -> 강의 계획서 등록인증
    public static final int PT_SYLLABUS_ENROLL_STARTDATE_POS = 1;
    public static final int PT_SYLLABUS_ENROLL_ENDDATE_POS = 2;

    //강의 계획서 조회
    
    //개설 교과목 목록 조회
    //딱히 필요한 정보 없음

    //교수담당과목 조회 관련 위치
    public static final int PT_TEACHING_KEY_POS = 1;

    //담당교과목 수강신청 학생 목록 조회 요청 위치
    public static final int PT_MYSTUDENT_KEY_POS = 1;
    public static final int PT_MYSTUDENT_PAGENUM_POS = 2;

    ////교수,학생 정보 조회 요청
    public static final int PT_MEMBER_VIEW_KEY_POS = 1; //key값 들어갈 위치
    public static final int PT_MEMBER_VIEW_DATA_POS = 1;

    //사용자 계정 등록 요청
    public static final int PT_MEMBER_ENROLL_KEY_POS = 1;
    public static final int PT_MEMBER_ENROLL_NAME_POS = 2;
    public static final int PT_MEMBER_ENROLL_PASSWORD_POS = 3;
    public static final int PT_MEMBER_ENROLL_DEPT_POS = 4;
    public static final int PT_MEMBER_ENROLL_PHONE_POS = 5;
    public static final int PT_MEMBER_ENROLL_GRADE_POS = 6;

    //교과목 정보
    public static final int PT_SUBINFO_KEY_POS = 1; //교과목 코드 위치
    public static final int PT_SUBINFO_NAME_POS = 2; // 이름위치
    public static final int PT_SUBINFO_GRADE_POS = 3; // 학년위치

    //개설 교과목 정보 조회
    public static final int PT_LECTUREINFO_KEY_POS = 1; //개설 교과목 코드 위치


    //관리자가 개설 교과목 등록
    public static final int PT_LECTURE_ENROLL_SUBJECT_KEY_POS = 1;
    public static final int PT_LECTURE_ENROLL_PROFESSOR_KEY_POS = 2;
    public static final int PT_LECTURE_ENROLL_LECTURE_TIME_POS = 3;
    public static final int PT_LECTURE_ENROLL_MAXIMUM_POS = 4;
    public static final int PT_LECTURE_ENROLL_CLASSROOM_POS = 5;

    //관리자가 교과목 수정
    public static final int PT_Subject_OLD_NAME_POS = 1; //old name 위치
    public static final int PT_Subject_NEW_NAME_POS = 2; //new name 위치

    //관리자가 교과목 삭제
    public static final int PT_Subject_KEY_POS = 1;


    //관리자가 //교과목 수정 요청
    public static final int PT_LECTURE_KEY_POS= 1;
    public static final int PT_LECTURE_CLASSROOM_POS= 2;
    public static final int PT_LECTURE_MAXIMUM_POS= 3;

    //수강신청 기간 등록 요청
    public static final int PT_REGISTRATIONPERIOD_GRADE_POS = 1;
    public static final int PT_REGISTRATIONPERIOD_START_POS = 2;
    public static final int PT_REGISTRATIONPERIOD_END_POS = 3;

    //프로토콜 타입
    public static final String PT_EXIT = "0"; //프로그램 종료
    public static final String PT_REQ_LOGIN = "1"; //로그인 요청
    public static final String PT_LOGIN_RESULT = "2"; //로그인 요청 응답
    public static final String PT_REQ_SENDFILE = "3"; //파일 전송 요청
    public static final String PT_RES_SENDFILE = "4"; //파일 전송 요청 응답
    public static final String PT_REQ_FILE = "5"; //파일 전송 데이터
    public static final String PT_RES_FILE = "6"; //파일 전송 성공 여부
    public static final String PT_REQ_VIEW = "7"; //조회 요청
    public static final String PT_RES_VIEW = "8"; //조회 요청 응답
    public static final String PT_REQ_ENROLL = "9"; //등록 요청
    public static final String PT_RES_ENROLL = "A"; //등록 요청 응답
    public static final String PT_REQ_REVISE = "B"; //정정 요청
    public static final String PT_RES_REVISE = "C"; //정정 요청 응답

    //학생
    //수강과목 등록,수정,삭제
    public static final String CS_REQ_REGISTRATION = "9-0"; //수강 신청 요청
    public static final String SC_RES_REGISTRATION = "A-2"; //수강 신청 요청 응답

    public static final String CS_REQ_MYSUBJECT_ENROLL = "9-1"; //수강 과목 등록 요청
    public static final String SC_RES_MYSUBJECT_ENROLL = "A-4"; //수강 과목 등록 요청 응답

    public static final String CS_REQ_MYSUBJECT_VIEW = "7-9"; //수강 과목 조회 요청
    public static final String SC_RES_MYSUBJECT_VIEW = "8-10"; //수강 과목 조회 요청 응답
    public static final String CS_REQ_MYSUBJECT_DELETE = "B-5"; //수강 과목 삭제 요청
    public static final String SC_RES_MYSUBJECT_DELETE = "C-4"; //수강 과목 삭제 요청 응답

    //개인정보 및 비밀번호 조회 및 수정
    public static final String CS_REQ_PROFESSOR_PERSONALINFO_VIEW = "7-A"; //교수 개인정보 조회 요청
    public static final String SC_RES_PROFESSOR_PERSONALINFO_VIEW = "8-14"; //교수 개인정보 조회 요청 응답
    public static final String CS_REQ_STUDENT_PERSONALINFO_VIEW = "7-C"; //학생 개인정보 조회 요청
    public static final String SC_RES_STUDENT_PERSONALINFO_VIEW = "8-14"; //학생 개인정보 조회 요청 응답

    public static final String CS_REQ_PROFESSOR_PERSONALINFO_UPDATE = "B-3"; //교수 개인정보 수정 요청
    public static final String SC_RES_PROFESSOR_PERSONALINFO_UPDATE = "C-0"; //교수 개인정보 수정 요청 응답
    public static final String CS_REQ_STUDENT_PERSONALINFO_UPDATE = "B-3"; //학생 개인정보 수정 요청
    public static final String SC_RES_STUDENT_PERSONALINFO_UPDATE = "C-0"; //학생 개인정보 수정 요청 응답

    //본인 시간표 조회
    public static final String CS_REQ_TIMETABLE_VIEW = "7-8"; //본인 시간표 조회 요청
    public static final String SC_RES_TIMETABLE_VIEW = "8-10"; //본인 시간표 조회 요청 응답
    //개설 교과목 목록 조회
    public static final String CS_REQ_LECTURE_VIEW = "7-6"; //개설 교과목 목록 조회
    public static final String SC_RES_LECTURE_VIEW = "8-C"; //개설 교과목 목록 조회 응답
    //선택 교과목 강의계획서 조회
    public static final String CS_REQ_SYLLABUS_VIEW = "7-7"; //강의계획서 조회
    public static final String SC_RES_SYLLABUS_VIEW = "8-E"; //강의계획서 조회 응답

    //교수
    //담당 교과목 목록 조회
    public static final String CS_REQ_TEACHING_VIEW = "7-2"; //담당 교과목 목록 조회
    public static final String SC_RES_TEACHING_VIEW = "8-4"; //담당 교과목 목록 조회 응답
    //강의계획서 등록,수정
    public static final String CS_REQ_SYLLABUS_ENROLL = "9-2"; //담당 교과목 강의계획서 등록 요청
    public static final String SC_RES_SYLLABUS_ENROLL = "A-0"; //담당 교과목 강의계획서 등록 요청 응답

    public static final String CS_REQ_SYLLABUS_SENDFILE = "3-0"; //강의계획서 파일 전송 요청
    public static final String SC_RES_SYLLABUS_SENDFILE = "4-1"; //강의계획서 파일 전송 요청 응답

    public static final String CS_REQ_SYLLABUS_FILE = "5-0"; //강의계획서 파일 전송
    public static final String SC_RES_SYLLABUS_FILE = "6-1"; //강의계획서 파일 전송 응답

    //담당 교과목 강의계획서 조회
    public static final String CS_REQ_MYSYLLABUS_VIEW = "7-3"; //담당교과목 강의계획서 조회 요청
    public static final String SC_RES_MYSYLLABUS_VIEW = "8-6"; //담당교과목 강의계획서 조회 요청 응답
    //담당 교과목 수강 신청 학생 목록 조회
    public static final String CS_REQ_MYSTUDENT_VIEW = "7-4"; //담당교과목 수강신청 학생 목록 조회 요청
    public static final String SC_RES_MYSTUDENT_VIEW = "8-8"; //담당교과목 수강신청 학생 목록 조회 요청 응답
    //담당 교과목 시간표 조회
    public static final String CS_REQ_TEACHINGTABLE_VIEW = "7-5"; //담당교과목 시간표 조회 요청
    public static final String SC_RES_TEACHINGTABLE_VIEW = "8-A"; //담당교과목 시간표 조회 요청 응답

    //관리자

    //교수 정보 조회 TODO:값이 똑같음
    public static final String CS_REQ_PROFESSOR_VIEW = "7-11"; //교수 정보 조회 요청
    public static final String SC_RES_PROFESSOR_VIEW = "8-0"; //교수 정보 조회 요청 응답
    //학생 정보 조회
    public static final String CS_REQ_STUDENT_VIEW = "7-1"; //학생 정보 조회 요청
    public static final String SC_RES_STUDENT_VIEW = "8-0"; //학생 정보 조회 요청 응답

    //모든 교수,학생 정보 조회
    public static final String CS_REQ_ALLMEMBER_VIEW = "7-B"; //모든 교수,학생 정보 조회 요청
    public static final String SC_RES_ALLMEMBER_VIEW = "8-16"; //모든 교수, 학생 정보 조회 요청 응답

    //교수 계정 생성
    public static final String CS_REQ_PROFESSOR_ENROLL = "9-13"; //교수 계정 등록 요청
    public static final String SC_RES_PROFESSOR_ENROLL = "A-6"; //교수 계정 등록 요청에 대한 응답
    //학생 계정 생성
    public static final String CS_REQ_STUDENT_ENROLL = "9-3"; //학생 계정 등록 요청
    public static final String SC_RES_STUDENT_ENROLL = "A-6"; //학생 계정 등록 요청에 대한 응답

    //전체 교과목 정보 조회
    public static final String CS_REQ_SUBJECT_VIEW = "7-E"; //전체 교과목 정보 조회 요청
    public static final String SC_RES_SUBJECT_VIEW = "8-21"; //전체 교과목 정보 조회 요청 응답

    //개설교과목 정보 조회
    public static final String CS_REQ_LECTUREINFO_VIEW = "7-D"; //개설교과목 정보 조회 요청
    public static final String SC_RES_LECTUREINFO_VIEW = "8-19"; //개설교과목 정보 조회 요청 응답

    //교과목 생성,수정,삭제
    public static final String CS_REQ_SUBJECT_ENROLL = "9-4"; //교과목 등록 요청
    public static final String SC_RES_SUBJECT_ENROLL = "A-8"; //교과목 등록 요청 응답
    public static final String CS_REQ_SUBJECT_UPDATE = "B-2"; //교과목 수정 요청
    public static final String SC_RES_SUBJECT_UPDATE = "C-8"; //교과목 수정 요청 응답
    public static final String CS_REQ_SUBJECT_DELETE = "B-13"; //교과목 삭제 요청
    public static final String SC_RES_SUBJECT_DELETE = "C-A"; //교과목 삭제 요청 응답

    //개설 교과목 생성, 수정, 삭제
    public static final String CS_REQ_LECTURE_ENROLL = "9-7"; //개설교과목 등록 요청
    public static final String SC_RES_LECTURE_ENROLL = "A-E"; //개설교과목 등록 요청 응답
    public static final String CS_REQ_LECTURE_UPDATE = "B-15"; //개설교과목 수정 요청
    public static final String SC_RES_LECTURE_UPDATE = "C-C"; //개설교과목 수정 요청 응답
    public static final String CS_REQ_LECTURE_DELETE = "B-6"; //개설교과목 삭제 요청
    public static final String SC_RES_LECTURE_DELETE = "C-E"; //개설교과목 삭제 요청 응답

   //강의계획서 입력/학년별 수강신청 기간 설정
    public static final String CS_REQ_SYLLABUSPERIOD_ENROLL = "9-5"; //강의계획서 입력 기간 등록 요청
    public static final String SC_RES_SYLLABUSPERIOD_ENROLL = "A-A"; //강의계획서 입력 기간 등록 요청 응답
    public static final String CS_REQ_REGISTRATIONPERIOD_ENROLL = "9-6"; //수강신청 기간 등록 요청
    public static final String SC_RES_REGISTRATIONPERIOD_ENROLL = "A-B"; //수강신청 기간 등록 요청 응답

    //<-------------프로토콜 메소드 ------------->
    //프로토콜에는 String 변수하나
    //서버 클라이언트가 패킷 배열 들고있으면 그걸로 각각 알아서 처리

    public void setPacket (String[] stringValue)
    {
       int size = stringValue.length;
       packet += protocolType + splitter;

       for(int i=0 ; i<size ; i++) {
           if(i == size-1) packet += stringValue[i];
           else packet += stringValue[i] + splitter;
       }
    }

    public String getPacket(String protocolType) { return packet; }
    public String getPacket() { return packet; }
}