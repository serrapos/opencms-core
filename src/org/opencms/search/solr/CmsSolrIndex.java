/*
 * File   : $Source$
 * Date   : $Date$
 * Version: $Revision$
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
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

package org.opencms.search.solr;

import org.opencms.configuration.CmsConfigurationException;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.types.CmsResourceTypeXmlContainerPage;
import org.opencms.file.types.CmsResourceTypeXmlContent;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.report.I_CmsReport;
import org.opencms.search.A_CmsSearchIndex;
import org.opencms.search.CmsSearchException;
import org.opencms.search.CmsSearchParameters;
import org.opencms.search.CmsSearchResource;
import org.opencms.search.CmsSearchResultList;
import org.opencms.search.I_CmsIndexWriter;
import org.opencms.search.I_CmsSearchDocument;
import org.opencms.search.documents.I_CmsDocumentFactory;
import org.opencms.util.CmsRequestUtil;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.FastWriter;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.BinaryQueryResponseWriter;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;

/**
 * Implements the search within an Solr index.<p>
 * 
 * @since 8.5.0 
 */
public class CmsSolrIndex extends A_CmsSearchIndex {

    /** The name for the default Solr offline index. */
    public static final String SOLR_OFFLINE_INDEX_NAME = "Solr Offline";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsSolrIndex.class);

    /** Indicates the maximum number of documents from the complete result set to return. */
    private static final int ROWS_MAX = 50;

    /** A constant for UTF-8 charset. */
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /** The post document manipulator. */
    private I_CmsSolrPostSearchProcessor m_postProcessor;

    /** The embedded Solr server, only one embedded instance per OpenCms. */
    private SolrServer m_solr;

    /**
     * Default constructor.<p>
     */
    public CmsSolrIndex() {

        super();
    }

    /**
     * Public constructor to create a Solr index.<p>
     * 
     * @param name the name for this index.<p>
     * 
     * @throws CmsIllegalArgumentException if something goes wrong
     */
    public CmsSolrIndex(String name)
    throws CmsIllegalArgumentException {

        super(name);
    }

    /**
     * @see org.opencms.search.A_CmsSearchIndex#createIndexWriter(boolean, org.opencms.report.I_CmsReport)
     */
    @Override
    public I_CmsIndexWriter createIndexWriter(boolean create, I_CmsReport report) {

        return new CmsSolrIndexWriter(m_solr, this);
    }

    /**
     * @see org.opencms.search.A_CmsSearchIndex#getDocument(java.lang.String, java.lang.String)
     */
    @Override
    public I_CmsSearchDocument getDocument(String fieldname, String term) {

        try {
            SolrQuery query = new SolrQuery();
            query.setQuery(fieldname + ":" + term);
            QueryResponse res = m_solr.query(query);
            if (res != null) {
                SolrDocumentList sdl = m_solr.query(query).getResults();
                if ((sdl.getNumFound() == 1L) && (sdl.get(0) != null)) {
                    return new CmsSolrDocument(ClientUtils.toSolrInputDocument(sdl.get(0)));
                }
            }
        } catch (Exception e) {
            // ignore and assume that the document could not be found
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @see org.opencms.search.CmsLuceneIndex#getDocumentFactory(org.opencms.file.CmsResource)
     */
    @Override
    public I_CmsDocumentFactory getDocumentFactory(CmsResource res) {

        if ((res != null) && (getSources() != null)) {
            // the result can only be null or the type configured for the resource
            if (CmsResourceTypeXmlContent.isXmlContent(res) || CmsResourceTypeXmlContainerPage.isContainerPage(res)) {
                return OpenCms.getSearchManager().getDocumentFactory(
                    CmsSolrDocumentXmlContent.TYPE_XMLCONTENT_SOLR,
                    "text/html");
            } else {
                return super.getDocumentFactory(res);
            }
        }
        return null;
    }

    /**
     * Returns the language locale for the given resource in this index.<p>
     * 
     * @param cms the current OpenCms user context
     * @param resource the resource to check
     * @param availableLocales a list of locales supported by the resource
     * 
     * @return the language locale for the given resource in this index
     */
    @Override
    public Locale getLocaleForResource(CmsObject cms, CmsResource resource, List<Locale> availableLocales) {

        Locale result;
        List<Locale> defaultLocales = OpenCms.getLocaleManager().getDefaultLocales(cms, resource);
        if ((availableLocales != null) && (availableLocales.size() > 0)) {
            result = OpenCms.getLocaleManager().getBestMatchingLocale(
                defaultLocales.get(0),
                defaultLocales,
                availableLocales);
        } else {
            result = defaultLocales.get(0);
        }
        return result;
    }

    /**
     * Returns the search post processor.<p>
     *
     * @return the post processor to use
     */
    public I_CmsSolrPostSearchProcessor getPostProcessor() {

        return m_postProcessor;
    }

    /**
     * @see org.opencms.search.A_CmsSearchIndex#initialize()
     */
    @Override
    public void initialize() throws CmsSearchException {

        super.initialize();
        try {
            m_solr = OpenCms.getSearchManager().registerSolrIndex(this);
        } catch (CmsConfigurationException ex) {
            LOG.error(ex.getMessage());
            setEnabled(false);
        }
    }

    /**
     * <code>
     * #################<br>
     * ### DON'T USE ###<br>
     * #################<br>
     * </code><p>
     * 
     * @Deprecated Use {@link #search(CmsObject, SolrQuery)} or {@link #search(CmsObject, String)} instead
     */
    @Override
    @Deprecated
    public CmsSearchResultList search(CmsObject cms, CmsSearchParameters params) {

        throw new UnsupportedOperationException();
    }

    /**
     * <h4>Performs a search on the Solr index</h4>
     * 
     * Returns a list of 'OpenCms resource documents' 
     * ({@link CmsSearchResource}) encapsulated within the class  {@link CmsSolrResultList}.
     * This list can be accessed exactly like an {@link List} which entries are 
     * {@link CmsSearchResource} that extend {@link CmsResource} and holds the Solr 
     * implementation of {@link I_CmsSearchDocument} as member. <b>This enables you to deal 
     * with the resulting list as you do with well known {@link List} and work on it's entries
     * like you do on {@link CmsResource}.</b>
     * 
     * <h4>What will be done with the Solr search result?</h4>
     * <ul>
     * <li>Although it can happen, that there are less results returned than rows were requested
     * (imagine an index containing less documents than requested rows) we try to guarantee
     * the requested amount of search results and to provide a working pagination with
     * security check.</li>
     * 
     * <li>To be sure we get enough documents left even the permission check reduces the amount
     * of found documents, the rows are multiplied by <code>'5'</code> and the current page 
     * additionally the offset is added. The count of documents we don't have enough 
     * permissions for grows with increasing page number, that's why we also multiply 
     * the rows by the current page count.</li>
     * 
     * <li>Also make sure we perform the permission check for all found documents, so start with
     * the first found doc.</li>
     * </ul>
     * 
     * <b>NOTE:</b> If latter pages than the current one are containing protected documents the
     * total hit count will be incorrect, because the permission check ends if we have 
     * enough results found for the page to display. With other words latter pages than 
     * the current can contain documents that will first be checked if those pages are 
     * requested to be displayed, what causes a incorrect hit count.<p>
     * 
     * @param cms the CMS object
     * @param query the Solr query can also be a {@link CmsSolrQuery}
     * 
     * @return the list of found documents
     * 
     * @throws CmsSearchException if something goes wrong
     * 
     * @see org.opencms.search.solr.CmsSolrResultList
     * @see org.opencms.search.CmsSearchResource
     * @see org.opencms.search.I_CmsSearchDocument
     * @see org.opencms.search.solr.CmsSolrQuery
     */
    public synchronized CmsSolrResultList search(CmsObject cms, SolrQuery query) throws CmsSearchException {

        LOG.debug("### START SRARCH (time in ms) ###");
        int previousPriority = Thread.currentThread().getPriority();
        long startTime = System.currentTimeMillis();
        try {

            // initialize the search context
            CmsObject searchCms = OpenCms.initCmsObject(cms);

            // change thread priority in order to reduce search impact on overall system performance
            if (getPriority() > 0) {
                Thread.currentThread().setPriority(getPriority());
            }

            // the lists storing the found documents that will be returned
            List<CmsSearchResource> resourceDocumentList = new ArrayList<CmsSearchResource>();
            SolrDocumentList solrDocumentList = new SolrDocumentList();

            // Initialize rows, offset, end and the current page.
            int rows = query.getRows().intValue() <= ROWS_MAX ? query.getRows().intValue() : ROWS_MAX;
            int start = query.getStart() != null ? query.getStart().intValue() : 0;
            int end = start + rows;
            int page = Math.round(start / rows) + 1;

            // set the start to '0' and expand the rows before performing the query
            query.setStart(new Integer(0));
            query.setRows(new Integer((5 * rows * page) + start));

            // perform the Solr query and remember the original Solr response
            QueryResponse queryResponse = m_solr.query(query);
            LOG.debug("### Query Time After Execution  : " + (System.currentTimeMillis() - startTime));

            // initialize the hit count and the max score
            long hitCount, visibleHitCount = hitCount = queryResponse.getResults().getNumFound();
            float maxScore = 0;

            // iterate over found documents
            for (int i = 0, cnt = 0; (i < hitCount) && (cnt < end); i++) {
                try {
                    SolrDocument doc = queryResponse.getResults().get(i);
                    I_CmsSearchDocument searchDoc = new CmsSolrDocument(doc);
                    if (needsPermissionCheck(searchDoc)) {
                        // only if the document is an OpenCms internal resource perform the permission check
                        CmsResource resource = getResource(searchCms, searchDoc);
                        if (resource != null) {
                            // permission check performed successfully: the user has read permissions!
                            if (cnt >= start) {
                                if (m_postProcessor != null) {
                                    doc = m_postProcessor.process(searchCms, resource, doc);
                                }
                                resourceDocumentList.add(new CmsSearchResource(resource, searchDoc));
                                solrDocumentList.add(doc);
                                maxScore = maxScore < searchDoc.getScore() ? searchDoc.getScore() : maxScore;
                            }
                            cnt++;
                        } else {
                            visibleHitCount--;
                        }
                    }
                } catch (Exception e) {
                    // should not happen, but if it does we want to go on with the next result nevertheless                        
                    LOG.warn(Messages.get().getBundle().key(Messages.LOG_RESULT_ITERATION_FAILED_0), e);
                }
            }

            SolrCore core = null;
            if (m_solr instanceof EmbeddedSolrServer) {
                core = ((EmbeddedSolrServer)m_solr).getCoreContainer().getCore(getName());
            }
            LOG.debug("### Query Time After Permission : " + (System.currentTimeMillis() - startTime));

            // create and return the result
            return new CmsSolrResultList(
                core,
                query,
                queryResponse,
                solrDocumentList,
                resourceDocumentList,
                start,
                new Integer(rows),
                end,
                page,
                visibleHitCount,
                new Float(maxScore),
                startTime);

        } catch (RuntimeException e) {
            throw new CmsSearchException(Messages.get().container(
                Messages.ERR_SEARCH_INVALID_SEARCH_1,
                query.toString()), e);
        } catch (Exception e) {
            throw new CmsSearchException(Messages.get().container(
                Messages.ERR_SEARCH_INVALID_SEARCH_1,
                query.toString()), e);
        } finally {
            // re-set thread to previous priority
            Thread.currentThread().setPriority(previousPriority);
        }
    }

    /**
     * Performs a search.<p>
     * 
     * @param cms the cms object
     * @param solrQuery the Solr query
     * 
     * @return a list of documents
     * 
     * @throws CmsSearchException if something goes wrong
     * 
     * @see CmsSolrIndex#search(CmsObject, SolrQuery)
     */
    public CmsSolrResultList search(CmsObject cms, String solrQuery) throws CmsSearchException {

        return search(cms, new CmsSolrQuery(cms, CmsRequestUtil.createParameterMap(solrQuery)));
    }

    /**
     * Sets the search post processor.<p>
     *
     * @param postProcessor the search post processor to set
     */
    public void setPostProcessor(I_CmsSolrPostSearchProcessor postProcessor) {

        m_postProcessor = postProcessor;
    }

    /**
     * Writes the response into the writer.<p>
     * 
     * NOTE: Currently not available for HTTP server.<p>
     * 
     * @param response the servlet response
     * @param result the result to print
     * 
     * @throws Exception if there is no embedded server
     */
    public void writeResponse(ServletResponse response, CmsSolrResultList result) throws Exception {

        if (m_solr instanceof EmbeddedSolrServer) {

            SolrCore core = ((EmbeddedSolrServer)m_solr).getCoreContainer().getCore(getName());
            SolrQueryResponse queryResponse = result.getSolrQueryResponse();
            SolrQueryRequest queryRequest = result.getSolrQueryRequest();
            QueryResponseWriter responseWriter = core.getQueryResponseWriter(queryRequest);

            final String ct = responseWriter.getContentType(queryRequest, queryResponse);
            if (null != ct) {
                response.setContentType(ct);
            }

            if (responseWriter instanceof BinaryQueryResponseWriter) {
                BinaryQueryResponseWriter binWriter = (BinaryQueryResponseWriter)responseWriter;
                binWriter.write(response.getOutputStream(), queryRequest, queryResponse);
            } else {
                String charset = ContentStreamBase.getCharsetFromContentType(ct);
                Writer out = ((charset == null) || charset.equalsIgnoreCase(UTF8.toString())) ? new OutputStreamWriter(
                    response.getOutputStream(),
                    UTF8) : new OutputStreamWriter(response.getOutputStream(), charset);
                out = new FastWriter(out);
                responseWriter.write(out, queryRequest, queryResponse);
                out.flush();
            }
        } else {
            throw new UnsupportedOperationException();
        }
        LOG.debug("### Query Time After Write      : " + (System.currentTimeMillis() - result.getStartTime()));
    }

    /**
     * @see org.opencms.search.A_CmsSearchIndex#indexSearcherClose()
     */
    @Override
    protected void indexSearcherClose() {

        // nothing to do here
    }

    /**
     * @see org.opencms.search.A_CmsSearchIndex#indexSearcherOpen(java.lang.String)
     */
    @Override
    protected void indexSearcherOpen(String path) {

        // nothing to do here
    }

    /**
     * @see org.opencms.search.A_CmsSearchIndex#indexSearcherUpdate()
     */
    @Override
    protected void indexSearcherUpdate() {

        // nothing to do here
    }
}
