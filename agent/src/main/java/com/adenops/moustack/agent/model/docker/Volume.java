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

package com.adenops.moustack.agent.model.docker;

import java.util.List;
import java.util.stream.Collectors;

// TODO: we use a String for the mode, it would be better to use an enum
public class Volume {
	private final String hostPath;
	private final String guestPath;
	private final String mode;

	public Volume(String hostPath, String guestPath, String mode) {
		this.hostPath = hostPath;
		this.guestPath = guestPath;
		this.mode = mode;
	}

	public String getHostPath() {
		return hostPath;
	}

	public String getGuestPath() {
		return guestPath;
	}

	public String getMode() {
		return mode;
	}

	public String asString() {
		return String.format("%s:%s:%s", hostPath, guestPath, mode);
	}

	public static List<String> asStringList(List<Volume> volumes) {
		return volumes.stream().map(v -> v.asString()).collect(Collectors.toList());
	}
}
