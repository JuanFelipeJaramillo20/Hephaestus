- name: Deploy Containerized App to ECS
  uses: aws-actions/amazon-ecs-deploy-task-definition@v1
  with:
    task-definition: path/to/task-definition.json
    service: my-ecs-service
    cluster: my-ecs-cluster
