/*******************************************************************************
 * Copyright (c) 2008, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   VMware Inc. - initial contribution
 *******************************************************************************/

package org.eclipse.virgo.snaps.core.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HostSelector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final SnapHostDefinition hostDefinition;
    
    private final String moduleScope;

    HostSelector(SnapHostDefinition hostDefinition, String moduleScope) {
        this.hostDefinition = hostDefinition;
        this.moduleScope = moduleScope;
    }

    ServiceReference selectHost(ServiceReference[] candidates) {
        ServiceReference bestSoFar = null;
        if (candidates != null) {

            for (ServiceReference candidateServiceReference : candidates) {
                if (isPossibleHost(candidateServiceReference) && isHigherPriority(bestSoFar, candidateServiceReference)) {
                    bestSoFar = candidateServiceReference;
                    logger.info("Found best-so-far Host candidate from bundle '{}'", candidateServiceReference.getBundle());
                }
            }
        }

        if (bestSoFar != null) {
            logger.info("Host service '{}' found", bestSoFar);
        }
        return bestSoFar;
    }

    private boolean isHigherPriority(ServiceReference bestSoFar, ServiceReference candidate) {
        return (bestSoFar == null || candidate.getBundle().getVersion().compareTo(bestSoFar.getBundle().getVersion()) > 0);
    }

    private boolean isPossibleHost(ServiceReference servletContextReference) {
        Bundle bundle = servletContextReference.getBundle();
        return bundleMatchesHostName(bundle) && bundleMatchesHostRange(bundle) && bundleIsActive(bundle);
    }

    private boolean bundleIsActive(Bundle bundle) {
        return bundle.getState() == Bundle.ACTIVE;
    }

    private boolean bundleMatchesHostRange(Bundle bundle) {
        return this.hostDefinition.getVersionRange().includes(bundle.getVersion());
    }

    private boolean bundleMatchesHostName(Bundle bundle) {
        if (this.moduleScope != null) {
            String scopedHostSymbolicName = this.moduleScope + "-" + this.hostDefinition.getSymbolicName();
            if (scopedHostSymbolicName.equals(bundle.getSymbolicName())) {
                return true;
            }
        }
        return this.hostDefinition.getSymbolicName().equals(bundle.getSymbolicName());
    }
}
