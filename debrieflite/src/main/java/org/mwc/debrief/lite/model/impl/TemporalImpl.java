/*******************************************************************************
 * Copyright (c) 2014, PlanetMayo Ltd. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package org.mwc.debrief.lite.model.impl;

import java.util.Date;

import org.mwc.debrief.lite.model.Temporal;

/**
 * @author snpe
 *
 */
public class TemporalImpl implements Temporal, Comparable<Temporal> {
	
	private long micros;
	
	public TemporalImpl(final long millis, final long micros)
	{
		this.micros = millis * 1000 + micros;
	}

	public TemporalImpl(final long millis)
	{
		this(millis, 0);
	}

	public TemporalImpl(final Date val)
	{
		this(val.getTime());
	}

	public TemporalImpl(final Temporal other)
	{
		this.micros = other.getMicros();
	}

	public TemporalImpl()
	{
		this(new Date().getTime());
	}
	

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.Temporal#getTime()
	 */
	@Override
	public long getTime() {
		return micros/1000;
	}

	/**
	 * @return the micros
	 */
	public long getMicros() {
		return micros;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.Temporal#getDate()
	 */
	@Override
	public Date getDate() {
		return new Date(micros / 1000);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Temporal o) {
		if (micros == o.getMicros()) {
			return 0;
		} else if (micros > o.getMicros()) {
			return 1;
		} else {
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (micros ^ (micros >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemporalImpl other = (TemporalImpl) obj;
		if (micros != other.micros)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TemporalImpl [micros=" + micros + ", getDate()=" + getDate()
				+ "]";
	}

}
