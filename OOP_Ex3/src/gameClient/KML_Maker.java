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

public class KML_Maker {

	private String MillisToString(Long millis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(millis));
	}


	private long StringToMillis(String TimeAsString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
		Date date = format.parse(TimeAsString.toString());
		long millis = date.getTime();
		return millis;
	}
	

	private String splitArr(String[] arr) {
		String temp= arr[0] + "|" + arr[1] + "|";
		return temp;
	}


	private String TimeNow() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	}

	
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
			ArrayList<Robot>  robots= mygame.robo_list;

			for (Robot robot: robots) {
				Placemark robotMark = doc.createAndAddPlacemark();
				Icon robIcon = new Icon();

				robIcon.setHref("robot.png");
				robIcon.setViewBoundScale(1);
				robIcon.setViewRefreshTime(1);
				robIcon.withRefreshInterval(1);
				IconStyle robIconeStyle = new IconStyle();
				robIconeStyle.setScale(1);
				robIconeStyle.setHeading(1);
				robIconeStyle.setColor("abcdefg");
				robIconeStyle.setIcon(robIcon);
				robotMark.createAndAddStyle().setIconStyle(robIconeStyle);
				robotMark.withDescription("Robot").withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(robot.getPos().x(),robot.getPos().y());
				String robtimeto = MillisToString(StringToMillis(TimeNow())+change*1000);
				String robtimeuntill = MillisToString(StringToMillis(TimeNow())+(change +1)*1000);
				String[] timearray =robtimeto.split(" ");
				robtimeto=splitArr(timearray);
				String[] timearray2 = robtimeuntill.split(" ");
				robtimeuntill=splitArr(timearray2);
				TimeSpan robSpan = robotMark.createAndSetTimeSpan();
				robSpan.setBegin(robtimeto);
				robSpan.setEnd(robtimeuntill);
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
				fruitIconStyle.setColor("abcedfg");
				fruitIconStyle.setIcon(fruitIcon);
				fruitMark.createAndAddStyle().setIconStyle(fruitIconStyle);
				fruitMark.withDescription("Fruit").withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(f.getPos().x(),f.getPos().y());
				String fruittime1 = MillisToString(StringToMillis(TimeNow())+change*1000);
				String fruitime2 = MillisToString(StringToMillis(TimeNow())+(change+1)*1000);
				String[] fruarr= fruittime1.split(" ");
				fruittime1=splitArr(fruarr);
				String[] fruarr2 = fruitime2.split(" ");
				fruitime2=splitArr(fruarr2);
				TimeSpan fruitSpan = fruitMark.createAndSetTimeSpan();
				fruitSpan.setBegin(fruittime1);
				fruitSpan.setEnd(fruitime2);

			}
			try{
				if(i==0) {
					kmlDoc.marshal(new File("kmlFile.kml"));
					i++;
				}
			}
			catch (Exception e){e.printStackTrace();}

		}

	}


}


