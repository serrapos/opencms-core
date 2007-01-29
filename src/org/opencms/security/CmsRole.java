/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/security/CmsRole.java,v $
 * Date   : $Date: 2007/01/29 09:44:54 $
 * Version: $Revision: 1.11.4.7 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (c) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.security;

import org.opencms.file.CmsRequestContext;
import org.opencms.file.CmsResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A role is used in the OpenCms security system to check if a user has access to a certain system function.<p>
 * 
 * Roles are used to ensure access permissions to system function that are not file based. 
 * For example, roles are used to check permissions to functions like "the user can schedule a 
 * job in the <code>{@link org.opencms.scheduler.CmsScheduleManager}</code>" or "the user can export (or import) 
 * the OpenCms database".<p>
 * 
 * All roles are based on <code>{@link org.opencms.file.CmsGroup}</code>. This means to have access to a role,
 * the user has to be a member in a certain predefined system group. Each role has exactly one group that
 * contains all "direct" members of this role.<p>
 * 
 * All roles have (optional) parent roles. If a user not a member of the role group of a role, but he is
 * a member of at last one of the parent role groups, he/she also has full access to this role. This is called 
 * "indirect" membership to the role.<p>
 * 
 * Please note that "indirect" membership does grant the user the same full access to a role that "direct" 
 * membership does. For example, the <code>{@link #ROOT_ADMIN}</code> role is a parent group of all other roles. 
 * So all users that are members of <code>{@link #ROOT_ADMIN}</code> have access to the functions of all other roles.<p>
 * 
 * Please do not perform automated sorting of members on this compilation unit. That leads 
 * to NPE's<p>
 * 
 * @author  Alexander Kandzior 
 *
 * @version $Revision: 1.11.4.7 $ 
 * 
 * @since 6.0.0 
 */
public final class CmsRole {

    /** The "ACCOUNT_MANAGER" role. */
    public static final CmsRole ACCOUNT_MANAGER;

    /** The "ADMINISTRATOR" role, which is a parent to all organizational unit roles. */
    public static final CmsRole ADMINISTRATOR;

    /** The "EXPORT_DATABASE" role. */
    public static final CmsRole DATABASE_MANAGER;

    /** The "DEVELOPER" role. */
    public static final CmsRole DEVELOPER;

    /** The "DIRECT_EDIT_USER" role. */
    public static final CmsRole DIRECT_EDIT_USER;

    /** The "PROJECT_MANAGER" role. */
    public static final CmsRole PROJECT_MANAGER;

    /** The "ROOT_ADMIN" role, which is a parent to all other roles. */
    public static final CmsRole ROOT_ADMIN;

    /** The "VFS_MANAGER" role. */
    public static final CmsRole VFS_MANAGER;

    /** The "WORKPLACE_MANAGER" role. */
    public static final CmsRole WORKPLACE_MANAGER;

    /** The "WORKPLACE_USER" role. */
    public static final CmsRole WORKPLACE_USER;

    /** The list of system roles. */
    private static final List SYSTEM_ROLES;

    /** The child roles of this role. */
    private final List m_childs = new ArrayList();

    /** The distinct group names of this role. */
    private List m_distictGroupNames;

    /** The name of the group this role is mapped to in the OpenCms database.*/
    private final String m_groupName;

    /** Indicates if this role is organizational unit dependent. */
    private boolean m_ouDependent;

    /** The parent role of this role. */
    private final CmsRole m_parentRole;

    /** The name of this role. */
    private final String m_roleName;

    /** Indicates if this role is a system role or a user defined role. */
    private boolean m_systemRole;

    /**
     * Creates a user defined role.<p>
     * 
     * @param roleName the name of this role
     * @param groupName the name of the group the members of this role are stored in
     * @param parentRole the parent role of this role
     * @param ouDependent if the role is organizational unit dependent
     */
    public CmsRole(String roleName, CmsRole parentRole, String groupName, boolean ouDependent) {

        this(roleName, parentRole, groupName);
        m_ouDependent = ouDependent;
        m_systemRole = false;
        initialize();
    }

    /**
     * Creates a system role.<p>
     * 
     * @param roleName the name of this role
     * @param parentRole the parent role of this role
     * @param groupName the related group name
     */
    private CmsRole(String roleName, CmsRole parentRole, String groupName) {

        m_roleName = roleName;
        m_groupName = groupName;
        m_parentRole = parentRole;
        m_systemRole = true;
        m_ouDependent = !groupName.startsWith(CmsOrganizationalUnit.SEPARATOR);
        if (parentRole != null) {
            parentRole.m_childs.add(this);
        }
    }

    /**
     * Initializes the system roles with the configured OpenCms system group names.<p>
     */
    static {

        ROOT_ADMIN = new CmsRole("ROOT_ADMIN", null, "/RoleRootAdmins");
        WORKPLACE_MANAGER = new CmsRole("WORKPLACE_MANAGER", CmsRole.ROOT_ADMIN, "/RoleWorkplaceManager");
        DATABASE_MANAGER = new CmsRole("DATABASE_MANAGER", CmsRole.ROOT_ADMIN, "/RoleDatabaseManager");

        ADMINISTRATOR = new CmsRole("ADMINISTRATOR", CmsRole.ROOT_ADMIN, "RoleAdministrators");
        PROJECT_MANAGER = new CmsRole("PROJECT_MANAGER", CmsRole.ADMINISTRATOR, "RoleProjectmanagers");
        ACCOUNT_MANAGER = new CmsRole("ACCOUNT_MANAGER", CmsRole.ADMINISTRATOR, "RoleAccountManagers");
        VFS_MANAGER = new CmsRole("VFS_MANAGER", CmsRole.ADMINISTRATOR, "RoleVfsManagers");
        DEVELOPER = new CmsRole("DEVELOPER", CmsRole.VFS_MANAGER, "RoleDevelopers");
        WORKPLACE_USER = new CmsRole("WORKPLACE_USER", CmsRole.ADMINISTRATOR, "RoleWorkplaceUsers");
        DIRECT_EDIT_USER = new CmsRole("DIRECT_EDIT_USER", CmsRole.WORKPLACE_USER, "RoleDirectEditUsers");

        // create a lookup list for the system roles
        SYSTEM_ROLES = Collections.unmodifiableList(Arrays.asList(new CmsRole[] {
            ROOT_ADMIN,
            WORKPLACE_MANAGER,
            DATABASE_MANAGER,
            ADMINISTRATOR,
            PROJECT_MANAGER,
            ACCOUNT_MANAGER,
            VFS_MANAGER,
            DEVELOPER,
            WORKPLACE_USER,
            DIRECT_EDIT_USER}));

        // now initilaize all system roles
        for (int i = 0; i < SYSTEM_ROLES.size(); i++) {
            ((CmsRole)SYSTEM_ROLES.get(i)).initialize();
        }
    }

    /**
     * Returns the list of system defined roles (instances of <code>{@link CmsRole}</code>).<p> 
     * 
     * @return the list of system defined roles
     */
    public static List getSystemRoles() {

        return SYSTEM_ROLES;
    }

    /**
     * Returns the role which is represented by the given virtual group flags.<p>
     * 
     * @param flags group flags
     * 
     * @return the role which is represented
     */
    public static CmsRole valueOf(int flags) {

        int index = (flags & (I_CmsPrincipal.FLAG_CORE_LIMIT - 1));
        index = index / (I_CmsPrincipal.FLAG_GROUP_VIRTUAL * 2);
        return (CmsRole)getSystemRoles().get(index);
    }

    /**
     * Returns the role for the given group name.<p>
     * 
     * @param groupName a group name to check for role representation
     * 
     * @return the role for the given group name
     */
    public static CmsRole valueOf(String groupName) {

        Iterator it = SYSTEM_ROLES.iterator();
        while (it.hasNext()) {
            CmsRole role = (CmsRole)it.next();
            // direct check
            if (groupName.equals(role.getGroupName())) {
                return role;
            }
            if (role.isOrganizationalUnitIndependent()) {
                // the role group name starts with "/", but the given group name not
                if (groupName.equals(role.getGroupName().substring(1))) {
                    return role;
                }
            } else {
                // the role group name does not start with "/", but the given group name does 
                if (groupName.endsWith(CmsOrganizationalUnit.SEPARATOR + role.getGroupName())) {
                    return role;
                }
            }
        }
        return null;
    }

    /**
     * Returns a role violation exception configured with a localized, role specific message 
     * for this role.<p>
     * 
     * @param requestContext the current users OpenCms request context
     * 
     * @return a role violation exception configured with a localized, role specific message 
     *      for this role
     */
    public CmsRoleViolationException createRoleViolationException(CmsRequestContext requestContext) {

        return new CmsRoleViolationException(Messages.get().container(
            Messages.ERR_USER_NOT_IN_ROLE_2,
            requestContext.currentUser().getName(),
            getName(requestContext.getLocale())));
    }

    /**
     * Returns a role violation exception configured with a localized, role specific message 
     * for this role.<p>
     * 
     * @param requestContext the current users OpenCms request context
     * @param orgUnitFqn the organizational unit used for the role check, it may be <code>null</code>
     * 
     * @return a role violation exception configured with a localized, role specific message 
     *      for this role
     */
    public CmsRoleViolationException createRoleViolationExceptionForOrgUnit(
        CmsRequestContext requestContext,
        String orgUnitFqn) {

        return new CmsRoleViolationException(Messages.get().container(
            Messages.ERR_USER_NOT_IN_ROLE_FOR_ORGUNIT_3,
            requestContext.currentUser().getName(),
            getName(requestContext.getLocale()),
            orgUnitFqn));
    }

    /**
     * Returns a role violation exception configured with a localized, role specific message 
     * for this role.<p>
     * 
     * @param requestContext the current users OpenCms request context
     * @param resource the resource used for the role check, it may be <code>null</code>
     * 
     * @return a role violation exception configured with a localized, role specific message 
     *      for this role
     */
    public CmsRoleViolationException createRoleViolationExceptionForResource(
        CmsRequestContext requestContext,
        CmsResource resource) {

        return new CmsRoleViolationException(Messages.get().container(
            Messages.ERR_USER_NOT_IN_ROLE_FOR_RESOURCE_3,
            requestContext.currentUser().getName(),
            getName(requestContext.getLocale()),
            requestContext.removeSiteRoot(resource.getRootPath())));
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (obj instanceof CmsRole) {
            return m_roleName.equals(((CmsRole)obj).m_roleName);
        }
        return false;
    }

    /**
     * Returns a list of all sub roles.<p>
     * 
     * @param recursive if not set just direct childs are returned
     * 
     * @return all sub roles as a list of {@link CmsRole} objects
     */
    public List getChilds(boolean recursive) {

        List childs = new ArrayList();
        Iterator itChilds = m_childs.iterator();
        while (itChilds.hasNext()) {
            CmsRole child = (CmsRole)itChilds.next();
            childs.add(child);
            if (recursive) {
                childs.addAll(child.getChilds(true));
            }
        }
        return childs;
    }

    /**
     * Returns a localized role description.<p>
     * 
     * @param locale the locale
     * 
     * @return the localized role description
     */
    public String getDescription(Locale locale) {

        if (m_systemRole) {
            // localize role names for system roles
            return Messages.get().getBundle(locale).key("GUI_ROLE_DESCRIPTION_" + m_roleName + "_0");
        } else {
            return getName(locale);
        }
    }

    /**
     * Returns the distinct group names of this role.<p>
     * 
     * @return the distinct group names of this role
     */
    public List getDistinctGroupNames() {

        return m_distictGroupNames;
    }

    /**
     * Returns the name of the group this role is mapped to in the OpenCms database.<p>
     * 
     * @return the name of the group this role is mapped to in the OpenCms database
     */
    public String getGroupName() {

        return m_groupName;
    }

    /**
     * Returns the group name of this role in the given organizational unit.<p>
     * 
     * @param ouFqn the organizational unit to get the group name for
     * 
     * @return the group name of this role in the given organizational unit
     */
    public String getGroupName(String ouFqn) {

        if (isOrganizationalUnitIndependent()) {
            return getGroupName();
        }
        return ouFqn + getGroupName();
    }

    /**
     * Returns a localized role name.<p>
     * 
     * @param locale the locale
     * 
     * @return the localized role name
     */
    public String getName(Locale locale) {

        if (m_systemRole) {
            // localize role names for system roles
            return Messages.get().getBundle(locale).key("GUI_ROLENAME_" + m_roleName + "_0");
        } else {
            return getRoleName();
        }
    }

    /**
     * Returns the parent role of this role.<p>
     *
     * @return the parent role of this role
     */
    public CmsRole getParentRole() {

        return m_parentRole;
    }

    /**
     * Returns the name of the role.<p>
     * 
     * @return the name of the role
     */
    public String getRoleName() {

        return m_roleName;
    }

    /**
     * Returns the flags needed for a group to emulate this role.<p>
     * 
     * @return the flags needed for a group to emulate this role
     */
    public int getVirtualGroupFlags() {

        int flags = I_CmsPrincipal.FLAG_GROUP_VIRTUAL;
        flags += I_CmsPrincipal.FLAG_GROUP_VIRTUAL * 2 * getSystemRoles().indexOf(this);
        return flags;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        return m_roleName.hashCode();
    }

    /**
     * Checks if this role is organizational unit independent.<p>
     * 
     * @return <code>true</code> if this role is organizational unit independent
     */
    public boolean isOrganizationalUnitIndependent() {

        return !m_ouDependent;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer result = new StringBuffer();

        result.append("[");
        result.append(this.getClass().getName());
        result.append(", role: ");
        result.append(getRoleName());
        result.append(", group: ");
        result.append(getGroupName());
        result.append("]");

        return result.toString();
    }

    /**
     * Returns a set of all roles group names.<p>
     * 
     * @return a set of all roles group names
     */
    private Set getAllGroupNames() {

        Set distinctGroups = new HashSet();
        // add role group name
        distinctGroups.add(getGroupName());
        if (getParentRole() != null) {
            // add parent roles group names
            distinctGroups.addAll(getParentRole().getAllGroupNames());
        }
        return distinctGroups;
    }

    /**
     * Initializes this role, creating an optimized data structure for 
     * the lookup of the role group names.<p>
     */
    private void initialize() {

        // calculate the distinct groups of this role
        Set distinctGroups = new HashSet();
        distinctGroups.addAll(getAllGroupNames());
        m_distictGroupNames = Collections.unmodifiableList(new ArrayList(distinctGroups));
    }
}
