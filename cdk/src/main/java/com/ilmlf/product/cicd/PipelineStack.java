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

import lombok.Data;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.pipelines.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class PipelineStack extends Stack {

    @lombok.Builder
    @Data
    public static class PipelineStackProps implements StackProps {
        private Set<Environment> StageEnvironments;
        private Environment env;
    }

    public PipelineStack(Construct scope, String id, PipelineStackProps options) throws IOException {
        super(scope, id, options);
        CodePipeline pipeline = CodePipeline.Builder.create(this, "Pipeline")
                .crossAccountKeys(true)
                .pipelineName("ProvMan")
                .selfMutation(true)
                .synth(
                        ShellStep.Builder.create("Synth")
                                .input(CodePipelineSource.gitHub(
                                        "flochaz/provman",
                                        "main",
                                        GitHubSourceOptions.builder().authentication(
                                                SecretValue.secretsManager("GITHUB_TOKEN")).build()
                                        )
                                )
                                .commands(Arrays.asList("npm install -g aws-cdk", "cd cdk", "npx cdk synth"))
                                .primaryOutputDirectory("cdk/cdk.out")
                                .build())
                .build();

        for (Environment stageEnvironment : options.getStageEnvironments()) {
            pipeline.addStage(new ProvmanStage(this, "test", StageProps.builder().env(stageEnvironment).build()));
        }
    }
}
