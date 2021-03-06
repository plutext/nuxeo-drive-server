/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Antoine Taillefer <ataillefer@nuxeo.com>
 */
package org.nuxeo.drive.service.impl;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.drive.adapter.FileSystemItem;
import org.nuxeo.drive.adapter.FolderItem;
import org.nuxeo.drive.adapter.impl.CollectionSyncRootFolderItem;
import org.nuxeo.drive.service.FileSystemItemFactory;
import org.nuxeo.drive.service.NuxeoDriveManager;
import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.runtime.api.Framework;

/**
 * {@link FileSystemItemFactory} for a collection synchronization root {@link FolderItem}.
 *
 * @author Antoine Taillefer
 * @since 6.0
 */
public class CollectionSyncRootFolderItemFactory extends DefaultSyncRootFolderItemFactory {

    private static final Log log = LogFactory.getLog(CollectionSyncRootFolderItemFactory.class);

    public static final String FACTORY_NAME = "collectionSyncRootFolderItemFactory";

    /**
     * The factory considers that a {@link DocumentModel} is adaptable as a {@link FileSystemItem} if:
     * <ul>
     * <li>It is a Collection</li>
     * <li>AND it is not HiddenInNavigation</li>
     * <li>AND it is not in the "deleted" life cycle state, unless {@code includeDeleted} is true</li>
     * <li>AND it is a synchronization root registered for the current user, unless {@code relaxSyncRootConstraint} is
     * true</li>
     * </ul>
     */
    @Override
    public boolean isFileSystemItem(DocumentModel doc, boolean includeDeleted, boolean relaxSyncRootConstraint) {

        // Check Collection
        if (!Framework.getService(CollectionManager.class).isCollection(doc)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Document %s is not a Collection, it cannot be adapted as a FileSystemItem.",
                        doc.getId()));
            }
            return false;
        }
        // Check HiddenInNavigation
        if (doc.hasFacet("HiddenInNavigation")) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Document %s is HiddenInNavigation, it cannot be adapted as a FileSystemItem.",
                        doc.getId()));
            }
            return false;
        }
        // Check "deleted" life cycle state
        if (!includeDeleted && LifeCycleConstants.DELETED_STATE.equals(doc.getCurrentLifeCycleState())) {
            if (log.isDebugEnabled()) {
                log.debug(String.format(
                        "Document %s is in the '%s' life cycle state, it cannot be adapted as a FileSystemItem.",
                        doc.getId(), LifeCycleConstants.DELETED_STATE));
            }
            return false;
        }
        if (!relaxSyncRootConstraint) {
            // Check synchronization root registered for the current user
            NuxeoDriveManager nuxeoDriveManager = Framework.getService(NuxeoDriveManager.class);
            Principal principal = doc.getCoreSession().getPrincipal();
            boolean isSyncRoot = nuxeoDriveManager.isSynchronizationRoot(principal, doc);
            if (!isSyncRoot) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format(
                            "Document %s is not a registered synchronization root for user %s, it cannot be adapted as a FileSystemItem.",
                            doc.getId(), principal.getName()));
                }
                return false;
            }
        }
        return true;
    }

    @Override
    protected FileSystemItem adaptDocument(DocumentModel doc, boolean forceParentItem, FolderItem parentItem,
            boolean relaxSyncRootConstraint, boolean getLockInfo) {
        return new CollectionSyncRootFolderItem(name, parentItem, doc, relaxSyncRootConstraint, getLockInfo);
    }

}
