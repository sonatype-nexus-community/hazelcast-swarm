/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.hazelcast.swarm;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;

import static com.hazelcast.config.properties.PropertyTypeConverter.INTEGER;
import static com.hazelcast.config.properties.PropertyTypeConverter.STRING;

public enum SwarmProperties {
  SERVICE_NAME("service-name", STRING),
  SERVICE_PORT("service-port", INTEGER);

  private final PropertyDefinition propertyDefinition;

  SwarmProperties(final String key, final PropertyTypeConverter typeConverter) {
    this.propertyDefinition = new SimplePropertyDefinition(key, typeConverter);
  }

  public PropertyDefinition getDefinition() {
    return propertyDefinition;
  }
}
