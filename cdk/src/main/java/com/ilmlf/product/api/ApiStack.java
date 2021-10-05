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
import software.amazon.awscdk.services.ecr.assets.DockerImageAsset;
import software.amazon.awscdk.services.ecr.assets.DockerImageAssetProps;

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

  private static final String CONTAINER_NAME = "ProvmanEcsContainer";

  @lombok.Builder
  @Data
  public static class ApiStackProps implements StackProps {
  }

  public ApiStack(final Construct scope, final String id, final ApiStackProps props)
      throws IOException {
    super(scope, id, props);

    DockerImageAsset imageAsset = this.createContainerImage();

    CfnInclude provmanEcs = new CfnInclude(this, "ProvmanCluster", CfnIncludeProps.builder()
        .templateFile("./src/main/resources/ecs-master.json")
        .parameters(Map.of(
            "ContainerImage", imageAsset.getImageUri(),
            "SingleDestName", CONTAINER_NAME))
        .build());

    for (Map.Entry<String, String> entry : nestedStackTemplates.entrySet()) {
      provmanEcs.loadNestedStack(entry.getKey(), CfnIncludeProps.builder()
          .templateFile(entry.getValue())
          .build());
    }

  }

  /**
   * Try to bundle the package locally. CDK can use this method to build locally (which is faster).
   * If the build doesn't work, it will build within a Docker image which should work regardless of
   * local environment.
   *
   * Note that CDK expects this function to return either true or false based on bundling result.
   *
   * @param outputPath
   * @return whether the bundling script was successfully executed
   */
  private Boolean buildWar(String outputPath) {
    try {
      ProcessBuilder pb =
          new ProcessBuilder(
              "bash",
              "-c",
              "cd ../app && mvn package && cp target/provman.war "
                  + outputPath);

      Process p = pb.start(); // Start the process.
      p.waitFor(); // Wait for the process to finish.

      if (p.exitValue() == 0) {
        System.out.println("Script executed successfully");
        return true;
      } else {
        System.out.println("Script executed failed");
        return false;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public DockerImageAsset createContainerImage() {
    // this.buildWar("../cdk/src/main/container/");

    DockerImageAsset imageAsset = new DockerImageAsset(this, "ProvmanImage", DockerImageAssetProps.builder()
        .directory("./src/main/container")
        .build());

    return imageAsset;
  }
}

