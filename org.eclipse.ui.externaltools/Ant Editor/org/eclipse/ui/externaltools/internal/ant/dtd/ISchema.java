/*******************************************************************************
 * Copyright (c) 2002, 2003 Object Factory Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *		Object Factory Inc. - Initial implementation
 *******************************************************************************/
package org.eclipse.ui.externaltools.internal.ant.dtd;

public interface ISchema {

	/**
	 * Find element by name.
	 * @param qname Element name.
	 * @return element or null if no such element.
	 */
	IElement getElement(String qname);
	
	/**
	 * @return IElement[] of all visible elements.
	 */
	IElement[] getElements();
	
	/**
	 * @return Exception thrown by parser when schema was built or <code>null</code> if none.
	 * Note that the exception does not necessarily mean the schema is
	 * incomplete.
	 */
	Exception getErrorException();
}
