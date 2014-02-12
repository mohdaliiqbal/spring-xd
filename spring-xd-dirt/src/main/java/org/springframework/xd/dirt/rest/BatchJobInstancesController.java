/*
 * Copyright 2013-2014 the original author or authors.
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

package org.springframework.xd.dirt.rest;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.xd.dirt.job.JobInstanceInfo;
import org.springframework.xd.dirt.job.NoSuchBatchJobException;
import org.springframework.xd.dirt.job.NoSuchBatchJobInstanceException;
import org.springframework.xd.rest.client.domain.JobInstanceInfoResource;

/**
 * Controller for batch job instances.
 * 
 * @author Ilayaperumal Gopinathan
 */
@RestController
@RequestMapping("/batch/instances")
@ExposesResourceFor(JobInstanceInfoResource.class)
public class BatchJobInstancesController extends AbstractBatchJobsController {

	/**
	 * Return job instance info by the given instance id.
	 * 
	 * @param instanceId job instance id
	 * @return job instance info
	 */
	@RequestMapping(value = "/{instanceId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public JobInstanceInfoResource getJobInstance(@PathVariable long instanceId) {
		try {
			JobInstance jobInstance = jobService.getJobInstance(instanceId);
			String jobName = jobInstance.getJobName();

			try {
				List<JobExecution> jobExecutions = (List<JobExecution>) jobService.getJobExecutionsForJobInstance(
						jobInstance.getJobName(), jobInstance.getId());
				return jobInstanceInfoResourceAssembler.toResource(new JobInstanceInfo(jobInstance, jobExecutions));
			}
			catch (NoSuchJobException e) {
				throw new NoSuchBatchJobException(jobName);
			}
		}
		catch (NoSuchJobInstanceException e) {
			throw new NoSuchBatchJobInstanceException(instanceId);
		}
	}
}
