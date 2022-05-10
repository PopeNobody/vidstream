/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.parse.controller;

import com.parse.utils.ParseEventuallyQueue;
import com.parse.boltsinternal.Task;
import com.parse.rest.ParseRESTAnalyticsCommand;
import com.parse.rest.ParseRESTCommand;
import cell411.json.JSONObject;

import java.util.Map;

public class ParseAnalyticsController {

  /* package for test */ ParseEventuallyQueue eventuallyQueue;

  public ParseAnalyticsController(ParseEventuallyQueue eventuallyQueue) {
    this.eventuallyQueue = eventuallyQueue;
  }

  public Task<Void> trackEventInBackground(
      final String name, Map<String, String> dimensions, String sessionToken
  )
  {
    ParseRESTCommand command = ParseRESTAnalyticsCommand.trackEventCommand(name, dimensions, sessionToken);

    Task<JSONObject> eventuallyTask = eventuallyQueue.enqueueEventuallyAsync(command, null);
    return eventuallyTask.makeVoid();
  }

  public Task<Void> trackAppOpenedInBackground(String pushHash, String sessionToken) {
    ParseRESTCommand command = ParseRESTAnalyticsCommand.trackAppOpenedCommand(pushHash, sessionToken);

    Task<JSONObject> eventuallyTask = eventuallyQueue.enqueueEventuallyAsync(command, null);
    return eventuallyTask.makeVoid();
  }
}

