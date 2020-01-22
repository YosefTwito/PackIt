//package gameClient;
//
//import de.micromata.opengis.kml.v_2_2_0.*;
//import de.micromata.opengis.kml.v_2_2_0.Icon;
//import elements.Fruit;
//import elements.Robot;
//
//import java.io.File;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
///**
// * this class enables exporting the game, including the robots and fruits to a KML file
// * which can be loaded to Google Maps.
// * On Maps you can view the progress of the game and iterate through the stages of the game
// * @author Eldar and Yossi
// */
//
//public class KML_Logger {
//
//	//File f = new File("kmlFile.kml");
//	
//	/**
//	 * converts long to string
//	 * represents mililis
//	 * @param millis
//	 * @return
//	 */
//    public String mil_to_str(Long millis) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return sdf.format(new Date(millis));
//    }
//    /**
//     * converts string to long
//     * represents millis
//     * @param str
//     * @return
//     * @throws ParseException
//     */
//    public long str_to_mil(String str) throws ParseException {
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//        Date d = f.parse(str.toString());
//        long millis = d.getTime();
//        return millis;
//    }
//    /**
//     * @return the time is specified format
//     */
//    public String time() {
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
//    }
//    /**
//     * splits an array to string;
//     * @param arr
//     * @return
//     */
//    public String splitArr(String[] arr){
//        String temp= arr[0] + "|" + arr[1] + "|";
//        return temp;
//    }
//    Kml kmlDoc = new Kml(); // new KML_DOC
//    Document doc = kmlDoc.createAndSetDocument(); // new DOC
//    
//    /**
//     * KML parser, runs while the game is running
//     * first time the function runs it creates a file
//     * than constantly saves the data to the existing file
//     * @param mg
//     * @param i
//     * @throws ParseException
//     * @throws InterruptedException
//     */
//    public void make_kml(MyGame mg,int i) throws ParseException, InterruptedException {
//        try {
//        	if(i==0) {		
//        		i++;
//        		kmlDoc.marshal(new File("kmlFile.kml"));
//        	}
// 	
//        }
//        catch (Exception e) {e.printStackTrace();}
//
//        int c=0;
//       
//        if(mg!=null){
//        		
//                ArrayList<Fruit> fruit = mg.fru_list;
//                ArrayList<Robot> robots = mg.robo_list;
//                //for every robot- save its data to the file
//                for (Robot robot: robots) {
//                    Placemark rob_mark = doc.createAndAddPlacemark();
//                    //set style
//                    IconStyle rob_style = new IconStyle();
//                    rob_style.setScale(1);
//                    rob_style.setHeading(1);
//                    rob_style.setColor("abcdefg");
//                    
//                    //set icon
//                    Icon rob_icon = new Icon();
//                    rob_style.setIcon(rob_icon);
//                    rob_icon.setHref("robot.png");
//                    rob_icon.setViewBoundScale(1);
//                    rob_icon.setViewRefreshTime(1);
//                    rob_icon.withRefreshInterval(1);
//                    //set marks and time
//                    rob_mark.createAndAddStyle().setIconStyle(rob_style);
//                    rob_mark.withDescription("\nType: Robot").withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(robot.getPos().x(),robot.getPos().y());
//                    String rob_t1 = mil_to_str(str_to_mil(time())+c*1000);
//                    String rob_t2 = mil_to_str(str_to_mil(time())+(c +1)*1000);
//                    String[] timeArr =rob_t1.split(" ");
//                    rob_t1=splitArr(timeArr);
//                    String[] timeArr2 = rob_t2.split(" ");
//                    rob_t2=splitArr(timeArr2);
//                    //create time span
//                    TimeSpan rob_span = rob_mark.createAndSetTimeSpan();
//                    rob_span.setBegin(rob_t1);
//                    rob_span.setEnd(rob_t2);
//                }
//                //for every fruit- saves its data to the file
//                for(Fruit f :fruit){
//                    Placemark f_mark = doc.createAndAddPlacemark();
//                    //create style and icon
//                    Icon f_icon = new Icon();
//                    IconStyle f_style = new IconStyle();
//                    f_style.setScale(1);
//                    f_style.setHeading(1);
//                    f_style.setColor("ff007db3");
//                    f_style.setIcon(f_icon);
//                    //sets icon
//                    f_icon.setHref("apple.png");
//                    f_icon.setViewBoundScale(1);
//                    f_icon.setViewRefreshTime(1);
//                    f_icon.withRefreshInterval(1);
//                    //set marks and time 
//                    f_mark.createAndAddStyle().setIconStyle(f_style);
//                    f_mark.withDescription("\nType: Fruit").withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(f.getPos().x(),f.getPos().y());
//                    String f_t1 = mil_to_str(str_to_mil(time())+c*1000);
//                    String f_t2 = mil_to_str(str_to_mil(time())+(c+1)*1000);
//                    String[] fruitArr= f_t1.split(" ");
//                    f_t1=splitArr(fruitArr);
//                    String[] fruitArr2 = f_t2.split(" ");
//                    f_t2=splitArr(fruitArr2);
//                    //create time span
//                    TimeSpan f_span = f_mark.createAndSetTimeSpan();
//                   
//                    f_span.setBegin(f_t1);
//                    f_span.setEnd(f_t2);
//
//                }
//                c++; //count changes
//            }
//
//  
//    }
//
//	//main for testing only
//    
//	public static void main(String[] args) {
//		
//	}
//}


  
package gameClient;


