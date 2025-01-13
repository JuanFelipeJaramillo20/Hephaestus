- name: Deploy to AWS Lambda
  uses: aws-actions/aws-lambda-deploy@v1
  with:
    function-name: my-lambda-function
    zip-file: path/to/deployment/package.zip
