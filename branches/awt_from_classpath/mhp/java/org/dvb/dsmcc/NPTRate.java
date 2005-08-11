package org.dvb.dsmcc;

/**
* Represents the rate at which an NPT time-base progresses.
* Rates are expressed as the combination of
* a numerator and a denominator. Instances of this class are
* constructed by the platform and returned to
* applications.
*
*
*
* @author tejopa
* @date 7.3.2004
* @status fully implemented
* @module internal
* TODO attach to adaptation layer (NPTRateChangeEvent)
* @HOME
*/
public class NPTRate {

	private int numerator = 0;
	private int denominator = 0;

	protected NPTRate(int n, int d){
		numerator = n;
		denominator = d;
	}

	public int getNumerator(){
		return numerator;
	}

	public int getDenominator() {
		return denominator;
	}

}
