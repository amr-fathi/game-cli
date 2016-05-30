/**
 * 
 */
package org.test.project.model;

import org.test.project.api.DisplacementUnits;

/**
 * @author abdelgam
 *
 */
public class Steps implements DisplacementUnits {

	private Integer stepCount;
	/**
	 * @return the stepCount
	 */
	public Integer getStepCount() {
		return stepCount;
	}
	/**
	 * @param stepCount
	 *            the stepCount to set
	 */
	public void setStepCount(Integer stepCount) {
		this.stepCount = stepCount;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.test.project.api.DisplacementUnits#getUnits()
	 */
	@Override
	public Integer getUnits() {
		return this.stepCount;
	}
	/**
	 * @param stepCount
	 */
	public Steps(Integer stepCount) {
		super();
		this.stepCount = stepCount;
	}

}
