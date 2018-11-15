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
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class SwarmDiscoveryStrategyTest
    extends HazelcastTestSupport
{
  private SwarmUtil swarmUtil = mock(SwarmUtil.class);

  private SwarmDiscoveryStrategy underTest = new SwarmDiscoveryStrategy(Collections.emptyMap(), swarmUtil);

  @Test
  public void discoverNodes() throws Exception {
    // given
    InetAddress localhost = InetAddress.getLocalHost();
    Address localhostAddress = new Address(localhost, 5701);
    given(swarmUtil.resolveServiceName(any(), any())).willReturn(Stream.of(localhost));

    // when
    List<DiscoveryNode> discoveryNodes = stream(underTest.discoverNodes().spliterator(), false)
        .collect(toList());

    // then
    assertThat(discoveryNodes, hasSize(1));

    DiscoveryNode discoveryNode = discoveryNodes.get(0);

    assertThat(discoveryNode.getPublicAddress(), is(localhostAddress));
    assertThat(discoveryNode.getPrivateAddress(), is(localhostAddress));
  }
}
