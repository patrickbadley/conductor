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

/**
 * @author Viren
 *
 */
public class RabbitMqObservableQueue implements ObservableQueue {

	private static Logger logger = LoggerFactory.getLogger(RabbitMqObservableQueue.class);

	private static final String QUEUE_TYPE = "rabbitmq";

	private String queueName;

	private RabbitMqObservableQueue(String queueName)//, AmazonRabbitMqClient client, int visibilityTimeoutInSeconds, int batchSize, int pollTimeInMS, List<String> accountsToAuthorize) {
	{
		this.queueName = queueName;
		logger.info(this.getType() + " Created!");
	}

	@Override
	public Observable<Message> observe() {
//		OnSubscribe<Message> subscriber = getOnSubscribe();
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

	// String toJson(Object value) {
    //     try {
    //         return objectMapper.writeValueAsString(value);
    //     } catch (JsonProcessingException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

	@Override
	public long size() {
		// GetQueueAttributesResult attributes = client.getQueueAttributes(queueURL, Collections.singletonList("ApproximateNumberOfMessages"));
		// String sizeAsStr = attributes.getAttributes().get("ApproximateNumberOfMessages");
		// try {
		// 	return Long.parseLong(sizeAsStr);
		// } catch(Exception e) {
		// 	return -1;
		// }
		return 0;
	}

	@Override
	public void setUnackTimeout(Message message, long unackTimeout) {
		// int unackTimeoutInSeconds = (int) (unackTimeout / 1000);
		// ChangeMessageVisibilityRequest request = new ChangeMessageVisibilityRequest(queueURL, message.getReceipt(), unackTimeoutInSeconds);
		// client.changeMessageVisibility(request);
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

		private int visibilityTimeout = 30;	//seconds

		private int batchSize = 5;

		private int pollTimeInMS = 100;

//		private AmazonRabbitMqClient client;

		private List<String> accountsToAuthorize = new LinkedList<>();

		public Builder withQueueName(String queueName) {
			this.queueName = queueName;
			return this;
		}

		public RabbitMqObservableQueue build() {
			return new RabbitMqObservableQueue(queueName);
		}
	}

	// //Private methods
	// @VisibleForTesting
	// String getOrCreateQueue() {
    //     List<String> queueUrls = listQueues(queueName);
	// 	if (queueUrls == null || queueUrls.isEmpty()) {
    //         CreateQueueRequest createQueueRequest = new CreateQueueRequest().withQueueName(queueName);
    //         CreateQueueResult result = client.createQueue(createQueueRequest);
    //         return result.getQueueUrl();
	// 	} else {
    //         return queueUrls.get(0);
    //     }
    // }

	// private String getQueueARN() {
	// 	GetQueueAttributesResult response = client.getQueueAttributes(queueURL, Collections.singletonList("QueueArn"));
	// 	return response.getAttributes().get("QueueArn");
	// }

	// private void addPolicy(List<String> accountsToAuthorize) {
	// 	if(accountsToAuthorize == null || accountsToAuthorize.isEmpty()) {
	// 		logger.info("No additional security policies attached for the queue " + queueName);
	// 		return;
	// 	}
	// 	logger.info("Authorizing " + accountsToAuthorize + " to the queue " + queueName);
	// 	Map<String, String> attributes = new HashMap<>();
	// 	attributes.put("Policy", getPolicy(accountsToAuthorize));
	// 	SetQueueAttributesResult result = client.setQueueAttributes(queueURL, attributes);
	// 	logger.info("policy attachment result: " + result);
	// 	logger.info("policy attachment result: status=" + result.getSdkHttpMetadata().getHttpStatusCode());
	// }

	// private String getPolicy(List<String> accountIds) {
	// 	Policy policy = new Policy("AuthorizedWorkerAccessPolicy");
	// 	Statement stmt = new Statement(Effect.Allow);
	// 	Action action = RabbitMqActions.SendMessage;
	// 	stmt.getActions().add(action);
	// 	stmt.setResources(new LinkedList<>());
	// 	for(String accountId : accountIds) {
	// 		Principal principal = new Principal(accountId);
	// 		stmt.getPrincipals().add(principal);
	// 	}
	// 	stmt.getResources().add(new Resource(getQueueARN()));
	// 	policy.getStatements().add(stmt);
	// 	return policy.toJson();
	// }

	// private List<String> listQueues(String queueName) {
    //     ListQueuesRequest listQueuesRequest = new ListQueuesRequest().withQueueNamePrefix(queueName);
    //     ListQueuesResult resultList = client.listQueues(listQueuesRequest);
    //     return resultList.getQueueUrls().stream()
	// 			.filter(queueUrl -> queueUrl.contains(queueName))
	// 			.collect(Collectors.toList());
    // }

	// private void publishMessages(List<Message> messages) {
	// 	logger.info("Sending {} messages to the RabbitMq queue: {}", messages.size(), queueName);
	// 	SendMessageBatchRequest batch = new SendMessageBatchRequest(queueURL);
	// 	messages.forEach(msg -> {
	// 		SendMessageBatchRequestEntry sendr = new SendMessageBatchRequestEntry(msg.getId(), msg.getPayload());
	// 		batch.getEntries().add(sendr);
	// 	});
	// 	logger.info("sending {} messages in batch", batch.getEntries().size());
	// 	SendMessageBatchResult result = client.sendMessageBatch(batch);
	// 	logger.info("send result: {} for RabbitMq queue: {}", result.getFailed().toString(), queueName);
	// }

	// @VisibleForTesting
	// List<Message> receiveMessages() {
	// 	try {
	// 		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
	// 				.withQueueUrl(queueURL)
	// 				.withVisibilityTimeout(visibilityTimeoutInSeconds)
	// 				.withMaxNumberOfMessages(batchSize);

	// 		ReceiveMessageResult result = client.receiveMessage(receiveMessageRequest);

	// 		List<Message> messages = result.getMessages().stream()
	// 				.map(msg -> new Message(msg.getMessageId(), msg.getBody(), msg.getReceiptHandle()))
	// 				.collect(Collectors.toList());
	// 		Monitors.recordEventQueueMessagesProcessed(QUEUE_TYPE, this.queueName, messages.size());
	// 		return messages;
	// 	} catch (Exception e) {
	// 		logger.error("Exception while getting messages from RabbitMq", e);
	// 		Monitors.recordObservableQMessageReceivedErrors(QUEUE_TYPE);
	// 	}
	// 	return new ArrayList<>();
	// }

	// @VisibleForTesting
	// OnSubscribe<Message> getOnSubscribe() {
	// 	return subscriber -> {
	// 		Observable<Long> interval = Observable.interval(pollTimeInMS, TimeUnit.MILLISECONDS);
	// 		interval.flatMap((Long x)->{
	// 			List<Message> msgs = receiveMessages();
	// 	        return Observable.from(msgs);
	// 		}).subscribe(subscriber::onNext, subscriber::onError);
	// 	};
	// }

	// private List<String> delete(List<Message> messages) {
	// 	if (messages == null || messages.isEmpty()) {
    //         return null;
    //     }

    //     DeleteMessageBatchRequest batch = new DeleteMessageBatchRequest().withQueueUrl(queueURL);
    // 	List<DeleteMessageBatchRequestEntry> entries = batch.getEntries();

    //     messages.forEach(m -> entries.add(new DeleteMessageBatchRequestEntry().withId(m.getId()).withReceiptHandle(m.getReceipt())));

    //     DeleteMessageBatchResult result = client.deleteMessageBatch(batch);
    //     List<String> failures = result.getFailed().stream()
	// 			.map(BatchResultErrorEntry::getId)
	// 			.collect(Collectors.toList());
	// 	logger.debug("Failed to delete messages from queue: {}: {}", queueName, failures);
    //     return failures;
    // }
}
