/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.batch.metrics;

import org.slf4j.MDC;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;

import de.codecentric.batch.listener.LoggingListener;

/**
 * See {@link BatchMetrics} for documentation.
 * 
 * @author Tobias Flohre
 */
public class BatchMetricsImpl implements BatchMetrics {
	
	private CounterService counterService;
	private GaugeService gaugeService;
	private CounterService transactionAwareCounterService;
	private GaugeService transactionAwareGaugeService;
	
	public BatchMetricsImpl(CounterService counterService,
			GaugeService gaugeService) {
		this.counterService = counterService;
		this.gaugeService = gaugeService;
		this.transactionAwareCounterService = new TransactionAwareCounterService(counterService);
		this.transactionAwareGaugeService = new TransactionAwareGaugeService(gaugeService);
	}

	@Override
	public void increment(String metricName) {
		transactionAwareCounterService.increment(wrap(metricName));
	}

	@Override
	public void decrement(String metricName) {
		transactionAwareCounterService.decrement(wrap(metricName));
	}

	@Override
	public void reset(String metricName) {
		transactionAwareCounterService.reset(wrap(metricName));
	}

	@Override
	public void submit(String metricName, double value) {
		transactionAwareGaugeService.submit(wrap(metricName), value);
	}

	@Override
	public void incrementNonTransactional(String metricName) {
		counterService.increment(wrap(metricName));
	}

	@Override
	public void decrementNonTransactional(String metricName) {
		counterService.decrement(wrap(metricName));
	}

	@Override
	public void resetNonTransactional(String metricName) {
		counterService.reset(wrap(metricName));
	}

	@Override
	public void submitNonTransactional(String metricName, double value) {
		gaugeService.submit(wrap(metricName), value);
	}
	
	private String wrap(String metricName) {
		return "batch." + MDC.get(LoggingListener.STEP_EXECUTION_IDENTIFIER) + "." + metricName;
	}

}
