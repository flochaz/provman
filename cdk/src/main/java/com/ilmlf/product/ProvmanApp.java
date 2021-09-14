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

import com.ilmlf.product.api.ApiStack;
import com.ilmlf.product.db.DbStack;
import java.io.IOException;

import software.amazon.awscdk.core.App;

/**
 * The entry point of CDK application. This class creates a CDK App with two stacks
 * 1. DbStack contains network resources (e.g. VPC, subnets), MySQL DB, DB Proxy, and secrets
 * 2. ApiStack contains API Gateway and Lambda functions for compute
 *
 * <p>
 * We separate the two stacks from each other as they have different life cycles. The ApiStack will
 * be updated more frequently while the DbStack should be rarely updated. This also allows us to
 * put different permission settings for each stack (e.g. prevent an innocent intern deleting
 * your DB accidentally).
 * </p>
 */
public class ProvmanApp {

  /**
   * Entry point of the CDK CLI.
   *
   * @param args Not used
   * @throws IOException can be thrown from ApiStack as it read and build Lambda package
   */
  public static void main(final String[] args) throws IOException {
    App app = new App();

    new DbStack(app, "ProvmanDbStack", DbStack.DbStackProps.builder()
        .dbPort(3306)
        .dbUsername("admin")
        .build());

    new com.ilmlf.product.api.ApiStack(app, "ProvmanClusterStack", ApiStack.ApiStackProps.builder()
        .build());

    app.synth();
  }
}
