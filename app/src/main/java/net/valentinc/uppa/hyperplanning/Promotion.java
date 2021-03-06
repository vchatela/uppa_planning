package net.valentinc.uppa.hyperplanning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by valentinc on 16/09/2015.
 * Manage Promotion instance
 */

public class Promotion implements Serializable{
	private List<Periode> periodes;
	private String name;
	private String code;

	public Promotion() {
		this.periodes = new ArrayList<>();
	}

	public Promotion(String name, String code) {
		this.periodes = new ArrayList<>();
		this.name = name;
		this.code = code;
	}

	public List<Periode> getPeriodes() {
		return periodes;
	}

	public void setPeriodes(List<Periode> periodes) {
		if(periodes != null)
		this.periodes = periodes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Periode getCurrentPeriode() {
		for (Periode aPeriode : periodes) {
			if (aPeriode.isCurrentPeriode())
				return aPeriode;
		}
		if (periodes.size() > 0)
			return periodes.get(0);
		else
			return null;
	}

	@Override
	public String toString() {
		
		return this.name;
	}
	@Override
	public boolean equals(Object o) {
		if( o instanceof Promotion){
			return ((Promotion) o).getCode().equalsIgnoreCase(this.code);
		}
		return super.equals(o);
	}

}
