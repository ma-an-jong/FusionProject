package persistence.DAO;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import persistence.DTO.CourseDetailsDTO;
import persistence.DTO.LectureRegistrationDateDTO;
import persistence.DTO.Lecture_Subject_ProfessorDTO;
import persistence.DTO.StudentDTO;
import persistence.Mapper.CourseMapper;
import persistence.Mapper.LectureRegistrationDateMapper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;


//CourseDetail + CourseCompletion DAO
public class CourseRegistration {

    private SqlSessionFactory sqlSessionFactory = null;

    public CourseRegistration(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<CourseDetailsDTO> selectMyCourse(String myCode){
        List<CourseDetailsDTO> list = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            CourseMapper mapper = session.getMapper(CourseMapper.class);

            list = mapper.selectMyCourse(myCode);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    //5.CourseRegistration -> selectCourseByIdx 를 과목코드로 조회하는 기능 만들기
    // lecture랑 subject JOIN해서 만들어야함 
    public CourseDetailsDTO selectCourseByIdx(int lecture_idx){
        CourseDetailsDTO dto = null;

        try(SqlSession session = sqlSessionFactory.openSession()){
            CourseMapper mapper = session.getMapper(CourseMapper.class);

            dto = mapper.selectCourseByIdx(lecture_idx);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return dto;
    }

    public void addCoure(CourseDetailsDTO courseDetailsDTO){
        SqlSession session = sqlSessionFactory.openSession();
        CourseMapper mapper = session.getMapper(CourseMapper.class);

        try{
            //수강신청 가능기간인지
            boolean flag[] = new boolean[5];
            LectureRegistrationDateMapper lrdMapper = session.getMapper(LectureRegistrationDateMapper.class);
            List<LectureRegistrationDateDTO> seasonList = lrdMapper.selectAll();

            SimpleDateFormat sdf=new SimpleDateFormat("2021-11-09");
            String ss=sdf.format(new java.util.Date());
            Date today= Date.valueOf(ss);

            for (LectureRegistrationDateDTO dto:seasonList) {
                if(dto.getStart_date().compareTo(today) <= 0  && dto.getEnd_date().compareTo(today) >= 0){
                    flag[dto.getGrade()] = true;
                }
            }

            if(!flag[courseDetailsDTO.getGrade()]){
                System.out.println("수강신청 기간이 아닙니다.");
                return;
            }

            //강의시간이 겹치는지, 이미 신청한 과목인지, 정원이 초과되었는지
            List<CourseDetailsDTO> list = selectMyCourse(courseDetailsDTO.getStudent_code());
            int DAY = 5; // 0,1,2,3,4 = 월화수목금
            int LECTURE_TIME_NUMBER = 10; //월12,목34,금67 등
            boolean timeTable[][] = new boolean[LECTURE_TIME_NUMBER][DAY];

            for (CourseDetailsDTO lecture: list) {
                String time = lecture.getLecture_time();
                String[] str = time.split("/");
                for(int j = 0; j < str.length; j++){
                    if(str[j] != null){
                        int day;
                        switch(str[j].charAt(0)){
                            case '월': day = 0;break;
                            case '화': day = 1;break;
                            case '수': day = 2;break;
                            case '목': day = 3;break;
                            case '금': day = 4;break;
                            default:
                                System.out.println("잘못된 강의시간");
                                return;
                        }
                        for(int i = 1; i < str[j].length();i++){
                            timeTable[str[j].charAt(i) -'1'][day] = true;
                        }
                    }
                }

                String lecture_time = courseDetailsDTO.getLecture_time();
                str = lecture_time.split("/");
                int lecture_day;

                for(int j = 0 ; j < str.length;j++){
                    switch(str[j].charAt(0))
                    {
                        case '월': lecture_day = 0;break;
                        case '화': lecture_day = 1;break;
                        case '수': lecture_day = 2;break;
                        case '목': lecture_day = 3;break;
                        case '금': lecture_day = 4;break;
                        default:
                            System.out.println("잘못된 강의시간");
                            return;
                    }

                    for(int i = 1 ; i < str[j].length();i++){
                        if(timeTable[str[j].charAt(i)-'1'][lecture_day]){
                            System.out.println("중복된 강의시간");
                            return;
                        }
                    }

                }

                if(lecture.getLecture_idx() == courseDetailsDTO.getLecture_idx()){
                    System.out.println("이미 수강신청된 과목입니다.");
                    return;
                }

            }

            if(courseDetailsDTO.getCurrent() >= courseDetailsDTO.getMaximum()){
                System.out.println("정원이 초과되었습니다.");
                return;
            }

            mapper.addCourse(courseDetailsDTO);
            mapper.addCurrent(courseDetailsDTO.getLecture_idx());
            session.commit();
            System.out.println("수강신청 성공");
        }
        catch (Exception e){
            e.printStackTrace();
            session.rollback();
        }
        finally {
            session.close();
        }

    }

    public void deleteCourse(CourseDetailsDTO courseDetailsDTO){
        SqlSession session = sqlSessionFactory.openSession();
        CourseMapper mapper = session.getMapper(CourseMapper.class);

        try{
            List<CourseDetailsDTO> list = selectMyCourse(courseDetailsDTO. getStudent_code());
            for (CourseDetailsDTO lecture: list) {
                if(lecture.getLecture_idx() == courseDetailsDTO.getLecture_idx()){
                    mapper.deleteCourse(courseDetailsDTO);
                    mapper.discountCurrent(courseDetailsDTO.getLecture_idx());
                }
            }
            session.commit();
            System.out.println("수강신청 삭제 완료");
        }
        catch (Exception e){
            e.printStackTrace();
            session.rollback();
        }
        finally {
            session.close();
        }

    }

    public List<StudentDTO> selectWithPaging(String key, int pageNum){

        List<StudentDTO> list = null;

        HashMap<String,Object> map = new HashMap<String,Object>();

        map.put("subject_code", key);
        map.put("pageNum",(pageNum-1)*2);

        try(SqlSession session = sqlSessionFactory.openSession()) {
            list = session.selectList("mapper.CourseMapper.selectWithPaging",map);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    return list;
    }



}
