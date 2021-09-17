/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ilmlf.product.api;

import java.io.IOException;
import java.util.Map;

import lombok.Data;
import software.amazon.awscdk.cloudformation.include.CfnInclude;
import software.amazon.awscdk.cloudformation.include.CfnIncludeProps;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

public class ApiStack extends Stack {
  private static final Map<String, String> nestedStackTemplates = Map.of(
      "VPCStack", "./src/main/resources/ecs-vpc.yml",
      "ALBStack", "./src/main/resources/ecs-public-load-balancer.yml",
      "PrivateALBStack", "./src/main/resources/ecs-private-load-balancer.yml",
      "gMSASetupStack", "./src/main/resources/ecs-gmsa.yml",
      "ClusterStack", "./src/main/resources/ecs-cluster.yml",
      "PrivateAppStack", "./src/main/resources/ecs-private-app.yml",
      "LBWebAppStack", "./src/main/resources/ecs-lb-webapp.yml",
      "Route53Stack", "./src/main/resources/ecs-route53.yml"
  );

  @lombok.Builder
  @Data
  public static class ApiStackProps implements StackProps {
  }

  public ApiStack(final Construct scope, final String id, final ApiStackProps props)
      throws IOException {
    super(scope, id, props);

    CfnInclude provmanEcs = new CfnInclude(this, "ProvmanCluster", CfnIncludeProps.builder()
        .templateFile("./src/main/resources/ecs-master.json")
        .build());

    for (Map.Entry<String, String> entry : nestedStackTemplates.entrySet()) {
      provmanEcs.loadNestedStack(entry.getKey(), CfnIncludeProps.builder()
          .templateFile(entry.getValue())
          .build());
    }

  }
}