import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.Icon;
import elements.Fruit;
import elements.Robot;


import javax.swing.*;
import java.io.File;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class KML_Logger {
    Kml kmlDoc = new Kml();
    Document doc = kmlDoc.createAndSetDocument();
    /**
     * this function make a file with format of KML. using open source libs.
     * @throws ParseException
     * @throws InterruptedException
     */
    public void makeKML(MyGame mygame,int i) throws ParseException, InterruptedException {

        int change=0;
        if(mygame!=null){

                change++;
                ArrayList<Fruit> fruit = mygame.fru_list;
                ArrayList<Robot>  players= mygame.robo_list;

                for (Robot robot: players) {
                    Placemark robotMark = doc.createAndAddPlacemark();
                    Icon robIcon = new Icon();

                    robIcon.setHref("robot.png");
                    robIcon.setViewBoundScale(1);
                    robIcon.setViewRefreshTime(1);
                    robIcon.withRefreshInterval(1);
                    IconStyle robIconeStyle = new IconStyle();
                    robIconeStyle.setScale(1);
                    robIconeStyle.setHeading(1);
                    robIconeStyle.setColor("ff007db3");
                    robIconeStyle.setIcon(robIcon);
                    robotMark.createAndAddStyle().setIconStyle(robIconeStyle);
                    robotMark.withDescription("Mac: " + "\nType: Robot").withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(robot.getPos().x(),robot.getPos().y());
                    String robTime1 = MillisToString(StringToMillis(TimeNow())+change*1000);
                    String robTime2 = MillisToString(StringToMillis(TimeNow())+(change +1)*1000);
                    String[] timeArr =robTime1.split(" ");
                    robTime1=splitArr(timeArr);
                    String[] timeArr2 = robTime2.split(" ");
                    robTime2=splitArr(timeArr2);
                    TimeSpan robSpan = robotMark.createAndSetTimeSpan();
                    robSpan.setBegin(robTime1);
                    robSpan.setEnd(robTime2);
                }
                for(Fruit f :fruit){
                    Placemark fruitMark = doc.createAndAddPlacemark();
                    Icon fruitIcon = new Icon();

                    fruitIcon.setHref("apple.png");
                    fruitIcon.setViewBoundScale(1);
                    fruitIcon.setViewRefreshTime(1);
                    fruitIcon.withRefreshInterval(1);
                    IconStyle fruitIconStyle = new IconStyle();
                    fruitIconStyle.setScale(1);
                    fruitIconStyle.setHeading(1);
                    fruitIconStyle.setColor("ff007db3");
                    fruitIconStyle.setIcon(fruitIcon);
                    fruitMark.createAndAddStyle().setIconStyle(fruitIconStyle);
                    fruitMark.withDescription("Mac: " + "\nType: Fruit").withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(f.getPos().x(),f.getPos().y());
                    String fruitTime1 = MillisToString(StringToMillis(TimeNow())+change*1000);
                    String fruitTime2 = MillisToString(StringToMillis(TimeNow())+(change+1)*1000);
                    String[] fruitArr= fruitTime1.split(" ");
                    fruitTime1=splitArr(fruitArr);
                    String[] fruitArr2 = fruitTime2.split(" ");
                    fruitTime2=splitArr(fruitArr2);
                    TimeSpan fruitSpan = fruitMark.createAndSetTimeSpan();
                    fruitSpan.setBegin(fruitTime1);
                    fruitSpan.setEnd(fruitTime2);

                }
                try{
                    if(i==0) {
                 	   kmlDoc.marshal(new File("kmlFile.kml"));
                 	   i++;
                    }
                 }catch (Exception e){e.printStackTrace();}

            }

        
   
    }

    private String MillisToString(Long millis)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(millis));
    }

    private long StringToMillis(String TimeAsString) throws ParseException
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = format.parse(TimeAsString.toString());
        long millis = date.getTime();
        return millis;
    }

    private String TimeNow()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    private String splitArr(String[] arr){
        String temp= arr[0] + "T" + arr[1] + "Z";
        return temp;
    }

}


