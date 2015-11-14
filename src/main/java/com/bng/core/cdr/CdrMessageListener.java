package com.bng.core.cdr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.Utility;

public class CdrMessageListener implements MessageListener {

	// should include "separator" at the end.
	private String pathPrefix;

	/**
	 * Implementation of <code>MessageListener</code>.
	 */
	public void onMessage(Message message) {
		String textMessage = null;
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setMinimumIntegerDigits(2);
		CdrSendObject cdrSendObject = null;
		Calendar date;
		try {
			Logger.sysLog(LogValues.info, this.getClass().getName(), "cdr message received");
			if (message instanceof TextMessage)
				textMessage = ((TextMessage) message).getText();
			
			if(textMessage==null) 
				Logger.sysLog(LogValues.info, this.getClass().getName(), "textmessage null");
			cdrSendObject = Utility.convertJsonStrToObject(textMessage,CdrSendObject.class);
			date = new GregorianCalendar();
			Logger.sysLog(LogValues.info, this.getClass().getName(), "enddate = "+cdrSendObject.getDate());
			date.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
					.parse(cdrSendObject.getDate()));
			String pathSuffix = File.separator 
					+ date.get(Calendar.YEAR) 
					+ File.separator
					+ format.format((date.get(Calendar.MONTH) + 1))
					+ File.separator
					+ format.format(date.get(Calendar.DAY_OF_MONTH))
					+ File.separator 
					+ format.format(date.get(Calendar.HOUR_OF_DAY))
					+ File.separator;
			String path = pathPrefix + pathSuffix + cdrSendObject.getFilename();//System.currentTimeMillis()+".xml";
			Logger.sysLog(LogValues.info, this.getClass().getName(), "saving file on "+ path);
			save(new File(path),cdrSendObject.getXml());
			Logger.sysLog(LogValues.info, this.getClass().getName(), "saved file");
		} catch (Exception e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
			String pathSuffix = File.separator + "error_xmls"+ File.separator;
			String path = pathPrefix + pathSuffix + System.currentTimeMillis()+".xml";//cdrSendObject.getFilename();
			Logger.sysLog(LogValues.error, this.getClass().getName(), "saving file on "+ path);
			save(new File(path),cdrSendObject.getXml());
			Logger.sysLog(LogValues.error, this.getClass().getName(), "saved file");
		}
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public void save(File file, String textToSave) {

		file.delete();
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(textToSave);
			out.close();
		} catch (IOException e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(),"exception while creating cdr");
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
		}
	}

}
