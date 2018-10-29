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
package org.sonatype.nexus.hazelcast.swarm;

import java.util.Map;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;

import static java.util.stream.Collectors.toSet;
import static org.sonatype.nexus.hazelcast.swarm.SwarmUtil.resolveServiceName;

public class SwarmDiscoveryStrategy
    extends AbstractDiscoveryStrategy
{
  private final String serviceName;

  private final int servicePort;

  public SwarmDiscoveryStrategy(final ILogger logger, final Map<String, Comparable> properties) {
    super(logger, properties);
    serviceName = getOrDefault(SwarmProperties.SERVICE_NAME, "localhost");
    servicePort = getOrDefault(SwarmProperties.SERVICE_PORT, 5701);
  }

  @Override
  public Iterable<DiscoveryNode> discoverNodes() {
    return resolveServiceName(serviceName, getLogger())
        .map(a -> new Address(a, servicePort))
        .map(SimpleDiscoveryNode::new)
        .collect(toSet());
  }
}
