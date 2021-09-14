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

package com.ilmlf.product.db;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.SecurityGroupProps;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.DatabaseInstanceProps;
import software.amazon.awscdk.services.rds.MySqlInstanceEngineProps;
import software.amazon.awscdk.services.rds.MysqlEngineVersion;
import software.amazon.awscdk.services.rds.StorageType;
import software.amazon.awscdk.services.secretsmanager.ISecret;
import software.amazon.awscdk.services.secretsmanager.Secret;


/**
 * Database stack contains the followings.
 * 1. Network resources (VPC, subnets, and Security Group)
 * 2. RDS Database
 * 3. RDS Proxy for connecting to database
 * 4. Secrets for Lambda to connect to RDS Proxy
 */
@Getter
public class DbStack extends Stack {
  /**
   * Database username for DB Proxy connection (via IAM Authorization).
   */
  private final String dbUsername;
  private final String instanceEndpoint;
  private final Integer dbPort;
  private final Vpc vpc;
  private final SecurityGroup securityGroup;

  /**
   * Contains database info, admin username, and password. This is a secret generated when
   * creating the DB. Example of keys are:
   * - username (admin username)
   * - password (admin password)
   * - engine (mysql
   * - port (port for connection)
   * - dbname (FarmerDB)
   * - host (used for connection)
   */
  private final ISecret adminSecret;

  /**
   * Secret to be used by Lambda to connect to RDS Proxy via IAM authentication.
   * - username (hardcoded to lambda_iam)
   * - password
   */
//  private final Secret userSecret;

  @lombok.Builder
  @Data
  public static class DbStackProps implements StackProps {
    private String description;
    private String dbUsername;
    private Integer dbPort;
    private Environment env;
    private boolean isPublicSubnetDb;
  }

  /**
   * Create a Database stack.
   *
   * @param scope used by superclass.
   * @param id used by superclass.
   * @param props used by superclass.
   */
  public DbStack(final Construct scope, final String id, final DbStackProps props) {
    super(scope, id, props);

    this.dbUsername = props.getDbUsername();
    this.dbPort = props.getDbPort();

    /**
     * #################
     * Network resources
     * #################
     *
     * Create a VPC (Virtual Private Cloud), used for network partitioning.
     *
     * The VPC contains multiple "Subnets" that could be either Internet-public or private.
     * Each Subnet is placed in different AZ (Availability Zones). Each AZ is in a different location
     * within the region. In production, you should place your database and its replica in multiple AZ
     * in case of failover. By default this stack deploys a database instance and its replica to different AZs.
     */

    // The `Vpc` construct creates subnets for you automatically
    // See https://docs.aws.amazon.com/cdk/api/latest/docs/aws-ec2-readme.html#vpc for details
    this.vpc = new Vpc(this, "farmer-Vpc");

    // Security group acts as a virtual firewall to control inbound and outbound traffic
    this.securityGroup =
        new SecurityGroup(
            this,
            "FarmerDeliverySG",
            SecurityGroupProps.builder()
                .vpc(vpc)
                .description("Shared SG for database and proxy")
                .allowAllOutbound(true)
                .build());

    /**
     * #################
     * ### DB Instance #
     * #################
     * Creates a MYSQL RDS instance.
     *
     * This construct also creates a secret store in AWS Secrets Manager. You can retrieve
     * reference to the secret store by calling farmerDB.getSecret()
     *
     * The secret store contains the admin username, password and other DB information for connecting to the DB
     *
     * For production, consider using `DatabaseCluster` to create multiple instances in different AZs.
     * This costs more but you will have higher availability.
     *
     * See https://docs.aws.amazon.com/cdk/api/latest/docs/aws-rds-readme.html for details.
     */
    String dbName = "FarmerDB";
    List<ISubnet> subnets;

    if (props.isPublicSubnetDb) {
       subnets = vpc.getPublicSubnets();
    } else {
      subnets = vpc.getPrivateSubnets();
    }

    DatabaseInstance farmerDb =
        new DatabaseInstance(
            this,
            dbName,
            DatabaseInstanceProps.builder()
                .vpc(vpc)
                .securityGroups(List.of(securityGroup))
                // Using MySQL engine
                .engine(
                    DatabaseInstanceEngine.mysql(
                        MySqlInstanceEngineProps.builder()
                            .version(MysqlEngineVersion.VER_5_7_31)
                            .build()))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.SMALL))
                .vpcSubnets(SubnetSelection.builder().subnets(subnets).build())
                .storageEncrypted(true)
                .multiAz(true)
                .autoMinorVersionUpgrade(true)
                .allocatedStorage(25)
                .publiclyAccessible(true)
                .storageType(StorageType.GP2)
                .backupRetention(Duration.days(7))
                .deletionProtection(false)
                // Create an admin credential for connectin to database. This credential will
                // be stored in a Secret Manager store.
                .credentials(Credentials.fromGeneratedSecret(dbName + "admin"))
                .databaseName(dbName)
                .port(this.dbPort)
                .build());

    /**
     * Creates a username/password that will be used for IAM authentication.
     *
     * This user will be created in the database in the ApiStack (see PopulateFarmDb.java and dbinit.sql).
     * The user will be used by API Lambda handlers to access the database via DB Proxy (with IAM Authentication).
     */
//    this.userSecret = new Secret(this, "DbUserSecret",
//        SecretProps.builder()
//            .description("Db Username and password")
//            .generateSecretString(
//                SecretStringGenerator.builder()
//                    .secretStringTemplate("{\"username\": \"" + this.dbUsername + "\"}")
//                    .generateStringKey("password")
//                    .passwordLength(16)
//                    .excludePunctuation(true)
//                    .build()
//            )
//            .build()
//    );

    this.instanceEndpoint = farmerDb.getDbInstanceEndpointAddress() + ":" + farmerDb.getDbInstanceEndpointPort();
    this.adminSecret = farmerDb.getSecret();
  }
}
