package org.eclipse.debug.internal.ui;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.util.Iterator;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugViewAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class ControlActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate {
	
	/**
	 * This action's view part, or <code>null</code>
	 * if not installed in a view.
	 */
	private IViewPart fViewPart;
	
	/**
	 * Cache of the most recent seletion
	 */
	private IStructuredSelection fSelection;
	
	/**
	 * Whether this delegate has been initialized
	 */
	private boolean fInitialized = false;

	/**
	 * It's crucial that delegate actions have a zero-arg constructor so that
	 * they can be reflected into existence when referenced in an action set
	 * in the plugin's plugin.xml file.
	 */
	public ControlActionDelegate() {
	}
	
	/**
	 * Not all ControlActionDelegates have an owner, only those that aren't
	 * specified as part of an action set in plugin.xml.  For those delegates,
	 * that do have a ControlAction owner, this is the place to do any
	 * action specific initialization.
	 */
	public void initializeForOwner(ControlAction controlAction) {
		setActionImages(controlAction);
	}
	
	/**
	 * Do the specific action using the current selection.
	 */
	public void run() {
		IStructuredSelection selection= getSelection();
		
		final Iterator enum= selection.iterator();
		String pluginId= DebugUIPlugin.getDefault().getDescriptor().getUniqueIdentifier();
		final MultiStatus ms= 
			new MultiStatus(pluginId, DebugException.REQUEST_FAILED, getStatusMessage(), null); 
		BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
			public void run() {
				while (enum.hasNext()) {
					Object element= enum.next();
					try {
						doAction(element);
					} catch (DebugException e) {
						ms.merge(e.getStatus());
					}
				}
			}
		});
		if (!ms.isOK()) {
			DebugUIPlugin.errorDialog(DebugUIPlugin.getActiveWorkbenchWindow().getShell(), getErrorDialogTitle(), getErrorDialogMessage(), ms);
		}		
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose(){
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#init(IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window){
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action){
		run();
	}

	/**
	 * Only interested in selection changes in the launches view.
	 * Set the icons for this action on the first selection changed
	 * event.  This is necessary because the XML currently only
	 * supports setting the enabled icon.  
	 * 
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection s) {
		initialize(action);		
			
		if (getView() == null) {
			// global action - update with debug view selection
			IDebugViewAdapter view= getDebugView();
			if (view != null) {
				ISelection sel = view.getViewer().getSelection();
				update(action, sel);
			}
		} else {
			// view specific action - use the view's selection
			update(action, s);
		}
	}
	
	
	protected void update(IAction action, ISelection s) {
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection)s;
			action.setEnabled(getEnableStateForSelection(ss));
			setSelection(ss);
		} else {
			action.setEnabled(false);
			setSelection(null);
		}
	}
	
	/**
	 * Return whether the action should be enabled or not based on the given selection.
	 */
	public boolean getEnableStateForSelection(IStructuredSelection selection) {
		if (selection.size() == 0) {
			return false;
		}
		Iterator enum= selection.iterator();
		int count= 0;
		while (enum.hasNext()) {
			count++;
			if (count > 1 && !enableForMultiSelection()) {
				return false;
			}
			Object element= enum.next();
			if (!isEnabledFor(element)) {
				return false;
			}
		}
		return true;		
	}
	
	/**
	 * Returns whether this action should be enabled if there is
	 * multi selection.
	 */
	protected boolean enableForMultiSelection() {
		return true;
	}
	
	/**
	 * Returns the debug view, or <code>null</code> if none.
	 */
	protected IDebugViewAdapter getDebugView() {		
		IWorkbenchWindow window= DebugUIPlugin.getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IViewPart part = page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
				if (part != null) {
					return (IDebugViewAdapter)part.getAdapter(IDebugViewAdapter.class);
				}
			}
		}
		return null;
	}
		
	/**
	 * Does the specific action of this action to the process.
	 */
	protected abstract void doAction(Object element) throws DebugException;

	/**
	 * Returns whether this action will work for the given element
	 */
	public abstract boolean isEnabledFor(Object element);
	
	/**
	 * Returns this action's help context id
	 */
	protected abstract String getHelpContextId();
	
	/**
	 * Set the enabled, disabled & hover icons for this action delegate
	 */
	protected abstract void setActionImages(IAction action);
	
	/**
	 * Returns the String to use as an error dialog title for
	 * a failed action.
	 */
	protected abstract String getErrorDialogTitle();
	
	/**
	 * Returns the String to use as an error dialog message for
	 * a failed action.
	 */
	protected abstract String getErrorDialogMessage();
	
	/**
	 * Returns the String to use as a status message for
	 * a failed action.
	 */
	protected abstract String getStatusMessage();
	
	/**
	 * Returns the text for this action.
	 */
	protected abstract String getText();
	
	/**
	 * Returns the tool tip text for this action.
	 */
	protected abstract String getToolTipText();
	
	/**
	 * @see IViewActionDelegate#init(IViewPart)
	 */
	public void init(IViewPart view) {
		fViewPart = view;
	}
	
	/**
	 * Returns this action's view part, or <code>null</code>
	 * if not installed in a view.
	 * 
	 * @return view part or <code>null</code>
	 */
	protected IViewPart getView() {
		return fViewPart;
	}

	/**
	 * Initialize this delegate, updating this delegate's
	 * presentation.
	 * 
	 * @param action the presentation for this action
	 */
	protected void initialize(IAction action) {
		if (!fInitialized) {
			setActionImages(action);
			fInitialized = true;
		}
	}

	/**
	 * Returns the most recent selection
	 * 
	 * @return structured selection, or <code>null</code>
	 */	
	protected IStructuredSelection getSelection() {
		return fSelection;
	}
	
	/**
	 * Sets the most recent selection
	 * 
	 * @parm selection structured selection, or <code>null</code>
	 */	
	private void setSelection(IStructuredSelection selection) {
		fSelection = selection;
	}	
}