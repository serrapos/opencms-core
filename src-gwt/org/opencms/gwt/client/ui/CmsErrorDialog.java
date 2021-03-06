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

package org.opencms.gwt.client.ui;

import org.opencms.gwt.client.Messages;
import org.opencms.gwt.client.ui.css.I_CmsLayoutBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

/**
 * Provides a generic error dialog.<p>
 * 
 * @since 8.0.0
 */
public class CmsErrorDialog extends CmsPopup {

    /** The 'close' button. */
    private CmsPushButton m_closeButton;

    /** The details fieldset. */
    private CmsFieldSet m_detailsFieldset;

    /** The message HTML. */
    private CmsMessageWidget m_messageWidget;

    /**
     * Constructor.<p>
     * 
     * @param message the error message
     * @param details the error details
     */
    public CmsErrorDialog(String message, String details) {

        super(Messages.get().key(Messages.GUI_ERROR_0));
        setAutoHideEnabled(false);
        setModal(true);
        setGlassEnabled(true);
        setWidth(512);
        m_closeButton = new CmsPushButton();
        m_closeButton.setText(Messages.get().key(Messages.GUI_OK_0));
        m_closeButton.setUseMinWidth(true);
        m_closeButton.addClickHandler(new ClickHandler() {

            /**
             * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent event) {

                onClose();
            }
        });
        addButton(m_closeButton);

        Panel content = new FlowPanel();
        m_messageWidget = createMessageWidget(message);
        content.add(m_messageWidget);
        if (details != null) {
            m_detailsFieldset = createDetailsFieldSet(details);
            m_detailsFieldset.addOpenHandler(new OpenHandler<CmsFieldSet>() {

                /**
                 * On open.<p>
                 * 
                 * @param event the open event
                 */
                public void onOpen(OpenEvent<CmsFieldSet> event) {

                    center();
                }
            });
            content.add(m_detailsFieldset);
        }
        setMainContent(content);
    }

    /**
     * @see org.opencms.gwt.client.ui.CmsPopup#center()
     */
    @Override
    public void center() {

        show();
        super.center();

    }

    /**
     * @see org.opencms.gwt.client.ui.CmsPopup#show()
     */
    @Override
    public void show() {

        super.show();
        onShow();
    }

    /**
     * Executed on 'close' click. <p>
     */
    protected void onClose() {

        m_closeButton.setEnabled(false);
        hide();
    }

    /**
     * Creates a field-set containing the error details.<p>
     * 
     * @param details the error details
     * 
     * @return the field-set widget
     */
    private CmsFieldSet createDetailsFieldSet(String details) {

        CmsFieldSet fieldset = new CmsFieldSet();
        fieldset.addStyleName(I_CmsLayoutBundle.INSTANCE.errorDialogCss().details());
        fieldset.setLegend(Messages.get().key(Messages.GUI_DETAILS_0));
        fieldset.addContent(new HTML(details));
        fieldset.setOpen(false);
        return fieldset;
    }

    /**
     * Creates the message HTML widget containing error icon and message.<p>
     * 
     * @param message the message
     * 
     * @return the HTML widget
     */
    private CmsMessageWidget createMessageWidget(String message) {

        CmsMessageWidget widget = new CmsMessageWidget();
        widget.setIconClass(I_CmsLayoutBundle.INSTANCE.errorDialogCss().errorIcon());
        widget.setMessageText(message);
        return widget;
    }

    /**
     * Checks the available space and sets max-height to the details field-set.
     */
    private void onShow() {

        if (m_detailsFieldset != null) {
            m_detailsFieldset.getContentPanel().getElement().getStyle().setPropertyPx(
                "maxHeight",
                getAvailableHeight(m_messageWidget.getOffsetHeight()));
        }
    }
}
