package us.rjuhsd.ohs.OHSApp.managers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import us.rjuhsd.ohs.OHSApp.R;
import us.rjuhsd.ohs.OHSApp.SchoolClass;
import us.rjuhsd.ohs.OHSApp.Tools;
import us.rjuhsd.ohs.OHSApp.activities.Preferences;
import us.rjuhsd.ohs.OHSApp.tasks.ClassesOverviewTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AeriesManager {

	public static String LOGIN_URL = "https://homelink.rjuhsd.us/LoginParent.aspx";
	public static String GRADES_DETAIL = "https://homelink.rjuhsd.us/GradebookDetails.aspx";
	//public static String DEFAULT_URL = "http://homelink.rjuhsd.us/Default.aspx";

	private static String CLASSES_FILENAME = "classes.json";
	private static int CURRENT_FILE_VERSION = 1;

	public HttpClient client = Tools.sslClient();

	private Activity activity;

	private ClassesOverviewTask classesTask;

	private ArrayList<SchoolClass> grades;

	public void getGradesOverview(Activity activity, boolean forceUpdate) {
		if(grades != null && !forceUpdate) {
			classesTask.inflateList(activity);
		} else {
			getGradesOverview(activity);
		}
	}

	private void writeAllData(Context context) {
		JSONObject json = new JSONObject();
		JSONArray gradesJson = new JSONArray();
		try {
			for(SchoolClass sc: grades) {
				gradesJson.put(sc.toJSON());
			}
			json.put("version",CURRENT_FILE_VERSION);
			json.put("classes", gradesJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			FileOutputStream fos = context.openFileOutput(CLASSES_FILENAME, Context.MODE_PRIVATE);
			fos.write(json.toString().getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readAllData(Context context) {
		try {
			FileInputStream fis = context.openFileInput(CLASSES_FILENAME);
			String input = String.valueOf(fis.read());
			fis.close();
			JSONObject json = new JSONObject(input);
			parseData(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseData(JSONObject json) {
		int jsonVersion;
		try {
			jsonVersion = json.getInt("version");
			switch(jsonVersion) {
				case 1:
					JSONArray gradesJson = json.getJSONArray("classes");
					grades = gradesJson
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private void getGradesOverview(final Activity activity) {
		if(!Tools.isConnected(activity)) {
			AlertDialog.Builder adb = new AlertDialog.Builder(activity);
			adb.setTitle("No internet!");
			adb.setMessage("Your phone is not connected to the internet. Please try again when you are connected");
			adb.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					activity.finish();
				}
			});
			adb.show();

		} else {
			startLoadingGrades(activity);
		}
	}

	private void startLoadingGrades(final Activity activity) {
		this.activity = activity;
		classesTask = new ClassesOverviewTask(activity, this);
		classesTask.execute();
	}

	public static String[] aeriesLoginData(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String[] toReturn = new String[2];
		toReturn[0] = prefs.getString("aeries_username", "");
		toReturn[1] = prefs.getString("aeries_password", "");
		return toReturn;
	}

	public SchoolClass getById(int id) {
		return grades.get(id);
	}

	public void setSchoolClasses(ArrayList<SchoolClass> grades) {
		this.grades = grades;
	}

	public void errorLoadingGrades(String errorText) {
		AlertDialog.Builder adb = new AlertDialog.Builder(activity)
				.setTitle("Login Failure!")
				.setMessage(errorText)
				.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}
				})
				.setPositiveButton(R.string.goto_settings, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.startActivity(new Intent(activity, Preferences.class));
					}
				});
		adb.show();
	}

	public void destroyAll(Context context) {
		grades = null;
	}
}