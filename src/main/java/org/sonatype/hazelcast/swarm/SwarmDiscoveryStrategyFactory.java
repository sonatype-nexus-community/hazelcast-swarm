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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;

public class SwarmDiscoveryStrategyFactory
    implements DiscoveryStrategyFactory
{
  @Override
  public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
    return SwarmDiscoveryStrategy.class;
  }

  @Override
  public DiscoveryStrategy newDiscoveryStrategy(final DiscoveryNode discoveryNode,
                                                final ILogger logger,
                                                final Map<String, Comparable> properties)
  {
    return new SwarmDiscoveryStrategy(properties);
  }

  @Override
  public Collection<PropertyDefinition> getConfigurationProperties() {
    return Arrays.stream(SwarmProperties.values())
        .map(SwarmProperties::getDefinition)
        .collect(Collectors.toSet());
  }
}
