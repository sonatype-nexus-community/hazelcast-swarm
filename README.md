<!--

    Copyright (c) 2018-present Sonatype, Inc. All rights reserved.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
[![DepShield Badge](https://depshield.sonatype.org/badges/sonatype-nexus-community/hazelcast-swarm/depshield.svg)](https://depshield.github.io)
# Table of Contents

* [Requirements](#requirements)
* [Maven Coordinates](#maven-coordinates)
* [Discovering Members within Docker Swarm](#discovering-members-within-docker-swarm)
  * [Configuring Hazelcast Members with Discovery SPI](#configuring-hazelcast-members-with-discovery-spi)

## Requirements

- Hazelcast 3.9+

## Maven Coordinates
```xml
<dependency>
  <groupId>org.sonatype.hazelcast</groupId>
  <artifactId>hazelcast-swarm</artifactId>
  <version>${hazelcast-swarm.version}</version>
</dependency>
```

## Discovering Members within Docker Swarm

### Configuring Hazelcast Members with Discovery SPI

```xml
    <network>
        <port auto-increment="false">5701</port>
        <member-address-provider enabled="true">
            <class-name>org.sonatype.hazelcast.swarm.SwarmMemberAddressProvider</class-name>
            <properties>
                <property name="service-name">nxrm</property>
            </properties>
        </member-address-provider>
        <join>
            <multicast enabled="false"/>
            <aws enabled="false"/>
            <tcp-ip enabled="false"/>

            <discovery-strategies>
                <discovery-strategy
                    enabled="true"
                    class="org.sonatype.hazelcast.swarm.SwarmDiscoveryStrategy">
                    <properties>
                        <property name="service-name">nxrm</property>
                        <property name="service-port">5701</property>
                    </properties>
                </discovery-strategy>
            </discovery-strategies>
        </join>
    </network>
```
