# [Studies][AWS] SAM Rest API and CI/CD

> This project represents hands on study, based on the [course 'AWS Serverless REST APIs for Java Developers. CI/CD included](https://www.udemy.com/course/aws-serverless-rest-apis-for-java-developers/).

## Summary

### **Hands-on using AWS servless services**

<p style="float:left">
    <img src="https://img.shields.io/badge/Amazon%20AWS-232F3E?style=plastic&logo=Amazon%20AWS&logoColor=white" /> 
    <img src="https://img.shields.io/badge/AWS%20Lambda-FF9900?style=plastic&logo=aws%20lambda&logoColor=white" />	 	
    <img src="https://img.shields.io/badge/Amazon%20API%20Gateway-FF4F8B?style=plastic&logo=Amazon%20API%20Gateway&logoColor=white" />  
    <img src="https://img.shields.io/badge/Amazon%20Cognito-7a3e64?style=plastic" /> 
    <img src="https://img.shields.io/badge/AWS%20IAM-71973c?style=plastic" />
    <img src="https://img.shields.io/badge/Java-ED8B00?style=plastic&logo=java&logoColor=white" />     
    <img src="https://img.shields.io/badge/AWS%20CodeCommit-496aeb?style=plastic" />    
    <img src="https://img.shields.io/badge/AWS%20CodeBuild-496aeb?style=plastic" />
    <img src="https://img.shields.io/badge/AWS%20CodePipeline-496aeb?style=plastic" />
    <img src="https://img.shields.io/badge/AWS%20Lambda%20Authorizer-FF9900?style=plastic&logo=aws%20lambda&logoColor=white" /> 
    <img src="https://img.shields.io/badge/API%20keys%20and%20Usage%20plans-FF4F8B?style=plastic&logoColor=white" />
    <img src="https://img.shields.io/badge/AWS%20SAM%20(Serverless%20Application%20Model)-645a4f?style=plastic&logoColor=white" />
    <img src="https://img.shields.io/badge/AWS%20Key%20Management%20Service%20(AWS KMS)-f04245?style=plastic&logoColor=white" />
    <img src="https://img.shields.io/badge/AWS%20CloudFormation-e93b7a?style=plastic&logoColor=white" />
    
  
</p>
<br/><br/>

The main goal of this project is to get some hands on experience on AWS. 
- sam-rest-api-hands-on
  - Simple app to pratice the AWS Serverless Application Model (SAM) concepts.
- sam-user-api-cognito
  - REST API integrated with Cognito.
  - User can perform actions like:
    - sign up
    - confirm
    - login
    - add user to group
- sam-lambda-authorizer
  - SAM app to deploy an Lambda Authorizer that can be used on AWS API Gateway


### **What I've learned**

#### ***Amazon API Gateway***
- Create an API
  - Resources and methods
  - mock responses
  - path parameters
    - to read on mapping: $input.params('{paramName}')
  - query strings parameters
    - to read on mapping: $input.params('{paramName}')
- Deploy using Stages  
- Documenting API
  - publishing documentation
  - versioning documentation
- Exporting API
  - Swagger
  - OpenAPI
  - Postman
- Validate http request
  - headers
    - by checking 'required' or not
  - parameters
    - by checking 'required' or not
  - body
    - by using models/schemas
- Models
   - attach models to methods executions
- Data Transformations


#### ***AWS Lambda***
  - understanding 
    - cold start
    - warm start
  - pricing based on:
    - number of request  
    - duration
    - GB-S
      - GB: amount of memory allocated to the funciton
      - S: seconds - execution time
  - Create Lambda functions
    - using Java 11+ 
    - maven dependencies
      - aws-lambda-java-core
      - aws-lambda-java-events
    - maven plugin
      - shade: for uber/fat jar  
    - upload jar file to a lambda function 
    - Version
    - Alias 
  - Assign lambda function to an API Gateway Method
  - working with Environment variables
  
#### ***AWS SAM (Serverless Application Model)***
  - Install AWS SAM CLI and AWS CLI
    - Config AWS SAM CLI on gitbash windows: 
      ```
      vim ~/.bashrc
      alias sam='sam.cmd' 
        or 
      alias sam="/c/Program\ Files/Amazon/AWSSAMCLI/bin/sam.cmd"`
      ```
  - Configure AWS credentials
    - by using aws configure
  - create new project using SAM
    ``` 
    sam init 
    ```
    > on windows: if there's issues on init, run this on powershell (as admin) 
    >> New-ItemProperty -Path "HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem" -Name "LongPathsEnabled" -Value 1 -PropertyType DWORD -Force
  - build
    ```
    sam build 
    ```
  - test it locally using Docker
    ``` 
      sam local invoke {FunctioName} --event {folder/file.json} 
      E.g.:
      sam local invoke TestFunciton --event events/event.json    
    ```
  - debug locally
    ``` 
      sam local invoke *{FunctioName}* --event {folder/file.json} -d {debugPort}
      E.g.:
      sam local invoke TestFunciton --event events/event.json -d 5858
    ``` 
  - deploy to AWS    
     ```  
     sam deploy --guided
     or, after preferences saved to a .toml file
     sam deploy --config-file {.toml file path}
     ``` 
  - View logs  
    ``` 
    sam logs --name {FunctioName} --stack-name {CloudFormationStackName}
    tailing:
    sam logs --name {FunctioName} --stack-name {CloudFormationStackName} --tail
     ``` 
  - Delete AWS SAM application  
     ``` 
    sam delete {CloudFormationStackName}
     ``` 

#### ***Amazon Cognito***
 - Create and configure 'user pools' 
 - Use User Pool in SAM application
    - aws sdk cognito identity
    - aws sdk cognito identity provider
    - aws sdk apache-client
- Groups

#### ***AWS KMS***
  - encrypt value using aws cli and kms
  ``` 
  aws kms encrypt --key-id {kms-key-id} --plaintext fileb://{file-path}
  
  output:
  {
    "CiphertextBlob": "{encrypted-value}",
    "KeyId": "arn:aws:kms:us-east-1:123456:key/{kms-key-id}",
    "EncryptionAlgorithm": "SYMMETRIC_DEFAULT"
  }
  ``` 

### ***AWS Lambda Authorizer***  
  - JWT


### ***API keys and Usage plans***  
  - create an api key
  - config the usage plan
  - config api gateway to use api key

### ***AWS Code Commit***  
  - create a repository
  - create an user credentials to perform commits/push to repos
    - go to IAM and create an user
      - check 'Access key - programmatic access' since we're just using it to code commit
      - grant permission do CodeCommit. e.g. AwsCodeCommitPowerUser
    - go to the user created, on the tab 'Security credentials'
      - Find the section 'HTTPS Git credentials for aws CodeCommit' and click on 'Generate credentials'
        - save username/password    

### ***AWS Code Build***  
  - create an s3 bucket to store files created during the build process
  - create a buildspec file
    - file that contains instructions to build the project
  - create a CodeBuild project to use CodeCommit as source provider
    - select the repo and the branch
    - config buildspec location (if it's not in the root folder)
    - config artifact to 
      - Type 'AmazonS3'
      - select the bucket created previously
      - Artifacts packing to 'Zip'

### ***AWS CodePipeline***
  - Create a pipeline  
    - on source stage
      - select AWS CodeCommit as source provider
        - select the repo and the branch
    - on build stage    
      - select a code build project
    - on deploy stage    
      - select CloudFormation and provide the StackName
    - add custom action on deploy stage to execute change set  
