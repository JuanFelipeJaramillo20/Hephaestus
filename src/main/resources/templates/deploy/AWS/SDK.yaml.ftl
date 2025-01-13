- name: Deploy SDK to AWS S3
  run: |
    aws s3 cp ./sdk.zip s3://my-sdk-bucket/sdk.zip
