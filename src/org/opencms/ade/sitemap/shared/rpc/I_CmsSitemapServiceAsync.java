/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.sitemap.shared.rpc;

import org.opencms.ade.sitemap.shared.CmsClientSitemapEntry;
import org.opencms.ade.sitemap.shared.CmsSitemapChange;
import org.opencms.ade.sitemap.shared.CmsSitemapData;
<<<<<<< HEAD
=======
import org.opencms.gwt.shared.alias.CmsAliasEditValidationReply;
import org.opencms.gwt.shared.alias.CmsAliasEditValidationRequest;
import org.opencms.gwt.shared.alias.CmsAliasImportResult;
import org.opencms.gwt.shared.alias.CmsAliasInitialFetchResult;
import org.opencms.gwt.shared.alias.CmsAliasSaveValidationRequest;
import org.opencms.gwt.shared.alias.CmsRewriteAliasValidationReply;
import org.opencms.gwt.shared.alias.CmsRewriteAliasValidationRequest;
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485
import org.opencms.util.CmsUUID;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SynchronizedRpcRequest;

/**
 * Handles all RPC services related to the sitemap.<p>
 * 
 * @since 8.0.0
 * 
 * @see org.opencms.ade.sitemap.CmsVfsSitemapService
 * @see org.opencms.ade.sitemap.shared.rpc.I_CmsSitemapService
 * @see org.opencms.ade.sitemap.shared.rpc.I_CmsSitemapServiceAsync
 */
public interface I_CmsSitemapServiceAsync {

    /**
     * Creates a sub-sitemap of the given sitemap starting from the given entry.<p>
     * 
     * @param entryId the structure id of the sitemap entry to create a sub sitemap of
     * @param callback the async callback  
     */
    void createSubSitemap(CmsUUID entryId, AsyncCallback<CmsSitemapChange> callback);
<<<<<<< HEAD
=======

    /**
     * Gets the alias import results from the server.<p>
     * 
     * @param resultKey the key which identifies the alias import results to get 
     * @param asyncCallback the asynchronous callback  
     */
    void getAliasImportResult(String resultKey, AsyncCallback<List<CmsAliasImportResult>> asyncCallback);

    /**
     * Gets the initial data for the bulk alias editor.<p>
     * 
     * @param callback the asynchronous callback  
     */
    void getAliasTable(AsyncCallback<CmsAliasInitialFetchResult> callback);
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485

    /**
     * Returns the sitemap children for the given entry.<p>
     * 
     * @param entryPointUri the URI of the sitemap entry point
     * @param entryId the entry id
     * @param levels the count of child levels to read
     * @param callback the async callback
     */
    void getChildren(String entryPointUri, CmsUUID entryId, int levels, AsyncCallback<CmsClientSitemapEntry> callback);

    /**
     * Merges a sub-sitemap into it's parent sitemap.<p>
     * 
     * @param entryPoint the sitemap entry point
     * @param subSitemapId the structure id of the sub sitemap folder
     * 
     * @param callback the async callback
     */
    void mergeSubSitemap(String entryPoint, CmsUUID subSitemapId, AsyncCallback<CmsSitemapChange> callback);

    /**
     * Returns the initialization data for the given sitemap.<p>
     * 
     * @param sitemapUri the site relative path
     * @param callback the async callback
     */
    void prefetch(String sitemapUri, AsyncCallback<CmsSitemapData> callback);

    /**
     * Saves the change to the given sitemap.<p>
     * 
     * @param sitemapUri the sitemap URI 
     * @param change the change to save
     * @param callback the async callback
     */
    void save(String sitemapUri, CmsSitemapChange change, AsyncCallback<CmsSitemapChange> callback);
<<<<<<< HEAD
=======

    /**
     * Saves the aliases for the bulk alias editor.<p>
     *  
     * @param saveRequest the object containing the data to save
     * @param callback the asynchronous callback  
     */
    void saveAliases(CmsAliasSaveValidationRequest saveRequest, AsyncCallback<CmsAliasEditValidationReply> callback);
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485

    /**
     * Save the change to the given sitemap.<p>
     * 
     * @param sitemapUri the sitemap URI 
     * @param change the change to save
     * @param callback the async callback
     */
    @SynchronizedRpcRequest
    void saveSync(String sitemapUri, CmsSitemapChange change, AsyncCallback<CmsSitemapChange> callback);
<<<<<<< HEAD
=======

    /**
     * Updates the alias editor status.<p>
     * 
     * This is used to keep two users from editing the alias table for a site root concurrently.<p>
     * 
     * @param editing true to indicate that the table is still being edited, false to indicate that the table isn't being edited anymore
     * @param callback the asynchronous callback 
     */
    void updateAliasEditorStatus(boolean editing, AsyncCallback<Void> callback);

    /**
     * Validates the aliases for the bulk alias editor.<p>
     * 
     * @param validationRequest an object indicating the type of validation to perform 
     * @param callback the asynchronous callback 
     */
    void validateAliases(
        CmsAliasEditValidationRequest validationRequest,
        AsyncCallback<CmsAliasEditValidationReply> callback);

    /**
     * Validates rewrite aliases.<p>
     * 
     * @param validationRequest the rewrite alias data to validate
     *  
     * @param callback the callback for the result 
     */
    void validateRewriteAliases(
        CmsRewriteAliasValidationRequest validationRequest,
        AsyncCallback<CmsRewriteAliasValidationReply> callback);

>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485
}
