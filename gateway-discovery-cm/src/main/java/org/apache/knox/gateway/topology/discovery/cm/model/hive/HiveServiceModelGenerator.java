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
package org.apache.knox.gateway.topology.discovery.cm.model.hive;

import com.cloudera.api.swagger.client.ApiException;
import com.cloudera.api.swagger.model.ApiConfigList;
import com.cloudera.api.swagger.model.ApiRole;
import com.cloudera.api.swagger.model.ApiService;
import com.cloudera.api.swagger.model.ApiServiceConfig;
import org.apache.knox.gateway.topology.discovery.cm.ServiceModel;
import org.apache.knox.gateway.topology.discovery.cm.model.AbstractServiceModelGenerator;

import java.util.Locale;

public class HiveServiceModelGenerator extends AbstractServiceModelGenerator {

  private static final String SERVICE      = "HIVE";
  private static final String SERVICE_TYPE = "HIVE";
  private static final String ROLE_TYPE    = "HIVESERVER2";

  @Override
  public boolean handles(ApiService service, ApiServiceConfig serviceConfig, ApiRole role, ApiConfigList roleConfig) {
    return SERVICE_TYPE.equals(service.getType()) && ROLE_TYPE.equals(role.getType()) && checkHiveServer2HTTPMode(roleConfig);
  }

  @Override
  public ServiceModel generateService(ApiService       service,
                                      ApiServiceConfig serviceConfig,
                                      ApiRole          role,
                                      ApiConfigList    roleConfig) throws ApiException {
    String hostname = role.getHostRef().getHostname();
    String hs2SafetyValve = getRoleConfigValue(roleConfig, "hive_hs2_config_safety_valve");
    String port = getSafetyValveValue(hs2SafetyValve, "hive.server2.thrift.http.port");
    String httpPath = getSafetyValveValue(hs2SafetyValve, "hive.server2.thrift.http.path");
    boolean sslEnabled = Boolean.parseBoolean(getRoleConfigValue(roleConfig, "hive.server2.use.SSL"));
    String scheme = sslEnabled ? "https" : "http";
    return new ServiceModel(ServiceModel.Type.API,
                            SERVICE,
                            String.format(Locale.getDefault(), "%s://%s:%s/%s", scheme, hostname, port, httpPath));
  }

  private boolean checkHiveServer2HTTPMode(ApiConfigList roleConfig) {
    String hiveServer2SafetyValve = getRoleConfigValue(roleConfig, "hive_hs2_config_safety_valve");
    if(hiveServer2SafetyValve != null) {
      String transportMode = getSafetyValveValue(hiveServer2SafetyValve, "hive.server2.transport.mode");
      return "http".equals(transportMode);
    }
    return false;
  }

}
