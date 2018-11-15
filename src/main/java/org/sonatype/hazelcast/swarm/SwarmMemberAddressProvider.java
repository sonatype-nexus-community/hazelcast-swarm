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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.MemberAddressProvider;

import static java.util.Collections.list;
import static java.util.stream.Collectors.toSet;
import static org.sonatype.hazelcast.swarm.SwarmUtil.resolveServiceName;

public class SwarmMemberAddressProvider
    implements MemberAddressProvider
{
  private static final ILogger logger = Logger.getLogger(SwarmMemberAddressProvider.class);

  private Properties properties;

  private InetSocketAddress bindAddress;

  public SwarmMemberAddressProvider(final Properties properties) {
    Objects.requireNonNull(properties);
    String serviceName = properties.getProperty(SwarmProperties.SERVICE_NAME.key(), "localhost");
    int servicePort = Integer.parseInt(properties.getProperty(SwarmProperties.SERVICE_PORT.key(), "0"));

    Set<InetAddress> potentialInetAddresses = resolveServiceName(serviceName, logger).collect(toSet());

    try {
      InetAddress address = list(NetworkInterface.getNetworkInterfaces()).stream()
          .flatMap(i -> list(i.getInetAddresses()).stream())
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
    return bindAddress;
  }
}
