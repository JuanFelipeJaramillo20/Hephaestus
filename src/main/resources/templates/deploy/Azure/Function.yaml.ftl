- name: Deploy to Azure Functions
  uses: azure/functions-action@v1
  with:
    app-name: my-function-app
    package: path/to/deployment/package.zip
