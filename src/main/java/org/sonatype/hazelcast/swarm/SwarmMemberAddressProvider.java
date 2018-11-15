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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.MemberAddressProvider;

import static java.util.stream.Collectors.toSet;
import static org.sonatype.hazelcast.swarm.SwarmProperties.SERVICE_NAME;
import static org.sonatype.hazelcast.swarm.SwarmProperties.SERVICE_PORT;

public class SwarmMemberAddressProvider
    implements MemberAddressProvider
{
  private static final ILogger logger = Logger.getLogger(SwarmMemberAddressProvider.class);

  private final SwarmUtil swarmUtil;

  private InetSocketAddress bindAddress;

  public SwarmMemberAddressProvider(final Properties properties) {
    this(properties, new SwarmUtil());
  }

  // available for testing
  SwarmMemberAddressProvider(final Properties properties, final SwarmUtil swarmUtil) {
    this.swarmUtil = swarmUtil;
    init(properties);
  }

  private void init(final Properties properties) {
    Objects.requireNonNull(properties);
    String serviceName = properties.getProperty(SERVICE_NAME.getDefinition().key(), "localhost");
    int servicePort = Integer.parseInt(properties.getProperty(SERVICE_PORT.getDefinition().key(), "0"));

    Set<InetAddress> potentialInetAddresses = swarmUtil.resolveServiceName(serviceName, logger).collect(toSet());

    try {
      InetAddress address = swarmUtil.getAvailableAddresses()
          .filter(potentialInetAddresses::contains)
          .findFirst()
          .orElse(null);
      bindAddress = new InetSocketAddress(address, servicePort);
    }
    catch (SocketException e) {
      logger.severe("Unable to bind socket, service-name: " + serviceName + ", service-port: " + servicePort, e);
    }
  }

  @Override
  public InetSocketAddress getBindAddress() {
    return bindAddress;
  }

  @Override
  public InetSocketAddress getPublicAddress() {
    return getBindAddress();
  }
}
