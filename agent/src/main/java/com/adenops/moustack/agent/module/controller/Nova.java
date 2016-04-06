/**
 * Copyright (C) 2016 Adenops Consultants Informatique Inc.
 *
 * This file is part of the Moustack project, see http://www.moustack.org for
 * more information.
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

package com.adenops.moustack.agent.module.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenops.moustack.agent.DeploymentException;
import com.adenops.moustack.agent.client.Clients;
import com.adenops.moustack.agent.config.StackConfig;
import com.adenops.moustack.agent.config.StackProperty;
import com.adenops.moustack.agent.model.docker.Volume;
import com.adenops.moustack.agent.module.ContainerModule;
import com.github.dockerjava.api.model.Capability;

public class Nova extends ContainerModule {
	private static final Logger log = LoggerFactory.getLogger(Nova.class);

	public Nova(String name, String image, List<String> files, List<String> environments, List<Volume> volumes,
			List<Capability> capabilities, boolean privileged, List<String> devices, boolean syslog) {
		super(name, image, files, environments, volumes, capabilities, privileged, devices, syslog);
	}

	@Override
	public boolean deploy(StackConfig stack) throws DeploymentException {
		boolean changed = false;
		changed |= Clients.getKeystoneClient().createService(stack, "nova", "OpenStack Compute service", "compute",
				"http://%s:8774/v2/%%(tenant_id)s", "http://%s:8774/v2/%%(tenant_id)s",
				"http://%s:8774/v2/%%(tenant_id)s");
		changed |= Clients.getKeystoneClient().createProjectUser(stack, StackProperty.KS_NOVA_USER, "Nova user",
				"nova@localhost", StackProperty.KS_NOVA_PASSWORD, StackProperty.KEYSTONE_SERVICES_PROJECT);
		changed |= Clients.getKeystoneClient().grantProjectRole(stack, StackProperty.KS_NOVA_USER,
				StackProperty.KEYSTONE_SERVICES_PROJECT, StackProperty.KEYSTONE_ADMIN_ROLE);

		changed |= Clients.getMySQLClient().createDatabaseUser("nova", "nova",
				stack.get(StackProperty.DB_NOVA_PASSWORD));

		changed |= deployConfig(stack);

		if (changed) {
			Clients.getDockerClient().stopContainer(this);
			log.info("running nova DB migration");
			Clients.getDockerClient().startEphemeralContainer(this, "nova", "nova-manage", "db sync");
			Clients.getDockerClient().startOrRestartContainer(this);
		}

		return changed;
	}
}
