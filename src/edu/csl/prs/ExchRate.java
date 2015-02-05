package edu.csl.prs;

/**
 * This stores the Exchange Rate
 */
public class ExchRate {

	private String value = null;

	public String getValue() {
		// remove unwanted unicode characters
		return value.replaceAll("\\P{Print}", "");
	}

	public void setValue(String record) {
		this.value = record;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExchRate))
			return false;
		ExchRate other = (ExchRate) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExchRate [value=" + value + "]";
	}

}
