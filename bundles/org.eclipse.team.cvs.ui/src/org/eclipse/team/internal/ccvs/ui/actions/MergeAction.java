/*******************************************************************************
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 * IBM - Initial implementation
 ******************************************************************************/
package org.eclipse.team.internal.ccvs.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.internal.ccvs.ui.merge.MergeWizard;

public class MergeAction extends WorkspaceAction {

	/*
	 * @see CVSAction#execute()
	 */
	public void execute(IAction action) {
		final Shell shell = getShell();
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				MergeWizard wizard = new MergeWizard();
				wizard.setResources(getSelectedResources());
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.open();
			}
		});
	}

}
