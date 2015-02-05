package edu.csl.enumeration;

public enum ExchangeRateCurrencyEnum {

	INR("Indian rupee   (INR)"), USD("U.S. dollar   (USD)"), AUD(
			"Australian dollar   (AUD)"), CAD("Canadian dollar   (CAD)"), DKK(
			"Danish krone   (DKK)"), EUR("euro   (EUR)");

	private String value;

	private ExchangeRateCurrencyEnum() {
		this.value = "";
	}

	private ExchangeRateCurrencyEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

}
