/**
 * Copyright 2017 Netflix, Inc.
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
package com.netflix.conductor.core.events.rabbitmq;

import com.netflix.conductor.contribs.queue.rabbitmq.RabbitMqObservableQueue;
import com.netflix.conductor.contribs.queue.rabbitmq.RabbitMqObservableQueue.Builder;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.core.events.EventQueueProvider;
import com.netflix.conductor.core.events.queue.ObservableQueue;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Patrick Badley
 *
 */
@Singleton
public class RabbitMqEventQueueProvider implements EventQueueProvider {

	private final Map<String, ObservableQueue> queues = new ConcurrentHashMap<>();

	@Inject
	public RabbitMqEventQueueProvider(Configuration config) {
	}

	@Override
	public ObservableQueue getQueue(String queueURI) {
		return queues.computeIfAbsent(queueURI, q -> {
			Builder builder = new RabbitMqObservableQueue.Builder();
			return builder
					.withQueueName(queueURI)
					.build();
		});
	}
}
