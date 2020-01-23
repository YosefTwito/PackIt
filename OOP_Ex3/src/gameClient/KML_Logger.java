package gameClient;

import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.Icon;
import elements.Fruit;
import elements.Robot;

import java.io.File;
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


