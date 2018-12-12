/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.netflix.conductor.contribs;

import static com.netflix.conductor.core.events.EventQueues.EVENT_QUEUE_PROVIDERS_QUALIFIER;

import com.google.inject.Singleton;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.ProvidesIntoMap;
import com.google.inject.multibindings.StringMapKey;
import com.google.inject.name.Named;
import com.netflix.conductor.common.metadata.tasks.Task.Status;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.core.events.EventQueueProvider;
import com.netflix.conductor.core.events.rabbitmq.RabbitMqEventQueueProvider;

/**
 * @author Viren
 *
 */
public class RabbitMqModule extends AbstractModule {
	@Override
	protected void configure() {
	}

	@ProvidesIntoMap
	@StringMapKey("rabbitmq")
	@Singleton
	@Named(EVENT_QUEUE_PROVIDERS_QUALIFIER)
	public EventQueueProvider getRabbitMqEventQueueProvider(Configuration config) {
		return new RabbitMqEventQueueProvider(config);
	}
}
