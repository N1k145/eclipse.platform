/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.resources;

import java.util.HashMap;
import org.eclipse.core.internal.events.BuildCommand;
import org.eclipse.core.internal.properties.PropertyStore;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectNature;

public class ProjectInfo extends ResourceInfo {

	/** The description of this object */
	protected ProjectDescription description = null;

	/** The list of natures for this project */
	protected HashMap natures = null;

	/** The property store for this resource */
	protected PropertyStore propertyStore = null;

	/**
	 * Discards any instantiated nature and builder instances associated with
	 * this project info.
	 */
	public synchronized void clearNaturesAndBuilders() {
		natures = null;
		if (description != null) {
			ICommand[] buildSpec = description.getBuildSpec(false);
			for (int i = 0; i < buildSpec.length; i++)
				((BuildCommand)buildSpec[i]).setBuilder(null);
		}
	}

	/**
	 * Returns the description associated with this info.  The return value may be null.
	 */
	public ProjectDescription getDescription() {
		return description;
	}

	public IProjectNature getNature(String natureId) {
		// thread safety: (Concurrency001)
		HashMap temp = natures;
		if (temp == null)
			return null;
		return (IProjectNature) temp.get(natureId);
	}

	/**
	 * Returns the property store associated with this info.  The return value may be null.
	 */
	public PropertyStore getPropertyStore() {
		return propertyStore;
	}

	/**
	 * Sets the description associated with this info.  The value may be null.
	 */
	public void setDescription(ProjectDescription value) {
		if (description != null) {
			//if we already have a description, assign the new
			//build spec on top of the old one to ensure we maintain
			//any existing builder instances in the old build commands
			ICommand[] oldSpec = description.buildSpec;
			ICommand[] newSpec = value.buildSpec;
			value.buildSpec = oldSpec;
			value.setBuildSpec(newSpec);
		}
		description = value;
	}

	public synchronized void setNature(String natureId, IProjectNature value) {
		// thread safety: (Concurrency001)
		if (value == null) {
			if (natures == null)
				return;
			HashMap temp = (HashMap) natures.clone();
			temp.remove(natureId);
			if (temp.isEmpty())
				natures = null;
			else
				natures = temp;
		} else {
			HashMap temp = natures;
			if (temp == null)
				temp = new HashMap(5);
			else
				temp = (HashMap) natures.clone();
			temp.put(natureId, value);
			natures = temp;
		}
	}

	/**
	 * Sets the property store associated with this info.  The value may be null.
	 */
	public void setPropertyStore(PropertyStore value) {
		propertyStore = value;
	}
}