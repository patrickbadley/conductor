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
package com.netflix.conductor.contribs.queue.rabbitmq;

import com.rabbitmq.client.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.netflix.conductor.metrics.Monitors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observable.OnSubscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Badley
 *
 */
public class RabbitMqObservableQueue implements ObservableQueue {

	private static Logger logger = LoggerFactory.getLogger(RabbitMqObservableQueue.class);

	private static final String QUEUE_TYPE = "rabbitmq";

	private String queueName;

	private RabbitMqObservableQueue(String queueName)
	{
		this.queueName = queueName;
	}

	@Override
	public Observable<Message> observe() {
		return Observable.empty();
	}

	@Override
	public List<String> ack(List<Message> messages) {
		return new ArrayList<String>();
	}

	@Override
	public void publish(List<Message> messages) {
		for (int i=0; i<messages.size(); i++){
			logger.info("Publishing to rabbitmq");
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("rabbitmq");
			try {
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();

				channel.queueDeclare(queueName, false, false, false, null);
				channel.basicPublish("", queueName, null, messages.get(i).getPayload().getBytes("UTF-8"));
				System.out.println(" [x] Sent '" + messages.get(i).getId() + "' to queue: " + queueName);

				channel.close();
				connection.close();
			}
			catch(Exception e){
				logger.error("Error connecting to rabbitmq", e);
			}
		}
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public void setUnackTimeout(Message message, long unackTimeout) {
	}

	@Override
	public String getType() {
		return QUEUE_TYPE;
	}

	@Override
	public String getName() {
		return queueName;
	}

	@Override
	public String getURI() {
		return queueName;
	}

	public static class Builder {

		private String queueName;

		public Builder withQueueName(String queueName) {
			this.queueName = queueName;
			return this;
		}

		public RabbitMqObservableQueue build() {
			return new RabbitMqObservableQueue(queueName);
		}
	}
}
