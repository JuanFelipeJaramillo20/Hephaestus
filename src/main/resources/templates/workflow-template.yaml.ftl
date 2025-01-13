name: ${workflowName}

on:
<#list triggerEvents as event>
    ${event}:
    branches: [
    <#list branches as branch>"${branch}"<#if branch_has_next>, </#if></#list>
    ]
</#list>

jobs:
    build-and-test:
      runs-on: ubuntu-latest
      steps:
        - name: Checkout code
          uses: actions/checkout@v2

        - name: Run ${buildTool} Build
          uses: CI-CD-assistant/${buildTool?lower_case}-build-action@V0.2
          with:
            project_path: "${buildProjectPath}"

        - name: Run Tests
          uses: CI-CD-assistant/run-tests-action@V0.1
          with:
            project_type: "${testBuildTool}"
            project_path: "${testProjectPath}"

    deploy:
        runs-on: ubuntu-latest
        needs: build-and-test
        steps:
<#if cloudProvider == "AWS">
    <#if deploymentType == "LambdaFunction">
                - name: Deploy to AWS Lambda
                  uses: aws-actions/aws-lambda-deploy@v1
                  with:
                    function-name: my-lambda-function
                    zip-file: path/to/deployment/package.zip
    <#elseif deploymentType == "WebResource">
                - name: Deploy Web Resource
                  run: echo "Deploy Web Resource to AWS (custom logic here)"
    <#elseif deploymentType == "ContainerizedAppECS">
                - name: Deploy Containerized App to ECS
                  uses: aws-actions/amazon-ecs-deploy-task-definition@v1
                  with:
                    task-definition: path/to/task-definition.json
                    service: my-ecs-service
                    cluster: my-ecs-cluster
    <#elseif deploymentType == "SDK">
                - name: Publish SDK to AWS
                  run: echo "Publishing SDK to AWS S3 or another SDK distribution service (custom logic)"
    </#if>
<#elseif cloudProvider == "Google Cloud">
    <#if deploymentType == "CloudFunction">
                - name: Deploy to Google Cloud Functions
                  uses: google-github-actions/deploy-cloud-functions@v0
                  with:
                    name: my-function
                    runtime: nodejs14
                    entry-point: functionHandler
                    source: ./src
    <#elseif deploymentType == "WebAppAppEngine">
                - name: Deploy Web App to Google App Engine
                  uses: google-github-actions/deploy-appengine@v1
                  with:
                    deliverables: ./build
    <#elseif deploymentType == "ContainerizedAppGKE">
                - name: Deploy Containerized App to GKE
                  uses: google-github-actions/deploy-gke@v0
                  with:
                    cluster: my-cluster
                    location: us-central1
                    manifests: path/to/kubernetes-manifests.yaml
    <#elseif deploymentType == "SDK">
                - name: Publish SDK to Google Cloud
                  run: echo "Publishing SDK to Google Cloud Storage or another SDK distribution service (custom logic)"
    </#if>
<#elseif cloudProvider == "Azure">
    <#if deploymentType == "Function">
                - name: Deploy to Azure Functions
                  uses: azure/functions-action@v1
                  with:
                    app-name: my-function-app
                    package: path/to/deployment/package.zip
    <#elseif deploymentType == "WebAppAppService">
                - name: Deploy Web App to Azure
                  uses: azure/webapps-deploy@v2
                  with:
                    app-name: my-web-app
                    package: path/to/deployment/package.zip
    <#elseif deploymentType == "ContainerizedAppAKS">
                - name: Deploy Containerized App to AKS
                  uses: azure/aks-deploy@v1
                  with:
                    resource-group: my-resource-group
                    cluster-name: my-aks-cluster
                    manifests: path/to/kubernetes-manifests.yaml
    <#elseif deploymentType == "SDK">
                - name: Publish SDK to Azure
                  run: echo "Publishing SDK to Azure Blob Storage or another SDK distribution service (custom logic)"
    </#if>
<#else>
            - name: Unsupported Deployment Type
              run: echo "Selected deployment type or cloud provider is not supported"
</#if>
