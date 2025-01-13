- name: Deploy Containerized App to Azure AKS
  run: |
    az aks deploy --resource-group myResourceGroup --name myAKSCluster --file path/to/deployment.yaml
