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
import org.opencms.gwt.CmsRpcException;
import org.opencms.gwt.shared.alias.CmsAliasEditValidationReply;
import org.opencms.gwt.shared.alias.CmsAliasEditValidationRequest;
import org.opencms.gwt.shared.alias.CmsAliasImportResult;
import org.opencms.gwt.shared.alias.CmsAliasInitialFetchResult;
import org.opencms.gwt.shared.alias.CmsAliasSaveValidationRequest;
import org.opencms.gwt.shared.alias.CmsRewriteAliasValidationReply;
import org.opencms.gwt.shared.alias.CmsRewriteAliasValidationRequest;
import org.opencms.util.CmsUUID;

import com.google.gwt.user.client.rpc.RemoteService;
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
public interface I_CmsSitemapService extends RemoteService {

    /**
     * Creates a sub-sitemap of the given sitemap starting from the given entry.<p>
     * 
     * @param entryId the structure id of the sitemap entry to create a sub sitemap of
     * 
     * @return the sub-sitemap creation result 
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    CmsSitemapChange createSubSitemap(CmsUUID entryId) throws CmsRpcException;
<<<<<<< HEAD
=======

    /**
     * Gets the alias import results from the server.<p>
     * 
     * @param resultKey the key which identifies the alias import results to get 
     * @return the list of alias import results 
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    List<CmsAliasImportResult> getAliasImportResult(String resultKey) throws CmsRpcException;

    /**
     * Gets the initial data for the bulk alias editor.<p>
     * 
     * @return the initial data for the alias editor 
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    CmsAliasInitialFetchResult getAliasTable() throws CmsRpcException;
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485

    /**
     * Returns the sitemap children for the given entry.<p>
     * 
     * @param entryPointUri the URI of the sitemap entry point
     * @param entryId the entry id
     * @param levels the count of child levels to read
     *  
     * @return the sitemap children
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    CmsClientSitemapEntry getChildren(String entryPointUri, CmsUUID entryId, int levels) throws CmsRpcException;

    /**
     * Merges a sub-sitemap into it's parent sitemap.<p>
     * 
     * @param entryPoint the sitemap entry point
     * @param subSitemapId the structure id of the sub sitemap folder
     * 
     * @return the result of the merge operation
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    CmsSitemapChange mergeSubSitemap(String entryPoint, CmsUUID subSitemapId) throws CmsRpcException;

    /**
     * Returns the initialization data for the given sitemap.<p>
     * 
     * @param sitemapUri the site relative path
     *  
     * @return the initialization data
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    CmsSitemapData prefetch(String sitemapUri) throws CmsRpcException;

    /**
     * Saves the change to the given sitemap.<p>
     * 
     * @param sitemapUri the sitemap URI 
     * @param change the change to save
     * 
     * @return the updated change
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    CmsSitemapChange save(String sitemapUri, CmsSitemapChange change) throws CmsRpcException;
<<<<<<< HEAD
=======

    /**
     * Saves the aliases for the bulk alias editor.<p>
     * 
     * @param saveRequest the object containing the data to save 
     * @return the result of saving the aliases 
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    CmsAliasEditValidationReply saveAliases(CmsAliasSaveValidationRequest saveRequest) throws CmsRpcException;
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485

    /**
     * Saves the change to the given sitemap.<p>
     * 
     * @param sitemapUri the sitemap URI 
     * @param change the change to save
     * 
     * @return the updated change
     * 
     * @throws CmsRpcException if something goes wrong
     */
    @SynchronizedRpcRequest
    CmsSitemapChange saveSync(String sitemapUri, CmsSitemapChange change) throws CmsRpcException;
<<<<<<< HEAD
=======

    /**
     * Updates the alias editor status.<p>
     * 
     * This is used to keep two users from editing the alias table for a site root concurrently.<p>
     * 
     * @param editing true to indicate that the table is still being edited, false to indicate that the table isn't being edited anymore
     *  
     * @throws CmsRpcException if something goes wrong 
     */
    void updateAliasEditorStatus(boolean editing) throws CmsRpcException;

    /**
     * Validates the aliases for the bulk alias editor.<p>
     * 
     * @param validationRequest an object indicating the type of validation to perform 
     * @return the validation result 
     * 
     * @throws CmsRpcException if something goes wrong 
     */
    CmsAliasEditValidationReply validateAliases(CmsAliasEditValidationRequest validationRequest) throws CmsRpcException;

    /**
     * Validates rewrite aliases.<p>
     * 
     * @param validationRequest the rewrite alias data to validate 
     * @return the validation result
     *  
     * @throws CmsRpcException if something goes wrong 
     */
    CmsRewriteAliasValidationReply validateRewriteAliases(CmsRewriteAliasValidationRequest validationRequest)
    throws CmsRpcException;

>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485
}
