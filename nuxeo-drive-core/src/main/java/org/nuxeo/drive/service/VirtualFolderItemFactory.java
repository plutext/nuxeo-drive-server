/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.drive.service;

import java.security.Principal;

import org.nuxeo.drive.adapter.FolderItem;

/**
 * Interface for the classes contributed to the {@code topLevelFolderItemFactory} extension point of the
 * {@link FileSystemItemAdapterService}.
 * <p>
 * Allows to get a virtual {@link FolderItem} for a given user.
 *
 * @author Antoine Taillefer
 * @see XXX
 */
public interface VirtualFolderItemFactory extends FileSystemItemFactory {

    /**
     * Returns the (unique) virtual {@link FolderItem}.
     */
    FolderItem getVirtualFolderItem(Principal principal);

    String getFolderName();

    void setFolderName(String folderName);

}
