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
package com.celements.sajson;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Simple Api for JSON The JSON Builder helps do generate a valid JSON expression based on
 * event handling.
 *
 * @author fabian
 * @deprecated instead use {@link JsonBuilder}
 */
@Deprecated
@NotThreadSafe
public class Builder extends JsonBuilder {

  public Builder() {
    super();
  }

  public void addStringProperty(String key, String value) {
    addProperty(key, value);
  }

  public void addStringProperty(String key, Object value) {
    addProperty(key, value);
  }

  public void addString(String value) {
    addValue(value);
  }

  public void addString(Object value) {
    addValue(value);
  }

  public void addBoolean(Boolean value) {
    addValue(value);
  }

  public void addNumber(Number value) {
    addValue(value);
  }

  /**
   * @deprecated instead use {@link #addNumber(Number)}
   */
  @Deprecated
  public void addFloat(Float value) {
    addNumber(value);
  }

  /**
   * @deprecated instead use {@link #addNumber(Number)}
   */
  @Deprecated
  public void addInteger(Integer value) {
    addNumber(value);
  }

  public void addNull() {
    addValue(null);
  }

}
