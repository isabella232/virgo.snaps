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

package org.eclipse.virgo.snaps.core.internal.deployer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springsource.kernel.deployer.core.DeploymentException;
import com.springsource.kernel.install.artifact.BundleInstallArtifact;
import com.springsource.kernel.install.artifact.InstallArtifact;
import com.springsource.kernel.install.environment.InstallEnvironment;
import com.springsource.kernel.install.pipeline.stage.transform.Transformer;
import com.springsource.osgi.webcontainer.core.WebBundleManifestTransformer;
import com.springsource.util.common.Tree;
import com.springsource.util.common.Tree.ExceptionThrowingTreeVisitor;
import com.springsource.util.osgi.manifest.BundleManifest;

/**
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Thread-safe.
 * 
 */
final class SnapTransformer implements Transformer {

    private static final String SLICE_MODULE_TYPE = "web-snap";

    private static final Logger logger = LoggerFactory.getLogger(SnapTransformer.class);

    private final WebBundleManifestTransformer manifestTransformer;

    public SnapTransformer(WebBundleManifestTransformer manifestTransformer) {
        this.manifestTransformer = manifestTransformer;
    }

    /**
     * {@inheritDoc}
     */
    public void transform(Tree<InstallArtifact> installTree, InstallEnvironment installEnvironment) throws DeploymentException {
        installTree.visit(new ExceptionThrowingTreeVisitor<InstallArtifact, DeploymentException>() {

            public boolean visit(Tree<InstallArtifact> node) throws DeploymentException {
                InstallArtifact installArtifact = node.getValue();
                if (SnapLifecycleListener.isSnap(installArtifact)) {
                    BundleManifest bundleManifest = SnapLifecycleListener.getBundleManifest((BundleInstallArtifact) installArtifact);
                    doTransform(bundleManifest, getSourceUrl(installArtifact));
                }
                return true;
            }
        });
    }

    void doTransform(BundleManifest bundleManifest, URL sourceUrl) throws DeploymentException {
        logger.info("Transforming bundle at '{}'", sourceUrl.toExternalForm());
        bundleManifest.setModuleType(SLICE_MODULE_TYPE);
        try {
            this.manifestTransformer.transform(bundleManifest, sourceUrl, null);
        } catch (IOException ioe) {
            logger.error(String.format("Error transforming manifest for snap '%s' version '%s'",
                bundleManifest.getBundleSymbolicName().getSymbolicName(), bundleManifest.getBundleVersion()), ioe);
            throw new DeploymentException("Error transforming manifest for snap '" + bundleManifest.getBundleSymbolicName().getSymbolicName()
                + "' version '" + bundleManifest.getBundleVersion() + "'", ioe);
        }
    }

    private static URL getSourceUrl(InstallArtifact installArtifact) throws DeploymentException {
        File file = installArtifact.getArtifactFS().getFile();
        if (file != null) {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException murle) {
                logger.error(String.format("Install artifact '%s' has source URI that is not a valid URL", installArtifact), murle);
                throw new DeploymentException("Install artifact '" + installArtifact + "' has source URI that is not a valid URL", murle);
            }
        } else {
            logger.error("Install artifact '{}' has a null source URI", installArtifact);
            throw new DeploymentException("Install artifact '" + installArtifact + "' has a null source URI");
        }
    }
}
