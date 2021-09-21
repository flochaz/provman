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

package com.ilmlf.product.cicd;

import software.amazon.awscdk.core.StageProps;
import software.amazon.awscdk.core.Stage;
import com.ilmlf.product.api.ApiStack;
import com.ilmlf.product.db.DbStack;

import java.io.IOException;

public class ProvmanStage extends Stage {

    public ProvmanStage(software.constructs.Construct scope,
                        java.lang.String id,
                        StageProps props) throws IOException {
        super(scope, id, props);
        new DbStack(this, "ProvmanDbStack",DbStack.DbStackProps.builder()
                .dbPort(3306)
                .dbUsername("admin")
                .build());

        new ApiStack(this, "ProvmanClusterStack", ApiStack.ApiStackProps.builder()
                .build());

    }


}
