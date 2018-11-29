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

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.impl.DefaultDiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceSettings;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class SwarmDiscoveryStrategyFactoryTest
    extends HazelcastTestSupport
{
  private static DiscoveryStrategy createStrategy(final Map<String, Comparable> props) {
    final SwarmDiscoveryStrategyFactory factory = new SwarmDiscoveryStrategyFactory();
    return factory.newDiscoveryStrategy(null, null, props);
  }

  private static Config createConfig(final String xmlFileName) {
    final InputStream xmlResource = SwarmDiscoveryStrategyFactoryTest.class.getClassLoader().getResourceAsStream(xmlFileName);
    assertThat(xmlResource, notNullValue());
    return new XmlConfigBuilder(xmlResource).build();
  }

  @Test
  public void testMinimalOk() {
    final Map<String, Comparable> props = new HashMap<>();
    createStrategy(props);
  }

  @Test
  public void testFull() {
    final Map<String, Comparable> props = new HashMap<>();
    props.put("service-name", "test-value");
    props.put("service-port", 1234);
    createStrategy(props);
  }

  @Test(expected = ClassCastException.class)
  public void testPortWrongType() {
    final Map<String, Comparable> props = new HashMap<>();
    props.put("service-name", "test-value");
    props.put("service-port", "1234");
    createStrategy(props);
  }

  @Test
  public void parseAndCreateDiscoveryStrategyPasses() {
    final Config config = createConfig("test-swarm-config.xml");
    validateConfig(config);
  }

  private void validateConfig(final Config config) {
    final DiscoveryConfig discoveryConfig = config.getNetworkConfig().getJoin().getDiscoveryConfig();
    final DiscoveryServiceSettings settings = new DiscoveryServiceSettings().setDiscoveryConfig(discoveryConfig);
    final DefaultDiscoveryService service = new DefaultDiscoveryService(settings);
    final Iterator<DiscoveryStrategy> strategies = service.getDiscoveryStrategies().iterator();

    assertTrue(strategies.hasNext());
    final DiscoveryStrategy strategy = strategies.next();
    assertTrue(strategy instanceof SwarmDiscoveryStrategy);
  }

  @Test
  public void parseDiscoveryStrategyConfigPasses() {
    final SwarmDiscoveryStrategyFactory factory = new SwarmDiscoveryStrategyFactory();
    final Config config = createConfig("test-swarm-config.xml");
    final JoinConfig joinConfig = config.getNetworkConfig().getJoin();

    assertThat(joinConfig.getAwsConfig().isEnabled(), is(false));
    assertThat(joinConfig.getTcpIpConfig().isEnabled(), is(false));
    assertThat(joinConfig.getMulticastConfig().isEnabled(), is(false));

    final DiscoveryConfig discoveryConfig = joinConfig.getDiscoveryConfig();

    assertThat(discoveryConfig.isEnabled(), is(true));
    assertThat(discoveryConfig.getDiscoveryStrategyConfigs(), hasSize(1));

    final DiscoveryStrategyConfig providerConfig = discoveryConfig.getDiscoveryStrategyConfigs().iterator().next();
    final Map<String, Comparable> providerProperties = providerConfig.getProperties();
    final Collection<PropertyDefinition> factoryConfigProperties = factory.getConfigurationProperties();

    assertThat(factory.getDiscoveryStrategyType().getName(), is(SwarmDiscoveryStrategy.class.getName()));
    assertThat(providerConfig.getClassName(), is(SwarmDiscoveryStrategy.class.getName()));
    assertThat(providerProperties.size(), is(factoryConfigProperties.size()));
    for (SwarmProperties prop : SwarmProperties.values()) {
      assertThat(factoryConfigProperties.contains(prop.getDefinition()), is(true));
    }

    assertThat(providerProperties.get("service-name"), is("nxrm"));
    assertThat(providerProperties.get("service-port"), is("5701"));
  }
}