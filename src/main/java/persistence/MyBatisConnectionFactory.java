package persistence;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import persistence.Mapper.CourseMapper;
import persistence.Mapper.LectureMapper;
import persistence.Mapper.LectureRegistrationDateMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

public class MyBatisConnectionFactory {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            String resource = "config/config.xml";
            Reader reader = Resources.getResourceAsReader(resource);

            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader,"development");

                Class[] mappers = {
                        LectureRegistrationDateMapper.class,
                        LectureMapper.class,
                        CourseMapper.class
                };

                for(Class mapper : mappers){
                    sqlSessionFactory.getConfiguration().addMapper(mapper);
                }
            }

        }
        catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
