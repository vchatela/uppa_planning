package net.valentinc.uppa.hyperplanning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Periode implements Serializable {
	private String code;
	private String label;
	private List<String> legendes;
	private Calendar startDate;
	private Calendar endDate;
	private int httpLength;
	private final static String baseUrl = "http://sciences.univ-pau.fr/edt/diplomes/";

	public Periode(String label, String code) {
		this.code = code;
		this.label = label;
		this.legendes = new ArrayList<String>();
		this.startDate = Calendar.getInstance();
		this.endDate = Calendar.getInstance();
		this.httpLength = 0;
		
		this.computeDates();
	}
	public Periode() {
		this.code = "";
		this.label = "";
		this.legendes = new ArrayList<String>();
		this.startDate = Calendar.getInstance();
		this.endDate = Calendar.getInstance();
		this.httpLength = 0;

	}
	public boolean isCurrentPeriode() {
		Calendar rightNow = Calendar.getInstance();
		if(this.endDate.after(rightNow)) return true;
		return false;
	}

	public  void addLegende(String legende) {
		this.legendes.add(legende);
	}
	public void setStrDate(String date) {
		this.label = date;
		this.computeDates();
	}
	private void computeDates() {
		ArrayList<String> months = new ArrayList<String>();
		months.add("janvier");
		months.add("février");
		months.add("mars");
		months.add("avril");
		months.add("mai");
		months.add("juin");
		months.add("juillet");
		months.add("aout");
		months.add("septembre");
		months.add("octobre");
		months.add("novembre");
		months.add("décembre");

		LinkedList<String> liste = new LinkedList<String>();
		StringTokenizer st = new StringTokenizer(this.label, " ");
		while (st.hasMoreTokens()) {
			liste.add(st.nextToken());
		}

		int year = Integer.parseInt(liste.get(liste.size() - 1));
		int monthEnd = 0;
		if (months.contains(liste.get(liste.size() - 2)))
			monthEnd = months.indexOf(liste.get(liste.size() - 2));

		int dayEnd = Integer.parseInt(liste.get(liste.size() - 3));

		int monthStart = 0;
		int dayStart = 1;
		if (liste.size() == 7) {
			monthStart = months.indexOf(liste.get(liste.size() - 5));
			dayStart = Integer.parseInt(liste.get(liste.size() - 6));
		}
		else if (liste.size() == 6) {
			monthStart = monthEnd;
			dayStart = Integer.parseInt(liste.get(liste.size() - 5));
		}
		
		this.startDate.set(year, monthStart, dayStart);
		this.endDate.set(year, monthEnd, dayEnd);
		

	}

	public String getImageCode() {
		return code;
	}

	public void setImageCode(String imagecode) {
		this.code = imagecode;
	}
	@Override
	public String toString() {
		return label;
	}
	public String toHtml(){
		String Html = "<html><head></head><body width=850><div><img src=\""+baseUrl+code+".png\"/>";
		for (String aLeg : legendes) {
			Html += "<p style=\"font-size: small\">" + aLeg + "</p>";
		}
		Html += "</div></body></html>";
		return Html;
	}
}
