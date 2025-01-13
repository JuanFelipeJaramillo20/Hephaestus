- name: Deploy Web Resource to AWS
  run: |
    aws s3 sync ./build s3://my-bucket-name --acl public-read
