/*
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.netflix.archaius.guice.ArchaiusModule;
import com.netflix.conductor.common.utils.JsonMapperProvider;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.core.config.CoreModule;
import com.netflix.conductor.dyno.SystemPropertiesDynomiteConfiguration;
import com.netflix.conductor.grpc.server.GRPCModule;
import com.netflix.conductor.jetty.server.JettyModule;
import com.netflix.runtime.health.guice.HealthModule;
import com.netflix.conductor.contribs.ContribsModule;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Viren
 */
public class ServerModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(ContribsModule.class);

    @Override
    protected void configure() {
        install(new CoreModule());
        install(new ArchaiusModule());
        install(new HealthModule());
        install(new JettyModule());
        install(new GRPCModule());
        install(new ContribsModule());

		logger.info("Finished ContribsModule");

        bind(ObjectMapper.class).toProvider(JsonMapperProvider.class);
        bind(Configuration.class).to(SystemPropertiesDynomiteConfiguration.class);
        bind(ExecutorService.class).toProvider(ExecutorServiceProvider.class).in(Scopes.SINGLETON);
    }
}
