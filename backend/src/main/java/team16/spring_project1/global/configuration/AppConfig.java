package team16.spring_project1.global.configuration;

import static team16.spring_project1.standard.util.Ut.jwt.getOs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import team16.spring_project1.global.enums.OsType;

@Configuration
public class AppConfig {
  public static String getSiteFrontUrl() {
    return "http://localhost:3000";
  }
  //public static String getStaticDirectory() {return "/NBE3-4-1-16Team/backend/src/main/resources/static/"; }
  public static String getStaticDirectory() {
    if(getOs() == OsType.WIN)
      return "C://";
    else if(getOs() == OsType.MAC) {
      return "/Users/"+System.getProperty("user.name");
    }
    else
      return"/";
  }
  public static String getImagesFolder() {return "images/";}
}
