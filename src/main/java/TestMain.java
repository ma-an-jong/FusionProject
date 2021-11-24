import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DAO.*;
import persistence.DTO.*;
import persistence.MyBatisConnectionFactory;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestMain {
    static AdminDAO adminDAO = new AdminDAO();
    static SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    static SubjectDAO subjectDAO = new SubjectDAO(sqlSessionFactory);
    static LectureDAO lectureDAO = new LectureDAO(sqlSessionFactory);
    static CourseRegistration courseRegistration = new CourseRegistration(sqlSessionFactory);
    static LectureRegistrationDateDAO lectureRegistrationDateDAO = new LectureRegistrationDateDAO(MyBatisConnectionFactory.getSqlSessionFactory());


    public static void printList(List list) {
        list.stream().forEach(v -> System.out.println("v.toString() = " + v.toString()));
    }


//1.회원 CRU

    //admin 1명 생성
    public static void adminCreate(){
        adminDAO.createAdmin("admin","1234");
    }
    //professor 2명 생성
    public static void professorCreate(){

        adminDAO.createProfessor("sungryul","1234","컴퓨터 소프트웨어","김성렬","010-106-107");
        adminDAO.createProfessor("sunmyeng","1234","컴퓨터 소프트웨어","김선명","010-441-442");

    }
    //학생 4명 생성
    public static void studentCreate(){

        adminDAO.createStudent("20180167","991113","의예과","김민종",2,"010-8589-0670");
        adminDAO.createStudent("20180017" , "991107" ,"축구학과","강수성",2,"010-2767-4303");
        adminDAO.createStudent("20171290","980707","연기학과","황주희",4,"010-7504-4057");
        adminDAO.createStudent("20180358","990912","호텔관광과","김호진",2,"010-1234-1234");
    }

    public static void selectUser(){

        List list = new ArrayList();
        list = adminDAO.selectAllStudent();
        printList(list);

        list = new ArrayList();
        list = adminDAO.selectAllProfessor();
        printList(list);

        list = new ArrayList();
        list = adminDAO.selectAllAdmin();
        printList(list);
    }

//2.교과목

    // 1) Create 2학년 2학기 전체
    public static void createSubject1(){
        System.out.println("2 학년 등록 시작");

        HashMap<String,Object> map = null;

        //1번째
        map = new HashMap<String,Object>();
        map.put("subject_code","CS0016-02");
        map.put("name","컴퓨터 네트워크");
        map.put("grade",2);
        subjectDAO.insertSubject(map);
        System.out.println("1번 성공");

        //2번째
        map = new HashMap<String,Object>();
        map.put("subject_code","CS0017-01");
        map.put("name","운영체제");
        map.put("grade",2);
        subjectDAO.insertSubject(map);
        System.out.println("2번 성공");

        //3번째
        map = new HashMap<String,Object>();
        map.put("subject_code","CS0069-02");
        map.put("name","융합 프로젝트");
        map.put("grade",2);
        subjectDAO.insertSubject(map);
        System.out.println("3번 성공");

        //4번째
        map = new HashMap<String,Object>();
        map.put("subject_code","CS0077-01");
        map.put("name","C++프로그래밍");
        map.put("grade",2);
        subjectDAO.insertSubject(map);
        System.out.println("4번 성공");

        //5번째
        map = new HashMap<String,Object>();
        map.put("subject_code","CS0080-01");
        map.put("name","오픈소스소프트웨어");
        map.put("grade",2);
        subjectDAO.insertSubject(map);
        System.out.println("5번 성공");
    }

    // 1) Create 134학년 2학기 1개씩
    public static void createSubjectGrade134(){
        System.out.println("1,3,4 학년 등록 시작");
        HashMap<String,Object> map = null;

        //1번째
        map = new HashMap<String,Object>();
        map.put("subject_code","CS0010-01");
        map.put("name","자바프로그래밍");
        map.put("grade",1);
        subjectDAO.insertSubject(map);
        System.out.println("1번 성공");

        //2번째
        map = new HashMap<String,Object>();
        map.put("subject_code","CS0072-01");
        map.put("name","빅데이터");
        map.put("grade",3);
        subjectDAO.insertSubject(map);
        System.out.println("2번 성공");

        //3번째
        map = new HashMap<String,Object>();
        map.put("subject_code","CS0035-01");
        map.put("name","컴파일러");
        map.put("grade",4);
        subjectDAO.insertSubject(map);
        System.out.println("3번 성공");

    }

    // 2) READ
    public static void selectSubjectAll(){
        System.out.println("전체 조회 기능");
        List<SubjectDTO> list = subjectDAO.selectAll();

        printList(list);
    }

    public static void selectSubjectByGrade(){
        System.out.println("학년별 조회 기능");
        System.out.println("\n1학년 조회");
        List<SubjectDTO> list = subjectDAO.selectByGrade(1);
        printList(list);

        System.out.println("\n2학년 조회");
        list = subjectDAO.selectByGrade(2);
        printList(list);

        System.out.println("\n3학년 조회");
        list = subjectDAO.selectByGrade(3);
        printList(list);

        System.out.println("\n4학년 조회");
        list = subjectDAO.selectByGrade(4);
        printList(list);
    }

    //3) 과목명 변경
    public static void updateSubjectName(){
    //#{new_name},#{old_name}
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("old_name","C++프로그래밍");
        map.put("new_name","C프로그래밍");
        subjectDAO.updateSubjectName(map);

        List<SubjectDTO> list = subjectDAO.selectByGrade(2);
        printList(list);
    }

//3. 개설 교과목

    //create
    public static void createLecture2(){
        System.out.println("2 학년 등록 시작");

        LectureDTO dto = null;

        //1번째
        dto = new LectureDTO();
        dto.setLecture_idx(1);
        dto.setLecture_professor_idx(3);
        dto.setLecture_time("목89/금67");
        dto.setMaximum(3);
        dto.setCurrent(0);
        dto.setClassroom("D327");
        lectureDAO.inserSubject(dto);
        System.out.println("1번 성공");

        //2번째
        dto = new LectureDTO();
        dto.setLecture_idx(2);
        dto.setLecture_professor_idx(3);
        dto.setLecture_time("월6/수12");
        dto.setMaximum(3);
        dto.setCurrent(0);
        dto.setClassroom("D327");
        lectureDAO.inserSubject(dto);
        System.out.println("2번 성공");


        //3번째
        dto = new LectureDTO();
        dto.setLecture_idx(3);
        dto.setLecture_professor_idx(2);
        dto.setLecture_time("수34");
        dto.setMaximum(3);
        dto.setCurrent(0);
        dto.setClassroom("D331");
        lectureDAO.inserSubject(dto);
        System.out.println("3번 성공");

        //4번째
        dto = new LectureDTO();
        dto.setLecture_idx(4);
        dto.setLecture_professor_idx(2);
        dto.setLecture_time("목67/금34");
        dto.setMaximum(3);
        dto.setCurrent(0);
        dto.setClassroom("D331");
        lectureDAO.inserSubject(dto);
        System.out.println("4번 성공");


        //5번째
        dto = new LectureDTO();
        dto.setLecture_idx(5);
        dto.setLecture_professor_idx(3);
        dto.setLecture_time("월34/수8");
        dto.setMaximum(3);
        dto.setCurrent(0);
        dto.setClassroom("D331");
        lectureDAO.inserSubject(dto);
        System.out.println("5번 성공");

        System.out.println("1,3,4 학년 등록 시작");

        //6번째
        dto = new LectureDTO();
        dto.setLecture_idx(6);
        dto.setLecture_professor_idx(3);
        dto.setLecture_time("화12/목67");
        dto.setMaximum(3);
        dto.setCurrent(0);
        dto.setClassroom("D438");
        lectureDAO.inserSubject(dto);
        System.out.println("6번 성공");

        //7번째
        dto = new LectureDTO();
        dto.setLecture_idx(7);
        dto.setLecture_professor_idx(2);
        dto.setLecture_time("화3/수67");
        dto.setMaximum(3);
        dto.setCurrent(0);
        dto.setClassroom("D331");
        lectureDAO.inserSubject(dto);
        System.out.println("7번 성공");


        //8번째
        dto = new LectureDTO();
        dto.setLecture_idx(8);
        dto.setLecture_professor_idx(3);
        dto.setLecture_time("월6/수34");
        dto.setMaximum(3);
        dto.setCurrent(0);
        dto.setClassroom("D329");
        lectureDAO.inserSubject(dto);
        System.out.println("8번 성공");

    }

    //select
    public static void readLecture(){
        System.out.println("전체 조회 시작");

        Lecture_Subject_ProfessorDTO dto = new Lecture_Subject_ProfessorDTO();

        List<Lecture_Subject_ProfessorDTO> list = lectureDAO.selectAll();
        printList(list);

        System.out.println("학년별 조회 시작");

        System.out.println("1학년");
        list = lectureDAO.selectByGrade(1);
        printList(list);

        System.out.println("2학년");
        list = lectureDAO.selectByGrade(2);
        printList(list);

        System.out.println("3학년");
        list = lectureDAO.selectByGrade(3);
        printList(list);

        System.out.println("4학년");
        list = lectureDAO.selectByGrade(4);
        printList(list);

        System.out.println("교수별 조회 시작");

        System.out.println("김성렬 교수님이 담당하는 교과목");
        list = lectureDAO.selectByProfessor("김성렬");
        printList(list);

        System.out.println("김선명 교수님이 담당하는 교과목");
        list = lectureDAO.selectByProfessor("김선명");
        printList(list);

        System.out.println("김선명 교수님이 담당하는 2학년 교과목");
        list = lectureDAO.selectByProfessor("김선명");
        printList(list);

        System.out.println("김성렬 교수님이 담당하는 3학년 교과목");
        list = lectureDAO.selectByProfessor("김성렬");
        printList(list);

    } //TODO: activity 어떡하징?

    //update
    public static void updateLecture(){
        lectureDAO.updateSubjectByClassRoom("D440",1);
        lectureDAO.updateSubjectByMaximum(4,1);
        List<Lecture_Subject_ProfessorDTO> list = lectureDAO.selectAll();
        printList(list);
    }

//4.수강신청 기간설정

    public static void setDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("2021-11-11");
        String ss=sdf.format(new java.util.Date());
        Date start= Date.valueOf(ss);

        sdf=new SimpleDateFormat("2021-11-13");
        ss=sdf.format(new java.util.Date());
        Date end= Date.valueOf(ss);

        lectureRegistrationDateDAO.setSeason(1,start,end);

    }

