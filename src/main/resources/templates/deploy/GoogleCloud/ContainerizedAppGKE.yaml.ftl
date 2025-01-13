- name: Deploy Containerized App to Google Kubernetes Engine
  uses: google-github-actions/deploy-gke@v1
  with:
    cluster: my-gke-cluster
    location: us-central1
    manifests: path/to/deployment.yaml
