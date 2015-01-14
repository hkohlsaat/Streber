package org.aweture.wonk.models;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;


public class Subjects {
	private final String LOG_TAG = this.getClass().getSimpleName();
	
	private final String ATTRIBUTE_SHORT= "short";
	private final String ATTRIBUTE_NAME = "name";
	
    Map<String, Subject> subjects;
	
	public Subjects(Context context) {
		subjects = new HashMap<String, Subject>();
        
		populateSubjectsMap(context);
	}
	
	public Subject getSubject(String abbreviation) {
		Subject subject = subjects.get(abbreviation);
		if (subject == null) {
			subject = new Subject();
			subject.setName(abbreviation);
			subject.setAbbreviation(abbreviation);
		}
		return subject;
	}
	
	
	private void populateSubjectsMap(Context context) {
		try (InputStream inputStream = context.getAssets().open("subjects.xml")) {
			
	        XmlPullParser parser = Xml.newPullParser();
	        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	        parser.setInput(inputStream, null);
	        parser.nextTag();
	        
	        while (parser.nextTag() == XmlPullParser.START_TAG){
	        	String abbreviation = parser.getAttributeValue(null, ATTRIBUTE_SHORT);
	    		String name = parser.getAttributeValue(null, ATTRIBUTE_NAME);
	    		
	    		Subject subject = new Subject();
	    		subject.setAbbreviation(abbreviation);
	    		subject.setName(name);
	    		
	    		subjects.put(abbreviation, subject);
	        	parser.nextText();
	        }
		} catch (IOException | XmlPullParserException e) {
			Log.e(LOG_TAG, Log.getStackTraceString(e));
		}
	}
	
	

	public class Subject {
		private String name = "";
		private String abbreviation = "";
		
		public String getName() {
			return name;
		}
		public String getAbbreviation() {
			return abbreviation;
		}
		private void setName(String name) {
			this.name = name;
		}
		private void setAbbreviation(String abbreviation) {
			this.abbreviation = abbreviation;
		}
		
	}

}
