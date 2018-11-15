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
import java.util.Properties;
import java.util.stream.Stream;

import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class SwarmMemberAddressProviderTest
    extends HazelcastTestSupport
{
  private SwarmUtil swarmUtil = mock(SwarmUtil.class);

  @Test
  public void getAddresses() throws Exception {
    // given
    InetAddress localhost = InetAddress.getLocalHost();
    InetSocketAddress localhostSocketAddress = new InetSocketAddress(localhost, 0);
    given(swarmUtil.getAvailableAddresses()).willReturn(Stream.of(localhost));
    given(swarmUtil.resolveServiceName(any(), any())).willReturn(Stream.of(localhost));

    // when
    SwarmMemberAddressProvider underTest = new SwarmMemberAddressProvider(new Properties(), swarmUtil);

    // then
    final InetSocketAddress bindAddress = underTest.getBindAddress();
    final InetSocketAddress publicAddress = underTest.getPublicAddress();
    assertThat(bindAddress, is(localhostSocketAddress));
    assertThat(publicAddress, is(localhostSocketAddress));
  }
}
