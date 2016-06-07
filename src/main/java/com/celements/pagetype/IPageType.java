/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.celements.pagetype;

import java.util.List;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;

public interface IPageType {

  public Document getTemplateDocument() throws XWikiException;

  public String getPageType();

  public String getCategoryString();

  public List<String> getCategories();

  public com.xpn.xwiki.api.Object getPageTypeObject();

  public boolean showFrame() throws XWikiException;

  public com.xpn.xwiki.api.Object getPageTypeProperties() throws XWikiException;

  public String getRenderTemplate(String renderMode) throws XWikiException;

  public boolean hasPageTitle();

  public boolean isVisible();

  public String getPrettyName();

  public String getPageTypeClassFullName();

}
