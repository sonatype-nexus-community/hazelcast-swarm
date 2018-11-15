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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.stream.Stream;

import com.hazelcast.logging.ILogger;

import static java.util.Arrays.stream;
import static java.util.Collections.list;

class SwarmUtil {
  Stream<InetAddress> resolveServiceName(final String serviceName, final ILogger logger) {
    try {
      return stream(InetAddress.getAllByName(serviceName));
    }
    catch (UnknownHostException e) {
      logger.severe("Unable to resolve service name: " + serviceName);
      return Stream.empty();
    }
  }

  Stream<InetAddress> getAvailableAddresses() throws SocketException {
    return list(NetworkInterface.getNetworkInterfaces()).stream()
        .flatMap(i -> list(i.getInetAddresses()).stream());
  }
}
