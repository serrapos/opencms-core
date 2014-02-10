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

import org.opencms.gwt.client.ui.I_CmsButton.ButtonStyle;
import org.opencms.util.CmsStringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
<<<<<<< HEAD
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
=======
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provides a menu button.<p>
 * 
 * @since 8.0.0
 */
public class CmsMenuButton extends Composite implements HasClickHandlers {

    /**
     * @see com.google.gwt.uibinder.client.UiBinder
     */
    interface I_CmsMenuButtonUiBinder extends UiBinder<Widget, CmsMenuButton> {
        // GWT interface, nothing to do here
    }

    /** The ui-binder instance for this class. */
    private static I_CmsMenuButtonUiBinder uiBinder = GWT.create(I_CmsMenuButtonUiBinder.class);

    /** The menu button. */
    @UiField
    protected CmsPushButton m_button;

    /** The menu content. */
    protected CmsToolbarPopup m_popup;

    /** Registration of the window resize handler. */
    protected HandlerRegistration m_resizeRegistration;

    /** Flag if the menu is open. */
    private boolean m_isOpen;

    /** Flag if the menu opens to the right hand side. */
    private boolean m_isOpenRight;

    /**
     * Constructor.<p>
     * 
     * @param buttonText the menu button text
     * @param imageClass the menu button image sprite class
     */
    @UiConstructor
    public CmsMenuButton(String buttonText, String imageClass) {

        this();
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(buttonText)) {
            m_button.setText(buttonText);
        }
        m_button.setImageClass(imageClass);
    }

    /**
     * Constructor.<p>
     */
    private CmsMenuButton() {

        initWidget(uiBinder.createAndBindUi(this));
        m_button.setSize(I_CmsButton.Size.big);
        m_button.setButtonStyle(ButtonStyle.MENU, null);
        m_isOpen = false;

        m_popup = new CmsToolbarPopup(m_button, false, this.getElement());
        m_popup.addCloseHandler(new CloseHandler<PopupPanel>() {

            public void onClose(CloseEvent<PopupPanel> event) {

                if (event.isAutoClosed()) {
                    autoClose();
                    if (m_resizeRegistration != null) {
                        m_resizeRegistration.removeHandler();
                        m_resizeRegistration = null;
                    }
                }
            }
        });
    }

    /**
     * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
     */
    public HandlerRegistration addClickHandler(ClickHandler handler) {

        return addDomHandler(handler, ClickEvent.getType());
    }

    /**
     * Removes all content from menu.<p>
     */
    public void clear() {

        m_popup.clear();

    }

    /**
     * Closes the menu and fires the on toggle event.<p>
     */
    public void closeMenu() {

        m_popup.hide();
        setButtonUp();
        if (m_resizeRegistration != null) {
            m_resizeRegistration.removeHandler();
            m_resizeRegistration = null;
        }
    }

    /**
     * Disables the menu button.<p>
     * 
     * @param disabledReason the reason to set in the button title
     */
    public void disable(String disabledReason) {

        m_button.disable(disabledReason);
<<<<<<< HEAD
        DOM.setElementPropertyBoolean(getElement(), "disabled", true);
=======
        getElement().setPropertyBoolean("disabled", true);
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485
    }

    /**
     * Enables or disables the button.<p>
     */
    public void enable() {

        m_button.enable();
<<<<<<< HEAD
        DOM.setElementPropertyBoolean(getElement(), "disabled", false);
=======
        getElement().setPropertyBoolean("disabled", false);
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485
    }

    /**
     * Hides the menu content as well as the menu connector.<p>
     */
    public void hide() {

        m_popup.hide();
    }

    /**
     * Returns if the menu is open.<p>
     * 
     * @return <code>true</code> if the menu is opened
     */
    public boolean isOpen() {

        return m_isOpen;
    }

    /**
     * Returns the isOpenRight.<p>
     *
     * @return the isOpenRight
     */
    public boolean isOpenRight() {

        return m_isOpenRight;
    }

    /**
     * @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event)
     */
    @Override
    public void onBrowserEvent(Event event) {

        // Should not act on button if disabled.
        if (!isEnabled()) {
            return;
        }
        super.onBrowserEvent(event);
    }

    /**
     * @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event)
     */
    @Override
    public void onBrowserEvent(Event event) {

        // Should not act on button if disabled.
        if (isEnabled() == false) {
            return;
        }
        super.onBrowserEvent(event);
    }

    /**
     * Opens the menu and fires the on toggle event.<p>
     */
    public void openMenu() {

        m_isOpen = true;
        setButtonDown();

        m_popup.show();
        m_popup.position();
        m_resizeRegistration = Window.addResizeHandler(new ResizeHandler() {

            public void onResize(ResizeEvent event) {

                m_popup.position();
            }
        });
    }

    /**
     * Enables or disables the button.<p>
     * 
     * @param enabled if true, enables the button, else disables it
     */
    public void setEnabled(boolean enabled) {

        if (enabled) {
            enable();
        } else {
            m_button.setEnabled(enabled);
<<<<<<< HEAD
            DOM.setElementPropertyBoolean(getElement(), "disabled", true);
=======
            getElement().setPropertyBoolean("disabled", true);
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485
        }
    }

    /**
     * This will set the menu content widget.<p>
     * 
     * @param widget the widget to set as content 
     */
    public void setMenuWidget(Widget widget) {

        m_popup.remove(widget);
        m_popup.add(widget);
    }

    /**
     * Sets the isOpenRight.<p>
     *
     * @param isOpenRight the isOpenRight to set
     */
    public void setOpenRight(boolean isOpenRight) {

        m_isOpenRight = isOpenRight;
    }

    /**
     * Sets the isToolbarMode.<p>
     *
     * @param isToolbarMode the isToolbarMode to set
     */
    public void setToolbarMode(boolean isToolbarMode) {

        m_popup.setToolbarMode(isToolbarMode);
    }

    /**
     * Shows the menu content as well as the menu connector.<p>
     */
    public void show() {

        m_popup.show();
    }

    /**
     * Called on auto close.<p>
     */
    protected void autoClose() {

        setButtonUp();
    }

    /**
     * Returns the popup content.<p>
     * 
     * @return the popup content
     */
    protected CmsPopup getPopup() {

        return m_popup;
    }

    /**
     * Hides the menu content without altering the button state.<p>
     */
    protected void hideMenu() {

        m_popup.hide();
        if (m_resizeRegistration != null) {
            m_resizeRegistration.removeHandler();
            m_resizeRegistration = null;
        }
    }

    /**
     * Returns if this button is enabled.<p>
     * 
     * @return <code>true</code> if the button is enabled
<<<<<<< HEAD
     */
    protected boolean isEnabled() {

        return !DOM.getElementPropertyBoolean(getElement(), "disabled");
    }

    /**
     * Positions the menu popup the button.<p>
     */
    protected void positionPopup() {

        int spaceAssurance = 20;
        int space = getToolbarWidth() + (2 * spaceAssurance);

        // get the window client width
        int windowWidth = Window.getClientWidth();
        // get the min left position
        int minLeft = (windowWidth - space) / 2;
        if (minLeft < spaceAssurance) {
            minLeft = spaceAssurance;
        }
        // get the max right position
        int maxRight = minLeft + space;
        // get the middle button position
        CmsPositionBean buttonPosition = CmsPositionBean.generatePositionInfo(m_button.getElement());
        int buttonMiddle = (buttonPosition.getLeft() - Window.getScrollLeft()) + (buttonPosition.getWidth() / 2);
        // get the content width
        int contentWidth = m_popup.getOffsetWidth();

        // the optimum left position is in the middle of the button minus the half content width
        // assume that the optimum fits into the space
        int contentLeft = buttonMiddle - (contentWidth / 2);

        if (minLeft > contentLeft) {
            // if the optimum left position of the popup is outside the min left position:
            // move the popup to the right (take the min left position as left)
            contentLeft = minLeft;
        } else if ((contentLeft + contentWidth) > maxRight) {
            // if the left position plus the content width is outside the max right position:
            // move the popup to the left (take the max right position minus the content width)
            contentLeft = maxRight - contentWidth;
        }

        // limit the right position if the popup is right outside the window 
        if ((contentLeft + contentWidth + spaceAssurance) > windowWidth) {
            contentLeft = windowWidth - contentWidth - spaceAssurance;
        }

        // limit the left position if the popup is left outside the window 
        if (contentLeft < spaceAssurance) {
            contentLeft = spaceAssurance;
        }

        int arrowSpace = 10;
        int arrowWidth = I_CmsLayoutBundle.INSTANCE.gwtImages().menuArrowTopImage().getWidth();
        int arrowHeight = I_CmsLayoutBundle.INSTANCE.gwtImages().menuArrowTopImage().getHeight();

        // the optimum position for the arrow is in the middle of the button
        int arrowLeft = buttonMiddle - contentLeft - (arrowWidth / 2);
        if ((arrowLeft + arrowWidth + arrowSpace) > contentWidth) {
            // limit the arrow position if the maximum is reached (content width 'minus x')
            arrowLeft = contentWidth - arrowWidth - arrowSpace;
        } else if ((arrowLeft - arrowSpace) < 0) {
            // limit the arrow position if the minimum is reached ('plus x')
            arrowLeft = arrowWidth + arrowSpace;
        }

        int arrowTop = -(arrowHeight - 2);
        String arrowClass = I_CmsLayoutBundle.INSTANCE.dialogCss().menuArrowTop();

        int contentTop = (((buttonPosition.getTop() + buttonPosition.getHeight()) - Window.getScrollTop()) + arrowHeight) - 2;
        if (!m_isToolbarMode) {
            contentTop = (buttonPosition.getTop() + buttonPosition.getHeight() + arrowHeight) - 2;
            int contentHeight = m_popup.getOffsetHeight();
            int windowHeight = Window.getClientHeight();

            if (((contentHeight + spaceAssurance) < windowHeight)
                && ((buttonPosition.getTop() - Window.getScrollTop()) > contentHeight)
                && (((contentHeight + spaceAssurance + contentTop) - Window.getScrollTop()) > windowHeight)) {
                // content fits into the window height, 
                // there is enough space above the button 
                // and there is to little space below the button
                // so show above
                contentTop = ((buttonPosition.getTop() - arrowHeight) + 2) - contentHeight;
                arrowTop = contentHeight - 1;
                arrowClass = I_CmsLayoutBundle.INSTANCE.dialogCss().menuArrowBottom();
            }
        } else {
            contentLeft = contentLeft - Window.getScrollLeft();
            m_popup.setPositionFixed();
        }

        m_arrow.setClassName(arrowClass);
        m_arrow.getStyle().setLeft(arrowLeft, Unit.PX);
        m_arrow.getStyle().setTop(arrowTop, Unit.PX);

        m_popup.showArrow(m_arrow);
        m_popup.setPopupPosition(contentLeft + Window.getScrollLeft(), contentTop);
=======
     */
    protected boolean isEnabled() {

        return !getElement().getPropertyBoolean("disabled");
>>>>>>> 9b75d93687f3eb572de633d63889bf11e963a485
    }

    /**
     * Sets the button to its 'down state'.
     */
    protected void setButtonDown() {

        m_button.setDown(true);
    }

    /**
     * Sets button to state up, hides menu fragments (not the content pop-up) and fires the toggle event.<p>
     */
    protected void setButtonUp() {

        m_isOpen = false;
        m_button.setDown(false);
    }

}
