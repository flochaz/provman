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

package com.ilmlf.product;

import com.ilmlf.product.cicd.PipelineStack;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awscdk.core.Annotations;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Environment;

/**
 * The entry point of CDK application.
 * This 
 *
 * <p>
 * We separate the two stacks from each other as they have different life cycles. The ApiStack will
 * be updated more frequently while the DbStack should be rarely updated. This also allows us to
 * put different permission settings for each stack (e.g. prevent an innocent intern deleting
 * your DB accidentally).
 * </p>
 *
 */
public class ProvmanApp {

  /**
   * Helper method to build an environment.
   * @param scope like app, construct etc.
   * @param contextKey referencing the env json object with region and account attributes.
   * @throws IOException can be thrown from ApiStack as it read and build Lambda package.
   */
  static Environment makeEnv(Construct scope, String contextKey) {

    Map<String, String> envParamsFromContext = (Map<String,String>) scope.getNode().tryGetContext(contextKey);
    if(envParamsFromContext != null) {
      return Environment.builder()
              .account(envParamsFromContext.get("account"))
              .region(envParamsFromContext.get("region"))
              .build();
    } else {
      Annotations.of(scope).addWarning(String.format("No environment params found in context for env {}, using default", contextKey));
      return Environment.builder()
              .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
              .region(System.getenv("CDK_DEFAULT_REGION"))
              .build();
    }

  }

  /**
   * Entry point of the CDK CLI.
   * @param args
   * @throws Exception
   */
  public static void main(final String[] args) throws Exception {
    App app = new App();

    Set<Environment> deployableEnvironments = new HashSet<>();

    Environment cicd = makeEnv(app, "CICD_ENV");
    Environment qa = makeEnv(app, "QA_ENV");

    deployableEnvironments.add(qa);
    new PipelineStack(app, "Pipeline", PipelineStack.PipelineStackProps.builder().env(cicd).stageEnvironments(deployableEnvironments).build());
    app.synth();
  }
}