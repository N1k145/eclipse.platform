/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.ui.internal;

import org.eclipse.swt.widgets.*;

/**
 *  
 */
public interface IHyperlinkListener {
	/**
	 * @param linkLabel
	 *            org.eclipse.swt.widgets.Label
	 */
	public void linkActivated(Control linkLabel);
	/**
	 * @param linkLabel
	 *            org.eclipse.swt.widgets.Label
	 */
	public void linkEntered(Control linkLabel);
	/**
	 * @param linkLabel
	 *            org.eclipse.swt.widgets.Label
	 */
	public void linkExited(Control linkLabel);
}