/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.knox.gateway.topology.discovery.cm.model.hdfs;

import com.cloudera.api.swagger.client.ApiException;
import com.cloudera.api.swagger.model.ApiConfigList;
import com.cloudera.api.swagger.model.ApiRole;
import com.cloudera.api.swagger.model.ApiService;
import com.cloudera.api.swagger.model.ApiServiceConfig;
import org.apache.knox.gateway.topology.discovery.cm.ServiceModel;

import java.util.Locale;

public class HdfsUIServiceModelGenerator extends NameNodeServiceModelGenerator {
  private static final String SERVICE = "HDFSUI";

  @Override
  public ServiceModel generateService(ApiService       service,
                                      ApiServiceConfig serviceConfig,
                                      ApiRole          role,
                                      ApiConfigList    roleConfig) throws ApiException {
    String hostname = role.getHostRef().getHostname();
    String scheme;
    String port;
    boolean sslEnabled = Boolean.parseBoolean(getServiceConfigValue(serviceConfig, "hdfs_hadoop_ssl_enabled"));
    if(sslEnabled) {
      scheme = "https";
      port = getRoleConfigValue(roleConfig, "dfs_https_port");
    } else {
      scheme = "http";
      port = getRoleConfigValue(roleConfig, "dfs_http_port");
    }
    String namenodeUrl = String.format(Locale.getDefault(), "%s://%s:%s", scheme, hostname, port);
    return new ServiceModel(ServiceModel.Type.UI, SERVICE, namenodeUrl);
  }

}
