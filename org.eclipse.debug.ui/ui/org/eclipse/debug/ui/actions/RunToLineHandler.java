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
package org.eclipse.debug.ui.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.actions.ActionMessages;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;

/**
 * Handles a run to line operation. Clients implementing a run to line action
 * can use this handler to carry out a run to line operation implemented with 
 * a breakpoint. Handles the user preference to skip breakpoints while performing
 * a run to line operation, and cancelling the run to line operation if another
 * breakpoint is encountered before the operation is completed. 
 * <p>
 * Clients may instantiate this class. This class is not intended to be subclassed.
 * </p>
 * @since 3.1
 */
public class RunToLineHandler implements IDebugEventSetListener, IBreakpointManagerListener, IWorkspaceRunnable {
    
    private IDebugTarget fTarget;
    private ISuspendResume fResumee;
    private IBreakpoint fBreakpoint;
    private boolean fAutoSkip = false;
    
    /**
     * Constructs a handler to perform a run to line operation.
     * 
     * @param target the debug target in which the operation is to be performed
     * @param suspendResume the element to be resumed to begin the operation
     * @param breakpoint the run to line breakpoint
     */
    public RunToLineHandler(IDebugTarget target, ISuspendResume suspendResume, IBreakpoint breakpoint) {
        fResumee = suspendResume;
        fTarget = target;
        fBreakpoint = breakpoint;
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
     */
    public void handleDebugEvents(DebugEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            DebugEvent event= events[i];
            Object source= event.getSource();
            if (source instanceof IThread && event.getKind() == DebugEvent.SUSPEND &&
                    event.getDetail() == DebugEvent.BREAKPOINT) {
                IThread thread = (IThread) source;
                IDebugTarget suspendee = (IDebugTarget) thread.getAdapter(IDebugTarget.class);
                if (fTarget.equals(suspendee)) {
                    // cleanup if the breakpoint was hit or not
                    cancel();
                }
            } else if (source instanceof IDebugTarget && event.getKind() == DebugEvent.TERMINATE) {
                if (source.equals(fTarget)) {
                    // Clean up if the debug target terminates without
                    // hitting the breakpoint.
                    cancel();
                }
            }
        }
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointManagerListener#breakpointManagerEnablementChanged(boolean)
     */
    public void breakpointManagerEnablementChanged(boolean enabled) {
        // if the user changes the breakpoint manager enablement, don't restore it
        fAutoSkip = false;
    }
    
    private IBreakpointManager getBreakpointManager() {
        return getDebugPlugin().getBreakpointManager();
    }
    
    private DebugPlugin getDebugPlugin() {
        return DebugPlugin.getDefault();
    }
    
    /**
     * Cancels the run to line operation.
     */
    public void cancel() {
        getDebugPlugin().removeDebugEventListener(this);
        IBreakpointManager manager = getBreakpointManager();
        manager.removeBreakpointManagerListener(this);
        fTarget.breakpointRemoved(fBreakpoint, null);
        if (fAutoSkip) {
            manager.setEnabled(true);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws CoreException {
        getDebugPlugin().addDebugEventListener(this);
        IBreakpointManager breakpointManager = getBreakpointManager();
        fAutoSkip = DebugUITools.getPreferenceStore().getBoolean(IDebugUIConstants.PREF_SKIP_BREAKPOINTS_DURING_RUN_TO_LINE) && breakpointManager.isEnabled();
        if (fAutoSkip) {
            getBreakpointManager().setEnabled(false);
            breakpointManager.addBreakpointManagerListener(this);
        }
        Job job = new Job(ActionMessages.getString("RunToLineHandler.0")) { //$NON-NLS-1$
            protected IStatus run(IProgressMonitor jobMonitor) {
                if (!jobMonitor.isCanceled()) {
                    fTarget.breakpointAdded(fBreakpoint);
                    try {
                        fResumee.resume();
                    } catch (DebugException e) {
                        cancel();
                        DebugUIPlugin.log(e);
                        return Status.CANCEL_STATUS;
                    }
                }
                return Status.OK_STATUS;
            }  
        };
        job.schedule();
    }
    
}
