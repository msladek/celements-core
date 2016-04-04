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
package com.celements.configuration;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.configuration.internal.CompositeConfigurationSource;

/**
 * Composite Configuration Source that looks in the following sources in that order:
 * <ul>
 * <li>user preferences wiki page</li>
 * <li>space preferences wiki page</li>
 * <li>wiki preferences wiki page</li>
 * <li>celements properties file (celements.properties)</li>
 * <li>xwiki properties file (xwiki.properties)</li>
 * </ul>
 * Should be used when a configuration can be overriden by the user in his/her profile.
 */
@Component("all")
public class CelementsAllConfigurationSource extends CompositeConfigurationSource
    implements Initializable {

  @Requirement("xwikiproperties")
  private ConfigurationSource xwikiPropertiesSource;

  @Requirement("celementsproperties")
  private ConfigurationSource celementsPropertiesSource;

  @Requirement("wiki")
  private ConfigurationSource wikiPreferencesSource;

  @Requirement("space")
  private ConfigurationSource spacePreferencesSource;

  @Requirement("user")
  private ConfigurationSource userPreferencesSource;

  @Override
  public void initialize() throws InitializationException {
    // First source is looked first when a property value is requested.
    addConfigurationSource(this.userPreferencesSource);
    addConfigurationSource(this.spacePreferencesSource);
    addConfigurationSource(this.wikiPreferencesSource);
    addConfigurationSource(this.celementsPropertiesSource);
    addConfigurationSource(this.xwikiPropertiesSource);
  }

}
