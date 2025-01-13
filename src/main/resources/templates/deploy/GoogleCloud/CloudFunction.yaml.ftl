- name: Deploy to Google Cloud Functions
  uses: google-github-actions/deploy-cloud-functions@v0
  with:
    name: my-function
    runtime: nodejs14
    entry-point: functionHandler
    source: ./src
