name: ${workflowName}

on:
<#list triggerEvents as event>
    ${event}:
        branches:
        <#list branches as branch>
          - ${branch}
        </#list>
</#list>

jobs:
  build-and-test:
    name: Build and Test
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
        continue-on-error: false

  deploy:
    name: Deploy Job
    runs-on: ubuntu-latest
    needs: build-and-test
    steps:
<#if cloudProvider == "AWS">
      - name: Setup AWS Credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: YOUR_AWS_ACCESS_KEY_ID
          aws-secret-access-key: YOUR_AWS_SECRET_ACCESS_KEY
          aws-region: us-east-1
    <#if deploymentType == "LambdaFunction">
      - name: Deploy AWS Lambda
        uses: aws-actions/aws-lambda-deploy@v1
        with:
          function-name: my-lambda-function
          zip-file: path/to/deployment/package.zip
    <#elseif deploymentType == "WebResource">
      - name: Deploy Web Resource
        run: echo "Deploying web resource on AWS"
    <#elseif deploymentType == "ContainerizedAppECS">
      - name: Deploy to ECS
        uses: aws-actions/amazon-ecs-deploy-task-definition@v1
        with:
          task-definition: path/to/task-definition.json
          service: my-ecs-service
          cluster: my-ecs-cluster
    <#elseif deploymentType == "SDK">
      - name: Deploy SDK
        run: echo "Deploying SDK to AWS"
    </#if>
<#elseif cloudProvider == "Azure">
      - name: Setup Azure Credentials
        uses: azure/login@v1
        with:
          creds: YOUR_AZURE_CREDENTIALS
    <#if deploymentType == "Function">
      - name: Deploy Azure Function
        uses: azure/functions-action@v1
        with:
          app-name: my-function-app
          package: path/to/deployment/package.zip
    <#elseif deploymentType == "WebAppAppService">
      - name: Deploy Web App
        uses: azure/webapps-deploy@v2
        with:
          app-name: my-web-app
          package: path/to/deployment/package.zip
    <#elseif deploymentType == "ContainerizedAppAKS">
      - name: Deploy to AKS
        uses: azure/k8s-deploy@v3
        with:
          manifests: path/to/k8s-manifests.yaml
    <#elseif deploymentType == "SDK">
      - name: Deploy SDK
        run: echo "Deploying SDK to Azure"
    </#if>
<#elseif cloudProvider == "Google Cloud">
      - name: Setup Google Cloud Credentials
        uses: google-github-actions/setup-gcloud@v1
        with:
          credentials: YOUR_GOOGLE_CLOUD_CREDENTIALS
          project_id: my-gcp-project
    <#if deploymentType == "CloudFunction">
      - name: Deploy Google Cloud Function
        uses: google-github-actions/deploy-cloud-functions@v0
        with:
          name: my-function
          runtime: nodejs14
          entry-point: functionHandler
          source: ./src
    <#elseif deploymentType == "WebAppAppEngine">
      - name: Deploy to App Engine
        uses: google-github-actions/deploy-appengine@v1
        with:
          deliverables: ./build
    <#elseif deploymentType == "ContainerizedAppGKE">
      - name: Deploy to GKE
        uses: google-github-actions/deploy-gke@v1
        with:
          cluster_name: my-cluster
          location: us-central1-a
          manifests: path/to/k8s-manifests.yaml
    <#elseif deploymentType == "SDK">
      - name: Deploy SDK
        run: echo "Deploying SDK to Google Cloud"
    </#if>
</#if>
      - name: Log deployment result
        run: echo "Deployment completed successfully for ${cloudProvider} - ${deploymentType}"
        continue-on-error: false
