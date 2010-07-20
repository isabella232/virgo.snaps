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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.eclipse.virgo.snaps.core.internal.deployer.SnapTransformer;
import org.junit.Test;

import org.eclipse.virgo.kernel.artifact.fs.ArtifactFS;
import org.eclipse.virgo.kernel.deployer.core.DeploymentException;
import org.eclipse.virgo.kernel.install.artifact.BundleInstallArtifact;
import org.eclipse.virgo.kernel.install.artifact.InstallArtifact;
import org.eclipse.gemini.web.core.InstallationOptions;
import org.eclipse.gemini.web.core.WebBundleManifestTransformer;
import org.eclipse.virgo.util.common.ThreadSafeArrayListTree;
import org.eclipse.virgo.util.common.Tree;
import org.eclipse.virgo.util.osgi.manifest.BundleManifest;
import org.eclipse.virgo.util.osgi.manifest.internal.StandardBundleManifest;

public class SnapTransformerTests {
    
    private final WebBundleManifestTransformer manifestTransformer = createMock(WebBundleManifestTransformer.class);
    
    private final BundleManifest bundleManifest = new StandardBundleManifest(null);
    
    private SnapTransformer snapTransformer = new SnapTransformer(manifestTransformer);
    
    @Test(expected = DeploymentException.class)
    public void testInvalidTransformation() throws Exception {

        this.manifestTransformer.transform(eq(this.bundleManifest), isA(URL.class), (InstallationOptions) isNull(), eq(false));
        expectLastCall().andThrow(new IOException());
        replayAll();
        snapTransformer.doTransform(bundleManifest, URI.create("file:bar").toURL());
        verifyAll();
    }

    @Test
    public void testValidTransformation() throws Exception {
        this.manifestTransformer.transform(eq(this.bundleManifest), isA(URL.class), (InstallationOptions) isNull(), eq(false));        
        replayAll();
        snapTransformer.doTransform(bundleManifest, URI.create("file:bar").toURL());
        verifyAll();
    }
    
    @Test
    public void treeTransformation() throws Exception {
        BundleManifest bundleManifest1 = new StandardBundleManifest(null);    
        bundleManifest1.setHeader("Snap-Host", "myHost");
        BundleInstallArtifact installArtifact1 = createMock(BundleInstallArtifact.class);
        expect(installArtifact1.getBundleManifest()).andReturn(bundleManifest1).anyTimes();
        ArtifactFS artifactFS1 = createMock(ArtifactFS.class);
        expect(installArtifact1.getArtifactFS()).andReturn(artifactFS1).anyTimes();
        File f1 = new File("/bar1");
        expect(artifactFS1.getFile()).andReturn(f1);
        
        BundleInstallArtifact installArtifact2 = createMock(BundleInstallArtifact.class);
        BundleManifest bundleManifest2 = new StandardBundleManifest(null);
        bundleManifest2.setHeader("Snap-Host", "myHost");
        expect(installArtifact2.getBundleManifest()).andReturn(bundleManifest2).anyTimes();
        ArtifactFS artifactFS2 = createMock(ArtifactFS.class);
        expect(installArtifact2.getArtifactFS()).andReturn(artifactFS2).anyTimes();
        File f2 = new File("/bar2");
        expect(artifactFS2.getFile()).andReturn(f2);
        
        this.manifestTransformer.transform(bundleManifest1, f1.toURI().toURL(), null, false);
        this.manifestTransformer.transform(bundleManifest2, f2.toURI().toURL(), null, false);
        
        replay(installArtifact1, artifactFS1, installArtifact2, artifactFS2, manifestTransformer);
        
        Tree<InstallArtifact> installTree = new ThreadSafeArrayListTree<InstallArtifact>(installArtifact1);
        installTree.addChild(new ThreadSafeArrayListTree<InstallArtifact>(installArtifact2));
        
        snapTransformer.transform(installTree, null);
        
        verify(installArtifact1, artifactFS1, installArtifact2, artifactFS2, manifestTransformer);
    }
    
    @Test(expected=DeploymentException.class)
    public void invalidTreeTransformation() throws Exception {
        BundleManifest bundleManifest1 = new StandardBundleManifest(null);    
        bundleManifest1.setHeader("Snap-Host", "myHost");
        BundleInstallArtifact installArtifact1 = createMock(BundleInstallArtifact.class);
        expect(installArtifact1.getBundleManifest()).andReturn(bundleManifest1).anyTimes();
        ArtifactFS artifactFS1 = createMock(ArtifactFS.class);
        expect(installArtifact1.getArtifactFS()).andReturn(artifactFS1).anyTimes();
        File f1 = new File("/bar1");
        expect(artifactFS1.getFile()).andReturn(f1);
        
        BundleInstallArtifact installArtifact2 = createMock(BundleInstallArtifact.class);
        BundleManifest bundleManifest2 = new StandardBundleManifest(null);
        bundleManifest2.setHeader("Snap-Host", "myHost");
        expect(installArtifact2.getBundleManifest()).andReturn(bundleManifest2).anyTimes();
        ArtifactFS artifactFS2 = createMock(ArtifactFS.class);
        expect(installArtifact2.getArtifactFS()).andReturn(artifactFS2).anyTimes();        
        expect(artifactFS2.getFile()).andReturn(null);
        
        this.manifestTransformer.transform(bundleManifest1, f1.toURI().toURL(), null, false);
        
        replay(installArtifact1, artifactFS1, installArtifact2, artifactFS2, manifestTransformer);
        
        Tree<InstallArtifact> installTree = new ThreadSafeArrayListTree<InstallArtifact>(installArtifact1);
        installTree.addChild(new ThreadSafeArrayListTree<InstallArtifact>(installArtifact2));
        
        try {
            snapTransformer.transform(installTree, null);
            fail();
        } catch (Exception e) {
            verify(installArtifact1, artifactFS1, installArtifact2, artifactFS2, manifestTransformer);
            throw e;
        }                
    }
    

    private void verifyAll() {
        verify(this.manifestTransformer);
    }

    private void replayAll() {
        replay(this.manifestTransformer);
    }
}