//5.수강 신청 학생관점
    //등록
    public static void registrate(){
        CourseDetailsDTO courseDetailsDTO = courseRegistration.selectCourseByIdx(1);
        courseDetailsDTO.setStudent_idx(4);
        courseRegistration.addCoure(courseDetailsDTO);
    }
    //조회
    public static void readMyCourse(){
        List<CourseDetailsDTO> list = courseRegistration.selectMyCourse("20180167");
        printList(list);
    }
    //삭제
    public static void deleteCourse(){
        CourseDetailsDTO courseDetailsDTO = courseRegistration.selectCourseByIdx(1);
        courseDetailsDTO.setStudent_idx(4);
        courseRegistration.deleteCourse(courseDetailsDTO);
    }

//6.중복 예외처리

    public static void exception_Already(){
        CourseDetailsDTO courseDetailsDTO = courseRegistration.selectCourseByIdx(3);
        courseDetailsDTO.setStudent_idx(4);
        courseRegistration.addCoure(courseDetailsDTO);

        courseDetailsDTO = courseRegistration.selectCourseByIdx(8);
        courseDetailsDTO.setStudent_idx(4);
        courseRegistration.addCoure(courseDetailsDTO);

    }

    public static void exception_Maximum(){

        CourseDetailsDTO courseDetailsDTO = courseRegistration.selectCourseByIdx(2);
        courseDetailsDTO.setStudent_idx(4);
        courseRegistration.addCoure(courseDetailsDTO);

        courseDetailsDTO = courseRegistration.selectCourseByIdx(2);
        courseDetailsDTO.setStudent_idx(5);
        courseRegistration.addCoure(courseDetailsDTO);

        courseDetailsDTO = courseRegistration.selectCourseByIdx(2);
        courseDetailsDTO.setStudent_idx(6);
        courseRegistration.addCoure(courseDetailsDTO);

        courseDetailsDTO = courseRegistration.selectCourseByIdx(2);
        courseDetailsDTO.setStudent_idx(7);
        courseRegistration.addCoure(courseDetailsDTO);

    }


//7. paging
    public static void paging(){
        CourseDetailsDTO courseDetailsDTO = new CourseDetailsDTO();
        courseDetailsDTO.setLecture_idx(2);
        List<StudentDTO> list = courseRegistration.selectWithPaging(courseDetailsDTO,2);
        printList(list);
    }


    public static void main(String args[]){
//
//            adminCreate();
//            professorCreate();
//            studentCreate();
//
//            StudentDAO studentDAO = new StudentDAO();
//            studentDAO.updateName(4,"박민종");
            selectUser();


//
//
//            createSubject1();
//            createSubjectGrade134();
//            selectSubjectAll();
//
//            selectSubjectByGrade();
//            updateSubjectName();
//
//
//           createLecture2();
//        readLecture();
//            updateLecture();
//
//            setDate();
//        readLecture();
//
//
//            registrate();
//            readMyCourse();
//            deleteCourse();
//
//        exception_Already();
//        exception_Maximum();
//          paging();


    }

}
