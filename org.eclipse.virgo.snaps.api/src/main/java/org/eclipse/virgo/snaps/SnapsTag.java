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

package org.eclipse.virgo.snaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import org.eclipse.gemini.web.core.WebContainer;

/**
 * TODO Document SnapsTag
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * TODO Document concurrent semantics of SnapsTag
 * 
 */
public final class SnapsTag extends BodyTagSupport {

    private static final long serialVersionUID = 4715961747733658692L;

    private static final String SNAP_SERVICE_CLASS = "org.eclipse.virgo.snaps.core.internal.Snap";

    public static final String SNAPS_ATTRIBUTE_NAME = "snaps";

    private volatile String attributeName = SNAPS_ATTRIBUTE_NAME;

    public void setVar(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getVar() {
        return this.attributeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        BundleContext bundleContext = (BundleContext) this.pageContext.getServletContext().getAttribute(WebContainer.ATTRIBUTE_BUNDLE_CONTEXT);
        long hostId = bundleContext.getBundle().getBundleId();
        try {
            ServiceReference[] serviceReferences = bundleContext.getServiceReferences(SNAP_SERVICE_CLASS, "(snap.host.id=" + hostId + ")");

            List<Snap> snaps = new ArrayList<Snap>();

            if (serviceReferences != null) {
                Arrays.sort(serviceReferences);
                for (ServiceReference serviceReference : serviceReferences) {
                    snaps.add(createSnap(serviceReference));
                }
            }
            this.pageContext.setAttribute(this.attributeName, snaps);
        } catch (InvalidSyntaxException ise) {
            throw new JspException("Unexpected InvalidSyntaxException when querying service registry for Snaps", ise);
        }

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        this.pageContext.removeAttribute(this.attributeName);
        return EVAL_PAGE;
    }

    private static Snap createSnap(ServiceReference serviceReference) {
        String[] propertyKeys = serviceReference.getPropertyKeys();
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (String key : propertyKeys) {
            attributes.put(key, serviceReference.getProperty(key));
        }
        return new Snap(attributes);
    }
}
